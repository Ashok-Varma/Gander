package com.ashokvarma.gander.internal.support.event;


import android.os.Handler;

import androidx.annotation.NonNull;

/**
 * Class description :
 * Batches all events in given interval and emits final item at the end of interval
 *
 * @author ashok
 * @version 1.0
 * @since 04/06/18
 */
public class Sampler<V> {

    private final int mInterval;
    private final Callback<V> mCallback;
    private final Handler mHandler;

    private Counter<V> currentRunnable;

    public Sampler(int intervalInMills, @NonNull Callback<V> callback) {
        mInterval = intervalInMills;
        mCallback = callback;
        mHandler = new Handler();
    }

    public void consume(V event) {
        if (currentRunnable == null) {
            // first runnable
            currentRunnable = new Counter<>(event, mCallback);
            mHandler.postDelayed(currentRunnable, mInterval);
        } else {
            if (currentRunnable.state == Counter.STATE_CREATED || currentRunnable.state == Counter.STATE_QUEUED) {
                //  yet to emit (with in an interval)
                currentRunnable.updateEvent(event);
            } else if (currentRunnable.state == Counter.STATE_RUNNING || currentRunnable.state == Counter.STATE_FINISHED) {
                // interval finished. open new batch
                currentRunnable = new Counter<>(event, mCallback);
                mHandler.postDelayed(currentRunnable, mInterval);
            }
        }
    }

    private static class Counter<T> implements Runnable {
        private T mEvent;
        private final Callback<T> mCallback;

        static final int STATE_CREATED = 1;
        static final int STATE_QUEUED = 2;
        static final int STATE_RUNNING = 3;
        static final int STATE_FINISHED = 4;
        int state;

        Counter(T event, Callback<T> callback) {
            mEvent = event;
            mCallback = callback;
            state = STATE_CREATED;
        }

        void updateEvent(T deliverable) {
            this.mEvent = deliverable;
        }

        @Override
        public void run() {
            state = STATE_RUNNING;
            mCallback.onEmit(mEvent);
            state = STATE_FINISHED;
        }
    }


}
