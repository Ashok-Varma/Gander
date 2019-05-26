package com.ashokvarma.gander.imdb;

import androidx.collection.ArraySet;
import androidx.collection.LongSparseArray;

import com.ashokvarma.gander.internal.data.HttpTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class SimpleTransactionDataStore implements TransactionDataStore {
    private final LongSparseArray<HttpTransaction> data = new LongSparseArray<>(200);

    private Set<DataChangeListener> dataChangeListeners = new ArraySet<>();

    SimpleTransactionDataStore() {
    }

    @Override
    public void addTransaction(HttpTransaction httpTransaction) {
        guardForZeroAndNegativeIndices(httpTransaction.getId());
        addVerifiedTransaction(httpTransaction);
        sendDataChangeEvent(Event.ADDED, httpTransaction);
    }

    @Override
    public boolean updateTransaction(HttpTransaction httpTransaction) {
        guardForZeroAndNegativeIndices(httpTransaction.getId());
        if (contains(httpTransaction)) {
            addVerifiedTransaction(httpTransaction);
            sendDataChangeEvent(Event.UPDATED, httpTransaction);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTransactionWithIndex(long index) {
        guardForZeroAndNegativeIndices(index);
        if (data.containsKey(index)) {
            HttpTransaction deletedTransaction = data.get(index);
            data.remove(index);
            sendDataChangeEvent(Event.DELETED, deletedTransaction);
            return true;
        }
        return false;
    }

    @Override
    public int clearAllTransactions() {
        List<HttpTransaction> toBeDeletedData = getDataList();
        data.clear();
        for (HttpTransaction httpTransaction : toBeDeletedData) {
            sendDataChangeEvent(Event.DELETED, httpTransaction);
        }
        return toBeDeletedData.size();
    }

    @Override
    public List<HttpTransaction> getDataList() {
        List<HttpTransaction> list = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            HttpTransaction httpTransaction = data.valueAt(i);
            list.add(httpTransaction);
        }
        return list;
    }

    @Override
    public HttpTransaction getTransactionWithId(long id) {
        guardForZeroAndNegativeIndices(id);
        if (data.containsKey(id)) {
            return data.get(id);
        }
        throw new IndexDoesNotExistException();
    }

    @Override
    public void addDataChangeListener(DataChangeListener dataChangeListener) {
        dataChangeListeners.add(dataChangeListener);
    }

    @Override
    public void removeDataChangeListener(DataChangeListener dataChangeListener) {
        dataChangeListeners.remove(dataChangeListener);
    }

    private void addVerifiedTransaction(HttpTransaction httpTransaction) {
        data.append(httpTransaction.getId(), httpTransaction);
    }

    private void sendDataChangeEvent(Event event, HttpTransaction httpTransaction) {
        for (DataChangeListener dataChangeListener : dataChangeListeners) {
            dataChangeListener.onDataChange(event, httpTransaction);
        }
    }

    private boolean contains(HttpTransaction httpTransaction) {
        return data.containsKey(httpTransaction.getId());
    }

    private void guardForZeroAndNegativeIndices(long index) {
        if (index < 0) {
            throw new NegativeIndexException();
        } else if (index == 0) {
            throw new ZeroIndexException();
        }
    }

}
