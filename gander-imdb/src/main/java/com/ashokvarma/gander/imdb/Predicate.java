package com.ashokvarma.gander.imdb;

public interface Predicate<T> {
    Predicate ALLOW_ALL = new Predicate() {
        @Override
        public boolean apply(Object data) {
            return true;
        }
    };

    boolean apply(T t);

}
