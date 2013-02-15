package com.vtence.molecule.support;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import com.vtence.molecule.Response;

public class WriteBody implements Action {
    public static Action writeBody(String body) {
        return new WriteBody(body);
    }

    private String body;

    public WriteBody(String body) {
        this.body = body;
    }

    public Object invoke(Invocation invocation) throws Throwable {
        Response response = (Response) invocation.getParameter(1);
        response.body(body);
        return null;
    }

    public void describeTo(Description description) {
        description.appendText("writes body ").appendValue(body);
    }
}
