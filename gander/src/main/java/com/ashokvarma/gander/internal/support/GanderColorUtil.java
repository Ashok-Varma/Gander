package com.ashokvarma.gander.internal.support;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.ui.HttpTransactionUIHelper;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 04/06/18
 */
public class GanderColorUtil {

    public static final int SEARCHED_HIGHLIGHT_BACKGROUND_COLOR = Color.parseColor("#FD953F");

    public static final int HIGHLIGHT_BACKGROUND_COLOR = Color.parseColor("#FFFD38");
    public static final int HIGHLIGHT_TEXT_COLOR = 0;//none
    public static final boolean HIGHLIGHT_UNDERLINE = false;

    private final int mColorDefault;
    private final int mColorDefaultTxt;
    private final int mColorRequested;
    private final int mColorError;
    private final int mColor500;
    private final int mColor400;
    private final int mColor300;

    private GanderColorUtil(Context context) {
        mColorDefault = ContextCompat.getColor(context, R.color.gander_status_default);
        mColorDefaultTxt = ContextCompat.getColor(context, R.color.gander_status_default_txt);
        mColorRequested = ContextCompat.getColor(context, R.color.gander_status_requested);
        mColorError = ContextCompat.getColor(context, R.color.gander_status_error);
        mColor500 = ContextCompat.getColor(context, R.color.gander_status_500);
        mColor400 = ContextCompat.getColor(context, R.color.gander_status_400);
        mColor300 = ContextCompat.getColor(context, R.color.gander_status_300);
    }

    private static GanderColorUtil TRANSACTION_COLOR_UTIL_INSTANCE;

    public static GanderColorUtil getInstance(Context context) {
        if (TRANSACTION_COLOR_UTIL_INSTANCE == null) {
            TRANSACTION_COLOR_UTIL_INSTANCE = new GanderColorUtil(context);
        }
        return TRANSACTION_COLOR_UTIL_INSTANCE;
    }

    public int getTransactionColor(HttpTransactionUIHelper transactionUIHelper) {
        return getTransactionColor(transactionUIHelper, false);
    }

    public int getTransactionColor(HttpTransactionUIHelper transactionUIHelper, boolean txtColors) {
        HttpTransactionUIHelper.Status status = transactionUIHelper.getStatus();
        Integer responseCode = transactionUIHelper.getResponseCode();
        return getTransactionColor(status, responseCode, txtColors);
    }

    public int getTransactionColor(HttpTransactionUIHelper.Status status, Integer responseCode) {
        return getTransactionColor(status, responseCode, false);
    }

    private int getTransactionColor(HttpTransactionUIHelper.Status status, Integer responseCode, boolean txtColors) {
        if (status == HttpTransactionUIHelper.Status.Failed) {
            return mColorError;
        } else if (status == HttpTransactionUIHelper.Status.Requested) {
            return mColorRequested;
        } else if (responseCode >= 500) {
            return mColor500;
        } else if (responseCode >= 400) {
            return mColor400;
        } else if (responseCode >= 300) {
            return mColor300;
        } else {
            return txtColors ? mColorDefaultTxt : mColorDefault;
        }
    }
}
