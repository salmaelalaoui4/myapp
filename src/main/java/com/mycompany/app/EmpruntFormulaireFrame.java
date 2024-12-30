package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class EmpruntFormulaireFrame extends JFrame {
    private int bibliothequeId;
    private JComboBox<String> clientComboBox;
    private JComboBox<String> livreComboBox;
    private JTextField dateEmpruntField;
    private JTextField dateRetourPrevueField;
    private JButton enregistrerButton;
    private LoanManagementFrame loanManagementFrame;

    public EmpruntFormulaireFrame(LoanManagementFrame loanManagementFrame, int bibliothequeId) {
        this.loanManagementFrame = loanManagementFrame;
        this.bibliothequeId = bibliothequeId;

        setTitle("Enregistrer un Emprunt");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Champs pour Client
        JLabel lblClient = new JLabel("Sélectionner un Client:");
        clientComboBox = new JComboBox<>();
        chargerClients();
        mainPanel.add(lblClient);
        mainPanel.add(clientComboBox);

        // Champs pour Livre
        JLabel lblLivre = new JLabel("Sélectionner un Livre:");
        livreComboBox = new JComboBox<>();
        chargerLivres();
        mainPanel.add(lblLivre);
        mainPanel.add(livreComboBox);

        // Champ pour Date Emprunt
        JLabel lblDateEmprunt = new JLabel("Date Emprunt (yyyy-mm-dd):");
        dateEmpruntField = new JTextField();
        mainPanel.add(lblDateEmprunt);
        mainPanel.add(dateEmpruntField);

        // Champ pour Date Retour Prévue
        JLabel lblDateRetourPrevue = new JLabel("Date Retour Prévue (yyyy-mm-dd):");
        dateRetourPrevueField = new JTextField();
        mainPanel.add(lblDateRetourPrevue);
        mainPanel.add(dateRetourPrevueField);

        // Bouton Enregistrer
        enregistrerButton = new JButton("Enregistrer Emprunt");
        enregistrerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enregistrerEmprunt();
            }
        });
        mainPanel.add(new JLabel());  // Empty label for layout
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
            String query = "SELECT idLivre, titre FROM livre";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idLivre = resultSet.getInt("idLivre");
                    String titre = resultSet.getString("titre");
                    livreComboBox.addItem(idLivre + " - " + titre);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    private void enregistrerEmprunt() {
        try {
            // Récupérer les valeurs du formulaire
            String selectedClient = (String) clientComboBox.getSelectedItem();
            int idClient = Integer.parseInt(selectedClient.split(" - ")[0]);

            String selectedLivre = (String) livreComboBox.getSelectedItem();
            int idLivre = Integer.parseInt(selectedLivre.split(" - ")[0]);

            String dateEmpruntStr = dateEmpruntField.getText();
            String dateRetourPrevueStr = dateRetourPrevueField.getText();

            // Convertir les dates en format Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dateEmprunt = dateFormat.parse(dateEmpruntStr);
            java.util.Date dateRetourPrevue = dateFormat.parse(dateRetourPrevueStr);

            // Convertir java.util.Date en java.sql.Date
            java.sql.Date sqlDateEmprunt = new java.sql.Date(dateEmprunt.getTime());
            java.sql.Date sqlDateRetourPrevue = new java.sql.Date(dateRetourPrevue.getTime());

            // Enregistrer l'emprunt
            loanManagementFrame.enregistrerEmprunt(idLivre, idClient, sqlDateEmprunt, sqlDateRetourPrevue);

            // Fermer le formulaire
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'emprunt : " + e.getMessage());
        }
    }
}
