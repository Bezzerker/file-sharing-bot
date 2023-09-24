package ru.zerrbild;

import ru.zerrbild.utils.ciphering.Encoder;
import ru.zerrbild.utils.ciphering.enums.Algorithm;

public class Main {
    public static void main(String[] args) {
        Encoder encoder = new Encoder(Algorithm.AES);
        String key = encoder.getBase64Key();
        System.out.println(key);
    }
}
