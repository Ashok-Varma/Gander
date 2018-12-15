package com.ashokvarma.gander;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 02/06/18
 */
public class GanderInterceptor implements Interceptor {

    public enum Period {
        /**
         * Retain data for the last hour.
         */
        ONE_HOUR,
        /**
         * Retain data for the last day.
         */
        ONE_DAY,
        /**
         * Retain data for the last week.
         */
        ONE_WEEK,
        /**
         * Retain data forever.
         */
        FOREVER
    }


    /**
     * @param context The current Context.
     */
    public GanderInterceptor(Context context, boolean showNotification) {
    }

    /**
     * Set the maximum length for request and response content before it is truncated.
     * Warning: setting this value too high may cause unexpected results.
     *
     * @param max the maximum length (in bytes) for request/response content.
     * @return The {@link GanderInterceptor} instance.
     */
    public GanderInterceptor maxContentLength(long max) {
        return this;
    }

    /**
     * Set the retention period for HTTP transaction data captured by this interceptor.
     * The default is one week.
     *
     * @param period the peroid for which to retain HTTP transaction data.
     * @return The {@link GanderInterceptor} instance.
     */
    public GanderInterceptor retainDataFor(Period period) {
        return this;
    }

    public GanderInterceptor redactHeader(String name) { return this; }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request());
    }
}
