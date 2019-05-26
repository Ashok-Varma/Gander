package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.HttpTransaction;

import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;

public class SimpleTransactionDataStoreTest extends BaseIMDBTestSuite {
    private SimpleTransactionDataStore simpleTransactionDataStore;
    @Mock
    TransactionDataStore.DataChangeListener dataChangeListener;
    @Mock
    TransactionDataStore.DataChangeListener dataChangeListener2;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        simpleTransactionDataStore = new SimpleTransactionDataStore();
        simpleTransactionDataStore.addDataChangeListener(dataChangeListener);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void onAddTransactionWithZeroTransactionId_shouldThrowIllegalStateException() {
        assertThatExceptionOfType(TransactionDataStore.ZeroIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.addTransaction(getHttpTransactionWithZeroIndex());
                    }
                })
                .withNoCause();
    }

    @Test
    public void onAddTransactionWithNegativeTransactionId_shouldThrowIllegalStateException() {
        assertThatExceptionOfType(TransactionDataStore.NegativeIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(-1));
                    }
                })
                .withNoCause();
    }

    @Test
    public void onAddTransaction_shouldCallListener() {
        HttpTransaction httpTransaction = getHttpTransactionWithIndex(1);
        simpleTransactionDataStore.addTransaction(httpTransaction);
        verify(dataChangeListener, only()).onDataChange(TransactionDataStore.Event.ADDED, httpTransaction);
    }

    @Test
    public void dataChangeEvents_mustBeSentToAllObservers() {
        HttpTransaction httpTransaction = getHttpTransactionWithIndex(1);

        simpleTransactionDataStore.addDataChangeListener(dataChangeListener2);
        simpleTransactionDataStore.addTransaction(httpTransaction);

        verify(dataChangeListener2, only()).onDataChange(TransactionDataStore.Event.ADDED, httpTransaction);
        verify(dataChangeListener, only()).onDataChange(TransactionDataStore.Event.ADDED, httpTransaction);
    }

    @Test
    public void dataChangeEvents_mustNotBeSentToRemovedObservers() {
        HttpTransaction httpTransaction = getHttpTransactionWithIndex(1);

        simpleTransactionDataStore.removeDataChangeListener(dataChangeListener);
        simpleTransactionDataStore.addDataChangeListener(dataChangeListener2);
        simpleTransactionDataStore.addTransaction(httpTransaction);

        verify(dataChangeListener2, only()).onDataChange(TransactionDataStore.Event.ADDED, httpTransaction);
        verify(dataChangeListener, never()).onDataChange(any(TransactionDataStore.Event.class), any(HttpTransaction.class));
    }

    @Test
    public void onUpdateTransactionWithZeroTransactionId_shouldThrowZeroIndexException() {
        assertThatExceptionOfType(TransactionDataStore.ZeroIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.updateTransaction(getHttpTransactionWithZeroIndex());
                    }
                })
                .withNoCause();
    }

    @Test
    public void onUpdateTransactionWithNegativeTransactionId_shouldThrowNegativeIndexException() {
        assertThatExceptionOfType(TransactionDataStore.NegativeIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.updateTransaction(getHttpTransactionWithIndex(-1));
                    }
                })
                .withNoCause();
    }

    @Test
    public void onUpdateTransactionWithNonExistingTransactionId_shouldReturnFalse() {
        assertThat(simpleTransactionDataStore.updateTransaction(getHttpTransactionWithIndex(23))).isFalse();
        assertThat(simpleTransactionDataStore.updateTransaction(getHttpTransactionWithIndex(200))).isFalse();
    }

    @Test
    public void onUpdateTransactionWithExistingTransactionId_shouldReturnTrue() {
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(23));
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(200));

        assertThat(simpleTransactionDataStore.updateTransaction(getHttpTransactionWithIndex(23))).isTrue();
        assertThat(simpleTransactionDataStore.updateTransaction(getHttpTransactionWithIndex(200))).isTrue();
    }

    @Test
    public void onUpdateTransaction_shouldCallListener() {
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(1));
        HttpTransaction updatedHttpTransaction = getHttpTransactionWithIndex(1);
        simpleTransactionDataStore.updateTransaction(updatedHttpTransaction);
        verify(dataChangeListener, times(1)).onDataChange(TransactionDataStore.Event.UPDATED, updatedHttpTransaction);
    }

    @Test
    public void onNonExistingTransactionUpdate_shouldNotCallListener() {
        simpleTransactionDataStore.updateTransaction(getHttpTransactionWithIndex(1));
        verify(dataChangeListener, never()).onDataChange(eq(TransactionDataStore.Event.UPDATED), any(HttpTransaction.class));
    }

    @Test
    public void onRemoveTransactionWithZeroTransactionId_shouldThrowZeroIndexException() {
        assertThatExceptionOfType(TransactionDataStore.ZeroIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.removeTransactionWithIndex(0);
                    }
                })
                .withNoCause();
    }

    @Test
    public void onRemoveTransactionWithNegativeTransactionId_shouldThrowNegativeIndexException() {
        assertThatExceptionOfType(TransactionDataStore.NegativeIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.removeTransactionWithIndex(-1);
                    }
                })
                .withNoCause();
    }

    @Test
    public void onDeleteTransactionWithNonExistingTransactionId_shouldReturnFalse() {
        assertThat(simpleTransactionDataStore.removeTransactionWithIndex(23)).isFalse();
        assertThat(simpleTransactionDataStore.removeTransactionWithIndex(200)).isFalse();
    }

    @Test
    public void onDeleteTransactionWithExistingTransactionId_shouldReturnTrue() {
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(23));
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(200));

        assertThat(simpleTransactionDataStore.removeTransactionWithIndex(23)).isTrue();
        assertThat(simpleTransactionDataStore.removeTransactionWithIndex(200)).isTrue();
    }

    @Test
    public void onDeleteTransaction_shouldCallListener() {
        HttpTransaction transaction = getHttpTransactionWithIndex(1);
        simpleTransactionDataStore.addTransaction(transaction);
        simpleTransactionDataStore.removeTransactionWithIndex(1);
        verify(dataChangeListener, times(1)).onDataChange(TransactionDataStore.Event.DELETED, transaction);
    }

    @Test
    public void onNonExistingTransactionDelete_shouldNotCallListener() {
        simpleTransactionDataStore.removeTransactionWithIndex(1);
        verify(dataChangeListener, never()).onDataChange(eq(TransactionDataStore.Event.DELETED), any(HttpTransaction.class));
    }

    @Test
    public void onFetchTransactionWithZeroTransactionId_shouldThrowZeroIndexException() {
        assertThatExceptionOfType(TransactionDataStore.ZeroIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.getTransactionWithId(0);
                    }
                })
                .withNoCause();
    }

    @Test
    public void onFetchTransactionWithNegativeTransactionId_shouldThrowNegativeIndexException() {
        assertThatExceptionOfType(TransactionDataStore.NegativeIndexException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.getTransactionWithId(-1);
                    }
                })
                .withNoCause();
    }

    @Test
    public void onFetch_shouldReturnListOfAllAddedTransactions() {
        HttpTransaction[] httpTransactions = getHttpTransactionsWithIndices(1, 2, 3);
        for (HttpTransaction transaction : httpTransactions) {
            simpleTransactionDataStore.addTransaction(transaction);
        }

        assertThat(simpleTransactionDataStore.getDataList()).containsOnly(httpTransactions);
    }

    @Test
    public void whenPositiveIndexTransactionIsInserted_onFetch_shouldReturnTheTransaction() {
        int index = 10;
        HttpTransaction transaction = getHttpTransactionWithIndex(index);
        simpleTransactionDataStore.addTransaction(transaction);
        assertThat(simpleTransactionDataStore.getTransactionWithId(index)).isEqualTo(transaction);
    }


    @Test
    public void whenPositiveIndexTransactionIsInsertedAndDeleted_onFetch_shouldThrowIndexNotFoundException() {
        int index = 10;
        HttpTransaction httpTransaction = getHttpTransactionWithIndex(index);
        simpleTransactionDataStore.addTransaction(httpTransaction);
        simpleTransactionDataStore.removeTransactionWithIndex(index);

        verifyTransactionDoesNotExistsWithId(index);
    }

    @Test
    public void whenPositiveIndexTransactionIsInsertedAndUpdated_onFetch_shouldReturnUpdatedTransaction() {
        int index = 10;
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(index));

        HttpTransaction transaction = getHttpTransactionWithIndex(index);
        simpleTransactionDataStore.updateTransaction(transaction);
        assertThat(simpleTransactionDataStore.getTransactionWithId(index)).isEqualTo(transaction);
    }


    @Test
    public void whenTransactionsInserted_onClear_shouldReturnInsertedTransactionCount() {
        int existingIndex1 = 1;
        int existingIndex2 = 30;
        int existingIndex3 = 5000;
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(existingIndex1));
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(existingIndex2));
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(existingIndex3));
        assertThat(simpleTransactionDataStore.clearAllTransactions()).isEqualTo(3);
    }


    @Test
    public void whenTransactionsInserted_onClear_shouldDeleteAllTransactions() {
        int existingIndex1 = 1;
        int existingIndex2 = 30;
        int existingIndex3 = 5000;

        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(existingIndex1));
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(existingIndex2));
        simpleTransactionDataStore.addTransaction(getHttpTransactionWithIndex(existingIndex3));

        simpleTransactionDataStore.clearAllTransactions();
        verifyTransactionDoesNotExistsWithId(existingIndex1);
        verifyTransactionDoesNotExistsWithId(existingIndex2);
        verifyTransactionDoesNotExistsWithId(existingIndex3);
    }

    private void verifyTransactionDoesNotExistsWithId(final long index) {
        assertThatExceptionOfType(TransactionDataStore.IndexDoesNotExistException.class)
                .isThrownBy(new ThrowableAssert.ThrowingCallable() {
                    @Override
                    public void call() {
                        simpleTransactionDataStore.getTransactionWithId(index);
                    }
                })
                .withNoCause();
    }
}