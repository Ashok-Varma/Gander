package com.ashokvarma.gander.internal.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 03/06/18
 */
@Database(entities = {HttpTransaction.class}, version = 1, exportSchema = false)
@TypeConverters({RoomTypeConverters.class})

public abstract class GanderDatabase extends RoomDatabase {
    public abstract TransactionDao httpTransactionDao();

    private static GanderDatabase GANDER_DATABASE_INSTANCE;

    public static GanderDatabase getInstance(Context context) {
        if (GANDER_DATABASE_INSTANCE == null) {
            GANDER_DATABASE_INSTANCE = Room.databaseBuilder(context, GanderDatabase.class, "GanderDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return GANDER_DATABASE_INSTANCE;
    }
}