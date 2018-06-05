package com.ashokvarma.gander.internal.support;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.data.HttpTransaction;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 04/06/18
 */
public class TransactionColorUtil {

    private final int colorDefault;
    private final int colorDefaultTxt;
    private final int colorRequested;
    private final int colorError;
    private final int color500;
    private final int color400;
    private final int color300;

    private TransactionColorUtil(Context context) {
        colorDefault = ContextCompat.getColor(context, R.color.gander_status_default);
        colorDefaultTxt = ContextCompat.getColor(context, R.color.gander_status_default_txt);
        colorRequested = ContextCompat.getColor(context, R.color.gander_status_requested);
        colorError = ContextCompat.getColor(context, R.color.gander_status_error);
        color500 = ContextCompat.getColor(context, R.color.gander_status_500);
        color400 = ContextCompat.getColor(context, R.color.gander_status_400);
        color300 = ContextCompat.getColor(context, R.color.gander_status_300);
    }

    private static TransactionColorUtil instance;

    public static TransactionColorUtil getInstance(Context context) {
        if (instance == null) {
            instance = new TransactionColorUtil(context);
        }
        return instance;
    }

    public int getTransactionColor(HttpTransaction transaction) {
        return getTransactionColor(transaction, false);
    }

    public int getTransactionColor(HttpTransaction transaction, boolean txtColors) {
        HttpTransaction.Status status = transaction.getStatus();
        Integer responseCode = transaction.getResponseCode();
        return getTransactionColor(status, responseCode, txtColors);
    }

    public int getTransactionColor(HttpTransaction.Status status, Integer responseCode) {
        return getTransactionColor(status, responseCode, false);
    }

    public int getTransactionColor(HttpTransaction.Status status, Integer responseCode, boolean txtColors) {
        if (status == HttpTransaction.Status.Failed) {
            return colorError;
        } else if (status == HttpTransaction.Status.Requested) {
            return colorRequested;
        } else if (responseCode >= 500) {
            return color500;
        } else if (responseCode >= 400) {
            return color400;
        } else if (responseCode >= 300) {
            return color300;
        } else {
            return txtColors ? colorDefaultTxt : colorDefault;
        }
    }
}
