package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

public class AchatFormulaireFrame extends JFrame {
    private int idBibliotheque;
    private JComboBox<String> clientComboBox;
    private JComboBox<String> livreComboBox;
    private JTextField dateAchatField;
    private JTextField quantiteField;
    private JButton enregistrerButton;
    private AchatManagementFrame achatManagementFrame;

    public AchatFormulaireFrame(int idBibliotheque, AchatManagementFrame achatManagementFrame) {
        this.idBibliotheque = idBibliotheque;
        this.achatManagementFrame = achatManagementFrame;

        setTitle("Enregistrer un Achat");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblClient = new JLabel("Sélectionner un Client:");
        clientComboBox = new JComboBox<>();
        chargerClients();
        mainPanel.add(lblClient);
        mainPanel.add(clientComboBox);

        JLabel lblLivre = new JLabel("Sélectionner un Livre:");
        livreComboBox = new JComboBox<>();
        chargerLivres();
        mainPanel.add(lblLivre);
        mainPanel.add(livreComboBox);

        JLabel lblDateAchat = new JLabel("Date Achat (yyyy-mm-dd):");
        dateAchatField = new JTextField();
        mainPanel.add(lblDateAchat);
        mainPanel.add(dateAchatField);

        JLabel lblQuantite = new JLabel("Quantité:");
        quantiteField = new JTextField();
        mainPanel.add(lblQuantite);
        mainPanel.add(quantiteField);

        enregistrerButton = new JButton("Enregistrer Achat");
        enregistrerButton.addActionListener(e -> enregistrerAchat());
        mainPanel.add(new JLabel());
        mainPanel.add(enregistrerButton);
    }

    private void chargerClients() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idClient, nom, prenom FROM client";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idClient = resultSet.getInt("idClient");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    clientComboBox.addItem(idClient + " - " + nom + " " + prenom);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage());
        }
    }

    private void chargerLivres() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idLivre, titre FROM livre WHERE idBibliotheque = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idBibliotheque);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idLivre = resultSet.getInt("idLivre");
                        String titre = resultSet.getString("titre");
                        livreComboBox.addItem(idLivre + " - " + titre);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    private void enregistrerAchat() {
        try {
            String selectedClient = (String) clientComboBox.getSelectedItem();
            int idClient = Integer.parseInt(selectedClient.split(" - ")[0]);

            String selectedLivre = (String) livreComboBox.getSelectedItem();
            int idLivre = Integer.parseInt(selectedLivre.split(" - ")[0]);

            String dateAchatStr = dateAchatField.getText();
            int quantite = Integer.parseInt(quantiteField.getText());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dateAchat = dateFormat.parse(dateAchatStr);
            java.sql.Date sqlDateAchat = new java.sql.Date(dateAchat.getTime());

            int statutValidation = 0; // Par défaut, statut "En attente"

            achatManagementFrame.enregistrerAchat(idLivre, idClient, sqlDateAchat, quantite, statutValidation);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'achat : " + e.getMessage());
        }
    }
}
