package com.ashokvarma.gander.internal.data;

import androidx.annotation.IntRange;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import java.util.Date;

public interface TransactionDao {
    enum SearchType {
        DEFAULT,
        INCLUDE_REQUEST,
        INCLUDE_RESPONSE,
        INCLUDE_REQUEST_RESPONSE,
    }

    /**
     * Insert if it doesn't exist Or Update if exists
     *
     * @param httpTransaction {@link HttpTransaction}
     * @return return the index of transaction
     */
    long insertTransaction(HttpTransaction httpTransaction);

    /**
     * Update if it exists
     *
     * @param httpTransaction {@link HttpTransaction}
     * @return return no of updates
     */
    @IntRange(from = 0, to = 1)
    int updateTransaction(HttpTransaction httpTransaction);

    /**
     * Delete if it exists
     *
     * @param httpTransactions {@link HttpTransaction}
     * @return return no of deletes (0 to size of transactions)
     */
    @IntRange(from = 0)
    int deleteTransactions(HttpTransaction... httpTransactions);

    /**
     * Delete all transactions before specified date
     *
     * @param beforeDate {@link Date}
     * @return return no of deletes
     */
    @IntRange(from = 0)
    int deleteTransactionsBefore(Date beforeDate);

    /**
     * ClearAll the transactions
     *
     * @return return no of deletes
     */
    @IntRange(from = 0)
    int clearAll();

    DataSource.Factory<Integer, HttpTransaction> getAllTransactions();

    LiveData<HttpTransaction> getTransactionsWithId(long id);

    DataSource.Factory<Integer, HttpTransaction> getAllTransactionsWith(String key, SearchType searchType);

}
