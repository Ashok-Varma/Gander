package com.ashokvarma.gander.persistence;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.DataSource;

import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.data.TransactionDao;

import java.util.Date;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */

class PersistentTransactionDao implements TransactionDao {
    private RoomTransactionDao roomTransactionDao;

    PersistentTransactionDao(RoomTransactionDao roomTransactionDao) {
        this.roomTransactionDao = roomTransactionDao;
    }

    @Override
    public long insertTransaction(HttpTransaction httpTransaction) {
        return roomTransactionDao.insertTransaction(DATA_TO_PERSISTENT_TRANSACTION_FUNCTION.apply(httpTransaction));
    }

    @Override
    public int updateTransaction(HttpTransaction httpTransaction) {
        return roomTransactionDao.updateTransaction(DATA_TO_PERSISTENT_TRANSACTION_FUNCTION.apply(httpTransaction));
    }

    @Override
    public int deleteTransactions(HttpTransaction... httpTransactions) {
        PersistentHttpTransaction[] persistentHttpTransactions = new PersistentHttpTransaction[httpTransactions.length];
        int index = 0;

        for (HttpTransaction transaction : httpTransactions) {
            persistentHttpTransactions[index++] = DATA_TO_PERSISTENT_TRANSACTION_FUNCTION.apply(transaction);
        }

        return roomTransactionDao.deleteTransactions(persistentHttpTransactions);
    }

    @Override
    public int deleteTransactionsBefore(Date beforeDate) {
        return roomTransactionDao.deleteTransactionsBefore(beforeDate);
    }

    @Override
    public int clearAll() {
        return roomTransactionDao.clearAll();
    }

    @Override
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactions() {
        return roomTransactionDao.getAllTransactions().map(PERSISTENT_TO_DATA_TRANSACTION_FUNCTION);
    }

    @Override
    public LiveData<HttpTransaction> getTransactionsWithId(long id) {
        LiveData<PersistentHttpTransaction> persistentHttpTransactionLiveData = roomTransactionDao.getTransactionsWithId(id);
        return Transformations.map(persistentHttpTransactionLiveData, PERSISTENT_TO_DATA_TRANSACTION_FUNCTION);
    }

    @Override
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactionsWith(String key, SearchType searchType) {
        String endWildCard = key + "%";
        String doubleSideWildCard = "%" + key + "%";

        DataSource.Factory<Integer, PersistentHttpTransaction> factory;
        switch (searchType) {
            case DEFAULT:
                factory = roomTransactionDao.getAllTransactions(endWildCard, doubleSideWildCard);
                break;
            case INCLUDE_REQUEST:
                factory = roomTransactionDao.getAllTransactionsIncludeRequest(endWildCard, doubleSideWildCard);
                break;
            case INCLUDE_RESPONSE:
                factory = roomTransactionDao.getAllTransactionsIncludeResponse(endWildCard, doubleSideWildCard);
                break;
            case INCLUDE_REQUEST_RESPONSE:
                factory = roomTransactionDao.getAllTransactionsIncludeRequestResponse(endWildCard, doubleSideWildCard);
                break;
            default:
                factory = roomTransactionDao.getAllTransactions(endWildCard, doubleSideWildCard);
                break;
        }

        return factory.map(PERSISTENT_TO_DATA_TRANSACTION_FUNCTION);
    }

    private static final Function<PersistentHttpTransaction, HttpTransaction> PERSISTENT_TO_DATA_TRANSACTION_FUNCTION
            = new Function<PersistentHttpTransaction, HttpTransaction>() {
        @Override
        public HttpTransaction apply(PersistentHttpTransaction input) {
            HttpTransaction httpTransaction = new HttpTransaction();

            httpTransaction.setId(input.getId());
            httpTransaction.setRequestDate(input.getRequestDate());
            httpTransaction.setResponseDate(input.getResponseDate());
            httpTransaction.setTookMs(input.getTookMs());

            httpTransaction.setProtocol(input.getProtocol());
            httpTransaction.setMethod(input.getMethod());
            httpTransaction.setUrl(input.getUrl());
            httpTransaction.setHost(input.getHost());
            httpTransaction.setPath(input.getPath());
            httpTransaction.setScheme(input.getScheme());

            httpTransaction.setRequestContentLength(input.getRequestContentLength());
            httpTransaction.setRequestContentType(input.getRequestContentType());
            httpTransaction.setRequestHeaders(input.getRequestHeaders());
            httpTransaction.setRequestBody(input.getRequestBody());
            httpTransaction.setRequestBodyIsPlainText(input.requestBodyIsPlainText());

            httpTransaction.setResponseCode(input.getResponseCode());
            httpTransaction.setResponseMessage(input.getResponseMessage());
            httpTransaction.setError(input.getError());
            httpTransaction.setResponseContentLength(input.getResponseContentLength());
            httpTransaction.setResponseContentType(input.getResponseContentType());
            httpTransaction.setResponseHeaders(input.getResponseHeaders());
            httpTransaction.setResponseBody(input.getResponseBody());
            httpTransaction.setResponseBodyIsPlainText(input.responseBodyIsPlainText());


            return httpTransaction;
        }
    };

    private static final Function<HttpTransaction, PersistentHttpTransaction> DATA_TO_PERSISTENT_TRANSACTION_FUNCTION
            = new Function<HttpTransaction, PersistentHttpTransaction>() {
        @Override
        public PersistentHttpTransaction apply(HttpTransaction input) {
            PersistentHttpTransaction persistentHttpTransaction = new PersistentHttpTransaction();

            persistentHttpTransaction.setId(input.getId());
            persistentHttpTransaction.setRequestDate(input.getRequestDate());
            persistentHttpTransaction.setResponseDate(input.getResponseDate());
            persistentHttpTransaction.setTookMs(input.getTookMs());

            persistentHttpTransaction.setProtocol(input.getProtocol());
            persistentHttpTransaction.setMethod(input.getMethod());
            persistentHttpTransaction.setUrl(input.getUrl());
            persistentHttpTransaction.setHost(input.getHost());
            persistentHttpTransaction.setPath(input.getPath());
            persistentHttpTransaction.setScheme(input.getScheme());

            persistentHttpTransaction.setRequestContentLength(input.getRequestContentLength());
            persistentHttpTransaction.setRequestContentType(input.getRequestContentType());
            persistentHttpTransaction.setRequestHeaders(input.getRequestHeaders());
            persistentHttpTransaction.setRequestBody(input.getRequestBody());
            persistentHttpTransaction.setRequestBodyIsPlainText(input.requestBodyIsPlainText());

            persistentHttpTransaction.setResponseCode(input.getResponseCode());
            persistentHttpTransaction.setResponseMessage(input.getResponseMessage());
            persistentHttpTransaction.setError(input.getError());
            persistentHttpTransaction.setResponseContentLength(input.getResponseContentLength());
            persistentHttpTransaction.setResponseContentType(input.getResponseContentType());
            persistentHttpTransaction.setResponseHeaders(input.getResponseHeaders());
            persistentHttpTransaction.setResponseBody(input.getResponseBody());
            persistentHttpTransaction.setResponseBodyIsPlainText(input.responseBodyIsPlainText());

            return persistentHttpTransaction;
        }
    };


}
