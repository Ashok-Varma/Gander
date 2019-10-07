package com.ashokvarma.gander.internal.ui.list;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.ashokvarma.gander.Gander;
import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.data.TransactionDao;
import com.ashokvarma.gander.internal.ui.HttpTransactionUIHelper;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class TransactionListViewModel extends AndroidViewModel {
    private LiveData<PagedList<HttpTransactionUIHelper>> mTransactions;
    private final TransactionDao mTransactionDao;

    private final static PagedList.Config config
            = new PagedList.Config.Builder()
            .setPageSize(15) // page size
            .setInitialLoadSizeHint(30)// items to fetch on first load
            .setPrefetchDistance(10)// trigger when to fetch a page
            .setEnablePlaceholders(true)
            .build();

    public TransactionListViewModel(Application application) {
        super(application);
        mTransactionDao = Gander.getGanderStorage().getTransactionDao();
        DataSource.Factory<Integer, HttpTransactionUIHelper> factory =
                mTransactionDao.getAllTransactions().map(HttpTransactionUIHelper.HTTP_TRANSACTION_UI_HELPER_FUNCTION);
        mTransactions = new LivePagedListBuilder<>(factory, config).build();
    }

    LiveData<PagedList<HttpTransactionUIHelper>> getTransactions(String key) {
        if (key == null || key.trim().length() == 0) {
            return mTransactions;
        } else {
            DataSource.Factory<Integer, HttpTransactionUIHelper> factory =
                    mTransactionDao.getAllTransactionsWith(
                            key,
                            TransactionDao.SearchType.DEFAULT).map(HttpTransactionUIHelper.HTTP_TRANSACTION_UI_HELPER_FUNCTION
                    );
            return new LivePagedListBuilder<>(factory, config).build();
        }
    }

    public void deleteItem(HttpTransaction transaction) {
        new deleteAsyncTask(mTransactionDao).execute(transaction);
    }

    void clearAll() {
        new clearAsyncTask(mTransactionDao).execute();
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
