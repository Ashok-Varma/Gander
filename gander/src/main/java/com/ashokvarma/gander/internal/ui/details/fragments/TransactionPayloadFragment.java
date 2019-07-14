package com.ashokvarma.gander.internal.ui.details.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.support.FormatUtils;
import com.ashokvarma.gander.internal.support.GanderColorUtil;
import com.ashokvarma.gander.internal.support.HighlightSpan;
import com.ashokvarma.gander.internal.support.TextUtil;
import com.ashokvarma.gander.internal.support.event.Callback;
import com.ashokvarma.gander.internal.support.event.Debouncer;
import com.ashokvarma.gander.internal.ui.HttpTransactionUIHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TransactionPayloadFragment extends Fragment implements TransactionFragment, View.OnClickListener {

    public static final int TYPE_REQUEST = 0;
    public static final int TYPE_RESPONSE = 1;

    private static final String ARG_TYPE = "type";

    private TextView mHeadersView;
    private AppCompatTextView mBodyView;
    private NestedScrollView mScrollParentView;
    private FloatingActionButton mSearchFab;
    private View mSearchBar;
    private EditText mSearchView;
    private TextView mSearchCountView;

    private int mType;
    private GanderColorUtil mColorUtil;
    private HttpTransactionUIHelper mTransactionUIHelper;

    private String mSearchKey;
    private int mCurrentSearchIndex = 0;
    private List<Integer> mHeaderSearchIndices = new ArrayList<>(0);
    private List<Integer> mBodySearchIndices = new ArrayList<>(0);
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Debouncer<String> mSearchDebouncer = new Debouncer<>(400, new Callback<String>() {
        @Override
        public void onEmit(String searchKey) {
            mSearchKey = searchKey;
            mHeaderSearchIndices = highlightSearchKeyword(mHeadersView, mSearchKey);
            mBodySearchIndices = highlightSearchKeyword(mBodyView, mSearchKey);
            updateSearchCount(1, searchKey);
        }
    });

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
        assert getArguments() != null;
        mType = getArguments().getInt(ARG_TYPE);
        mColorUtil = GanderColorUtil.getInstance(getContext());
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gander_frag_transaction_payload, container, false);
        mHeadersView = view.findViewById(R.id.gander_details_headers);
        mBodyView = view.findViewById(R.id.gander_details_body);
        mScrollParentView = view.findViewById(R.id.gander_details_scroll_parent);
        mSearchFab = view.findViewById(R.id.gander_details_search_fab);
        mSearchBar = view.findViewById(R.id.gander_details_search_bar);
        View searchBarPrev = view.findViewById(R.id.gander_details_search_prev);
        View searchBarNext = view.findViewById(R.id.gander_details_search_next);
        View searchBarClose = view.findViewById(R.id.gander_details_search_close);
        mSearchView = view.findViewById(R.id.gander_details_search);
        mSearchCountView = view.findViewById(R.id.gander_details_search_count);

        mSearchFab.setOnClickListener(this);
        searchBarPrev.setOnClickListener(this);
        searchBarNext.setOnClickListener(this);
        searchBarClose.setOnClickListener(this);

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchDebouncer.consume(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateUI();
    }

    @Override
    public void transactionUpdated(HttpTransactionUIHelper transactionUIHelper) {
        this.mTransactionUIHelper = transactionUIHelper;
        populateUI();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.gander_details_search_fab) {
            showSearch();
        } else if (id == R.id.gander_details_search_close) {
            hideOrClearSearch();
        } else if (id == R.id.gander_details_search_prev) {
            updateSearchCount(mCurrentSearchIndex - 1, mSearchKey);
        } else if (id == R.id.gander_details_search_next) {
            updateSearchCount(mCurrentSearchIndex + 1, mSearchKey);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            hideKeyboard();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void populateUI() {
        if (isAdded() && mTransactionUIHelper != null) {
            int color = mColorUtil.getTransactionColor(mTransactionUIHelper);
            mSearchFab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{color}));
            mSearchBar.setBackgroundColor(color);
            if (mType == TYPE_REQUEST) {
                mSearchView.setHint(R.string.gander_search_request_hint);
                populateHeaderText(mTransactionUIHelper.getRequestHeadersString(true));
                populateBody(mTransactionUIHelper.requestBodyIsPlainText());
            } else if (mType == TYPE_RESPONSE) {
                mSearchView.setHint(R.string.gander_search_response_hint);
                populateHeaderText(mTransactionUIHelper.getResponseHeadersString(true));
                populateBody(mTransactionUIHelper.responseBodyIsPlainText());
            }
//            updateSearchCount(1);
        }
    }

    private void populateHeaderText(CharSequence headersString) {
        if (TextUtil.isNullOrWhiteSpace(headersString)) {
            mHeadersView.setVisibility(View.GONE);
        } else {
            mHeadersView.setVisibility(View.VISIBLE);
            mHeadersView.setText(headersString, TextView.BufferType.SPANNABLE);
        }
        mHeaderSearchIndices = highlightSearchKeyword(mHeadersView, mSearchKey);
    }

    private void populateBody(boolean isPlainText) {
        if (!isPlainText) {
            mBodyView.setText(getString(R.string.gander_body_omitted));
        } else {
//            mExecutor.shutdown();
            TextUtil.asyncSetText(mExecutor, new TextUtil.AsyncTextProvider() {
                @Override
                public CharSequence getText() {
                    CharSequence body = null;
                    String searchKey = mSearchKey;
                    if (mType == TYPE_REQUEST) {
                        body = mTransactionUIHelper.getFormattedRequestBody();
                    } else if (mType == TYPE_RESPONSE) {
                        body = mTransactionUIHelper.getFormattedResponseBody();
                    }
                    if (TextUtil.isNullOrWhiteSpace(body) || TextUtil.isNullOrWhiteSpace(searchKey)) {
                        return body;
                    } else {
                        List<Integer> startIndexes = FormatUtils.indexOf(body, searchKey);
                        SpannableString spannableBody = new SpannableString(body);
                        FormatUtils.applyHighlightSpan(spannableBody, startIndexes, searchKey.length());
                        mBodySearchIndices = startIndexes;
                        return spannableBody;
                    }
                }

                @Override
                public AppCompatTextView getTextView() {
                    return mBodyView;
                }
            });
        }
    }

    private List<Integer> highlightSearchKeyword(TextView textView, String searchKey) {
        if (textView != null) {
            CharSequence body = textView.getText();
            if (body instanceof Spannable) {
                Spannable spannableBody = (Spannable) body;
                // remove old HighlightSpans
                HighlightSpan spansToRemove[] = spannableBody.getSpans(0, spannableBody.length() - 1, HighlightSpan.class);
                for (Object span : spansToRemove) {
                    spannableBody.removeSpan(span);
                }

                // add spans only if searchKey size is > 0
                if (searchKey != null && searchKey.length() > 0) {
                    // get indices of new search
                    List<Integer> startIndexes = FormatUtils.indexOf(body.toString(), searchKey);
                    // add spans
                    FormatUtils.applyHighlightSpan(spannableBody, startIndexes, searchKey.length());
                    return startIndexes;
                }
            }
        }
        return new ArrayList<>(0);
    }

    BackgroundColorSpan searchHighLightSpan = new BackgroundColorSpan(GanderColorUtil.SEARCHED_HIGHLIGHT_BACKGROUND_COLOR);

    private void updateSearchCount(int moveToIndex, String searchKey) {
        List<Integer> headerSearchIndices = mHeaderSearchIndices;
        List<Integer> bodySearchIndices = mBodySearchIndices;
        int headerIndicesCount = headerSearchIndices.size();
        int bodyIndicesCount = bodySearchIndices.size();
        int totalCount = headerIndicesCount + bodyIndicesCount;
        if (totalCount == 0) {
            moveToIndex = 0;
        } else {
            if (moveToIndex > totalCount) {
                moveToIndex = 1;
            } else if (moveToIndex <= 0) {
                moveToIndex = totalCount;
            }
            // else moveToIndex will be unchanged
        }

        mSearchCountView.setText(String.valueOf(moveToIndex).concat("/").concat(String.valueOf(totalCount)));
        ((Spannable) mHeadersView.getText()).removeSpan(searchHighLightSpan);
        ((Spannable) mBodyView.getText()).removeSpan(searchHighLightSpan);

        if (moveToIndex > 0) {
            int scrollToY;
            if (moveToIndex <= headerIndicesCount) {
                int headerSearchIndex = headerSearchIndices.get(moveToIndex - 1);
                int lineNumber = mHeadersView.getLayout().getLineForOffset(headerSearchIndex);
                scrollToY = mHeadersView.getLayout().getLineTop(lineNumber);
                ((Spannable) mHeadersView.getText()).setSpan(searchHighLightSpan,
                        headerSearchIndex, headerSearchIndex + searchKey.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                int bodySearchIndex = bodySearchIndices.get(moveToIndex - headerIndicesCount - 1);
                int lineNumber = mBodyView.getLayout().getLineForOffset(bodySearchIndex);
                scrollToY = mHeadersView.getMeasuredHeight() + mBodyView.getLayout().getLineTop(lineNumber);
                ((Spannable) mBodyView.getText()).setSpan(searchHighLightSpan,
                        bodySearchIndex, bodySearchIndex + searchKey.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
//            mScrollParentView.scrollTo(0, scrollToY + mLinearParentView.getPaddingTop());//move to exact spot
            mScrollParentView.scrollTo(0, scrollToY);
        }
        mCurrentSearchIndex = moveToIndex;
    }

    private void showKeyboard() {
        mSearchView.requestFocus();
        Context context = getContext();
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showSoftInput(mSearchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        Context context = getContext();
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        }
    }

    private void showSearch() {
        mSearchFab.hide();
        mSearchBar.setVisibility(View.VISIBLE);
        mScrollParentView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.gander_search_bar_height), 0, mScrollParentView.getBottom());
        showKeyboard();
    }

    private void hideOrClearSearch() {
        if (TextUtil.isNullOrWhiteSpace(mSearchKey)) {
            mSearchFab.show();
            mSearchBar.setVisibility(View.GONE);
            mScrollParentView.setPadding(0, 0, 0, mScrollParentView.getBottom());
            hideKeyboard();
        } else {
            mSearchView.setText("");
        }
    }
}
