package com.vtence.molecule.middlewares;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.vtence.molecule.Application;
import com.vtence.molecule.HttpStatus;
import com.vtence.molecule.support.MockRequest;
import com.vtence.molecule.support.MockResponse;

import static org.hamcrest.CoreMatchers.containsString;
import static com.vtence.molecule.support.MockRequest.aRequest;
import static com.vtence.molecule.support.MockResponse.aResponse;

@RunWith(JMock.class)
public class FailsafeTest {
    Mockery context = new JUnit4Mockery();

    Application successor = context.mock(Application.class, "successor");
    Failsafe failsafe = new Failsafe();

    String errorMessage = "An internal error occurred!";
    Exception error = new Exception(errorMessage);

    MockRequest request = aRequest();
    MockResponse response = aResponse();

    @Before public void
    handleRequest() throws Exception {
        error.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("stack", "trace", "line", 1),
                new StackTraceElement("stack", "trace", "line", 2)
        });

        context.checking(new Expectations() {{
            allowing(successor).handle(with(request), with(response)); will(new CustomAction("throw exception") {
                public Object invoke(Invocation invocation) throws Throwable {
                    throw error;
                }
            });
        }});

        failsafe.connectTo(successor);
        failsafe.handle(request, response);
    }

    @Test public void
    setStatusToInternalServerError() {
        response.assertStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test public void
    rendersErrorTemplate() {
        response.assertBody(containsString(errorMessage));
        response.assertBody(containsString("stack.trace(line:1)"));
        response.assertBody(containsString("stack.trace(line:2)"));
    }

    @Test public void
    setsResponseContentTypeToHtml() {
        response.assertHeader("Content-Type", containsString("text/html"));
    }
}