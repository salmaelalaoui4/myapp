package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookManagementFrame extends JFrame {

    private JTable bookTable;
    private DefaultTableModel tableModel;

    public BookManagementFrame() {
        setTitle("Gestion des Livres");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Livres", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Tableau des livres
        tableModel = new DefaultTableModel(new String[]{"ID Livre", "Titre", "Auteur", "ISBN", "Année", "Statut"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        JButton btnAdd = new JButton("Ajouter un Livre");

        btnAdd.addActionListener(e -> ajouterLivre());

        buttonPanel.add(btnAdd);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerLivres();
    }

    private void chargerLivres() {
        tableModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("idLivre"),
                        resultSet.getString("titre"),
                        resultSet.getString("auteur"),
                        resultSet.getString("isbn"),
                        resultSet.getInt("anneePublication"),
                        resultSet.getBoolean("statut") ? "Disponible" : "Indisponible"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    private void ajouterLivre() {
        String titre = JOptionPane.showInputDialog(this, "Titre du Livre :");
        String auteur = JOptionPane.showInputDialog(this, "Auteur :");
        String isbn = JOptionPane.showInputDialog(this, "ISBN :");
        String annee = JOptionPane.showInputDialog(this, "Année de Publication :");

        if (titre == null || auteur == null || isbn == null || annee == null ||
                titre.isEmpty() || auteur.isEmpty() || isbn.isEmpty() || annee.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO livre (titre, auteur, isbn, anneePublication, statut) VALUES (?, ?, ?, ?, 1)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, titre);
            statement.setString(2, auteur);
            statement.setString(3, isbn);
            statement.setInt(4, Integer.parseInt(annee));
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Livre ajouté avec succès !");
            chargerLivres();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du livre : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookManagementFrame().setVisible(true));
    }
}
