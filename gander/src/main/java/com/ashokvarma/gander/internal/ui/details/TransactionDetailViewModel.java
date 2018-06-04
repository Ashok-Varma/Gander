package com.ashokvarma.gander.internal.ui.details;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

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
public class TransactionDetailViewModel extends AndroidViewModel {
    private final TransactionDao transactionDao;

    public TransactionDetailViewModel(Application application) {
        super(application);
        transactionDao = GanderDatabase.getInstance(application).httpTransactionDao();
    }

    public LiveData<HttpTransaction> getTransactionWithId(long id) {
        return transactionDao.getTransactionsWithId(id);
    }
}
