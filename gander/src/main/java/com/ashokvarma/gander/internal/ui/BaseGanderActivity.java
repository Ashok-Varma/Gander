package com.ashokvarma.gander.internal.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ashokvarma.gander.internal.support.NotificationHelper;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public abstract class BaseGanderActivity extends AppCompatActivity {

    private static boolean IN_FOREGROUND;

    private NotificationHelper mNotificationHelper;

    public static boolean isInForeground() {
        return IN_FOREGROUND;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationHelper = new NotificationHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IN_FOREGROUND = true;
        mNotificationHelper.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IN_FOREGROUND = false;
    }

}
