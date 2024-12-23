package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AchatCreationFrame extends JFrame {

    private JComboBox<String> clientComboBox;
    private JComboBox<String> livreComboBox;
    private JTextField quantiteField;

    public AchatCreationFrame() {
        setTitle("Ajouter un Achat");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal avec un Layout GridBagLayout pour une meilleure gestion
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement entre les composants

        // Titre de la fenêtre
        JLabel lblTitle = new JLabel("Ajouter un Achat", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblTitle, gbc);

        // Liste des clients
        JLabel lblClient = new JLabel("Choisir le Client :");
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lblClient, gbc);

        clientComboBox = new JComboBox<>();
        chargerClients();
        gbc.gridx = 1;
        mainPanel.add(clientComboBox, gbc);

        // Liste des livres
        JLabel lblLivre = new JLabel("Choisir le Livre :");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(lblLivre, gbc);

        livreComboBox = new JComboBox<>();
        chargerLivres();
        gbc.gridx = 1;
        mainPanel.add(livreComboBox, gbc);

        // Quantité
        JLabel lblQuantite = new JLabel("Quantité :");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(lblQuantite, gbc);

        quantiteField = new JTextField();
        gbc.gridx = 1;
        mainPanel.add(quantiteField, gbc);

        // Bouton Ajouter Achat
        JButton btnAjouter = new JButton("Ajouter Achat");
        btnAjouter.addActionListener(e -> ajouterAchat());
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10); // Plus d'espacement pour le bouton
        mainPanel.add(btnAjouter, gbc);
    }

    // Fonction pour charger les clients depuis la base de données
    private void chargerClients() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idClient, nom, prenom FROM client";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String clientInfo = resultSet.getInt("idClient") + " - " + resultSet.getString("nom") + " " + resultSet.getString("prenom");
                clientComboBox.addItem(clientInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage());
        }
    }

    // Fonction pour charger les livres depuis la base de données
    private void chargerLivres() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idLivre, titre FROM livre";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String livreInfo = resultSet.getInt("idLivre") + " - " + resultSet.getString("titre");
                livreComboBox.addItem(livreInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    // Fonction pour ajouter un achat dans la base de données
    private void ajouterAchat() {
        String clientSelection = (String) clientComboBox.getSelectedItem();
        String livreSelection = (String) livreComboBox.getSelectedItem();
        String quantiteText = quantiteField.getText();

        // Validation des champs
        if (clientSelection == null || livreSelection == null || quantiteText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis !");
            return;
        }

        // Vérification de la validité de la quantité
        int quantite;
        try {
            quantite = Integer.parseInt(quantiteText);
            if (quantite <= 0) {
                throw new NumberFormatException();  // Pour les quantités inférieures ou égales à 0
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une quantité valide (un nombre entier positif) !");
            return;
        }

        int idClient = Integer.parseInt(clientSelection.split(" - ")[0]);
        int idLivre = Integer.parseInt(livreSelection.split(" - ")[0]);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            // Requête SQL pour insérer l'achat
            String query = "INSERT INTO achat (idClient, idLivre, quantite, statutValidation) VALUES (?, ?, ?, 'en attente')";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idClient);
            statement.setInt(2, idLivre);
            statement.setInt(3, quantite);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Achat ajouté avec succès !");
            this.dispose(); // Fermer la fenêtre après l'ajout
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'achat : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AchatCreationFrame().setVisible(true));
    }
}
