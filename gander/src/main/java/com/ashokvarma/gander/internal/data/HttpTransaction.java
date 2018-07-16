package com.ashokvarma.gander.internal.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.ashokvarma.gander.internal.support.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 02/06/18
 */
@Entity(tableName = "HttpTransaction")
public class HttpTransaction {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "request_date")
    private Date requestDate;
    @ColumnInfo(name = "response_date")
    private Date responseDate;
    @ColumnInfo(name = "took_ms")
    private Long tookMs;

    @ColumnInfo(name = "protocol")
    private String protocol;
    @ColumnInfo(name = "method")
    private String method;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "host")
    private String host;
    @ColumnInfo(name = "path")
    private String path;
    @ColumnInfo(name = "scheme")
    private String scheme;

    @ColumnInfo(name = "request_content_length")
    private Long requestContentLength;
    @ColumnInfo(name = "request_content_type")
    private String requestContentType;
    @ColumnInfo(name = "request_headers")
    private List<HttpHeader> requestHeaders;
    @ColumnInfo(name = "request_body", typeAffinity = ColumnInfo.TEXT)
    private String requestBody;
    @ColumnInfo(name = "request_body_is_plain_text")
    private boolean requestBodyIsPlainText = true;

    @ColumnInfo(name = "response_code")
    private Integer responseCode;
    @ColumnInfo(name = "response_message")
    private String responseMessage;
    @ColumnInfo(name = "error")
    private String error;

    @ColumnInfo(name = "response_content_length")
    private Long responseContentLength;
    @ColumnInfo(name = "response_content_type")
    private String responseContentType;
    @ColumnInfo(name = "response_headers")
    private List<HttpHeader> responseHeaders;
    @ColumnInfo(name = "response_body", typeAffinity = ColumnInfo.TEXT)
    private String responseBody;
    @ColumnInfo(name = "response_body_is_plain_text")
    private boolean responseBodyIsPlainText = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public Long getTookMs() {
        return tookMs;
    }

    public void setTookMs(Long tookMs) {
        this.tookMs = tookMs;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public Long getRequestContentLength() {
        return requestContentLength;
    }

    public void setRequestContentLength(Long requestContentLength) {
        this.requestContentLength = requestContentLength;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public List<HttpHeader> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(List<HttpHeader> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public boolean requestBodyIsPlainText() {
        return requestBodyIsPlainText;
    }

    public void setRequestBodyIsPlainText(boolean requestBodyIsPlainText) {
        this.requestBodyIsPlainText = requestBodyIsPlainText;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(Long responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public List<HttpHeader> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(List<HttpHeader> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public boolean responseBodyIsPlainText() {
        return responseBodyIsPlainText;
    }

    public void setResponseBodyIsPlainText(boolean responseBodyIsPlainText) {
        this.responseBodyIsPlainText = responseBodyIsPlainText;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extra Setters
    ///////////////////////////////////////////////////////////////////////////
    public void setUrlHostPathSchemeFromUrl(String url) {
        setUrl(url);
        Uri uri = Uri.parse(url);
        setHost(uri.getHost());
        setPath(uri.getPath() + ((uri.getQuery() != null) ? "?" + uri.getQuery() : ""));
        setScheme(uri.getScheme());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extras Getters
    ///////////////////////////////////////////////////////////////////////////
    public enum Status {
        Requested,
        Complete,
        Failed
    }

    public CharSequence getFormattedRequestBody() {
        return formatBody(requestBody, requestContentType);
    }

    public CharSequence getFormattedResponseBody() {
        return formatBody(responseBody, responseContentType);
    }

    public Status getStatus() {
        if (error != null) {
            return Status.Failed;
        } else if (responseCode == null) {
            return Status.Requested;
        } else {
            return Status.Complete;
        }
    }

    public String getNotificationText() {
        switch (getStatus()) {
            case Failed:
                return " ! ! !  " + path;
            case Requested:
                return " . . .  " + path;
            default:
                return String.valueOf(responseCode) + " " + path;
        }
    }

    public boolean isSsl() {
        return scheme.toLowerCase().equals("https");
    }

    private static final SimpleDateFormat TIME_ONLY_FMT = new SimpleDateFormat("HH:mm:ss", Locale.US);

    public String getRequestStartTimeString() {
        return (requestDate != null) ? TIME_ONLY_FMT.format(requestDate) : null;
    }

    public String getRequestDateString() {
        return (requestDate != null) ? requestDate.toString() : null;
    }

    public String getResponseDateString() {
        return (responseDate != null) ? responseDate.toString() : null;
    }

    public String getDurationString() {
        return (tookMs != null) ? +tookMs + " ms" : null;
    }

    public String getRequestSizeString() {
        return formatBytes((requestContentLength != null) ? requestContentLength : 0);
    }

    public String getResponseSizeString() {
        return (responseContentLength != null) ? formatBytes(responseContentLength) : null;
    }

    public String getTotalSizeString() {
        long reqBytes = (requestContentLength != null) ? requestContentLength : 0;
        long resBytes = (responseContentLength != null) ? responseContentLength : 0;
        return formatBytes(reqBytes + resBytes);
    }

    public String getResponseSummaryText() {
        switch (getStatus()) {
            case Failed:
                return error;
            case Requested:
                return null;
            default:
                return String.valueOf(responseCode) + " " + responseMessage;
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

    ///////////////////////////////////////////////////////////////////////////
    // for UI not related to model.
    ///////////////////////////////////////////////////////////////////////////
    @Ignore
    public String searchKey;
}