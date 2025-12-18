/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.util;

/**
 *
 * @author Pedro, Gabi
 */

public final class ParseUtil {
    private ParseUtil(){}

    public static Integer parseInt(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Integer.valueOf(s.trim());
        } catch (Exception e) { return null; }
    }
    public static Integer parseInt(String s, Integer def) {
        Integer v = parseInt(s);
        return v == null ? def : v;
    }

    public static Long parseLong(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Long.valueOf(s.trim());
        } catch (Exception e) { return null; }
    }
    public static Long parseLong(String s, Long def) {
        Long v = parseLong(s);
        return v == null ? def : v;
    }

    public static boolean parseBool(String s) {
        return "true".equalsIgnoreCase(s) || "on".equalsIgnoreCase(s) || "1".equals(s);
    }
}
