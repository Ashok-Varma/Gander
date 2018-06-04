package com.ashokvarma.gander.internal.support;

import android.util.Log;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class Logger {
    private static final String LOG_TAG = "GanderInterceptor";

    public static void i(String message) {
        Log.i(LOG_TAG, message);
    }

    public static void w(String message) {
        Log.w(LOG_TAG, message);
    }
}
