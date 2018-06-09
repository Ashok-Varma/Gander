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

    private TextView mUrlView;
    private TextView mMethodView;
    private TextView mProtocolView;
    private TextView mStatusView;
    private TextView mResponseView;
    private TextView mSslView;
    private TextView mRequestTimeView;
    private TextView mResponseTimeView;
    private TextView mDurationView;
    private TextView mRequestSizeView;
    private TextView mResponseSizeView;
    private TextView mTotalSizeView;

    private HttpTransaction mTransaction;

    public TransactionOverviewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gander_frag_transaction_overview, container, false);
        mUrlView = view.findViewById(R.id.gander_details_url);
        mMethodView = view.findViewById(R.id.gander_details_method);
        mProtocolView = view.findViewById(R.id.gander_details_protocol);
        mStatusView = view.findViewById(R.id.gander_details_status);
        mResponseView = view.findViewById(R.id.gander_details_response);
        mSslView = view.findViewById(R.id.gander_details_ssl);
        mRequestTimeView = view.findViewById(R.id.gander_details_request_time);
        mResponseTimeView = view.findViewById(R.id.gander_details_response_time);
        mDurationView = view.findViewById(R.id.gander_details_duration);
        mRequestSizeView = view.findViewById(R.id.gander_details_request_size);
        mResponseSizeView = view.findViewById(R.id.gander_details_response_size);
        mTotalSizeView = view.findViewById(R.id.gander_details_total_size);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateUI();
    }

    @Override
    public void transactionUpdated(HttpTransaction transaction) {
        this.mTransaction = transaction;
        populateUI();
    }

    private void populateUI() {
        if (isAdded() && mTransaction != null) {
            mUrlView.setText(mTransaction.getUrl());
            mMethodView.setText(mTransaction.getMethod());
            mProtocolView.setText(mTransaction.getProtocol());
            mStatusView.setText(mTransaction.getStatus().toString());
            mResponseView.setText(mTransaction.getResponseSummaryText());
            mSslView.setText((mTransaction.isSsl() ? R.string.gander_yes : R.string.gander_no));
            mRequestTimeView.setText(mTransaction.getRequestDateString());
            mResponseTimeView.setText(mTransaction.getResponseDateString());
            mDurationView.setText(mTransaction.getDurationString());
            mRequestSizeView.setText(mTransaction.getRequestSizeString());
            mResponseSizeView.setText(mTransaction.getResponseSizeString());
            mTotalSizeView.setText(mTransaction.getTotalSizeString());
        }
    }
}
