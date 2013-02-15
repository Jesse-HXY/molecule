package com.vtence.molecule;

public interface Session {

    boolean contains(Object key);

    void put(Object key, Object value);

    Object get(Object key);
}
