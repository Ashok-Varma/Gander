package com.ashokvarma.gander.internal.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ashokvarma.gander.GanderInterceptor;
import com.ashokvarma.gander.internal.data.GanderDatabase;

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

    private static long lastCleanup;

    private final Context context;
    private final GanderDatabase ganderDatabase;
    private final long period;
    private final long cleanupFrequency;
    private final SharedPreferences prefs;

    public RetentionManager(Context context, GanderInterceptor.Period retentionPeriod) {
        this.context = context;
        this.ganderDatabase = GanderDatabase.getInstance(context);
        period = toMillis(retentionPeriod);
        prefs = context.getSharedPreferences(PREFS_NAME, 0);
        cleanupFrequency = (retentionPeriod == GanderInterceptor.Period.ONE_HOUR) ?
                TimeUnit.MINUTES.toMillis(30) : TimeUnit.HOURS.toMillis(2);
    }

    public synchronized void doMaintenance() {
        if (period > 0) {
            long now = new Date().getTime();
            if (isCleanupDue(now)) {
                Logger.i("Performing data retention maintenance...");
                deleteSince(getThreshold(now));
                updateLastCleanup(now);
            }
        }
    }

    private long getLastCleanup(long fallback) {
        if (lastCleanup == 0) {
            lastCleanup = prefs.getLong(KEY_LAST_CLEANUP, fallback);
        }
        return lastCleanup;
    }

    private void updateLastCleanup(long time) {
        lastCleanup = time;
        prefs.edit().putLong(KEY_LAST_CLEANUP, time).apply();
    }

    private void deleteSince(long threshold) {
        long rows = ganderDatabase.httpTransactionDao().deleteTransactionsBefore(new Date(threshold));
        Logger.i(rows + " transactions deleted");
    }

    private boolean isCleanupDue(long now) {
        return (now - getLastCleanup(now)) > cleanupFrequency;
    }

    private long getThreshold(long now) {
        return (period == 0) ? now : now - period;
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
