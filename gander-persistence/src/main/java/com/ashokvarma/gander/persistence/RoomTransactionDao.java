package com.ashokvarma.gander.persistence;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;

import com.ashokvarma.gander.internal.data.TransactionDao;

import java.util.Date;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
@Dao
abstract class RoomTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insertTransaction(PersistentHttpTransaction persistentHttpTransaction);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract int updateTransaction(PersistentHttpTransaction persistentHttpTransactions);

    @Delete
    abstract int deleteTransactions(PersistentHttpTransaction... persistentHttpTransactions);

    @Query("DELETE FROM HttpTransaction WHERE request_date < :beforeDate")
    abstract int deleteTransactionsBefore(Date beforeDate);

    @Query("DELETE FROM HttpTransaction")
    abstract int clearAll();

    @Query("SELECT * FROM HttpTransaction ORDER BY id DESC")
    abstract DataSource.Factory<Integer, PersistentHttpTransaction> getAllTransactions();

    @Query("SELECT * FROM HttpTransaction WHERE id = :id")
    abstract LiveData<PersistentHttpTransaction> getTransactionsWithId(long id);

    DataSource.Factory<Integer, PersistentHttpTransaction> getAllTransactionsWith(String key, TransactionDao.SearchType searchType) {
        String endWildCard = key + "%";
        String doubleSideWildCard = "%" + key + "%";
        switch (searchType) {
            case DEFAULT:
                return getAllTransactions(endWildCard, doubleSideWildCard);
            case INCLUDE_REQUEST:
                return getAllTransactionsIncludeRequest(endWildCard, doubleSideWildCard);
            case INCLUDE_RESPONSE:
                return getAllTransactionsIncludeResponse(endWildCard, doubleSideWildCard);
            case INCLUDE_REQUEST_RESPONSE:
                return getAllTransactionsIncludeRequestResponse(endWildCard, doubleSideWildCard);
            default:
                return getAllTransactions(endWildCard, doubleSideWildCard);
        }
    }

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR request_body LIKE :doubleWildCard OR response_body LIKE :doubleWildCard OR response_message LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, PersistentHttpTransaction> getAllTransactionsIncludeRequestResponse(String endWildCard, String doubleWildCard);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR response_body LIKE :doubleWildCard OR response_message LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, PersistentHttpTransaction> getAllTransactionsIncludeResponse(String endWildCard, String doubleWildCard);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR request_body LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, PersistentHttpTransaction> getAllTransactionsIncludeRequest(String endWildCard, String doubleWildCard);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, method, url, path, host, scheme, request_date, error, response_code, took_ms, request_content_length, response_content_length, request_body_is_plain_text, response_body_is_plain_text FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    abstract DataSource.Factory<Integer, PersistentHttpTransaction> getAllTransactions(String endWildCard, String doubleWildCard);

}
