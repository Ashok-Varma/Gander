package com.ashokvarma.gander.persistence;

import android.content.Context;

import com.ashokvarma.gander.internal.data.GanderStorage;

public class GanderPersistence {

    public static GanderStorage getInstance(Context context) {
        return new GanderStorage() {
        };
    }
}
