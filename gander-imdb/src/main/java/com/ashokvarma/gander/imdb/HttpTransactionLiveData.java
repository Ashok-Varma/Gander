package com.ashokvarma.gander.imdb;

import androidx.lifecycle.MutableLiveData;

import com.ashokvarma.gander.internal.data.HttpTransaction;

class HttpTransactionLiveData extends MutableLiveData<HttpTransaction> implements TransactionDataStore.DataChangeListener {

    private final TransactionDataStore transactionDataStore;
    private final long transactionId;

    HttpTransactionLiveData(TransactionDataStore transactionDataStore, long transactionId) {
        this.transactionDataStore = transactionDataStore;
        this.transactionId = transactionId;
        this.transactionDataStore.addDataChangeListener(this);
        updateData();
    }

    private void updateData() {
        postValue(transactionDataStore.getTransactionWithId(transactionId));
    }

    @Override
    protected void onInactive() {
        this.transactionDataStore.removeDataChangeListener(this);
        super.onInactive();
    }

    @Override
    protected void onActive() {
        super.onActive();
        updateData();
        this.transactionDataStore.addDataChangeListener(this);
    }

    @Override
    public void onDataChange(TransactionDataStore.Event event, HttpTransaction httpTransaction) {
        if (httpTransaction.getId() == transactionId) {
            updateData();
        }
    }
}
