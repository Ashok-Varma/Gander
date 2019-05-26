package com.ashokvarma.gander.imdb;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.data.TransactionDao;

import java.util.Date;
import java.util.List;

final class IMDBTransactionDao implements TransactionDao {
    private long currentIndex = 1;
    private final TransactionDataStore transactionDataStore;
    private final TransactionArchComponentProvider transactionArchComponentProvider;
    private final TransactionPredicateProvider transactionPredicateProvider;

    IMDBTransactionDao(TransactionDataStore transactionDataStore, TransactionArchComponentProvider transactionArchComponentProvider, TransactionPredicateProvider transactionPredicateProvider) {
        this.transactionDataStore = transactionDataStore;
        this.transactionArchComponentProvider = transactionArchComponentProvider;
        this.transactionPredicateProvider = transactionPredicateProvider;
    }

    @Override
    public long insertTransaction(HttpTransaction httpTransaction) {
        long newTransactionIndex;
        if (httpTransaction.getId() == 0) {
            newTransactionIndex = currentIndex;
        } else {
            newTransactionIndex = httpTransaction.getId();
        }
        return addTransactionWithIndex(httpTransaction, newTransactionIndex);
    }

    @Override
    public int updateTransaction(HttpTransaction httpTransaction) {
        if (httpTransaction.getId() > 0)
            return transactionDataStore.updateTransaction(httpTransaction) ? 1 : 0;

        return 0;
    }

    @Override
    public int deleteTransactions(HttpTransaction... httpTransactions) {
        int updates = 0;
        for (HttpTransaction httpTransaction : httpTransactions) {
            if (httpTransaction.getId() > 0 && transactionDataStore.removeTransactionWithIndex(httpTransaction.getId())) {
                updates++;
            }
        }
        return updates;
    }

    @Override
    public int deleteTransactionsBefore(Date beforeDate) {
        int deletedTransactionCount = 0;
        List<HttpTransaction> httpTransactionList = transactionDataStore.getDataList();
        for (HttpTransaction transaction : httpTransactionList) {
            if (transaction.getRequestDate() != null && transaction.getRequestDate().before(beforeDate)) {
                if (transactionDataStore.removeTransactionWithIndex(transaction.getId())) {
                    deletedTransactionCount++;
                }
            }
        }
        return deletedTransactionCount;
    }

    @Override
    public int clearAll() {
        return transactionDataStore.clearAllTransactions();
    }

    @Override
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactions() {
        return transactionArchComponentProvider.getDataSourceFactory(transactionDataStore, Predicate.ALLOW_ALL);
    }

    @Override
    public LiveData<HttpTransaction> getTransactionsWithId(long id) {
        return transactionArchComponentProvider.getLiveData(transactionDataStore, id);
    }

    @Override
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactionsWith(String key, SearchType searchType) {
        Predicate<HttpTransaction> predicate;
        switch (searchType) {
            case DEFAULT:
            default:
                predicate = transactionPredicateProvider.getDefaultSearchPredicate(key);
                break;
            case INCLUDE_REQUEST:
                predicate = transactionPredicateProvider.getRequestSearchPredicate(key);
                break;
            case INCLUDE_RESPONSE:
                predicate = transactionPredicateProvider.getResponseSearchPredicate(key);
                break;
            case INCLUDE_REQUEST_RESPONSE:
                predicate = transactionPredicateProvider.getRequestResponseSearchPredicate(key);
                break;
        }
        return transactionArchComponentProvider.getDataSourceFactory(transactionDataStore, predicate);
    }

    @VisibleForTesting
    HttpTransaction getTransactionWithId(long id) {
        return transactionDataStore.getTransactionWithId(id);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal Methods
    ///////////////////////////////////////////////////////////////////////////

    private long addTransactionWithIndex(HttpTransaction httpTransaction, long newTransactionIndex) {
        transactionDataStore.addTransaction(httpTransaction.toBuilder().setId(newTransactionIndex).build());
        updateCurrentIndex(newTransactionIndex);
        return newTransactionIndex;
    }

    private void updateCurrentIndex(long newTransactionIndex) {
        if (currentIndex <= newTransactionIndex) {
            currentIndex = newTransactionIndex + 1;
        }
    }

}
