package ru.zerrbild.utils.ciphering.enums;

public enum Algorithm {
    AES("AES"),
    ARCFOUR("ARCFOUR"),
    BLOWFISH("Blowfish"),
    RC2("RC2");

    private final String name;

    Algorithm(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
