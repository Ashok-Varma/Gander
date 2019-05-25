package com.ashokvarma.gander.internal.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

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
