package com.ashokvarma.gander.imdb;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.ashokvarma.gander.internal.data.HttpTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionArchComponentLiveDataTest extends BaseIMDBTestSuite {

    @Mock
    TransactionDataStore transactionDataStore;

    @Mock
    Observer<HttpTransaction> testObserver;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private LiveData<HttpTransaction> httpTransactionLiveData;
    private LifecycleRegistry lifecycleRegistry;
    private TransactionDataStore.DataChangeListener dataChangeListener;
    private HttpTransaction httpTransactionWithId1;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return dataChangeListener = invocation.getArgument(0);
            }
        }).when(transactionDataStore).addDataChangeListener(any(TransactionDataStore.DataChangeListener.class));


        httpTransactionWithId1 = getHttpTransactionWithIndex(1);
        when(transactionDataStore.getTransactionWithId(1)).thenReturn(httpTransactionWithId1);

        httpTransactionLiveData = new TransactionArchComponentProvider().getLiveData(transactionDataStore, 1);
        LifecycleOwner lifecycleOwner = mock(LifecycleOwner.class);
        lifecycleRegistry = new LifecycleRegistry(lifecycleOwner);
        when(lifecycleOwner.getLifecycle()).thenReturn(lifecycleRegistry);
        httpTransactionLiveData.observe(lifecycleOwner, testObserver);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void whenLifeCycleIsNotActive_DataShouldNeverBeUpdated() {
        verify(testObserver, never()).onChanged(any(HttpTransaction.class));
    }

    @Test
    public void whenLifeCycleIsNotActiveAndDataIsChanged_DataShouldNeverBeUpdated() {
        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, httpTransactionWithId1);
        verify(testObserver, never()).onChanged(any(HttpTransaction.class));
    }

    @Test
    public void whenLifeCycleIsActive_DataShouldBeUpdated() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        verify(testObserver, times(1)).onChanged(httpTransactionWithId1);
    }

    @Test
    public void whenLifeCycleIsActiveAndDataIsChanged_DataShouldBeUpdated() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);


        HttpTransaction updatedHttpTransactionWithId1 = getHttpTransactionWithIndex(1);
        when(transactionDataStore.getTransactionWithId(1)).thenReturn(updatedHttpTransactionWithId1);
        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, updatedHttpTransactionWithId1);

        verify(testObserver, times(1)).onChanged(httpTransactionWithId1);
        verify(testObserver, times(1)).onChanged(updatedHttpTransactionWithId1);
    }

    @Test
    public void whenLifeCycleIsActiveAndOtherTransactionDataIsChanged_DataShouldNotBeUpdated() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, getHttpTransactionWithIndex(2));

        verify(testObserver, times(1)).onChanged(httpTransactionWithId1);
    }

    @Test
    public void whenLifeCycleWentFromActiveToInactiveAndDataIsChanged_DataShouldNeverBeUpdated() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);


        HttpTransaction updatedHttpTransactionWithId1 = getHttpTransactionWithIndex(1);
        when(transactionDataStore.getTransactionWithId(1)).thenReturn(updatedHttpTransactionWithId1);
        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, updatedHttpTransactionWithId1);

        verify(testObserver, times(1)).onChanged(httpTransactionWithId1);
        verify(testObserver, never()).onChanged(updatedHttpTransactionWithId1);
    }

    @Test
    public void whenLifeCycleIsNotActiveAndDataIsChanged_onLifeCycleBecomingActiveUpdatedDataNeedsToBeSentToObserver() {
        HttpTransaction updatedHttpTransactionWithId1 = getHttpTransactionWithIndex(1);
        when(transactionDataStore.getTransactionWithId(1)).thenReturn(updatedHttpTransactionWithId1);

        dataChangeListener.onDataChange(TransactionDataStore.Event.UPDATED, updatedHttpTransactionWithId1);

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        verify(testObserver, times(1)).onChanged(updatedHttpTransactionWithId1);
    }

}
