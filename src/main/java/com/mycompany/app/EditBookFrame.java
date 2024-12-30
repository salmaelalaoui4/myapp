package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EditBookFrame extends JFrame {

    private JTextField txtTitle, txtAuthor, txtYear, txtIsbn;
    private JButton btnSave;
    private int bookId; // ID du livre à modifier

    public EditBookFrame(JFrame parent, int bookId) {
        this.bookId = bookId;

        // Configuration de la fenêtre
        setTitle("Modifier le Livre");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Titre:");
        lblTitle.setForeground(new Color(241, 242, 246));
        mainPanel.add(lblTitle);
        txtTitle = new JTextField();
        mainPanel.add(txtTitle);

        // Auteur
        JLabel lblAuthor = new JLabel("Auteur:");
        lblAuthor.setForeground(new Color(241, 242, 246));
        mainPanel.add(lblAuthor);
        txtAuthor = new JTextField();
        mainPanel.add(txtAuthor);

        // Année de publication
        JLabel lblYear = new JLabel("Année:");
        lblYear.setForeground(new Color(241, 242, 246));
        mainPanel.add(lblYear);
        txtYear = new JTextField();
        mainPanel.add(txtYear);

        // ISBN
        JLabel lblIsbn = new JLabel("ISBN:");
        lblIsbn.setForeground(new Color(241, 242, 246));
        mainPanel.add(lblIsbn);
        txtIsbn = new JTextField();
        mainPanel.add(txtIsbn);

        // Bouton de sauvegarde
        btnSave = new JButton("Enregistrer");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSave.setBackground(new Color(0, 184, 148));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> enregistrerModifications());

        mainPanel.add(new JLabel());  // Empty cell for spacing
        mainPanel.add(btnSave);

        // Charger les informations du livre dans les champs de texte
        chargerLivre();
    }

    // Méthode pour charger les informations du livre dans les champs de texte
    private void chargerLivre() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre WHERE idLivre = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, bookId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Remplir les champs de texte avec les données du livre
                txtTitle.setText(resultSet.getString("titre"));
                txtAuthor.setText(resultSet.getString("auteur"));
                txtYear.setText(resultSet.getString("anneePublication"));
                txtIsbn.setText(resultSet.getString("isbn"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données du livre : " + e.getMessage());
        }
    }

    // Méthode pour enregistrer les modifications
    private void enregistrerModifications() {
        String titre = txtTitle.getText();
        String auteur = txtAuthor.getText();
        String annee = txtYear.getText();
        String isbn = txtIsbn.getText();

        if (titre.isEmpty() || auteur.isEmpty() || annee.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE livre SET titre = ?, auteur = ?, anneePublication = ?, isbn = ? WHERE idLivre = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, titre);
            statement.setString(2, auteur);
            statement.setString(3, annee);
            statement.setString(4, isbn);
            statement.setInt(5, bookId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Livre modifié avec succès.");
                dispose();  // Fermer la fenêtre de modification
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification du livre.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement des modifications : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Exemple d'ID livre, à remplacer par l'ID réel lors de l'utilisation
            EditBookFrame frame = new EditBookFrame(null, 1); // Remplacer 1 par l'ID du livre
            frame.setVisible(true);
        });
    }
}
