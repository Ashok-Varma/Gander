package com.ashokvarma.gander.internal.support;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;

import com.ashokvarma.gander.Gander;
import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.ui.BaseGanderActivity;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "gander_notif";
    private static final int NOTIFICATION_ID = 1139;// in case if someone uses chuck
    private static final int BUFFER_SIZE = 10;

    private static final LongSparseArray<HttpTransaction> TRANSACTION_BUFFER = new LongSparseArray<>();
    private static int TRANSACTION_COUNT;

    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private final GanderColorUtil mColorUtil;

    public static synchronized void clearBuffer() {
        TRANSACTION_BUFFER.clear();
        TRANSACTION_COUNT = 0;
    }

    private static synchronized void addToBuffer(HttpTransaction transaction) {
        if (transaction.getStatus() == HttpTransaction.Status.Requested) {
            TRANSACTION_COUNT++;
        }
        TRANSACTION_BUFFER.put(transaction.getId(), transaction);
        if (TRANSACTION_BUFFER.size() > BUFFER_SIZE) {
            TRANSACTION_BUFFER.removeAt(0);
        }
    }

    public NotificationHelper(Context context) {
        this.mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mColorUtil = GanderColorUtil.getInstance(context);
    }

    public void setUpChannelIfNecessary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID,
                            mContext.getString(R.string.gander_notification_category), NotificationManager.IMPORTANCE_LOW));
        }
    }

    public synchronized void show(HttpTransaction transaction) {
        addToBuffer(transaction);
        if (!BaseGanderActivity.isInForeground()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setContentIntent(PendingIntent.getActivity(mContext, 0, Gander.getLaunchIntent(mContext), 0))
                    .setLocalOnly(true)
                    .setSmallIcon(R.drawable.gander_ic_notification_white_24dp)
                    .setColor(ContextCompat.getColor(mContext, R.color.gander_colorPrimary))
                    .setContentTitle(mContext.getString(R.string.gander_notification_title));
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            int count = 0;
            for (int i = TRANSACTION_BUFFER.size() - 1; i >= 0; i--) {
                if (count < BUFFER_SIZE) {
                    if (count == 0) {
                        builder.setContentText(getNotificationText(TRANSACTION_BUFFER.valueAt(i)));
                    }
                    inboxStyle.addLine(getNotificationText(TRANSACTION_BUFFER.valueAt(i)));
                }
                count++;
            }
            builder.setAutoCancel(true);
            builder.setStyle(inboxStyle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(String.valueOf(TRANSACTION_COUNT));
            } else {
                builder.setNumber(TRANSACTION_COUNT);
            }
            builder.addAction(getClearAction());
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private CharSequence getNotificationText(HttpTransaction transaction) {
        int color = mColorUtil.getTransactionColor(transaction);
        String text = transaction.getNotificationText();
        // Simple span no Truss required
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, text.length(), SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @NonNull
    private NotificationCompat.Action getClearAction() {
        CharSequence clearTitle = mContext.getString(R.string.gander_clear);
        Intent deleteIntent = new Intent(mContext, ClearTransactionsService.class);
        PendingIntent intent = PendingIntent.getService(mContext, 11, deleteIntent, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Action(R.drawable.gander_ic_delete_white_24dp, clearTitle, intent);
    }

    public void dismiss() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}