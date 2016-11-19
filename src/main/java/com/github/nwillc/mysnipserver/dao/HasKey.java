package com.github.nwillc.mysnipserver.dao;

public class HasKey<K> {
    private K key;

    public HasKey(K key) {
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "HasKey{" +
                "key=" + key +
                '}';
    }
}
