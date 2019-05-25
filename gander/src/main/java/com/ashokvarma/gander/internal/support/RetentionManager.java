package com.ashokvarma.gander.internal.support;

import android.content.Context;
import android.content.SharedPreferences;

import com.ashokvarma.gander.Gander;
import com.ashokvarma.gander.GanderInterceptor;
import com.ashokvarma.gander.internal.data.GanderStorage;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class RetentionManager {
    private static final String PREFS_NAME = "gander_preferences";
    private static final String KEY_LAST_CLEANUP = "last_cleanup";

    private static long LAST_CLEAN_UP;

    private final GanderStorage mGanderStorage;
    private final long mPeriod;
    private final long mCleanupFrequency;
    private final SharedPreferences mPrefs;

    public RetentionManager(Context context, GanderInterceptor.Period retentionPeriod) {
        this.mGanderStorage = Gander.getGanderStorage();
        mPeriod = toMillis(retentionPeriod);
        mPrefs = context.getSharedPreferences(PREFS_NAME, 0);
        mCleanupFrequency = (retentionPeriod == GanderInterceptor.Period.ONE_HOUR) ?
                TimeUnit.MINUTES.toMillis(30) : TimeUnit.HOURS.toMillis(2);
    }

    public synchronized void doMaintenance() {
        if (mPeriod > 0) {
            long now = new Date().getTime();
            if (isCleanupDue(now)) {
                Logger.i("Performing data retention maintenance...");
                deleteSince(getThreshold(now));
                updateLastCleanup(now);
            }
        }
    }

    private long getLastCleanup(long fallback) {
        if (LAST_CLEAN_UP == 0) {
            LAST_CLEAN_UP = mPrefs.getLong(KEY_LAST_CLEANUP, fallback);
        }
        return LAST_CLEAN_UP;
    }

    private void updateLastCleanup(long time) {
        LAST_CLEAN_UP = time;
        mPrefs.edit().putLong(KEY_LAST_CLEANUP, time).apply();
    }

    private void deleteSince(long threshold) {
        long rows = mGanderStorage.getTransactionDao().deleteTransactionsBefore(new Date(threshold));
        Logger.i(rows + " transactions deleted");
    }

    private boolean isCleanupDue(long now) {
        return (now - getLastCleanup(now)) > mCleanupFrequency;
    }

    private long getThreshold(long now) {
        return (mPeriod == 0) ? now : now - mPeriod;
    }

    private long toMillis(GanderInterceptor.Period period) {
        switch (period) {
            case ONE_HOUR:
                return TimeUnit.HOURS.toMillis(1);
            case ONE_DAY:
                return TimeUnit.DAYS.toMillis(1);
            case ONE_WEEK:
                return TimeUnit.DAYS.toMillis(7);
            default:
                return 0;
        }
    }
}
