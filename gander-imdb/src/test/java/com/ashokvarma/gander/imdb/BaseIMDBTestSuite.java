package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.HttpHeader;
import com.ashokvarma.gander.internal.data.HttpTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


class BaseIMDBTestSuite {
    static HttpTransaction[] getHttpTransactionsWithIndices(long... ids) {
        HttpTransaction[] httpTransactions = new HttpTransaction[ids.length];
        int index = 0;
        for (long id : ids) {
            httpTransactions[index++] = getHttpTransactionWithIndex(id);
        }
        return httpTransactions;
    }

    static HttpTransaction getHttpTransactionWithZeroIndex() {
        return getHttpTransactionWithIndex(0);
    }

    static HttpTransaction getHttpTransactionWithIndex(long index) {
        return getHttpTransactionWithIndexAndDate(index, new Date(), new Date());
    }

    static HttpTransaction getHttpTransactionWithIndexAndDate(long index, Date requestDate, Date responseDate) {
        return HttpTransaction.newBuilder()
                .setId(index)
                .setRequestDate(requestDate)
                .setResponseDate(responseDate)
                .setTookMs(1000L)

                .setProtocol("Protocol")
                .setMethod("Method")
                .setUrl("Url")
                .setHost("Host")
                .setPath("Path")
                .setScheme("Scheme")

                .setRequestContentLength(1000L)
                .setRequestContentType("RequestContentType")
                .setRequestHeaders(new ArrayList<HttpHeader>())
                .setRequestBody("RequestBody")
                .setRequestBodyIsPlainText(true)

                .setResponseCode(200)
                .setResponseMessage("ResponseMessage")
                .setError("Error")
                .setResponseContentLength(1000L)
                .setResponseContentType("ResponseContentType")
                .setResponseHeaders(new ArrayList<HttpHeader>())
                .setResponseBody("ResponseBody")
                .setResponseBodyIsPlainText(true)

                .build();
    }

    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    Date getDate(String dateAsString) throws ParseException {
        return SIMPLE_DATE_FORMAT.parse(dateAsString);
    }

}
