package com.ashokvarma.gander.imdb;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionPredicateProviderTest extends BaseTransactionPredicateProviderTest {

    private PredicateTester<String> stringPredicateTester;
    private PredicateTester<Integer> intPredicateTester;

    @Before
    public void setUp() throws Exception {
        stringPredicateTester = new PredicateTester<>(
                "test",
                new String[]{null, "test", "prefixtest", "testsuffix", "prefixtestsuffix", "random"},
                new boolean[]{false, true, false, true, false, false},
                new boolean[]{false, true, true, true, true, false}
        );

        intPredicateTester = new PredicateTester<>(
                101,
                new Integer[]{null, 101, 2101, 1012, 21012, 12345},
                new boolean[]{false, true, false, true, false, false},
                new boolean[]{false, true, true, true, true, false}
        );
    }

    @Test
    public void allowTrue_shouldAlwaysReturnTrue() {
        assertThat(Predicate.ALLOW_ALL.apply(null)).isTrue();
        assertThat(Predicate.ALLOW_ALL.apply("1")).isTrue();
        assertThat(Predicate.ALLOW_ALL.apply(1)).isTrue();
        assertThat(Predicate.ALLOW_ALL.apply(1L)).isTrue();
        assertThat(Predicate.ALLOW_ALL.apply(1.34F)).isTrue();
        assertThat(Predicate.ALLOW_ALL.apply(1.3412D)).isTrue();
    }

    @Test
    public void defaultPredicate_shouldSearchProtocolThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getDefaultPredicateProvider(),
                getTransactionProtocolModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }


    @Test
    public void defaultPredicate_shouldSearchMethodThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getDefaultPredicateProvider(),
                getTransactionMethodModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }

    @Test
    public void defaultPredicate_shouldSearchURLThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getDefaultPredicateProvider(),
                getTransactionURLModifier(),
                getContainsResultArrayProvider()
        );
    }


    @Test
    public void defaultPredicate_shouldSearchResponseCodeThatStartWithSearchWord() {
        testPredicate(
                intPredicateTester,
                getDefaultPredicateProvider(),
                getTransactionResponseCodeModifier(),
                this.<Integer>getPrefixResultArrayProvider()
        );
    }

    @Test
    public void requestPredicate_shouldSearchProtocolThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestPredicateProvider(),
                getTransactionProtocolModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }


    @Test
    public void requestPredicate_shouldSearchMethodThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestPredicateProvider(),
                getTransactionMethodModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }

    @Test
    public void requestPredicate_shouldSearchURLThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestPredicateProvider(),
                getTransactionURLModifier(),
                getContainsResultArrayProvider()
        );
    }


    @Test
    public void requestPredicate_shouldSearchResponseCodeThatStartWithSearchWord() {
        testPredicate(
                intPredicateTester,
                getRequestPredicateProvider(),
                getTransactionResponseCodeModifier(),
                this.<Integer>getPrefixResultArrayProvider()
        );
    }


    @Test
    public void requestPredicate_shouldSearchRequestBodyThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestPredicateProvider(),
                getTransactionRequestBodyModifier(),
                getContainsResultArrayProvider()
        );
    }

    @Test
    public void responsePredicate_shouldSearchProtocolThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getResponsePredicateProvider(),
                getTransactionProtocolModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }


    @Test
    public void responsePredicate_shouldSearchMethodThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getResponsePredicateProvider(),
                getTransactionMethodModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }

    @Test
    public void responsePredicate_shouldSearchURLThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getResponsePredicateProvider(),
                getTransactionURLModifier(),
                getContainsResultArrayProvider()
        );
    }


    @Test
    public void responsePredicate_shouldSearchResponseCodeThatStartWithSearchWord() {
        testPredicate(
                intPredicateTester,
                getResponsePredicateProvider(),
                getTransactionResponseCodeModifier(),
                this.<Integer>getPrefixResultArrayProvider()
        );
    }


    @Test
    public void responsePredicate_shouldSearchResponseBodyThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getResponsePredicateProvider(),
                getTransactionResponseBodyModifier(),
                getContainsResultArrayProvider()
        );
    }


    @Test
    public void responsePredicate_shouldSearchResponseMessageThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getResponsePredicateProvider(),
                getTransactionResponseMessageModifier(),
                getContainsResultArrayProvider()
        );
    }

    @Test
    public void requestResponsePredicate_shouldSearchProtocolThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestResponsePredicateProvider(),
                getTransactionProtocolModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }

    @Test
    public void requestResponsePredicate_shouldSearchMethodThatStartWithSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestResponsePredicateProvider(),
                getTransactionMethodModifier(),
                this.<String>getPrefixResultArrayProvider()
        );
    }

    @Test
    public void requestResponsePredicate_shouldSearchURLThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestResponsePredicateProvider(),
                getTransactionURLModifier(),
                getContainsResultArrayProvider()
        );
    }


    @Test
    public void requestResponsePredicate_shouldSearchResponseCodeThatStartWithSearchWord() {
        testPredicate(
                intPredicateTester,
                getRequestResponsePredicateProvider(),
                getTransactionResponseCodeModifier(),
                this.<Integer>getPrefixResultArrayProvider()
        );
    }

    @Test
    public void requestResponsePredicate_shouldSearchResponseBodyThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestResponsePredicateProvider(),
                getTransactionResponseBodyModifier(),
                getContainsResultArrayProvider()
        );
    }


    @Test
    public void requestResponsePredicate_shouldSearchResponseMessageThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestResponsePredicateProvider(),
                getTransactionResponseMessageModifier(),
                getContainsResultArrayProvider()
        );
    }


    @Test
    public void requestRequestPredicate_shouldSearchRequestBodyThatContainsSearchWord() {
        testPredicate(
                stringPredicateTester,
                getRequestResponsePredicateProvider(),
                getTransactionRequestBodyModifier(),
                getContainsResultArrayProvider()
        );
    }
}