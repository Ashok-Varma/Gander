package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.HttpTransaction;

import static org.assertj.core.api.Assertions.assertThat;

class BaseTransactionPredicateProviderTest extends BaseIMDBTestSuite {
    private TransactionPredicateProvider transactionPredicateProvider = new TransactionPredicateProvider();


    ///////////////////////////////////////////////////////////////////////////
    // PredicateProviders
    ///////////////////////////////////////////////////////////////////////////

    PredicateProvider getDefaultPredicateProvider() {
        return new PredicateProvider() {
            @Override
            public Predicate<HttpTransaction> getPredicateToTest(String searchKeyWord) {
                return transactionPredicateProvider.getDefaultSearchPredicate(searchKeyWord);
            }
        };
    }

    PredicateProvider getRequestPredicateProvider() {
        return new PredicateProvider() {
            @Override
            public Predicate<HttpTransaction> getPredicateToTest(String searchKeyWord) {
                return transactionPredicateProvider.getRequestSearchPredicate(searchKeyWord);
            }
        };
    }

    PredicateProvider getResponsePredicateProvider() {
        return new PredicateProvider() {
            @Override
            public Predicate<HttpTransaction> getPredicateToTest(String searchKeyWord) {
                return transactionPredicateProvider.getResponseSearchPredicate(searchKeyWord);
            }
        };
    }

    PredicateProvider getRequestResponsePredicateProvider() {
        return new PredicateProvider() {
            @Override
            public Predicate<HttpTransaction> getPredicateToTest(String searchKeyWord) {
                return transactionPredicateProvider.getRequestResponseSearchPredicate(searchKeyWord);
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // Modifiers
    ///////////////////////////////////////////////////////////////////////////

    HttpTransactionProvider<String> getTransactionProtocolModifier() {
        return new HttpTransactionProvider<String>() {
            @Override
            public HttpTransaction getTransactionForData(String data) {
                return getHttpTransactionWithZeroIndex().toBuilder().setProtocol(data).build();
            }
        };
    }

    HttpTransactionProvider<String> getTransactionMethodModifier() {
        return new HttpTransactionProvider<String>() {
            @Override
            public HttpTransaction getTransactionForData(String data) {
                return getHttpTransactionWithZeroIndex().toBuilder().setMethod(data).build();
            }
        };
    }

    HttpTransactionProvider<String> getTransactionURLModifier() {
        return new HttpTransactionProvider<String>() {
            @Override
            public HttpTransaction getTransactionForData(String data) {
                return getHttpTransactionWithZeroIndex().toBuilder().setUrl(data).build();
            }
        };
    }

    HttpTransactionProvider<Integer> getTransactionResponseCodeModifier() {
        return new HttpTransactionProvider<Integer>() {
            @Override
            public HttpTransaction getTransactionForData(Integer data) {
                return getHttpTransactionWithZeroIndex().toBuilder().setResponseCode(data).build();
            }
        };
    }

    HttpTransactionProvider<String> getTransactionRequestBodyModifier() {
        return new HttpTransactionProvider<String>() {
            @Override
            public HttpTransaction getTransactionForData(String data) {
                return getHttpTransactionWithZeroIndex().toBuilder().setRequestBody(data).build();
            }
        };
    }

    HttpTransactionProvider<String> getTransactionResponseBodyModifier() {
        return new HttpTransactionProvider<String>() {
            @Override
            public HttpTransaction getTransactionForData(String data) {
                return getHttpTransactionWithZeroIndex().toBuilder().setResponseBody(data).build();
            }
        };
    }

    HttpTransactionProvider<String> getTransactionResponseMessageModifier() {
        return new HttpTransactionProvider<String>() {
            @Override
            public HttpTransaction getTransactionForData(String data) {
                return getHttpTransactionWithZeroIndex().toBuilder().setResponseMessage(data).build();
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // ResultArrayProviders
    ///////////////////////////////////////////////////////////////////////////

    <T> ResultArrayProvider<T> getPrefixResultArrayProvider() {
        return new ResultArrayProvider<T>() {
            @Override
            public boolean[] getResultArray(PredicateTester<T> predicateTester) {
                return predicateTester.doesTestKeyWordHasSearchKeyWordAsPrefix;
            }
        };
    }

    ResultArrayProvider<String> getContainsResultArrayProvider() {
        return new ResultArrayProvider<String>() {
            @Override
            public boolean[] getResultArray(PredicateTester<String> predicateTester) {
                return predicateTester.doesTestKeyWordContainsSearchKeyWord;
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test Methods
    ///////////////////////////////////////////////////////////////////////////

    <T> void testPredicate(PredicateTester<T> predicateTester, PredicateProvider predicateProvider, HttpTransactionProvider<T> httpTransactionProvider, ResultArrayProvider<T> resultArrayProvider) {
        Predicate<HttpTransaction> predicate = predicateProvider.getPredicateToTest(predicateTester.searchKeyWord.toString());
        boolean[] resultArray = resultArrayProvider.getResultArray(predicateTester);
        int index = 0;
        for (T testKeyWord : predicateTester.testKeyWords) {
            HttpTransaction httpTransaction = httpTransactionProvider.getTransactionForData(testKeyWord);
            assertThat(predicate.apply(httpTransaction)).isEqualTo(resultArray[index++]);
        }
    }

    class PredicateTester<T> {
        final T searchKeyWord;
        final T[] testKeyWords;
        final boolean[] doesTestKeyWordHasSearchKeyWordAsPrefix;
        final boolean[] doesTestKeyWordContainsSearchKeyWord;

        PredicateTester(T searchKeyWord, T[] testKeyWords, boolean[] doesTestKeyWordHasSearchKeyWordAsPrefix, boolean[] doesTestKeyWordContainsSearchKeyWord) {
            this.searchKeyWord = searchKeyWord;
            this.testKeyWords = testKeyWords;
            this.doesTestKeyWordHasSearchKeyWordAsPrefix = doesTestKeyWordHasSearchKeyWordAsPrefix;
            this.doesTestKeyWordContainsSearchKeyWord = doesTestKeyWordContainsSearchKeyWord;
        }
    }

    interface PredicateProvider {
        Predicate<HttpTransaction> getPredicateToTest(String keyword);
    }

    interface HttpTransactionProvider<T> {
        HttpTransaction getTransactionForData(T data);
    }

    interface ResultArrayProvider<T> {
        boolean[] getResultArray(PredicateTester<T> predicateTester);
    }
}
