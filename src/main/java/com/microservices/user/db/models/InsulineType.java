package com.microservices.user.db.models;

public enum InsulineType {
    LONG("LONG"),
    SHORT("SHORT"),
    ULTRA_SHORT("ULTRA_SHORT");
    
    private final String text;
    
    InsulineType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}