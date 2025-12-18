/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.util;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Usuario;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    private static final String KEY = "usuarioLogado";

    public static void setUsuarioLogado(HttpSession s, Usuario u) {
        if (s == null) return;
        if (u == null) s.removeAttribute(KEY);
        else s.setAttribute(KEY, u);
    }

    public static Usuario getUsuarioLogado(HttpSession s) {
        return s == null ? null : (Usuario) s.getAttribute(KEY);
    }
}
