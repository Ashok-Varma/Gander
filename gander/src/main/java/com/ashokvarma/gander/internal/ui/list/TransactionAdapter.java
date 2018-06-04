package com.ashokvarma.gander.internal.ui.list;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.gander.R;
import com.ashokvarma.gander.internal.data.HttpTransaction;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class TransactionAdapter extends PagedListAdapter<HttpTransaction, RecyclerView.ViewHolder> {

    private final LayoutInflater layoutInflater;

    private final int colorDefault;
    private final int colorDefaultTxt;
    private final int colorRequested;
    private final int colorError;
    private final int color500;
    private final int color400;
    private final int color300;

    private Listener listener;

    protected TransactionAdapter(Context context) {
        super(new ListDiffUtil());

        layoutInflater = LayoutInflater.from(context);


        colorDefault = ContextCompat.getColor(context, R.color.gander_status_default);
        colorDefaultTxt = ContextCompat.getColor(context, R.color.gander_status_default_txt);
        colorRequested = ContextCompat.getColor(context, R.color.gander_status_requested);
        colorError = ContextCompat.getColor(context, R.color.gander_status_error);
        color500 = ContextCompat.getColor(context, R.color.gander_status_500);
        color400 = ContextCompat.getColor(context, R.color.gander_status_400);
        color300 = ContextCompat.getColor(context, R.color.gander_status_300);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private static final int EMPTY_VIEW = 1;
    private static final int TRANSACTION_VIEW = 2;

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null) {
            return EMPTY_VIEW;
        } else {
            return TRANSACTION_VIEW;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TRANSACTION_VIEW) {
            return new TransactionViewHolder(layoutInflater.inflate(R.layout.gander_list_item_transaction, parent, false));
        } else {
            //(viewType == EMPTY_VIEW)
            return new EmptyTransactionViewHolder(layoutInflater.inflate(R.layout.gander_list_item_empty_transaction, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder genericHolder, int position) {
        HttpTransaction transaction = getItem(position);
        if (transaction != null) {
            TransactionViewHolder holder = ((TransactionViewHolder) genericHolder);
            holder.path.setText(transaction.getMethod().concat(" ").concat(transaction.getPath()));
            holder.host.setText(transaction.getHost());
            holder.start.setText(transaction.getRequestStartTimeString());
            holder.ssl.setVisibility(transaction.isSsl() ? View.VISIBLE : View.GONE);
            if (transaction.getStatus() == HttpTransaction.Status.Complete) {
                holder.code.setText(String.valueOf(transaction.getResponseCode()));
                holder.duration.setText(transaction.getDurationString());
                holder.size.setText(transaction.getTotalSizeString());
            } else {
                holder.code.setText(null);
                holder.duration.setText(null);
                holder.size.setText(null);
            }
            if (transaction.getStatus() == HttpTransaction.Status.Failed) {
                holder.code.setText("!!!");
            }
            setStatusColor(holder, transaction);
        }
        // null no changes

    }

    private void setStatusColor(TransactionViewHolder holder, HttpTransaction transaction) {
        int color;
        if (transaction.getStatus() == HttpTransaction.Status.Failed) {
            color = colorError;
        } else if (transaction.getStatus() == HttpTransaction.Status.Requested) {
            color = colorRequested;
        } else if (transaction.getResponseCode() >= 500) {
            color = color500;
        } else if (transaction.getResponseCode() >= 400) {
            color = color400;
        } else if (transaction.getResponseCode() >= 300) {
            color = color300;
        } else {
            color = colorDefaultTxt;
        }

        holder.path.setTextColor(color);
        holder.code.setTextColor(color);
    }

    static class EmptyTransactionViewHolder extends RecyclerView.ViewHolder {
        EmptyTransactionViewHolder(View itemView) {
            super(itemView);
        }
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView code;
        final TextView path;
        final TextView host;
        final TextView start;
        final TextView duration;
        final TextView size;
        final ImageView ssl;

        TransactionViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            code = view.findViewById(R.id.gander_list_code);
            path = view.findViewById(R.id.gander_list_path);
            host = view.findViewById(R.id.gander_list_host);
            start = view.findViewById(R.id.gander_list_start);
            duration = view.findViewById(R.id.gander_list_duration);
            size = view.findViewById(R.id.gander_list_size);
            ssl = view.findViewById(R.id.gander_list_ssl);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        HttpTransaction transaction = getItem(getAdapterPosition());
                        listener.onTransactionClicked(transaction);
                    }
                }
            });
        }
    }

    interface Listener {
        void onTransactionClicked(HttpTransaction httpTransaction);
    }
}
