package com.ashokvarma.gander.imdb;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.data.TransactionDao;

import java.util.Date;

final class IMDBTransactionDao implements TransactionDao {
    @Override
    public long insertTransaction(HttpTransaction httpTransaction) {
        return 0;
    }

    @Override
    public int updateTransaction(HttpTransaction httpTransaction) {
        return 0;
    }

    @Override
    public int deleteTransactions(HttpTransaction... httpTransactions) {
        return 0;
    }

    @Override
    public int deleteTransactionsBefore(Date beforeDate) {
        return 0;
    }

    @Override
    public int clearAll() {
        return 0;
    }

    @Override
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactions() {
        return null;
    }

    @Override
    public LiveData<HttpTransaction> getTransactionsWithId(long id) {
        return null;
    }

    @Override
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactionsWith(String key, SearchType searchType) {
        return null;
    }
}
