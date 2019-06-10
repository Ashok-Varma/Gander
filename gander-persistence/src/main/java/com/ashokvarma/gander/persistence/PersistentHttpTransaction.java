package com.ashokvarma.gander.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.ashokvarma.gander.internal.data.HttpHeader;

import java.util.Date;
import java.util.List;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 02/06/18
 */
@Entity(tableName = "HttpTransaction")
public class PersistentHttpTransaction {

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
}
