package com.ashokvarma.gander;

import android.content.Context;
import android.content.Intent;

import com.ashokvarma.gander.internal.ui.list.TransactionListActivity;

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
     * @return An Intent for the main Chuck Activity that can be started with {@link Context#startActivity(Intent)}.
     */
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, TransactionListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
