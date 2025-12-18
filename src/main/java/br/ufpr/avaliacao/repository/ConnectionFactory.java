/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/*
 * @author Pedro
 */

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionFactory {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Properties p = new Properties();
            try (InputStream in = ConnectionFactory.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties")) {
                if (in == null) throw new RuntimeException("db.properties não encontrado em src/main/resources");
                p.load(in);
            }
            URL = p.getProperty("db.url");
            USER = p.getProperty("db.user");
            PASSWORD = p.getProperty("db.password");
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar ConnectionFactory", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter conexão JDBC", e);
        }
    }
}
