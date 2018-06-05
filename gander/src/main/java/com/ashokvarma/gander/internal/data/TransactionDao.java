package com.ashokvarma.gander.internal.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;
import android.support.annotation.IntRange;

import java.util.Date;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
@Dao
public abstract class TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertTransaction(HttpTransaction httpTransaction);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract int updateTransaction(HttpTransaction httpTransactions);

    @Delete
    public abstract int deleteTransactions(HttpTransaction... httpTransactions);

    @Query("DELETE FROM HttpTransaction WHERE request_date < :beforeDate")
    public abstract int deleteTransactionsBefore(Date beforeDate);

    @Query("DELETE FROM HttpTransaction")
    public abstract int clearAll();

    @Query("SELECT * FROM HttpTransaction ORDER BY id DESC")
    public abstract DataSource.Factory<Integer, HttpTransaction> getAllTransactions();

    @Query("SELECT * FROM HttpTransaction WHERE id = :id")
    public abstract LiveData<HttpTransaction> getTransactionsWithId(long id);

    public static final int SEARCH_DEFAULT = 1;
    public static final int SEARCH_INCLUDE_REQUEST = 2;
    public static final int SEARCH_INCLUDE_RESPONSE = 3;
    public static final int SEARCH_INCLUDE_REQUEST_RESPONSE = 4;

    public DataSource.Factory<Integer, HttpTransaction> getAllTransactionsWith(String key, @IntRange(from = 1, to = 4) int searchType) {
        String endWildCard = key + "%";
        String doubleSideWildCard = "%" + key + "%";
        switch (searchType) {
            case SEARCH_DEFAULT:
                return getAllTransactions(endWildCard, doubleSideWildCard);
            case SEARCH_INCLUDE_REQUEST:
                return getAllTransactionsIncludeRequest(endWildCard, doubleSideWildCard);
            case SEARCH_INCLUDE_RESPONSE:
                return getAllTransactionsIncludeResponse(endWildCard, doubleSideWildCard);
            case SEARCH_INCLUDE_REQUEST_RESPONSE:
                return getAllTransactionsIncludeRequestResponse(endWildCard, doubleSideWildCard);
            default:
                return getAllTransactions(endWildCard, doubleSideWildCard);
        }
    }

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR request_body LIKE :doubleWildCard OR response_body LIKE :doubleWildCard OR response_message LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, HttpTransaction> getAllTransactionsIncludeRequestResponse(String endWildCard, String doubleWildCard);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR response_body LIKE :doubleWildCard OR response_message LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, HttpTransaction> getAllTransactionsIncludeResponse(String endWildCard, String doubleWildCard);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR request_body LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, HttpTransaction> getAllTransactionsIncludeRequest(String endWildCard, String doubleWildCard);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, HttpTransaction> getAllTransactions(String endWildCard, String doubleWildCard);

}
