package com.ashokvarma.gander.internal.support;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.ashokvarma.gander.Gander;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class ClearTransactionsService extends IntentService {

    public ClearTransactionsService() {
        super("Gander-ClearTransactionsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int deletedTransactionCount = Gander.getGanderStorage().getTransactionDao().clearAll();
        Logger.i(deletedTransactionCount + " transactions deleted");
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.dismiss();
    }
}
