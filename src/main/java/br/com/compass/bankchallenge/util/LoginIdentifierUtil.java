package br.com.compass.bankchallenge.util;

public class LoginIdentifierUtil {

    public static boolean isCPF(String input) {
        return input.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    public static boolean isEmail(String input) {
        return input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}