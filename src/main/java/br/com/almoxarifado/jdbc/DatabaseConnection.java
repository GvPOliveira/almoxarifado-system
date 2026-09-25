package br.com.almoxarifado.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;


public class DatabaseConnection {
        private final String URL = "jdbc:mysql://localhost:3306/almoxarifado";
        private final String USER = "root";
        private final String PASSWORD = "suasenha";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);

    }


}
