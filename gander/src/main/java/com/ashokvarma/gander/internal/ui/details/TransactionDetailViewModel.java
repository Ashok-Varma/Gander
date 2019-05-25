package com.ashokvarma.gander.internal.ui.details;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ashokvarma.gander.Gander;
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
    private final TransactionDao mTransactionDao;

    public TransactionDetailViewModel(Application application) {
        super(application);
        mTransactionDao = Gander.getGanderStorage().getTransactionDao();
    }

    LiveData<HttpTransaction> getTransactionWithId(long id) {
        return mTransactionDao.getTransactionsWithId(id);
    }
}
