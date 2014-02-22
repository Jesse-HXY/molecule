package com.vtence.molecule.simple.session;

import com.vtence.molecule.Request;
import com.vtence.molecule.Response;
import com.vtence.molecule.Session;

public class CookieTracker implements SessionTracker {

    private static final String STANDARD_SERVLET_SESSION_COOKIE = "JSESSIONID";

    private final SessionStore store;
    private final SessionIdentifierPolicy policy;
    private final String cookieName;

    public CookieTracker(SessionStore store) {
        this(store, new SecureIdentifierPolicy());
    }

    public CookieTracker(SessionStore store, SessionIdentifierPolicy policy) {
        this(store, policy, STANDARD_SERVLET_SESSION_COOKIE);
    }

    public CookieTracker(SessionStore store, SessionIdentifierPolicy policy, String cookieName) {
        this.store = store;
        this.policy = policy;
        this.cookieName = cookieName;
    }

    public Session acquireSession(Request request, Response response) {
        String sessionId = request.cookie(cookieName);
        return sessionId != null ? store.load(sessionId) : null;
    }

    public Session openSession(Request request, Response response) {
        Session session = store.create(policy.generateId());
        response.cookie(cookieName, session.id());
        return session;
    }
}
