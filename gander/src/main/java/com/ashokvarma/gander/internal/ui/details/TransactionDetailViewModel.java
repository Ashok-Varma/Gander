package com.ashokvarma.gander.internal.ui.details;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.ashokvarma.gander.Gander;
import com.ashokvarma.gander.internal.data.TransactionDao;
import com.ashokvarma.gander.internal.ui.HttpTransactionUIHelper;

import static com.ashokvarma.gander.internal.ui.HttpTransactionUIHelper.HTTP_TRANSACTION_UI_HELPER_FUNCTION;

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

    LiveData<HttpTransactionUIHelper> getTransactionWithId(long id) {
        return Transformations.map(
                mTransactionDao.getTransactionsWithId(id),
                HTTP_TRANSACTION_UI_HELPER_FUNCTION
        );
    }
}
