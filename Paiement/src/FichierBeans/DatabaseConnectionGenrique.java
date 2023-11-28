package FichierBeans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;


public class DatabaseConnectionGenrique {
    Connection connection;

    public DatabaseConnectionGenrique() {
        try {
            // Chargement du driver
            Class leDriver = Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Obtention du driver OK...");

            // Connexion a la BD
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.101/PourStudent","Student","PassStudent1_");

            System.out.println("Connexion à la BD PourStudent OK...");
        }
        catch (ClassNotFoundException ex)
        {
            System.out.println("Erreur ClassNotFoundException: " + ex.getMessage());
        }
        catch (SQLException ex)
        {
            System.out.println("Erreur SQLException: " + ex.getMessage());
        }

    }
    //Creation d'un objet Statement permettant d'executer une requete SELECT
    public synchronized ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    // Creation d'un objet Statement permettant d'executer des requete INSERT, UPDATE, DELETE
    public synchronized int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);
    }
    public synchronized void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("Connexion fermer avec DB!");
            connection.close();
        }
    }
}
