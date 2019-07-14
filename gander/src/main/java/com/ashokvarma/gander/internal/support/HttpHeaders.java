package com.ashokvarma.gander.internal.support;

import okhttp3.Headers;
import okhttp3.Response;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;

/**
 * Copied from OkHttp internal classes, as suggested in
 * https://github.com/square/okhttp/issues/5246
 * for OkHttp 4+ support
 * **/
public class HttpHeaders {

    private static final int HTTP_CONTINUE = 100;

    private static long contentLength(Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }

    private static long stringToLong(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Returns true if the response must have a (possibly 0-length) body. See RFC 7231. */
    public static boolean hasBody(Response response) {
        // HEAD requests never yield a body regardless of the response headers.
        if (response.request().method().equals("HEAD")) {
            return false;
        }

        int responseCode = response.code();
        if ((responseCode < HTTP_CONTINUE || responseCode >= 200)
                && responseCode != HTTP_NO_CONTENT
                && responseCode != HTTP_NOT_MODIFIED) {
            return true;
        }

        // If the Content-Length or Transfer-Encoding headers disagree with the response code, the
        // response is malformed. For best compatibility, we honor the headers.
        return contentLength(response.headers()) != -1
                || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"));
    }
}
