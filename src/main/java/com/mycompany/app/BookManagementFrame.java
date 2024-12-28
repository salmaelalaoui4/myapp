package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class BookManagementFrame extends JFrame {

    private JTable bookTable;
    private DefaultTableModel tableModel;

    public BookManagementFrame() {
        setTitle("Gestion des Livres");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Livres", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Tableau des livres
        tableModel = new DefaultTableModel(new Object[]{"ID", "Titre", "Auteur", "Année", "ISBN"}, 0);  // Correction: Object[] au lieu de String[]
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        buttonPanel.setBackground(new Color(45, 52, 54));

        JButton btnAddBook = new JButton("Ajouter un Livre");
        btnAddBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAddBook.setBackground(new Color(0, 184, 148));
        btnAddBook.setForeground(Color.WHITE);
        btnAddBook.setFocusPainted(false);
        btnAddBook.addActionListener(e -> {
            AddBookFrame addBookFrame = new AddBookFrame(this);  // Passe la référence du parent
            addBookFrame.setVisible(true);
        });

        buttonPanel.add(btnAddBook);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerLivres();  // Charger les livres lors de l'initialisation
    }

    // Méthode pour charger les livres depuis la base de données
    public void chargerLivres() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre";  // Requête pour récupérer les livres
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Réinitialiser les lignes existantes dans le tableau avant de les ajouter
            tableModel.setRowCount(0);

            // Parcourir les résultats et ajouter les livres dans le modèle de la table
            while (resultSet.next()) {
                int idLivre = resultSet.getInt("idLivre");
                String titre = resultSet.getString("titre");
                String auteur = resultSet.getString("auteur");
                String isbn = resultSet.getString("isbn");
                String annee = resultSet.getString("anneePublication");

                // Ajouter les données du livre à la table
                tableModel.addRow(new Object[]{idLivre, titre, auteur, annee, isbn});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookManagementFrame().setVisible(true));
    }
}
