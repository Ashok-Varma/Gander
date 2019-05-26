package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.HttpTransaction;
import com.ashokvarma.gander.internal.data.TransactionDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class IMDBTransactionDaoTest extends BaseIMDBTestSuite {
    private IMDBTransactionDao transactionDao;
    @Mock
    TransactionDataStore transactionDataStore;
    @Mock
    TransactionArchComponentProvider transactionArchComponentProvider;
    @Mock
    TransactionPredicateProvider transactionPredicateProvider;
    @Mock
    Predicate<HttpTransaction> testPredicate;
    @Captor
    ArgumentCaptor<HttpTransaction> transactionArgumentCaptor;
    @Captor
    ArgumentCaptor<Long> idArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        transactionDao = new IMDBTransactionDao(transactionDataStore, transactionArchComponentProvider, transactionPredicateProvider);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void whenZeroIndexTransaction_isInserted_returnedIndexShouldNotBeZero() {
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isNotZero();

    }

    @Test
    public void whenZeroIndexTransactions_areInserted_thereIdsShouldIncrementByOneStartingFromOneAndShouldReturnTheSame() {
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(1);
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(2);
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(3);
        verifyOnlyTransactionsAddedAreWithIndices(1, 2, 3);
    }

    @Test
    public void whenPositiveIndexTransactionIsInserted_shouldBeAddedAndReturnSameIndex() {
        int index = 10;
        HttpTransaction transaction = getHttpTransactionWithIndex(index);
        assertThat(transactionDao.insertTransaction(transaction)).isEqualTo(10);

        verifyOnlyTransactionAdded(transaction);
    }

    @Test
    public void whenATransactionWithZeroIndex_isInserted_transactionIndexShouldAlwaysIncrementByOneFromMaxIndexOfAllTransactions() {
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(1);//currentMaxIndex = 0;

        int transactionIndex = 22;
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithIndex(transactionIndex))).isEqualTo(22);

        transactionIndex = 2;
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithIndex(transactionIndex))).isEqualTo(2);
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(23);//currentMaxIndex = 22;

        transactionIndex = 200;
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithIndex(transactionIndex))).isEqualTo(200);
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(201);//currentMaxIndex = 200;

        verifyOnlyTransactionsAddedAreWithIndices(1, 22, 2, 23, 200, 201);
    }

    @Test
    public void getTransactionWithId_shouldBeForwardToData() {
        transactionDao.getTransactionWithId(2);
        verify(transactionDataStore).getTransactionWithId(2);
        transactionDao.getTransactionWithId(0);
        verify(transactionDataStore).getTransactionWithId(0);
        transactionDao.getTransactionWithId(-1);
        verify(transactionDataStore).getTransactionWithId(-1);
    }

    @Test
    public void whenZeroIndexTransaction_isUpdated_shouldReturnZero() {
        assertThat(transactionDao.updateTransaction(getHttpTransactionWithIndex(0))).isZero();
    }

    @Test
    public void whenPositiveIndexTransaction_isUpdated_updateShouldBeCalledOnData() {
        HttpTransaction transaction = getHttpTransactionWithIndex(22);

        transactionDao.updateTransaction(transaction);
        verify(transactionDataStore).updateTransaction(transaction);
    }

    @Test
    public void whenNegativeIndexTransaction_isUpdated_shouldReturnZero() {
        assertThat(transactionDao.updateTransaction(getHttpTransactionWithIndex(-1))).isZero();
    }

    @Test
    public void whenZeroAndNegativeTransactions_areDeleted_shouldReturnZero() {
        assertThat(transactionDao.deleteTransactions(getHttpTransactionsWithIndices(0, -1, -40, -100))).isZero();
    }

    @Test
    public void whenTransaction_areDeleted_onlyPositiveIndexShouldBeForwardedToData() {
        transactionDao.deleteTransactions(getHttpTransactionsWithIndices(0, -1, -40, -100, 2, 350));
        verifyOnlyTransactionsDeletedAreWithIndices(2, 350);
    }

    @Test
    public void whenTransactions_areDeleted_shouldReturnTotalNoOfDeletedTransactions() {
        when(transactionDataStore.removeTransactionWithIndex(anyLong())).thenAnswer(
                new Answer<Boolean>() {
                    @Override
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        long a = (Long) invocation.getArguments()[0];
                        return a % 2 == 0;
                    }
                }
        );

        assertThat(transactionDao.deleteTransactions(getHttpTransactionsWithIndices(1, 2, 4, 5, 10, 30, 99))).isEqualTo(4);
    }

    @Test
    public void clearAllCall_shouldBeForwardedToData() {
        transactionDao.clearAll();
        verify(transactionDataStore).clearAllTransactions();
    }

    @Test
    public void whenTransactionsInserted_onClear_indexShouldContinueIncrement() {
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(1);
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(2);
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(3);
        transactionDao.clearAll();
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(4);
        assertThat(transactionDao.insertTransaction(getHttpTransactionWithZeroIndex())).isEqualTo(5);
        verifyOnlyTransactionsAddedAreWithIndices(1, 2, 3, 4, 5);
    }

    @Test
    public void deleteTransactionsBefore_shouldReturnCountOfDeletedTransactions() throws ParseException {
        when(transactionDataStore.removeTransactionWithIndex(anyLong())).thenReturn(true);

        Date einsteinBornTime = getDate("18-Mar-1879 11:30:00");
        Date dateBeforeEinsteinBorn1 = getDate("09-Apr-1869 05:35:58");
        Date dateBeforeEinsteinBorn2 = getDate("22-May-1859 12:00:10");
        Date dateAfterEinsteinBorn1 = getDate("29-Jun-2079 01:09:46");
        Date dateAfterEinsteinBorn2 = getDate("10-Jan-1899 09:57:03");
        Date dateAfterEinsteinBorn3 = getDate("01-Feb-1979 10:10:40");

        List<HttpTransaction> list = new ArrayList<>();
        list.add(getHttpTransactionWithIndexAndDate(1, dateAfterEinsteinBorn1, dateAfterEinsteinBorn3));
        list.add(getHttpTransactionWithIndexAndDate(2, dateAfterEinsteinBorn1, dateBeforeEinsteinBorn2));
        list.add(getHttpTransactionWithIndexAndDate(3, dateBeforeEinsteinBorn1, dateAfterEinsteinBorn2));
        list.add(getHttpTransactionWithIndexAndDate(4, dateBeforeEinsteinBorn2, dateBeforeEinsteinBorn1));
        list.add(getHttpTransactionWithIndexAndDate(5, einsteinBornTime, dateBeforeEinsteinBorn1));

        list.add(getHttpTransactionWithIndex(99));

        when(transactionDataStore.getDataList()).thenReturn(list);

        assertThat(transactionDao.deleteTransactionsBefore(einsteinBornTime)).isEqualTo(2);
    }

    @Test
    public void deleteTransactionsBefore_shouldDeleteTransactionsWithRequestDateBeforeSpecifiedDateAndReturnNoOfDeletedTransactions() throws ParseException {

        Date einsteinBornTime = getDate("18-Mar-1879 11:30:00");
        Date dateBeforeEinsteinBorn1 = getDate("09-Apr-1869 05:35:58");
        Date dateBeforeEinsteinBorn2 = getDate("22-May-1859 12:00:10");
        Date dateAfterEinsteinBorn1 = getDate("29-Jun-2079 01:09:46");
        Date dateAfterEinsteinBorn2 = getDate("10-Jan-1899 09:57:03");
        Date dateAfterEinsteinBorn3 = getDate("01-Feb-1979 10:10:40");

        transactionDao.insertTransaction(getHttpTransactionWithIndexAndDate(1, dateAfterEinsteinBorn1, dateAfterEinsteinBorn3));
        transactionDao.insertTransaction(getHttpTransactionWithIndexAndDate(2, dateAfterEinsteinBorn1, dateBeforeEinsteinBorn2));
        transactionDao.insertTransaction(getHttpTransactionWithIndexAndDate(3, dateBeforeEinsteinBorn1, dateAfterEinsteinBorn2));
        transactionDao.insertTransaction(getHttpTransactionWithIndexAndDate(4, dateBeforeEinsteinBorn2, dateBeforeEinsteinBorn1));
        transactionDao.insertTransaction(getHttpTransactionWithIndexAndDate(5, einsteinBornTime, dateBeforeEinsteinBorn1));

        verify(transactionDataStore, times(5)).addTransaction(transactionArgumentCaptor.capture());
        when(transactionDataStore.getDataList()).thenReturn(transactionArgumentCaptor.getAllValues());

        transactionDao.deleteTransactionsBefore(einsteinBornTime);

        verifyOnlyTransactionsDeletedAreWithIndices(3, 4);
    }

    @Test
    public void getAllTransactions_shouldCallArchComponentProviderWithPredicateAll() {
        transactionDao.getAllTransactions();

        verify(transactionArchComponentProvider).getDataSourceFactory(eq(transactionDataStore), eq(Predicate.ALLOW_ALL));
    }

    @Test
    public void getTransactionsWithId_shouldCallArchComponentProviderWithRequestedTransactionId() {
        transactionDao.getTransactionsWithId(10L);

        verify(transactionArchComponentProvider).getLiveData(eq(transactionDataStore), eq(10L));
    }

    @Test
    public void getAllTransactionsWithDefault_shouldCallArchComponentProviderWithRequestedSearchTermAndDefaultSearchPredicate() {
        when(transactionPredicateProvider.getDefaultSearchPredicate("test")).thenReturn(testPredicate);

        transactionDao.getAllTransactionsWith("test", TransactionDao.SearchType.DEFAULT);
        verify(transactionPredicateProvider).getDefaultSearchPredicate("test");
        verify(transactionArchComponentProvider).getDataSourceFactory(eq(transactionDataStore), eq(testPredicate));
    }

    @Test
    public void getAllTransactionsWithRequest_shouldCallArchComponentProviderWithRequestedSearchTermAndRequestSearchPredicate() {
        when(transactionPredicateProvider.getRequestSearchPredicate("test")).thenReturn(testPredicate);

        transactionDao.getAllTransactionsWith("test", TransactionDao.SearchType.INCLUDE_REQUEST);
        verify(transactionPredicateProvider).getRequestSearchPredicate("test");
        verify(transactionArchComponentProvider).getDataSourceFactory(eq(transactionDataStore), eq(testPredicate));
    }

    @Test
    public void getAllTransactionsWithResponse_shouldCallArchComponentProviderWithRequestedSearchTermAndResponseSearchPredicate() {
        when(transactionPredicateProvider.getResponseSearchPredicate("test")).thenReturn(testPredicate);

        transactionDao.getAllTransactionsWith("test", TransactionDao.SearchType.INCLUDE_RESPONSE);
        verify(transactionPredicateProvider).getResponseSearchPredicate("test");
        verify(transactionArchComponentProvider).getDataSourceFactory(eq(transactionDataStore), eq(testPredicate));
    }

    @Test
    public void getAllTransactionsWithRequestResponse_shouldCallArchComponentProviderWithRequestedSearchTermAndRequestResponseSearchPredicate() {
        when(transactionPredicateProvider.getRequestResponseSearchPredicate("test")).thenReturn(testPredicate);

        transactionDao.getAllTransactionsWith("test", TransactionDao.SearchType.INCLUDE_REQUEST_RESPONSE);
        verify(transactionPredicateProvider).getRequestResponseSearchPredicate("test");
        verify(transactionArchComponentProvider).getDataSourceFactory(eq(transactionDataStore), eq(testPredicate));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////////////////////////

    private void verifyOnlyTransactionAdded(HttpTransaction transaction) {
        verify(transactionDataStore).addTransaction(transaction);
    }

    private void verifyOnlyTransactionsAddedAreWithIndices(long... indices) {
        verify(transactionDataStore, times(indices.length)).addTransaction(transactionArgumentCaptor.capture());

        List<HttpTransaction> capturedTransactions = transactionArgumentCaptor.getAllValues();
        int i = 0;
        for (HttpTransaction httpTransaction : capturedTransactions) {
            assertThat(httpTransaction.getId()).isEqualTo(indices[i++]);
        }
    }

    private void verifyOnlyTransactionsDeletedAreWithIndices(long... indices) {
        verify(transactionDataStore, times(indices.length)).removeTransactionWithIndex(idArgumentCaptor.capture());

        List<Long> capturedTransactions = idArgumentCaptor.getAllValues();
        int i = 0;
        for (Long httpTransactionIds : capturedTransactions) {
            assertThat(httpTransactionIds).isEqualTo(indices[i++]);
        }
    }
}