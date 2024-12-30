package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddBookFrame extends JFrame {

    private BookManagementFrame parentFrame;  // Reference to parent frame
    private int libraryId; // ID de la bibliothèque

    public AddBookFrame(BookManagementFrame parentFrame) {
        this.parentFrame = parentFrame; // Store parent reference
        this.libraryId = 1;  // Default value for now (you can pass it dynamically)

        setTitle("Ajouter un Livre");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitre = new JLabel("Titre:");
        JTextField txtTitre = new JTextField();

        JLabel lblAuteur = new JLabel("Auteur:");
        JTextField txtAuteur = new JTextField();

        JLabel lblAnnee = new JLabel("Année:");
        JTextField txtAnnee = new JTextField();

        JLabel lblISBN = new JLabel("ISBN:");
        JTextField txtISBN = new JTextField();

        JLabel lblPhoto = new JLabel("Photo (chemin):");
        JTextField txtPhoto = new JTextField();

        JButton btnSave = new JButton("Enregistrer");
        btnSave.addActionListener(e -> {
            String titre = txtTitre.getText();
            String auteur = txtAuteur.getText();
            String annee = txtAnnee.getText();
            String isbn = txtISBN.getText();
            String photo = txtPhoto.getText();

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
                String query = "INSERT INTO livre (titre, auteur, anneePublication, isbn, photo, idBibliotheque) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, titre);
                statement.setString(2, auteur);
                statement.setString(3, annee);
                statement.setString(4, isbn);
                statement.setString(5, photo);
                statement.setInt(6, libraryId);

                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Livre ajouté avec succès !");
                parentFrame.chargerLivres();  // Refresh the parent frame's book list
                dispose(); // Close the window
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du livre : " + ex.getMessage());
            }
        });

        mainPanel.add(lblTitre);
        mainPanel.add(txtTitre);
        mainPanel.add(lblAuteur);
        mainPanel.add(txtAuteur);
        mainPanel.add(lblAnnee);
        mainPanel.add(txtAnnee);
        mainPanel.add(lblISBN);
        mainPanel.add(txtISBN);
        mainPanel.add(lblPhoto);
        mainPanel.add(txtPhoto);
        mainPanel.add(new JLabel()); // Espace vide
        mainPanel.add(btnSave);

        add(mainPanel);
    }
}
