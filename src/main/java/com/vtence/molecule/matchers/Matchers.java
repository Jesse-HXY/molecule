package com.vtence.molecule.matchers;

import com.vtence.molecule.Matcher;
import com.vtence.molecule.Request;

public final class Matchers {

    public static <T> Matcher<T> equalTo(T other) {
        return IsEqual.equalTo(other);
    }

    public static <T> Matcher<T> anything() {
        return Anything.anything();
    }

    public static Matcher<Request> anyRequest() {
        return anything();
    }

    public static Matcher<String> anyMethod() {
        return anything();
    }

    public static <T> Matcher<T> nothing() {
        return Nothing.nothing();
    }

    public static Matcher<Request> withPath(Matcher<? super String> path) {
        return RequestWithPath.withPath(path);
    }

    public static Matcher<String> startingWith(String prefix) {
        return StartingWith.startingWith(prefix);
    }

    public static Matcher<Request> withMethod(Matcher<? super String> method) {
        return RequestWithMethod.withMethod(method);
    }

    public static Matcher<Request> withMethod(String method) {
        return RequestWithMethod.withMethod(method);
    }

    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
        return AllOf.allOf(matchers);
    }

    private Matchers() {}
}
