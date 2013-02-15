package com.vtence.molecule.routing;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import com.vtence.molecule.Application;
import com.vtence.molecule.HttpMethod;
import com.vtence.molecule.Request;
import com.vtence.molecule.Response;

import static com.vtence.molecule.routing.DynamicRouteDefinition.route;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static com.vtence.molecule.support.MockRequest.*;
import static com.vtence.molecule.support.MockResponse.aResponse;

public class DynamicRouteTest {

    Mockery context = new JUnit4Mockery();
    Application app = context.mock(Application.class);

    @Test(expected = IllegalStateException.class) public void
    isInvalidIfNoPathWasSpecified() throws Exception {
        route().draw();
    }

    @Test public void
    isValidWithAPath() throws Exception {
        route().map("/resource").draw();
    }

    @Test public void
    routesRequestIfPathMatchesPattern() throws Exception {
        Route route = route().map("/resource/:id").draw();
        assertThat("no match", route.matches(GET("/resource/1")));
    }

    @Test public void
    ignoresRequestWhenPathDoesNotMatchPattern() throws Exception {
        Route route = route().map("/resource/:id").draw();
        assertThat("match", !route.matches(GET("/other/1")));
    }

    @Test public void
    routesRequestOnlyWhenHttpMethodMatches() throws Exception {
        Route route = route().map("/resource").via(HttpMethod.POST).draw();
        assertThat("no match", route.matches(POST("/resource")));
        assertThat("match", !route.matches(GET("/resource")));
    }

    @Test public void
    makesParametersBoundToPathAvailableAsRequestParameters() throws Exception {
        Route route = route().map("/resource/:id").to(app).draw();

        context.checking(new Expectations() {{
            oneOf(app).handle(with(hasParameter("id", "1")), with(any(Response.class)));
        }});

        route.handle(aRequest().withPath("/resource/1"), aResponse());
    }

    private Matcher<Request> hasParameter(final String name, String value) {
        return new FeatureMatcher<Request, String>(equalTo(value), "request with parameter " + name, name) {
            protected String featureValueOf(Request actual) {
                return actual.parameter(name);
            }
        };
    }
}
