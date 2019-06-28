package com.ashokvarma.gander.sample;

import android.app.Application;
import android.os.StrictMode;

import com.ashokvarma.gander.Gander;
import com.ashokvarma.gander.persistence.GanderPersistence;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());

        Gander.setGanderStorage(GanderPersistence.getInstance(this));
//        Gander.setGanderStorage(GanderIMDB.getInstance());
    }
}
