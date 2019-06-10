package com.ashokvarma.gander.internal.data;

import android.net.Uri;

import java.util.Date;
import java.util.List;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 02/06/18
 */
public class HttpTransaction {

    private final long id;
    private final Date requestDate;
    private final Date responseDate;
    private final Long tookMs;

    private final String protocol;
    private final String method;
    private final String url;
    private final String host;
    private final String path;
    private final String scheme;

    private final Long requestContentLength;
    private final String requestContentType;
    private final List<HttpHeader> requestHeaders;
    private final String requestBody;
    private final boolean requestBodyIsPlainText;

    private final Integer responseCode;
    private final String responseMessage;
    private final String error;
    private final Long responseContentLength;
    private final String responseContentType;
    private final List<HttpHeader> responseHeaders;
    private final String responseBody;
    private final boolean responseBodyIsPlainText;

    private HttpTransaction(Builder builder) {
        this.id = builder.id;
        this.requestDate = builder.requestDate;
        this.responseDate = builder.responseDate;
        this.tookMs = builder.tookMs;
        this.protocol = builder.protocol;
        this.method = builder.method;
        this.url = builder.url;
        this.host = builder.host;
        this.path = builder.path;
        this.scheme = builder.scheme;
        this.requestContentLength = builder.requestContentLength;
        this.requestContentType = builder.requestContentType;
        this.requestHeaders = builder.requestHeaders;
        this.requestBody = builder.requestBody;
        this.requestBodyIsPlainText = builder.requestBodyIsPlainText;
        this.responseCode = builder.responseCode;
        this.responseMessage = builder.responseMessage;
        this.error = builder.error;
        this.responseContentLength = builder.responseContentLength;
        this.responseContentType = builder.responseContentType;
        this.responseHeaders = builder.responseHeaders;
        this.responseBody = builder.responseBody;
        this.responseBodyIsPlainText = builder.responseBodyIsPlainText;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.id = this.getId();
        builder.requestDate = this.getRequestDate();
        builder.responseDate = this.getResponseDate();
        builder.tookMs = this.getTookMs();
        builder.protocol = this.getProtocol();
        builder.method = this.getMethod();
        builder.url = this.getUrl();
        builder.host = this.getHost();
        builder.path = this.getPath();
        builder.scheme = this.getScheme();
        builder.requestContentLength = this.getRequestContentLength();
        builder.requestContentType = this.getRequestContentType();
        builder.requestHeaders = this.getRequestHeaders();
        builder.requestBody = this.getRequestBody();
        builder.requestBodyIsPlainText = this.requestBodyIsPlainText();
        builder.responseCode = this.getResponseCode();
        builder.responseMessage = this.getResponseMessage();
        builder.error = this.getError();
        builder.responseContentLength = this.getResponseContentLength();
        builder.responseContentType = this.getResponseContentType();
        builder.responseHeaders = this.getResponseHeaders();
        builder.responseBody = this.getResponseBody();
        builder.responseBodyIsPlainText = this.responseBodyIsPlainText();
        return builder;
    }

    public long getId() {
        return id;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public Long getTookMs() {
        return tookMs;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getScheme() {
        return scheme;
    }

    public Long getRequestContentLength() {
        return requestContentLength;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public List<HttpHeader> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public boolean requestBodyIsPlainText() {
        return requestBodyIsPlainText;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getError() {
        return error;
    }

    public Long getResponseContentLength() {
        return responseContentLength;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public List<HttpHeader> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public boolean responseBodyIsPlainText() {
        return responseBodyIsPlainText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpTransaction that = (HttpTransaction) o;

        if (id != that.id) return false;
        if (requestBodyIsPlainText != that.requestBodyIsPlainText) return false;
        if (responseBodyIsPlainText != that.responseBodyIsPlainText) return false;
        if (requestDate != null ? !requestDate.equals(that.requestDate) : that.requestDate != null)
            return false;
        if (responseDate != null ? !responseDate.equals(that.responseDate) : that.responseDate != null)
            return false;
        if (tookMs != null ? !tookMs.equals(that.tookMs) : that.tookMs != null) return false;
        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null)
            return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        if (requestContentLength != null ? !requestContentLength.equals(that.requestContentLength) : that.requestContentLength != null)
            return false;
        if (requestContentType != null ? !requestContentType.equals(that.requestContentType) : that.requestContentType != null)
            return false;
        if (requestHeaders != null ? !requestHeaders.equals(that.requestHeaders) : that.requestHeaders != null)
            return false;
        if (requestBody != null ? !requestBody.equals(that.requestBody) : that.requestBody != null)
            return false;
        if (responseCode != null ? !responseCode.equals(that.responseCode) : that.responseCode != null)
            return false;
        if (responseMessage != null ? !responseMessage.equals(that.responseMessage) : that.responseMessage != null)
            return false;
        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        if (responseContentLength != null ? !responseContentLength.equals(that.responseContentLength) : that.responseContentLength != null)
            return false;
        if (responseContentType != null ? !responseContentType.equals(that.responseContentType) : that.responseContentType != null)
            return false;
        if (responseHeaders != null ? !responseHeaders.equals(that.responseHeaders) : that.responseHeaders != null)
            return false;
        return responseBody != null ? responseBody.equals(that.responseBody) : that.responseBody == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (responseDate != null ? responseDate.hashCode() : 0);
        result = 31 * result + (tookMs != null ? tookMs.hashCode() : 0);
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (requestContentLength != null ? requestContentLength.hashCode() : 0);
        result = 31 * result + (requestContentType != null ? requestContentType.hashCode() : 0);
        result = 31 * result + (requestHeaders != null ? requestHeaders.hashCode() : 0);
        result = 31 * result + (requestBody != null ? requestBody.hashCode() : 0);
        result = 31 * result + (requestBodyIsPlainText ? 1 : 0);
        result = 31 * result + (responseCode != null ? responseCode.hashCode() : 0);
        result = 31 * result + (responseMessage != null ? responseMessage.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (responseContentLength != null ? responseContentLength.hashCode() : 0);
        result = 31 * result + (responseContentType != null ? responseContentType.hashCode() : 0);
        result = 31 * result + (responseHeaders != null ? responseHeaders.hashCode() : 0);
        result = 31 * result + (responseBody != null ? responseBody.hashCode() : 0);
        result = 31 * result + (responseBodyIsPlainText ? 1 : 0);
        return result;
    }

    public static final class Builder {
        private long id;
        private Date requestDate;
        private Date responseDate;
        private Long tookMs;
        private String protocol;
        private String method;
        private String url;
        private String host;
        private String path;
        private String scheme;
        private Long requestContentLength;
        private String requestContentType;
        private List<HttpHeader> requestHeaders;
        private String requestBody;
        private boolean requestBodyIsPlainText = true;
        private Integer responseCode;
        private String responseMessage;
        private String error;
        private Long responseContentLength;
        private String responseContentType;
        private List<HttpHeader> responseHeaders;
        private String responseBody;
        private boolean responseBodyIsPlainText = true;

        private Builder() {
        }

        public Builder setId(long val) {
            id = val;
            return this;
        }

        public Builder setRequestDate(Date val) {
            requestDate = val;
            return this;
        }

        public Builder setResponseDate(Date val) {
            responseDate = val;
            return this;
        }

        public Builder setTookMs(Long val) {
            tookMs = val;
            return this;
        }

        public Builder setProtocol(String val) {
            protocol = val;
            return this;
        }

        public Builder setMethod(String val) {
            method = val;
            return this;
        }

        public Builder setUrl(String val) {
            url = val;
            return this;
        }

        public Builder setHost(String val) {
            host = val;
            return this;
        }

        public Builder setPath(String val) {
            path = val;
            return this;
        }

        public Builder setScheme(String val) {
            scheme = val;
            return this;
        }

        public Builder setRequestContentLength(Long val) {
            requestContentLength = val;
            return this;
        }

        public Builder setRequestContentType(String val) {
            requestContentType = val;
            return this;
        }

        public Builder setRequestHeaders(List<HttpHeader> val) {
            requestHeaders = val;
            return this;
        }

        public Builder setRequestBody(String val) {
            requestBody = val;
            return this;
        }

        public Builder setRequestBodyIsPlainText(boolean val) {
            requestBodyIsPlainText = val;
            return this;
        }

        public Builder setResponseCode(Integer val) {
            responseCode = val;
            return this;
        }

        public Builder setResponseMessage(String val) {
            responseMessage = val;
            return this;
        }

        public Builder setError(String val) {
            error = val;
            return this;
        }

        public Builder setResponseContentLength(Long val) {
            responseContentLength = val;
            return this;
        }

        public Builder setResponseContentType(String val) {
            responseContentType = val;
            return this;
        }

        public Builder setResponseHeaders(List<HttpHeader> val) {
            responseHeaders = val;
            return this;
        }

        public Builder setResponseBody(String val) {
            responseBody = val;
            return this;
        }

        public Builder setResponseBodyIsPlainText(boolean val) {
            responseBodyIsPlainText = val;
            return this;
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

        public HttpTransaction build() {
            return new HttpTransaction(this);
        }
    }
}
