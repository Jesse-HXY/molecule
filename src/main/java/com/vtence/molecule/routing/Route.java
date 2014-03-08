package com.vtence.molecule.routing;

import com.vtence.molecule.Application;
import com.vtence.molecule.matchers.Matcher;
import com.vtence.molecule.Request;
import com.vtence.molecule.Response;

public interface Route extends Matcher<Request>, Application {

    public void handle(Request request, Response response) throws Exception;
}
