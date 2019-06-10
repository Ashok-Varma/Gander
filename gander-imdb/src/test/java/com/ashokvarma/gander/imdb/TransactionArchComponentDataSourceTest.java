package com.ashokvarma.gander.imdb;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import com.ashokvarma.gander.internal.data.HttpTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionArchComponentDataSourceTest extends BaseIMDBTestSuite {

    @Mock
    TransactionDataStore transactionDataStore;
    @Mock
    DataSource.InvalidatedCallback invalidatedCallback;
    @Mock
    Predicate<HttpTransaction> filter;
    @Mock
    PositionalDataSource.LoadInitialCallback<HttpTransaction> loadInitialCallback;
    @Mock
    PositionalDataSource.LoadRangeCallback<HttpTransaction> loadRangeCallback;
    @Captor
    ArgumentCaptor<List<HttpTransaction>> dataCapture;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private TransactionDataStore.DataChangeListener dataChangeListener;
    private PositionalDataSource<HttpTransaction> httpTransactionDataSource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return dataChangeListener = invocation.getArgument(0);
            }
        }).when(transactionDataStore).addDataChangeListener(any(TransactionDataStore.DataChangeListener.class));
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }


    @Test
    public void whenNoTransactions_onLoadInitialCall_shouldReturnNoData() {
        when(filter.apply(any(HttpTransaction.class))).thenReturn(true);
        when(transactionDataStore.getDataList()).thenReturn(Collections.<HttpTransaction>emptyList());

        setUpDataSource();

        PositionalDataSource.LoadInitialParams params = new PositionalDataSource.LoadInitialParams(0, 10, 5, false);
        httpTransactionDataSource.loadInitial(params, loadInitialCallback);
        verify(loadInitialCallback).onResult(dataCapture.capture(), eq(0), eq(0));
        assertThat(dataCapture.getValue()).isEmpty();
    }

    private void setUpDataSource() {
        // This is how recycler views does it casting to PositionalDataSource. need to change to better api
        httpTransactionDataSource
                = (PositionalDataSource<HttpTransaction>) new TransactionArchComponentProvider()
                .getDataSourceFactory(transactionDataStore, filter)
                .create();
        httpTransactionDataSource.addInvalidatedCallback(invalidatedCallback);
    }


    @Test
    public void whenNoTransactionsMatchFilter_onLoadInitialCall_shouldReturnNoData() {
        when(filter.apply(any(HttpTransaction.class))).thenReturn(false);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(getHttpTransactionsWithIndices(0, 1, 2, 3)));

        setUpDataSource();

        PositionalDataSource.LoadInitialParams params = new PositionalDataSource.LoadInitialParams(0, 10, 5, false);
        httpTransactionDataSource.loadInitial(params, loadInitialCallback);
        verify(loadInitialCallback).onResult(dataCapture.capture(), eq(0), eq(0));
        assertThat(dataCapture.getValue()).isEmpty();
    }


    @Test
    public void whenTransactionsExist_onLoadInitialCall_shouldReturnDataInOrderThatMatchFilter() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        PositionalDataSource.LoadInitialParams params = new PositionalDataSource.LoadInitialParams(0, 10, 5, false);
        httpTransactionDataSource.loadInitial(params, loadInitialCallback);
        verify(loadInitialCallback).onResult(dataCapture.capture(), eq(0), eq(2));
        assertThat(dataCapture.getValue()).containsExactlyInAnyOrder(transaction1, transaction3);
    }


    @Test
    public void whenTransactionsExist_onLoadRange_shouldReturnDataThatMatchFilterInOrderInSpecifiedRange() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        HttpTransaction transaction5 = getHttpTransactionWithIndex(4);
        HttpTransaction transaction6 = getHttpTransactionWithIndex(5);
        HttpTransaction transaction7 = getHttpTransactionWithIndex(6);
        HttpTransaction transaction8 = getHttpTransactionWithIndex(7);
        HttpTransaction transaction9 = getHttpTransactionWithIndex(8);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4, transaction5, transaction6, transaction7, transaction8, transaction9}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        PositionalDataSource.LoadRangeParams params = new PositionalDataSource.LoadRangeParams(1, 3);
        httpTransactionDataSource.loadRange(params, loadRangeCallback);
        verify(loadRangeCallback).onResult(dataCapture.capture());
        assertThat(dataCapture.getValue()).containsExactlyInAnyOrder(transaction3, transaction5, transaction7);
    }

    @Test
    public void whenTransactionAddedMatchingFilter_verifyInvalidateCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        HttpTransaction newTransaction1 = getHttpTransactionWithIndex(4);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4, newTransaction1}));
        dataChangeListener.onDataChange(TransactionDataStore.Event.ADDED, newTransaction1);

        verify(invalidatedCallback).onInvalidated();
    }

    @Test
    public void whenTransactionAddedNotMatchingFilter_verifyInvalidateNeverCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        HttpTransaction newTransaction1 = getHttpTransactionWithIndex(5);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4, newTransaction1}));
        dataChangeListener.onDataChange(TransactionDataStore.Event.ADDED, newTransaction1);

        verify(invalidatedCallback, never()).onInvalidated();
    }

    @Test
    public void whenExistingTransactionMatchingFilterDeleted_verifyInvalidateCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.DELETED, transaction1);

        verify(invalidatedCallback).onInvalidated();
    }

    @Test
    public void whenExistingTransactionNotMatchingFilterDeleted_verifyInvalidateNeverCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.DELETED, transaction2);

        verify(invalidatedCallback, never()).onInvalidated();
    }

    @Test
    public void whenNonExistingTransactionMatchingFilterDeleted_verifyInvalidateNeverCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.DELETED, getHttpTransactionWithIndex(4));

        verify(invalidatedCallback, never()).onInvalidated();
    }

    @Test
    public void whenNonExistingTransactionNotMatchingFilterDeleted_verifyNeverInvalidateCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.DELETED, getHttpTransactionWithIndex(5));

        verify(invalidatedCallback, never()).onInvalidated();
    }


    @Test
    public void whenExistingTransactionMatchingFilterUpdated_verifyInvalidateCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, transaction1);

        verify(invalidatedCallback).onInvalidated();
    }

    @Test
    public void whenExistingTransactionNotMatchingFilterUpdated_verifyInvalidateNeverCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, transaction2);

        verify(invalidatedCallback, never()).onInvalidated();
    }

    @Test
    public void whenNonExistingTransactionMatchingFilterUpdated_verifyInvalidateCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, getHttpTransactionWithIndex(4));

        verify(invalidatedCallback).onInvalidated();
    }

    @Test
    public void whenNonExistingTransactionNotMatchingFilterUpdated_verifyNeverInvalidateCalled() {
        HttpTransaction transaction1 = getHttpTransactionWithIndex(0);
        HttpTransaction transaction2 = getHttpTransactionWithIndex(1);
        HttpTransaction transaction3 = getHttpTransactionWithIndex(2);
        HttpTransaction transaction4 = getHttpTransactionWithIndex(3);
        when(transactionDataStore.getDataList()).thenReturn(Arrays.asList(new HttpTransaction[]{transaction1, transaction2, transaction3, transaction4}));
        when(filter.apply(any(HttpTransaction.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                HttpTransaction httpTransaction = invocation.getArgument(0);
                return httpTransaction.getId() % 2 == 0;
            }
        });

        setUpDataSource();

        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, getHttpTransactionWithIndex(5));

        verify(invalidatedCallback, never()).onInvalidated();
    }
}
