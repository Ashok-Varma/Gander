package com.ashokvarma.gander.sample;

import android.app.Application;

import com.ashokvarma.gander.Gander;
import com.ashokvarma.gander.persistence.GanderPersistence;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Gander.setGanderStorage(GanderPersistence.getInstance(this));
    }
}