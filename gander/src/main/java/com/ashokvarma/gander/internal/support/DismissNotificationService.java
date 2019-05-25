package com.ashokvarma.gander.internal.support;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class DismissNotificationService extends IntentService {

    public DismissNotificationService() {
        super("Gander-DismissNotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.dismiss();
    }
}
