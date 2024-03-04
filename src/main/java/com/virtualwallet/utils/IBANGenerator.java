package com.virtualwallet.utils;

import java.util.Random;

public class IBANGenerator {
    private static final String COUNTRY_CODE = "BG";
    private static final int IBAN_LENGTH = 22;
    private static Random random = new Random();

    public static String generateRandomIBAN() {
        StringBuilder iban = new StringBuilder(COUNTRY_CODE);
        for (int i = 0; i < IBAN_LENGTH - COUNTRY_CODE.length(); i++) {
            iban.append(random.nextInt(10));
        }
        return iban.toString();
    }
}
