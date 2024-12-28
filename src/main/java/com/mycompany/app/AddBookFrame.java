package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddBookFrame extends JFrame {

    private BookManagementFrame parent;  // Référence du parent

    public AddBookFrame(BookManagementFrame parent) {
        this.parent = parent;  // Stockez la référence du parent

        setTitle("Ajouter un Livre");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Champs pour les informations du livre
        JTextField txtTitle = new JTextField();
        JTextField txtAuthor = new JTextField();
        JTextField txtIsbn = new JTextField();
        JTextField txtYear = new JTextField();
        JTextArea txtDescription = new JTextArea();
        JScrollPane scrollDescription = new JScrollPane(txtDescription);

        // Ajouter les composants
        mainPanel.add(new JLabel("Titre :"));
        mainPanel.add(txtTitle);
        mainPanel.add(new JLabel("Auteur :"));
        mainPanel.add(txtAuthor);
        mainPanel.add(new JLabel("ISBN :"));
        mainPanel.add(txtIsbn);
        mainPanel.add(new JLabel("Année de publication :"));
        mainPanel.add(txtYear);
        mainPanel.add(new JLabel("Description :"));
        mainPanel.add(scrollDescription);

        // Bouton pour enregistrer le livre
        JButton btnSave = new JButton("Enregistrer");
        btnSave.addActionListener(e -> {
            // Code pour ajouter un livre à la base de données
            ajouterLivre(txtTitle.getText(), txtAuthor.getText(), txtIsbn.getText(), txtYear.getText(), txtDescription.getText());

            // Rafraîchir la liste des livres dans le parent
            parent.chargerLivres();  // Appeler la méthode de rafraîchissement
            dispose();  // Fermer la fenêtre d'ajout
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void ajouterLivre(String title, String author, String isbn, String year, String description) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO livre (titre, auteur, isbn, anneePublication, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, isbn);
            statement.setString(4, year);
            statement.setString(5, description);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du livre : " + e.getMessage());
        }
    }
}
