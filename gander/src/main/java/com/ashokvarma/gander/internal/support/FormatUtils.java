package com.ashokvarma.gander.internal.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.data.HttpHeader;
import com.ashokvarma.gander.internal.data.HttpTransaction;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class FormatUtils {

    public static CharSequence formatTextHighlight(String text, String searchKey) {
        if (TextUtil.isNullOrWhiteSpace(text) || TextUtil.isNullOrWhiteSpace(searchKey)) {
            return text;
        } else {
            List<Integer> startIndexes = indexOf(text, searchKey);
            SpannableString spannableString = new SpannableString(text);
            applyHighlightSpan(spannableString, startIndexes, searchKey.length());
            return spannableString;
        }
    }

    @NonNull
    public static List<Integer> indexOf(CharSequence charSequence, String criteria) {
        String text = charSequence.toString().toLowerCase();
        criteria = criteria.toLowerCase();

        List<Integer> startPositions = new ArrayList<>();
        int index = text.indexOf(criteria);
        while (index >= 0) {
            startPositions.add(index);
            index = text.indexOf(criteria, index + 1);
        }
        return startPositions;
    }

    public static void applyHighlightSpan(Spannable spannableString, List<Integer> indexes, int length) {
        for (Integer position : indexes) {
            spannableString.setSpan(new HighlightSpan(GanderColorUtil.HIGHLIGHT_BACKGROUND_COLOR, GanderColorUtil.HIGHLIGHT_TEXT_COLOR, GanderColorUtil.HIGHLIGHT_UNDERLINE),
                    position, position + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static CharSequence formatHeaders(List<HttpHeader> httpHeaders, boolean withMarkup) {
        Truss truss = new Truss();
        if (httpHeaders != null) {
            for (HttpHeader header : httpHeaders) {
                if (withMarkup) {
                    truss.pushSpan(new StyleSpan(android.graphics.Typeface.BOLD));
                }
                truss.append(header.getName()).append(": ");
                if (withMarkup) {
                    truss.popSpan();
                }
                truss.append(header.getValue()).append("\n");

            }
        }
        return truss.build();
    }

    public static String formatByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static CharSequence formatJson(String json) {
        try {
            json = json.trim();
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                return jsonArray.toString(4);
            } else {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.toString(4);
            }
        } catch (Exception e) {
            Logger.e("non json content", e);
            return json;
        }
    }

    public static CharSequence formatXml(String xml) {
        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch (Exception e) {
            Logger.e("non xml content", e);
            return xml;
        }
    }

    public static CharSequence formatFormEncoded(String formEncoded) {
        try {
            Truss truss = new Truss();
            if (formEncoded != null) {
                formEncoded = URLDecoder.decode(formEncoded, "UTF-8");
                String[] pairs = formEncoded.split("&");
                for (String pair : pairs) {
                    if (pair.contains("=")) {
                        int idx = pair.indexOf("=");

                        truss.pushSpan(new StyleSpan(android.graphics.Typeface.BOLD));
                        truss.append(pair.substring(0, idx)).append("= ");
                        truss.popSpan();
                        truss.append(pair.substring(idx + 1)).append("\n");
                    }
                }
            }
            return truss.build();
        } catch (Exception e) {
            Logger.e("non form url content content", e);
            return formEncoded;
        }
    }

    public static CharSequence getShareText(Context context, HttpTransaction transaction) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(context.getString(R.string.gander_url)).append(": ").append(v(transaction.getUrl())).append("\n");
        text.append(context.getString(R.string.gander_method)).append(": ").append(v(transaction.getMethod())).append("\n");
        text.append(context.getString(R.string.gander_protocol)).append(": ").append(v(transaction.getProtocol())).append("\n");
        text.append(context.getString(R.string.gander_status)).append(": ").append(v(transaction.getStatus().toString())).append("\n");
        text.append(context.getString(R.string.gander_response)).append(": ").append(v(transaction.getResponseSummaryText())).append("\n");
        text.append(context.getString(R.string.gander_ssl)).append(": ").append(v(context.getString(transaction.isSsl() ? R.string.gander_yes : R.string.gander_no))).append("\n");
        text.append("\n");
        text.append(context.getString(R.string.gander_request_time)).append(": ").append(v(transaction.getRequestDateString())).append("\n");
        text.append(context.getString(R.string.gander_response_time)).append(": ").append(v(transaction.getResponseDateString())).append("\n");
        text.append(context.getString(R.string.gander_duration)).append(": ").append(v(transaction.getDurationString())).append("\n");
        text.append("\n");
        text.append(context.getString(R.string.gander_request_size)).append(": ").append(v(transaction.getRequestSizeString())).append("\n");
        text.append(context.getString(R.string.gander_response_size)).append(": ").append(v(transaction.getResponseSizeString())).append("\n");
        text.append(context.getString(R.string.gander_total_size)).append(": ").append(v(transaction.getTotalSizeString())).append("\n");
        text.append("\n");
        text.append("---------- ").append(context.getString(R.string.gander_request)).append(" ----------\n\n");
        CharSequence headers = formatHeaders(transaction.getRequestHeaders(), false);
        if (!TextUtil.isNullOrWhiteSpace(headers)) {
            text.append(headers).append("\n");
        }
        text.append((transaction.requestBodyIsPlainText()) ? v(transaction.getFormattedRequestBody()) :
                context.getString(R.string.gander_body_omitted));
        text.append("\n\n");
        text.append("---------- ").append(context.getString(R.string.gander_response)).append(" ----------\n\n");
        headers = formatHeaders(transaction.getResponseHeaders(), false);
        if (!TextUtil.isNullOrWhiteSpace(headers)) {
            text.append(headers).append("\n");
        }
        text.append((transaction.responseBodyIsPlainText()) ? v(transaction.getFormattedResponseBody()) :
                context.getString(R.string.gander_body_omitted));
        return text;
    }

    public static String getShareCurlCommand(HttpTransaction transaction) {
        boolean compressed = false;
        StringBuilder curlCmd = new StringBuilder("curl");
        curlCmd.append(" -X ").append(transaction.getMethod());
        List<HttpHeader> headers = transaction.getRequestHeaders();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.get(i).getName();
            String value = headers.get(i).getValue();
            if ("Accept-Encoding".equalsIgnoreCase(name) && "gzip".equalsIgnoreCase(value)) {
                compressed = true;
            }
            curlCmd.append(" -H ").append("\"").append(name).append(": ").append(value).append("\"");
        }
        String requestBody = transaction.getRequestBody();
        if (requestBody != null && requestBody.length() > 0) {
            // try to keep to a single line and use a subshell to preserve any line breaks
            curlCmd.append(" --data $'").append(requestBody.replace("\n", "\\n")).append("'");
        }
        curlCmd.append(((compressed) ? " --compressed " : " ")).append(transaction.getUrl());
        return curlCmd.toString();
    }

    private static CharSequence v(CharSequence charSequence) {
        return (charSequence != null) ? charSequence : "";
    }
}
