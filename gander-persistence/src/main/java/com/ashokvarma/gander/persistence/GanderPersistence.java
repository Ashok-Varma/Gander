package com.ashokvarma.gander.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ashokvarma.gander.internal.data.GanderStorage;
import com.ashokvarma.gander.internal.data.TransactionDao;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
@Database(entities = {PersistentHttpTransaction.class}, version = 1, exportSchema = false)
@TypeConverters({RoomTypeConverters.class})
public abstract class GanderPersistence extends RoomDatabase implements GanderStorage {
    private volatile TransactionDao _transactionDao;
    private static GanderStorage GANDER_DATABASE_INSTANCE;

    public static GanderStorage getInstance(Context context) {
        if (GANDER_DATABASE_INSTANCE == null) {
            GANDER_DATABASE_INSTANCE = Room.databaseBuilder(context, GanderPersistence.class, "GanderDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return GANDER_DATABASE_INSTANCE;
    }

    @Override
    public TransactionDao getTransactionDao() {
        if (_transactionDao != null) {
            return _transactionDao;
        } else {
            synchronized (this) {
                if (_transactionDao == null) {
                    _transactionDao = new PersistentTransactionDao(roomTransactionDao());
                }
                return _transactionDao;
            }
        }
    }

    protected abstract RoomTransactionDao roomTransactionDao();
}
