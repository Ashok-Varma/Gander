package com.ashokvarma.gander.internal.support.event;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 07/06/18
 */
public interface Callback<T> {
    void onEmit(T event);
}