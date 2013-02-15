package com.vtence.molecule.matchers;

import com.vtence.molecule.HttpMethod;
import com.vtence.molecule.Matcher;
import com.vtence.molecule.Request;

public class RequestWithMethod implements Matcher<Request> {

    private final Matcher<? super String> nameMatcher;

    public RequestWithMethod(Matcher<? super String> nameMatcher) {
        this.nameMatcher = nameMatcher;
    }

    public boolean matches(Request actual) {
        return nameMatcher.matches(actual.method().name());
    }

    public static RequestWithMethod withMethod(HttpMethod method) {
        return withMethod(method.name());
    }

    public static RequestWithMethod withMethod(String methodName) {
        return withMethod(IsEqual.equalTo(methodName));
    }

    public static RequestWithMethod withMethod(Matcher<? super String> nameMatcher) {
        return new RequestWithMethod(nameMatcher);
    }
}
