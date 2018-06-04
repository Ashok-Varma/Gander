package com.ashokvarma.gander.internal.ui.list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

import com.ashokvarma.gander.internal.data.GanderDatabase;
import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.data.TransactionDao;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class TransactionListViewModel extends AndroidViewModel {
    private LiveData<PagedList<HttpTransaction>> transactions;
    private final TransactionDao transactionDao;

    private final static PagedList.Config config
            = new PagedList.Config.Builder()
            .setPageSize(15) // page size
            .setInitialLoadSizeHint(30)// items to fetch on first load
            .setPrefetchDistance(10)// trigger when to fetch a page
            .setEnablePlaceholders(true)
            .build();

    public TransactionListViewModel(Application application) {
        super(application);
        transactionDao = GanderDatabase.getInstance(application).httpTransactionDao();
        DataSource.Factory<Integer, HttpTransaction> factory = transactionDao.getAllTransactions();
        transactions = new LivePagedListBuilder<>(factory, config).build();
    }

    public LiveData<PagedList<HttpTransaction>> getTransactions(String key) {
        if (key == null || key.trim().length() == 0) {
            return transactions;
        } else {
            DataSource.Factory<Integer, HttpTransaction> factory = transactionDao.getAllTransactionsWith(key + "%", "%" + key + "%");
            return new LivePagedListBuilder<>(factory, config).build();
        }
    }

    public void deleteItem(HttpTransaction transaction) {
        new deleteAsyncTask(transactionDao).execute(transaction);
    }

    public void clearAll() {
        new clearAsyncTask(transactionDao).execute();
    }

    private static class deleteAsyncTask extends AsyncTask<HttpTransaction, Void, Integer> {

        private final TransactionDao transactionDao;

        deleteAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }

        @Override
        protected Integer doInBackground(final HttpTransaction... params) {
            return transactionDao.deleteTransactions(params);
        }

    }

    private static class clearAsyncTask extends AsyncTask<HttpTransaction, Void, Integer> {

        private final TransactionDao transactionDao;

        clearAsyncTask(TransactionDao transactionDao) {
            this.transactionDao = transactionDao;
        }

        @Override
        protected Integer doInBackground(final HttpTransaction... params) {
            return transactionDao.clearAll();
        }

    }
}
