package com.ashokvarma.gander.internal.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertTransaction(HttpTransaction httpTransaction);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public int updateTransaction(HttpTransaction httpTransactions);

    @Delete
    public int deleteTransactions(HttpTransaction... httpTransactions);

    @Query("DELETE FROM HttpTransaction WHERE request_date < :beforeDate")
    public int deleteTransactionsBefore(Date beforeDate);

    @Query("DELETE FROM HttpTransaction")
    public int clearAll();

    @Query("SELECT * FROM HttpTransaction ORDER BY id DESC")
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactions();

    @Query("SELECT * FROM HttpTransaction WHERE protocol LIKE :endWildCard OR method LIKE :endWildCard OR url LIKE :doubleWildCard OR request_body LIKE :doubleWildCard OR response_body LIKE :doubleWildCard OR response_message LIKE :doubleWildCard OR response_code LIKE :endWildCard ORDER BY id DESC")
    public DataSource.Factory<Integer, HttpTransaction> getAllTransactionsWith(String endWildCard, String doubleWildCard);

    @Query("SELECT * FROM HttpTransaction WHERE id = :id")
    public LiveData<HttpTransaction> getTransactionsWithId(long id);
}
