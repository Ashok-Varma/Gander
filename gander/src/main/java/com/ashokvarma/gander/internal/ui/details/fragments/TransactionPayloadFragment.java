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
import com.ashokvarma.gander.internal.support.FormatUtils;
import com.ashokvarma.gander.internal.support.TextUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TransactionPayloadFragment extends Fragment implements TransactionFragment {

    public static final int TYPE_REQUEST = 0;
    public static final int TYPE_RESPONSE = 1;

    private static final String ARG_TYPE = "type";

    private TextView headers;
    private TextView body;

    private int type;
    private HttpTransaction transaction;
    private String mSearchKey;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TransactionPayloadFragment() {
    }

    public static TransactionPayloadFragment newInstance(int type) {
        TransactionPayloadFragment fragment = new TransactionPayloadFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_TYPE, type);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(ARG_TYPE);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gander_fragment_transaction_payload, container, false);
        headers = view.findViewById(R.id.gander_details_headers);
        body = view.findViewById(R.id.gander_details_body);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateUI(true);
    }

    @Override
    public void transactionUpdated(HttpTransaction transaction) {
        this.transaction = transaction;
        populateUI(true);
    }

    @Override
    public void onSearchUpdated(String searchKey) {
        mSearchKey = searchKey;
        populateUI(false);
    }

    private void populateUI(boolean updateHeaders) {
        if (isAdded() && transaction != null) {
            switch (type) {
                case TYPE_REQUEST:
                    if (updateHeaders) {
                        setHeaderText(transaction.getRequestHeadersString(true));
                    }
                    setBody(transaction.requestBodyIsPlainText(), new Callable<CharSequence>() {
                        @Override
                        public CharSequence call() {
                            String body = transaction.getFormattedRequestBody();
                            return FormatUtils.formatTextHighlight(body, mSearchKey);
                        }
                    });
                    break;
                case TYPE_RESPONSE:
                    if (updateHeaders) {
                        setHeaderText(transaction.getResponseHeadersString(true));
                    }
                    setBody(transaction.responseBodyIsPlainText(),
                            new Callable<CharSequence>() {
                                @Override
                                public CharSequence call() {
                                    String body = transaction.getFormattedResponseBody();
                                    return FormatUtils.formatTextHighlight(body, mSearchKey);
                                }
                            });
                    break;
            }
        }
    }

    private void setHeaderText(CharSequence headersString) {
        headers.setVisibility((TextUtil.isNullOrWhiteSpace(headersString) ? View.GONE : View.VISIBLE));
        headers.setText(headersString);
    }

    private void setBody(boolean isPlainText, Callable<CharSequence> bodyString) {
        if (!isPlainText) {
            body.setText(getString(R.string.gander_body_omitted));
        } else {
//            executor.shutdown();
            TextUtil.asyncSetText(body, executor, bodyString);
        }
    }
}