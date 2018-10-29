package com.ashokvarma.gander;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ashokvarma.gander.internal.data.GanderDatabase;
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
import java.util.Date;
import java.util.List;
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


    private static final Period DEFAULT_RETENTION = Period.ONE_WEEK;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Context mContext;
    private final GanderDatabase mGanderDatabase;
    private final NotificationHelper mNotificationHelper;
    private RetentionManager mRetentionManager;
    private boolean mShowNotification;
    private long mMaxContentLength = 250000L;

    /**
     * @param context          The current Context.
     * @param showNotification true to show a notification, false to suppress it.
     */
    public GanderInterceptor(Context context, boolean showNotification) {
        this.mContext = context.getApplicationContext();
        mGanderDatabase = GanderDatabase.getInstance(context);
        mNotificationHelper = new NotificationHelper(this.mContext);
        mRetentionManager = new RetentionManager(this.mContext, DEFAULT_RETENTION);
        showNotification(showNotification);// to avoid un-necessary channel creation it's requested in constructor
    }

    /**
     * Set the maximum length for request and response content before it is truncated.
     * Warning: setting this value too high may cause unexpected results.
     *
     * @param max the maximum length (in bytes) for request/response content.
     * @return The {@link GanderInterceptor} instance.
     */
    public GanderInterceptor maxContentLength(long max) {
        this.mMaxContentLength = Math.min(max, 999999L);// close to => 1 MB Max in a BLOB SQLite.
        return this;
    }

    /**
     * Set the retention period for HTTP transaction data captured by this interceptor.
     * The default is one week.
     *
     * @param period the peroid for which to retain HTTP transaction data.
     * @return The {@link GanderInterceptor} instance.
     */
    public GanderInterceptor retainDataFor(Period period) {
        mRetentionManager = new RetentionManager(mContext, period);
        return this;
    }

    /**
     * Control whether a notification is shown while HTTP activity is recorded.
     *
     * @param show true to show a notification, false to suppress it.
     */
    private void showNotification(boolean show) {
        mShowNotification = show;
        if (mShowNotification) {
            mNotificationHelper.setUpChannelIfNecessary();
        }
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
            transaction.setError(e.toString());
            update(transaction);

            throw e;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Conversion from Request Response to HttpTransaction
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    private HttpTransaction createTransactionFromRequest(Request request) throws IOException {
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        HttpTransaction transaction = new HttpTransaction();
        transaction.setRequestDate(new Date());

        transaction.setMethod(request.method());
        transaction.setUrlHostPathSchemeFromUrl(request.url().toString());

        transaction.setRequestHeaders(toHttpHeaderList(request.headers()));
        if (hasRequestBody) {
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                transaction.setRequestContentType(contentType.toString());
            }
            if (requestBody.contentLength() != -1) {
                transaction.setRequestContentLength(requestBody.contentLength());
            }
        }

        transaction.setRequestBodyIsPlainText(bodyHasSupportedEncoding(request.headers()));
        if (hasRequestBody && transaction.requestBodyIsPlainText()) {
            BufferedSource source = getNativeSource(new Buffer(), bodyGzipped(request.headers()));
            Buffer buffer = source.buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (isPlaintext(buffer)) {
                transaction.setRequestBody(readFromBuffer(buffer, charset));
            } else {
                transaction.setResponseBodyIsPlainText(false);
            }
        }

        return create(transaction);// need to be sequential to get the id
    }

    private void updateTransactionFromResponse(HttpTransaction transaction, Response response, long tookMs) throws IOException {
        ResponseBody responseBody = response.body();

        if (response.cacheResponse() != null) {
            // receivedResponseAtMillis, sentRequestAtMillis =>
            // If response is being served from the cache then these are the timestamp of the original response.
            // So using calculated time
            transaction.setResponseDate(new Date());
            transaction.setTookMs(tookMs);
        } else {
            // most accurate time, will not include the delay by other interceptors
            transaction.setTookMs(response.receivedResponseAtMillis() - response.sentRequestAtMillis());
            transaction.setRequestDate(new Date(response.sentRequestAtMillis()));
            transaction.setResponseDate(new Date(response.receivedResponseAtMillis()));
        }
        transaction.setRequestHeaders(toHttpHeaderList(response.request().headers())); // includes headers added/modified/removed later in the chain
        transaction.setProtocol(response.protocol().toString());
        transaction.setResponseCode(response.code());
        transaction.setResponseMessage(response.message());

        if (responseBody != null) {
            transaction.setResponseContentLength(responseBody.contentLength());
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                transaction.setResponseContentType(contentType.toString());
            }
        }
        transaction.setResponseHeaders(toHttpHeaderList(response.headers()));

        transaction.setResponseBodyIsPlainText(bodyHasSupportedEncoding(response.headers()));
        if (HttpHeaders.hasBody(response) && transaction.responseBodyIsPlainText()) {
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
                    update(transaction);
                    return;
                }
            }
            if (isPlaintext(buffer)) {
                transaction.setResponseBody(readFromBuffer(buffer.clone(), charset));
            } else {
                transaction.setResponseBodyIsPlainText(false);
            }
            transaction.setResponseContentLength(buffer.size());
        }

        update(transaction);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Database update/create
    ///////////////////////////////////////////////////////////////////////////

    private HttpTransaction create(HttpTransaction transaction) {
        long transactionId = mGanderDatabase.httpTransactionDao().insertTransaction(transaction);
        transaction.setId(transactionId);
        if (mShowNotification) {
            mNotificationHelper.show(transaction);
        }
        mRetentionManager.doMaintenance();
        return transaction;
    }

    private void update(HttpTransaction transaction) {
        int updatedTransactionCount = mGanderDatabase.httpTransactionDao().updateTransaction(transaction);

        if (mShowNotification && updatedTransactionCount > 0) {
            mNotificationHelper.show(transaction);
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Body Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private boolean isPlaintext(Buffer buffer) {
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

    private boolean bodyHasSupportedEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding == null ||
                contentEncoding.equalsIgnoreCase("identity") ||
                contentEncoding.equalsIgnoreCase("gzip");
    }

    private boolean bodyGzipped(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return "gzip".equalsIgnoreCase(contentEncoding);
    }

    private String readFromBuffer(Buffer buffer, Charset charset) {
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

    private BufferedSource getNativeSource(BufferedSource input, boolean isGzipped) {
        if (isGzipped) {
            GzipSource source = new GzipSource(input);
            return Okio.buffer(source);
        } else {
            return input;
        }
    }

    private BufferedSource getNativeSource(Response response) throws IOException {
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

    private List<HttpHeader> toHttpHeaderList(Headers headers) {
        List<HttpHeader> httpHeaders = new ArrayList<>();
        for (int i = 0, count = headers.size(); i < count; i++) {
            httpHeaders.add(new HttpHeader(headers.name(i), headers.value(i)));
        }
        return httpHeaders;
    }
}
