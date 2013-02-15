package com.vtence.molecule.routing;

import com.vtence.molecule.HttpMethod;

import java.util.ArrayList;
import java.util.Collection;

public class Router implements RouteBuilder {

    private final Collection<DynamicRouteDefinition> routes = new ArrayList<DynamicRouteDefinition>();

    public void build(RouteSet routeSet) {
        for (DynamicRouteDefinition route : this.routes) {
            routeSet.add(route.draw());
        }
    }

    public RouteDefinition map(String path) {
        return openRoute().map(path);
    }

    public RouteDefinition get(String path) {
        return map(path).via(HttpMethod.GET);
    }

    public RouteDefinition post(String path) {
        return map(path).via(HttpMethod.POST);
    }

    public RouteDefinition put(String path) {
        return map(path).via(HttpMethod.PUT);
    }

    public RouteDefinition delete(String path) {
        return map(path).via(HttpMethod.DELETE);
    }

    private RouteDefinition openRoute() {
        DynamicRouteDefinition definition = DynamicRouteDefinition.route();
        routes.add(definition);
        return definition;
    }
}
