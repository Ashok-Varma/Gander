package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.GanderStorage;
import com.ashokvarma.gander.internal.data.TransactionDao;

public class GanderIMDB implements GanderStorage {

    private final static TransactionDao IMDB_TRANSACTION_DAO = new IMDBTransactionDao(new SimpleTransactionDataStore(), new TransactionArchComponentProvider(), new TransactionPredicateProvider());

    private final static GanderIMDB INSTANCE = new GanderIMDB();

    private GanderIMDB() {

    }

    public static GanderIMDB getInstance() {
        return INSTANCE;
    }

    @Override
    public TransactionDao getTransactionDao() {
        return IMDB_TRANSACTION_DAO;
    }
}
