package com.ashokvarma.gander;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashokvarma.gander.internal.data.GanderStorage;
import com.ashokvarma.gander.internal.data.HttpHeader;
import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.support.Logger;
import com.ashokvarma.gander.internal.support.NotificationHelper;
import com.ashokvarma.gander.internal.support.RetentionManager;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 02/06/18
 */
public class GanderInterceptor implements Interceptor {

    public enum Period {
        /**
         * Retain data for the last hour.
         */
        ONE_HOUR,
        /**
         * Retain data for the last day.
         */
        ONE_DAY,
        /**
         * Retain data for the last week.
         */
        ONE_WEEK,
        /**
         * Retain data forever.
         */
        FOREVER
    }

    @NonNull
    private static final Period DEFAULT_RETENTION = Period.ONE_WEEK;
    @NonNull
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @NonNull
    private final Context mContext;
    @NonNull
    private final GanderStorage mGanderStorage;
    @Nullable
    private NotificationHelper mNotificationHelper;
    @NonNull
    private RetentionManager mRetentionManager;
    private long mMaxContentLength = 250000L;
    @NonNull
    private volatile Set<String> headersToRedact = Collections.emptySet();
    private boolean stickyNotification = false;

    /**
     * @param context The current Context.
     */
    public GanderInterceptor(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        mGanderStorage = Gander.getGanderStorage();
        mRetentionManager = new RetentionManager(this.mContext, DEFAULT_RETENTION);
    }

    /**
     * Control whether a notification is shown while HTTP activity is recorded.
     *
     * @param sticky true to show a sticky notification.
     * @return The {@link GanderInterceptor} instance.
     */
    @NonNull
    public GanderInterceptor showNotification(boolean sticky) {
        this.stickyNotification = sticky;
        mNotificationHelper = new NotificationHelper(this.mContext);
        return this;
    }


    /**
     * Set the retention period for HTTP transaction data captured by this interceptor.
     * The default is one week.
     *
     * @param period the period for which to retain HTTP transaction data.
     * @return The {@link GanderInterceptor} instance.
     */
    @NonNull
    public GanderInterceptor retainDataFor(Period period) {
        mRetentionManager = new RetentionManager(mContext, period);
        return this;
    }

    /**
     * Set the maximum length for request and response content before it is truncated.
     * Warning: setting this value too high may cause unexpected results.
     *
     * @param max the maximum length (in bytes) for request/response content.
     * @return The {@link GanderInterceptor} instance.
     */
    @NonNull
    public GanderInterceptor maxContentLength(long max) {
        this.mMaxContentLength = Math.min(max, 999999L);// close to => 1 MB Max in a BLOB SQLite.
        return this;
    }

    /**
     * Set headers names that shouldn't be stored by gander
     *
     * @param name the name of header to redact
     * @return The {@link GanderInterceptor} instance.
     */
    @NonNull
    public GanderInterceptor redactHeader(String name) {
        Set<String> newHeadersToRedact = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        newHeadersToRedact.addAll(headersToRedact);
        newHeadersToRedact.add(name);
        headersToRedact = newHeadersToRedact;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpTransaction transaction = createTransactionFromRequest(request);
        long startNs = System.nanoTime();

        try {
            Response response = chain.proceed(request);

            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            updateTransactionFromResponse(transaction, response, tookMs);

            return response;
        } catch (Exception e) {
            update(transaction.toBuilder().setError(e.toString()).build());

            throw e;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Conversion from Request Response to HttpTransaction
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    private HttpTransaction createTransactionFromRequest(@NonNull Request request) throws IOException {
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        HttpTransaction.Builder transactionBuilder = HttpTransaction.newBuilder();
        transactionBuilder.setRequestDate(new Date());

        transactionBuilder.setMethod(request.method());
        transactionBuilder.setUrlHostPathSchemeFromUrl(request.url().toString());

        transactionBuilder.setRequestHeaders(toHttpHeaderList(request.headers()));
        if (hasRequestBody) {
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                transactionBuilder.setRequestContentType(contentType.toString());
            }
            if (requestBody.contentLength() != -1) {
                transactionBuilder.setRequestContentLength(requestBody.contentLength());
            }
        }

        boolean requestBodyIsPlainText = bodyHasSupportedEncoding(request.headers());
        transactionBuilder.setRequestBodyIsPlainText(requestBodyIsPlainText);
        if (hasRequestBody && requestBodyIsPlainText) {
            BufferedSource source = getNativeSource(new Buffer(), bodyGzipped(request.headers()));
            Buffer buffer = source.buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (isPlaintext(buffer)) {
                transactionBuilder.setRequestBody(readFromBuffer(buffer, charset));
            } else {
                transactionBuilder.setResponseBodyIsPlainText(false);
            }
        }

        return create(transactionBuilder.build());// need to be sequential to get the id
    }

    private void updateTransactionFromResponse(@NonNull HttpTransaction transaction, @NonNull Response response, long tookMs) throws IOException {
        ResponseBody responseBody = response.body();
        HttpTransaction.Builder newTransactionBuilder = transaction.toBuilder();

        if (response.cacheResponse() != null) {
            // receivedResponseAtMillis, sentRequestAtMillis =>
            // If response is being served from the cache then these are the timestamp of the original response.
            // So using calculated time
            newTransactionBuilder.setResponseDate(new Date());
            newTransactionBuilder.setTookMs(tookMs);
        } else {
            // most accurate time, will not include the delay by other interceptors
            newTransactionBuilder.setTookMs(response.receivedResponseAtMillis() - response.sentRequestAtMillis());
            newTransactionBuilder.setRequestDate(new Date(response.sentRequestAtMillis()));
            newTransactionBuilder.setResponseDate(new Date(response.receivedResponseAtMillis()));
        }
        newTransactionBuilder.setRequestHeaders(toHttpHeaderList(response.request().headers())); // includes headers added/modified/removed later in the chain
        newTransactionBuilder.setProtocol(response.protocol().toString());
        newTransactionBuilder.setResponseCode(response.code());
        newTransactionBuilder.setResponseMessage(response.message());

        if (responseBody != null) {
            newTransactionBuilder.setResponseContentLength(responseBody.contentLength());
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                newTransactionBuilder.setResponseContentType(contentType.toString());
            }
        }
        newTransactionBuilder.setResponseHeaders(toHttpHeaderList(response.headers()));

        boolean responseBodyIsPlainText = bodyHasSupportedEncoding(response.headers());
        newTransactionBuilder.setResponseBodyIsPlainText(responseBodyIsPlainText);
        if (HttpHeaders.hasBody(response) && responseBodyIsPlainText) {
            BufferedSource source = getNativeSource(response);
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = null;
            if (responseBody != null) {
                contentType = responseBody.contentType();
            }
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    update(newTransactionBuilder.build());
                    return;
                }
            }
            if (isPlaintext(buffer)) {
                newTransactionBuilder.setResponseBody(readFromBuffer(buffer.clone(), charset));
            } else {
                newTransactionBuilder.setResponseBodyIsPlainText(false);
            }
            newTransactionBuilder.setResponseContentLength(buffer.size());
        }

        update(newTransactionBuilder.build());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Database update/create
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    private HttpTransaction create(@NonNull HttpTransaction transaction) {
        long transactionId = mGanderStorage.getTransactionDao().insertTransaction(transaction);
        HttpTransaction newTransaction = transaction.toBuilder().setId(transactionId).build();
        if (mNotificationHelper != null) {
            mNotificationHelper.show(newTransaction, stickyNotification);
        }
        mRetentionManager.doMaintenance();
        return newTransaction;
    }

    private void update(@NonNull HttpTransaction transaction) {
        int updatedTransactionCount = mGanderStorage.getTransactionDao().updateTransaction(transaction);

        if (mNotificationHelper != null && updatedTransactionCount > 0) {
            mNotificationHelper.show(transaction, stickyNotification);
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Body Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private boolean isPlaintext(@NonNull Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyHasSupportedEncoding(@NonNull Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding == null ||
                contentEncoding.equalsIgnoreCase("identity") ||
                contentEncoding.equalsIgnoreCase("gzip");
    }

    private boolean bodyGzipped(@NonNull Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return "gzip".equalsIgnoreCase(contentEncoding);
    }

    @NonNull
    private String readFromBuffer(@NonNull Buffer buffer, @NonNull Charset charset) {
        long bufferSize = buffer.size();
        long maxBytes = Math.min(bufferSize, mMaxContentLength);
        String body = "";
        try {
            body = buffer.readString(maxBytes, charset);
        } catch (EOFException e) {
            body += mContext.getString(R.string.gander_body_unexpected_eof);
        }
        if (bufferSize > mMaxContentLength) {
            body += mContext.getString(R.string.gander_body_content_truncated);
        }
        return body;
    }

    @NonNull
    private BufferedSource getNativeSource(@NonNull BufferedSource input, boolean isGzipped) {
        if (isGzipped) {
            GzipSource source = new GzipSource(input);
            return Okio.buffer(source);
        } else {
            return input;
        }
    }

    @NonNull
    private BufferedSource getNativeSource(@NonNull Response response) throws IOException {
        if (bodyGzipped(response.headers())) {
            BufferedSource source = response.peekBody(mMaxContentLength).source();
            if (source.buffer().size() < mMaxContentLength) {
                return getNativeSource(source, true);
            } else {
                Logger.w("gzip encoded response was too long");
            }
        }
        return response.body().source();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Header Converter
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    private List<HttpHeader> toHttpHeaderList(@NonNull Headers headers) {
        List<HttpHeader> httpHeaders = new ArrayList<>();
        for (int i = 0, count = headers.size(); i < count; i++) {
            if (headersToRedact.contains(headers.name(i))) {
                httpHeaders.add(new HttpHeader(headers.name(i), "\u2588\u2588\u2588\u2588"));
            } else {
                httpHeaders.add(new HttpHeader(headers.name(i), headers.value(i)));
            }
        }
        return httpHeaders;
    }
}
