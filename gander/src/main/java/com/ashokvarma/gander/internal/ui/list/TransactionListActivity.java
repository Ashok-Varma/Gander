package com.ashokvarma.gander.internal.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.support.NotificationHelper;
import com.ashokvarma.gander.internal.support.Debouncer;
import com.ashokvarma.gander.internal.ui.BaseGanderActivity;
import com.ashokvarma.gander.internal.ui.details.TransactionDetailsActivity;

public class TransactionListActivity extends BaseGanderActivity implements TransactionAdapter.Listener, SearchView.OnQueryTextListener, Debouncer.Callback<String> {

    TransactionAdapter transactionAdapter;
    RecyclerView recyclerView;
    TransactionListViewModel viewModel;
    Debouncer<String> debouncer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        Toolbar toolbar = findViewById(R.id.gander_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getApplicationName());

        recyclerView = findViewById(R.id.gander_txn_list);
        transactionAdapter = new TransactionAdapter(this);
        transactionAdapter.setListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(transactionAdapter);

        viewModel = ViewModelProviders.of(this).get(TransactionListViewModel.class);

        debouncer = new Debouncer<>(300, this);

        loadResults(null);
    }

    private void loadResults(String searchKey) {
        subscribeTo(viewModel.getTransactions(searchKey));
    }

    @Override
    public void onEmit(String key) {
        loadResults(key);
    }

    private LiveData<PagedList<HttpTransaction>> currentSubscription;

    private void subscribeTo(LiveData<PagedList<HttpTransaction>> pagedListLiveData) {
        if (currentSubscription != null && currentSubscription.hasObservers()) {
            currentSubscription.removeObservers(this);
        }
        currentSubscription = pagedListLiveData;
        currentSubscription.observe(TransactionListActivity.this, new Observer<PagedList<HttpTransaction>>() {
            @Override
            public void onChanged(@Nullable PagedList<HttpTransaction> itemAndPeople) {
                transactionAdapter.submitList(itemAndPeople);
            }
        });
    }

    @Override
    public void onTransactionClicked(HttpTransaction transaction) {
        TransactionDetailsActivity.start(this, transaction.getId(), transaction.getStatus(), transaction.getResponseCode());
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
            viewModel.clearAll();
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
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        debouncer.consume(newText);
        return true;
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);
    }
}
