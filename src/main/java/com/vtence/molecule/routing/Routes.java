package com.vtence.molecule.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import static com.vtence.molecule.http.HttpMethod.DELETE;
import static com.vtence.molecule.http.HttpMethod.GET;
import static com.vtence.molecule.http.HttpMethod.HEAD;
import static com.vtence.molecule.http.HttpMethod.OPTIONS;
import static com.vtence.molecule.http.HttpMethod.PATCH;
import static com.vtence.molecule.http.HttpMethod.POST;
import static com.vtence.molecule.http.HttpMethod.PUT;

public class Routes implements RouteBuilder {

    private final Collection<RouteDefinition> routes = new ArrayList<>();

    public void build(RouteSet routeSet) {
        for (RouteDefinition definition : this.routes) {
            routeSet.route(definition.toRoute());
        }
    }

    public ViaClause map(String path) {
        return openRoute(new DynamicPath(path));
    }

    public ViaClause map(Predicate<? super String> path) {
        return openRoute(path);
    }

    public AcceptClause get(String path) {
        return get(new DynamicPath(path));
    }

    public AcceptClause get(Predicate<? super String> path) {
        return map(path).via(GET);
    }

    public AcceptClause post(String path) {
        return post(new DynamicPath(path));
    }

    public AcceptClause post(Predicate<? super String> path) {
        return map(path).via(POST);
    }

    public AcceptClause put(String path) {
        return put(new DynamicPath(path));
    }

    public AcceptClause put(Predicate<? super String> path) {
        return map(path).via(PUT);
    }

    public AcceptClause delete(String path) {
        return delete(new DynamicPath(path));
    }

    public AcceptClause delete(Predicate<? super String> path) {
        return map(path).via(DELETE);
    }

    public AcceptClause patch(String path) {
        return patch(new DynamicPath(path));
    }

    public AcceptClause patch(Predicate<? super String> path) {
        return map(path).via(PATCH);
    }

    public AcceptClause head(String path) {
        return head(new DynamicPath(path));
    }

    public AcceptClause head(Predicate<? super String> path) {
        return map(path).via(HEAD);
    }

    public AcceptClause options(String path) {
        return options(new DynamicPath(path));
    }

    public AcceptClause options(Predicate<? super String> path) {
        return map(path).via(OPTIONS);
    }

    private RouteDefinition openRoute(Predicate<? super String> path) {
        RouteDefinition definition = RouteDefinition.map(path);
        routes.add(definition);
        return definition;
    }
}