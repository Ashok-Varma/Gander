/*
 * Copyright (C) 2017 Jeff Gilfelt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashokvarma.gander.internal.ui.details.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.data.HttpTransaction;


public class TransactionOverviewFragment extends Fragment implements TransactionFragment {

    private TextView url;
    private TextView method;
    private TextView protocol;
    private TextView status;
    private TextView response;
    private TextView ssl;
    private TextView requestTime;
    private TextView responseTime;
    private TextView duration;
    private TextView requestSize;
    private TextView responseSize;
    private TextView totalSize;

    private HttpTransaction transaction;
    private String mSearchKey;

    public TransactionOverviewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gander_fragment_transaction_overview, container, false);
        url = view.findViewById(R.id.gander_details_url);
        method = view.findViewById(R.id.gander_details_method);
        protocol = view.findViewById(R.id.gander_details_protocol);
        status = view.findViewById(R.id.gander_details_status);
        response = view.findViewById(R.id.gander_details_response);
        ssl = view.findViewById(R.id.gander_details_ssl);
        requestTime = view.findViewById(R.id.gander_details_request_time);
        responseTime = view.findViewById(R.id.gander_details_response_time);
        duration = view.findViewById(R.id.gander_details_duration);
        requestSize = view.findViewById(R.id.gander_details_request_size);
        responseSize = view.findViewById(R.id.gander_details_response_size);
        totalSize = view.findViewById(R.id.gander_details_total_size);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateUI();
    }

    @Override
    public void transactionUpdated(HttpTransaction transaction) {
        this.transaction = transaction;
        populateUI();
    }


    @Override
    public void onSearchUpdated(String searchKey) {
        mSearchKey = searchKey;
        //populateUI();
    }

    private void populateUI() {
        if (isAdded() && transaction != null) {
            url.setText(transaction.getUrl());
            method.setText(transaction.getMethod());
            protocol.setText(transaction.getProtocol());
            status.setText(transaction.getStatus().toString());
            response.setText(transaction.getResponseSummaryText());
            ssl.setText((transaction.isSsl() ? R.string.gander_yes : R.string.gander_no));
            requestTime.setText(transaction.getRequestDateString());
            responseTime.setText(transaction.getResponseDateString());
            duration.setText(transaction.getDurationString());
            requestSize.setText(transaction.getRequestSizeString());
            responseSize.setText(transaction.getResponseSizeString());
            totalSize.setText(transaction.getTotalSizeString());
        }
    }
}
