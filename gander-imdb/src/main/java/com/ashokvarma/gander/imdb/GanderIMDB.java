package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.GanderStorage;
import com.ashokvarma.gander.internal.data.TransactionDao;

public class GanderIMDB implements GanderStorage {

    private final static TransactionDao IMDB_TRANSACTION_DAO = new IMDBTransactionDao();

    @Override
    public TransactionDao getTransactionDao() {
        return IMDB_TRANSACTION_DAO;
    }
}
