package com.mycompany.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseConnection {

    // Informations de connexion
    private static final String URL = "jdbc:mysql://localhost:3306/biblio";
    private static final String USER = "root"; // Remplacez par votre utilisateur MySQL
    private static final String PASSWORD = ""; // Remplacez par votre mot de passe MySQL

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Étape 1 : Charger le driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Étape 2 : Établir la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion réussie à la base de données!");

            // Étape 3 : Exécuter une requête pour afficher les tables
            String query = "SHOW TABLES;";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Étape 4 : Parcourir les résultats
            System.out.println("Tables disponibles dans la base de données :");
            while (resultSet.next()) {
                System.out.println("- " + resultSet.getString(1)); // La première colonne contient le nom des tables
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connexion fermée.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
