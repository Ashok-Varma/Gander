package com.ashokvarma.gander.internal.ui.details.fragments;

import com.ashokvarma.gander.internal.data.HttpTransaction;

public interface TransactionFragment {
    void transactionUpdated(HttpTransaction transaction);

    void onSearchUpdated(String searchKey);
}