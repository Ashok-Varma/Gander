package com.ashokvarma.gander.internal.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import java.util.Date;

public interface TransactionDao {
    enum SearchType {
        DEFAULT,
        INCLUDE_REQUEST,
        INCLUDE_RESPONSE,
        INCLUDE_REQUEST_RESPONSE,
    }

    long insertTransaction(HttpTransaction httpTransaction);

    int updateTransaction(HttpTransaction httpTransaction);

    int deleteTransactions(HttpTransaction... httpTransactions);

    int deleteTransactionsBefore(Date beforeDate);

    int clearAll();

    DataSource.Factory<Integer, HttpTransaction> getAllTransactions();

    LiveData<HttpTransaction> getTransactionsWithId(long id);

    DataSource.Factory<Integer, HttpTransaction> getAllTransactionsWith(String key, SearchType searchType);

}
