package com.vtence.molecule;

import com.vtence.molecule.util.Clock;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

public interface Session {

    String id();

    boolean contains(Object key);

    void put(Object key, Object value);

    <T> T get(Object key);

    Set<?> keys();

    Collection<?> values();

    long timeout();

    void timeout(long inSeconds);

    boolean expired(Clock clock);

    void touch(Clock clock);

    void invalidate();

    boolean invalid();

    Date createdAt();

    Date lastAccessedAt();
}