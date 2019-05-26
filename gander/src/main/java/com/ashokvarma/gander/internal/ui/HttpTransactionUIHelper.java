package com.ashokvarma.gander.internal.ui;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;

import com.ashokvarma.gander.internal.data.HttpHeader;
import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.support.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HttpTransactionUIHelper {

    private final HttpTransaction httpTransaction;
    public String searchKey;

    public HttpTransactionUIHelper(HttpTransaction httpTransaction) {
        this.httpTransaction = httpTransaction;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Deligated Methods
    ///////////////////////////////////////////////////////////////////////////

    public long getId() {
        return httpTransaction.getId();
    }

    private Date getRequestDate() {
        return httpTransaction.getRequestDate();
    }

    private Date getResponseDate() {
        return httpTransaction.getResponseDate();
    }

    private Long getTookMs() {
        return httpTransaction.getTookMs();
    }

    public String getProtocol() {
        return httpTransaction.getProtocol();
    }

    public String getMethod() {
        return httpTransaction.getMethod();
    }

    public String getUrl() {
        return httpTransaction.getUrl();
    }

    public String getHost() {
        return httpTransaction.getHost();
    }

    public String getPath() {
        return httpTransaction.getPath();
    }

    private String getScheme() {
        return httpTransaction.getScheme();
    }

    private Long getRequestContentLength() {
        return httpTransaction.getRequestContentLength();
    }

    private String getRequestContentType() {
        return httpTransaction.getRequestContentType();
    }

    public List<HttpHeader> getRequestHeaders() {
        return httpTransaction.getRequestHeaders();
    }

    public String getRequestBody() {
        return httpTransaction.getRequestBody();
    }

    public boolean requestBodyIsPlainText() {
        return httpTransaction.requestBodyIsPlainText();
    }

    public Integer getResponseCode() {
        return httpTransaction.getResponseCode();
    }

    private String getResponseMessage() {
        return httpTransaction.getResponseMessage();
    }

    private String getError() {
        return httpTransaction.getError();
    }

    private Long getResponseContentLength() {
        return httpTransaction.getResponseContentLength();
    }

    private String getResponseContentType() {
        return httpTransaction.getResponseContentType();
    }

    public List<HttpHeader> getResponseHeaders() {
        return httpTransaction.getResponseHeaders();
    }

    private String getResponseBody() {
        return httpTransaction.getResponseBody();
    }

    public boolean responseBodyIsPlainText() {
        return httpTransaction.responseBodyIsPlainText();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    public enum Status {
        Requested,
        Complete,
        Failed
    }

    public CharSequence getFormattedRequestBody() {
        return formatBody(getRequestBody(), getRequestContentType());
    }

    public CharSequence getFormattedResponseBody() {
        return formatBody(getResponseBody(), getResponseContentType());
    }

    public Status getStatus() {
        if (getError() != null) {
            return Status.Failed;
        } else if (getResponseCode() == null) {
            return Status.Requested;
        } else {
            return Status.Complete;
        }
    }

    public String getNotificationText() {
        switch (getStatus()) {
            case Failed:
                return " ! ! !  " + getPath();
            case Requested:
                return " . . .  " + getPath();
            default:
                return String.valueOf(getResponseCode()) + " " + getPath();
        }
    }

    public boolean isSsl() {
        return getScheme().toLowerCase().equals("https");
    }

    private static final SimpleDateFormat TIME_ONLY_FMT = new SimpleDateFormat("HH:mm:ss", Locale.US);

    public String getRequestStartTimeString() {
        return (getRequestDate() != null) ? TIME_ONLY_FMT.format(getRequestDate()) : null;
    }

    public String getRequestDateString() {
        return (getRequestDate() != null) ? getRequestDate().toString() : null;
    }

    public String getResponseDateString() {
        return (getResponseDate() != null) ? getResponseDate().toString() : null;
    }

    public String getDurationString() {
        return (getTookMs() != null) ? +getTookMs() + " ms" : null;
    }

    public String getRequestSizeString() {
        return formatBytes((getRequestContentLength() != null) ? getRequestContentLength() : 0);
    }

    public String getResponseSizeString() {
        return (getResponseContentLength() != null) ? formatBytes(getResponseContentLength()) : null;
    }

    public String getTotalSizeString() {
        long reqBytes = (getRequestContentLength() != null) ? getRequestContentLength() : 0;
        long resBytes = (getResponseContentLength() != null) ? getResponseContentLength() : 0;
        return formatBytes(reqBytes + resBytes);
    }

    public String getResponseSummaryText() {
        switch (getStatus()) {
            case Failed:
                return getError();
            case Requested:
                return null;
            default:
                return String.valueOf(getResponseCode()) + " " + getResponseMessage();
        }
    }

    private CharSequence formatBody(String body, @Nullable String contentType) {
        if (contentType != null) {
            if (contentType.toLowerCase().contains("json")) {
                return FormatUtils.formatJson(body);
            } else if (contentType.toLowerCase().contains("xml")) {
                return FormatUtils.formatXml(body);
            } else if (contentType.toLowerCase().contains("form-urlencoded")) {
                return FormatUtils.formatFormEncoded(body);
            }
        }
        return body;
    }

    private String formatBytes(long bytes) {
        return FormatUtils.formatByteCount(bytes, true);
    }

    public CharSequence getResponseHeadersString(boolean withMarkup) {
        return FormatUtils.formatHeaders(getResponseHeaders(), withMarkup);
    }

    public CharSequence getRequestHeadersString(boolean withMarkup) {
        return FormatUtils.formatHeaders(getRequestHeaders(), withMarkup);
    }

    public static final Function<HttpTransaction, HttpTransactionUIHelper> HTTP_TRANSACTION_UI_HELPER_FUNCTION =
            new Function<HttpTransaction, HttpTransactionUIHelper>() {
                @Override
                public HttpTransactionUIHelper apply(HttpTransaction httpTransaction) {
                    return new HttpTransactionUIHelper(httpTransaction);
                }
            };
}
