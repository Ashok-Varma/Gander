package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.HttpTransaction;

import java.util.List;

interface TransactionDataStore {
    public enum Event {
        ADDED,
        UPDATED,
        DELETED
    }

    public interface DataChangeListener {
        void onDataChange(Event event, HttpTransaction httpTransaction);
    }

    void addTransaction(HttpTransaction httpTransaction);

    boolean updateTransaction(HttpTransaction httpTransaction);

    boolean removeTransactionWithIndex(long index);

    int clearAllTransactions();

    List<HttpTransaction> getDataList();

    HttpTransaction getTransactionWithId(long id);

    void addDataChangeListener(DataChangeListener dataChangeListener);

    void removeDataChangeListener(DataChangeListener dataChangeListener);


    class IndexDoesNotExistException extends RuntimeException {
    }

    class NegativeIndexException extends RuntimeException {
    }

    class ZeroIndexException extends RuntimeException {
    }
}
