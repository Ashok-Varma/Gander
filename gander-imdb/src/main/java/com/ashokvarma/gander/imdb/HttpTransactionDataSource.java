package com.ashokvarma.gander.imdb;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.ashokvarma.gander.internal.data.HttpTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class HttpTransactionDataSource extends PositionalDataSource<HttpTransaction> implements TransactionDataStore.DataChangeListener {

    private final TransactionDataStore transactionDataStore;
    private final Predicate<HttpTransaction> filter;
    private List<HttpTransaction> filteredTransactions;

    HttpTransactionDataSource(TransactionDataStore transactionDataStore, Predicate<HttpTransaction> filter) {
        this.filter = filter;
        this.transactionDataStore = transactionDataStore;
        updateTransactions();
        this.transactionDataStore.addDataChangeListener(this);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<HttpTransaction> callback) {

        int totalCount = countItems();
        if (totalCount == 0) {
            callback.onResult(Collections.<HttpTransaction>emptyList(), 0, 0);
            return;
        }
        final int firstLoadPosition = computeInitialLoadPosition(params, totalCount);
        final int firstLoadSize = computeInitialLoadSize(params, firstLoadPosition, totalCount);

        List<HttpTransaction> list = loadRange(firstLoadPosition, firstLoadSize);
        if (list.size() == firstLoadSize) {
            callback.onResult(list, firstLoadPosition, totalCount);
        } else {
            // size doesn't match request - List modified between count and load
            invalidate();
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<HttpTransaction> callback) {
        List<HttpTransaction> list = loadRange(params.startPosition, params.loadSize);
        callback.onResult(list);
    }

    @Override
    public void onDataChange(TransactionDataStore.Event event, HttpTransaction httpTransaction) {
        if (isInTheList(httpTransaction) || checkIfEventCanEffectTheList(event, httpTransaction)) {
            updateTransactions();
            invalidate();
        }
    }

    private List<HttpTransaction> loadRange(int startPosition, int loadSize) {
        return filteredTransactions.subList(startPosition, startPosition + loadSize);
    }

    private int countItems() {
        return filteredTransactions.size();
    }

    private boolean isInTheList(HttpTransaction modifiedTransaction) {
        long modifiedTransactionId = modifiedTransaction.getId();
        for (HttpTransaction httpTransaction : filteredTransactions) {
            if (httpTransaction.getId() == modifiedTransactionId) {
                return true;
            }
        }
        return false;
    }

    // if new events is added/existing event updated to match the filter
    private boolean checkIfEventCanEffectTheList(TransactionDataStore.Event event, HttpTransaction httpTransaction) {
        return (event == TransactionDataStore.Event.ADDED || event == TransactionDataStore.Event.UPDATED) && filter.apply(httpTransaction);
    }

    private void updateTransactions() {
        List<HttpTransaction> newFilteredTransactions = new ArrayList<>();

        for (HttpTransaction httpTransaction : transactionDataStore.getDataList()) {
            if (filter.apply(httpTransaction))
                newFilteredTransactions.add(httpTransaction);
        }

        Collections.sort(newFilteredTransactions, new Comparator<HttpTransaction>() {
            @Override
            public int compare(HttpTransaction httpTransaction1, HttpTransaction httpTransaction2) {
                long httpTransactionId2 = httpTransaction2.getId();
                long httpTransactionId1 = httpTransaction1.getId();
                return (httpTransactionId2 < httpTransactionId1) ? -1 : ((httpTransactionId2 == httpTransactionId1) ? 0 : 1);
            }
        });

        filteredTransactions = newFilteredTransactions;
    }
}
