package com.ashokvarma.gander.internal.ui.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.ashokvarma.gander.internal.ui.HttpTransactionUIHelper;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
public class ListDiffUtil extends DiffUtil.ItemCallback<HttpTransactionUIHelper> {

    private String mSearchKey;

    void setSearchKey(String searchKey) {
        this.mSearchKey = searchKey;
    }

    @Override
    public boolean areItemsTheSame(@NonNull HttpTransactionUIHelper oldItem, @NonNull HttpTransactionUIHelper newItem) {
        // might not work always due to async nature of Adapter fails in very rare race conditions but increases pref.
        newItem.searchKey = mSearchKey;
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull HttpTransactionUIHelper oldItem, @NonNull HttpTransactionUIHelper newItem) {
        // both will non null. because of areItemsTheSame logic only non nulls come here
        // comparing only items shown in the list

        return areEqual(oldItem.getMethod(), newItem.getMethod()) &&
                areEqual(oldItem.getPath(), newItem.getPath()) &&
                areEqual(oldItem.getHost(), newItem.getHost()) &&
                areEqual(oldItem.getRequestStartTimeString(), newItem.getRequestStartTimeString()) &&
                (oldItem.isSsl() == newItem.isSsl()) &&
                oldItem.getStatus().equals(newItem.getStatus()) &&
                areEqual(oldItem.getResponseCode(), newItem.getResponseCode()) &&
                areEqual(oldItem.getDurationString(), newItem.getDurationString()) &&
                areEqual(oldItem.getTotalSizeString(), newItem.getTotalSizeString()) &&
                areEqual(oldItem.searchKey, newItem.searchKey);
    }

    private static boolean areEqual(@Nullable Object oldItem, @Nullable Object newItem) {
        if (oldItem == null && newItem == null) {
            // both are null
            return true;
        } else if (oldItem == null || newItem == null) {
            // only one is null => return false
            return false;
        }
        return oldItem.equals(newItem);
    }
}
