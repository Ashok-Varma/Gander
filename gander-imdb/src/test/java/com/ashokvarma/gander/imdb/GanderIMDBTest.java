package com.ashokvarma.gander.imdb;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class GanderIMDBTest {

    @Test
    public void getInstance_shouldReturnSingleton() {
        assertThat(GanderIMDB.getInstance()).isEqualTo(GanderIMDB.getInstance());
    }

    @Test
    public void getTransactionDao_shouldReturnSingleton() {
        assertThat(GanderIMDB.getInstance().getTransactionDao()).isEqualTo(GanderIMDB.getInstance().getTransactionDao());
    }
}