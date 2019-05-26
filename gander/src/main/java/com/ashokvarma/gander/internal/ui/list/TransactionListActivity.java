package com.ashokvarma.gander.internal.ui.list;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.support.NotificationHelper;
import com.ashokvarma.gander.internal.support.event.Callback;
import com.ashokvarma.gander.internal.support.event.Debouncer;
import com.ashokvarma.gander.internal.support.event.Sampler;
import com.ashokvarma.gander.internal.ui.BaseGanderActivity;
import com.ashokvarma.gander.internal.ui.HttpTransactionUIHelper;
import com.ashokvarma.gander.internal.ui.details.TransactionDetailsActivity;

public class TransactionListActivity extends BaseGanderActivity implements TransactionAdapter.Listener, SearchView.OnQueryTextListener {

    private TransactionAdapter mTransactionAdapter;
    private ListDiffUtil mListDiffUtil;
    private RecyclerView mRecyclerView;
    private TransactionListViewModel mViewModel;
    private LiveData<PagedList<HttpTransactionUIHelper>> mCurrentSubscription;

    // 300 mills delay min. Max no limit
    private Debouncer<String> mSearchDebouncer = new Debouncer<>(300, new Callback<String>() {
        @Override
        public void onEmit(String event) {
            loadResults(event, mViewModel.getTransactions(event));
        }
    });

    // 100 mills delay. batch all changes in 100 mills and emit last item at the end of 100 mills
    private Sampler<TransactionListWithSearchKeyModel> mTransactionSampler = new Sampler<>(100, new Callback<TransactionListWithSearchKeyModel>() {
        @Override
        public void onEmit(TransactionListWithSearchKeyModel event) {
            mListDiffUtil.setSearchKey(event.mSearchKey);
            mTransactionAdapter.setSearchKey(event.mSearchKey).submitList(event.pagedList);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gander_act_transaction_list);
        Toolbar toolbar = findViewById(R.id.gander_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getApplicationName());

        mRecyclerView = findViewById(R.id.gander_transaction_list);
        mListDiffUtil = new ListDiffUtil();
        mTransactionAdapter = new TransactionAdapter(this, mListDiffUtil).setListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mTransactionAdapter);

        mViewModel = ViewModelProviders.of(this).get(TransactionListViewModel.class);

        loadResults(null, mViewModel.getTransactions(null));
    }

    private void loadResults(final String searchKey, LiveData<PagedList<HttpTransactionUIHelper>> pagedListLiveData) {
        if (mCurrentSubscription != null && mCurrentSubscription.hasObservers()) {
            mCurrentSubscription.removeObservers(this);
        }
        mCurrentSubscription = pagedListLiveData;
        mCurrentSubscription.observe(TransactionListActivity.this, new Observer<PagedList<HttpTransactionUIHelper>>() {
            @Override
            public void onChanged(@Nullable PagedList<HttpTransactionUIHelper> transactionPagedList) {
                mTransactionSampler.consume(new TransactionListWithSearchKeyModel(searchKey, transactionPagedList));
            }
        });
    }

    @Override
    public void onTransactionClicked(HttpTransactionUIHelper transactionUIHelper) {
        TransactionDetailsActivity.start(this, transactionUIHelper.getId(), transactionUIHelper.getStatus(), transactionUIHelper.getResponseCode());
    }

    @Override
    public void onItemsInserted(int firstInsertedItemPosition) {
        mRecyclerView.smoothScrollToPosition(firstInsertedItemPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gander_list_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear) {
            mViewModel.clearAll();
            NotificationHelper.clearBuffer();
            return true;
        } else if (item.getItemId() == R.id.browse_sql) {
            //todo
//            SQLiteUtils.browseDatabase(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchDebouncer.consume(newText);
        return true;
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);
    }

    static class TransactionListWithSearchKeyModel {
        final String mSearchKey;
        final PagedList<HttpTransactionUIHelper> pagedList;

        TransactionListWithSearchKeyModel(String mSearchKey, PagedList<HttpTransactionUIHelper> pagedList) {
            this.mSearchKey = mSearchKey;
            this.pagedList = pagedList;
        }
    }
}
