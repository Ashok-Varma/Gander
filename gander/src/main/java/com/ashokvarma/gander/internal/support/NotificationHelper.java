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
import android.text.SpannableStringBuilder;
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

    private static final LongSparseArray<HttpTransaction> transactionBuffer = new LongSparseArray<>();
    private static int transactionCount;

    private final Context context;
    private final NotificationManager notificationManager;
    private final TransactionColorUtil colorUtil;
//    private Method setChannelId;

    public static synchronized void clearBuffer() {
        transactionBuffer.clear();
        transactionCount = 0;
    }

    private static synchronized void addToBuffer(HttpTransaction transaction) {
        if (transaction.getStatus() == HttpTransaction.Status.Requested) {
            transactionCount++;
        }
        transactionBuffer.put(transaction.getId(), transaction);
        if (transactionBuffer.size() > BUFFER_SIZE) {
            transactionBuffer.removeAt(0);
        }
    }

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        colorUtil = TransactionColorUtil.getInstance(context);
    }

    public void setUpChannelIfNecessary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID,
                            context.getString(R.string.notification_category), NotificationManager.IMPORTANCE_LOW));
        }
    }

    public synchronized void show(HttpTransaction transaction) {
        addToBuffer(transaction);
        if (!BaseGanderActivity.isInForeground()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(PendingIntent.getActivity(context, 0, Gander.getLaunchIntent(context), 0))
                    .setLocalOnly(true)
                    .setSmallIcon(R.drawable.gander_ic_notification_white_24dp)
                    .setColor(ContextCompat.getColor(context, R.color.gander_colorPrimary))
                    .setContentTitle(context.getString(R.string.gander_notification_title))
                    .setChannelId(CHANNEL_ID);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            int count = 0;
            for (int i = transactionBuffer.size() - 1; i >= 0; i--) {
                if (count < BUFFER_SIZE) {
                    if (count == 0) {
                        builder.setContentText(getNotificationText(transactionBuffer.valueAt(i)));
                    }
                    inboxStyle.addLine(getNotificationText(transactionBuffer.valueAt(i)));
                }
                count++;
            }
            builder.setAutoCancel(true);
            builder.setStyle(inboxStyle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(String.valueOf(transactionCount));
            } else {
                builder.setNumber(transactionCount);
            }
            builder.addAction(getClearAction());
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private CharSequence getNotificationText(HttpTransaction transaction) {
        int color = colorUtil.getTransactionColor(transaction);
        String text = transaction.getNotificationText();
        // Simple span no Truss required
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(color), 0, text.length(), SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @NonNull
    private NotificationCompat.Action getClearAction() {
        CharSequence clearTitle = context.getString(R.string.gander_clear);
        Intent deleteIntent = new Intent(context, ClearTransactionsService.class);
        PendingIntent intent = PendingIntent.getService(context, 11, deleteIntent, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Action(R.drawable.gander_ic_delete_white_24dp, clearTitle, intent);
    }

    public void dismiss() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}