package com.ashokvarma.gander.imdb;

import com.ashokvarma.gander.internal.data.HttpTransaction;

class TransactionPredicateProvider {
    TransactionPredicateProvider() {
    }

    Predicate<HttpTransaction> getDefaultSearchPredicate(String searchKeyWord) {
        return new DefaultSearchPredicate(searchKeyWord);
    }

    Predicate<HttpTransaction> getRequestSearchPredicate(String searchKeyWord) {
        return new RequestSearchPredicate(searchKeyWord);
    }

    Predicate<HttpTransaction> getResponseSearchPredicate(String searchKeyWord) {
        return new ResponseSearchPredicate(searchKeyWord);
    }

    Predicate<HttpTransaction> getRequestResponseSearchPredicate(String searchKeyWord) {
        return new RequestResponseSearchPredicate(searchKeyWord);
    }

    private static class DefaultSearchPredicate implements Predicate<HttpTransaction> {

        String searchKeyWord;

        private DefaultSearchPredicate(String searchKeyWord) {
            this.searchKeyWord = searchKeyWord;
        }

        @Override
        public boolean apply(HttpTransaction httpTransaction) {
            if (httpTransaction.getProtocol() != null && httpTransaction.getProtocol().toLowerCase().startsWith(searchKeyWord.toLowerCase())) {
                return true;
            } else if (httpTransaction.getMethod() != null && httpTransaction.getMethod().toLowerCase().startsWith(searchKeyWord.toLowerCase())) {
                return true;
            } else if (httpTransaction.getUrl() != null && httpTransaction.getUrl().toLowerCase().contains(searchKeyWord.toLowerCase())) {
                return true;
            } else
                return httpTransaction.getResponseCode() != null && httpTransaction.getResponseCode().toString().startsWith(searchKeyWord);
        }
    }

    private static class RequestSearchPredicate extends DefaultSearchPredicate {

        private RequestSearchPredicate(String searchKeyWord) {
            super(searchKeyWord);
        }

        @Override
        public boolean apply(HttpTransaction httpTransaction) {
            return super.apply(httpTransaction)
                    || (httpTransaction.getRequestBody() != null && httpTransaction.getRequestBody().toLowerCase().contains(searchKeyWord.toLowerCase()));
        }
    }

    private static class ResponseSearchPredicate extends DefaultSearchPredicate {

        private ResponseSearchPredicate(String searchKeyWord) {
            super(searchKeyWord);
        }

        @Override
        public boolean apply(HttpTransaction httpTransaction) {
            return super.apply(httpTransaction)
                    || (httpTransaction.getResponseBody() != null && httpTransaction.getResponseBody().toLowerCase().contains(searchKeyWord.toLowerCase()))
                    || (httpTransaction.getResponseMessage() != null && httpTransaction.getResponseMessage().toLowerCase().contains(searchKeyWord.toLowerCase()));
        }
    }

    private static class RequestResponseSearchPredicate extends DefaultSearchPredicate {

        private RequestResponseSearchPredicate(String searchKeyWord) {
            super(searchKeyWord);
        }

        @Override
        public boolean apply(HttpTransaction httpTransaction) {
            return super.apply(httpTransaction)
                    || (httpTransaction.getResponseBody() != null && httpTransaction.getResponseBody().toLowerCase().contains(searchKeyWord.toLowerCase()))
                    || (httpTransaction.getResponseMessage() != null && httpTransaction.getResponseMessage().toLowerCase().contains(searchKeyWord.toLowerCase()))
                    || (httpTransaction.getRequestBody() != null && httpTransaction.getRequestBody().toLowerCase().contains(searchKeyWord.toLowerCase()));
        }
    }
}
