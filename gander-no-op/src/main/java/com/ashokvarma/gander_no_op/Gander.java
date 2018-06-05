package com.ashokvarma.gander_no_op;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class Gander {

    /**
     * Get an Intent to launch the Gander UI directly.
     *
     * @param context A Context.
     * @return An Intent for the main Gander Activity that can be started with {@link Context#startActivity(Intent)}.
     */
    public static Intent getLaunchIntent(Context context) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    @SuppressWarnings("WeakerAccess")
    public static String addAppShortcut(Context context) {
        return null;
    }
}
