package com.ashokvarma.gander.internal.support;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ashokvarma.gander.GanderInterceptor;
import com.ashokvarma.gander.internal.data.GanderDatabase;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class ClearTransactionsService extends IntentService {

    public ClearTransactionsService() {
        super("Chuck-ClearTransactionsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int deletedTransactionCount = GanderDatabase.getInstance(this).httpTransactionDao().clearAll();
        Logger.i(deletedTransactionCount + " transactions deleted");
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.dismiss();
    }
}