package com.ashokvarma.gander.imdb;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import com.ashokvarma.gander.internal.data.HttpTransaction;

class TransactionArchComponentProvider {
    TransactionArchComponentProvider() {
    }

    DataSource.Factory<Integer, HttpTransaction> getDataSourceFactory(final TransactionDataStore transactionDataStore, final Predicate<HttpTransaction> filter) {
        return new DataSource.Factory<Integer, HttpTransaction>() {
            @NonNull
            @Override
            public DataSource<Integer, HttpTransaction> create() {
                return new HttpTransactionDataSource(transactionDataStore, filter);
            }
        };
    }

    LiveData<HttpTransaction> getLiveData(TransactionDataStore transactionDataStore, long id) {
        return new HttpTransactionLiveData(transactionDataStore, id);
    }
}
