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

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        JLabel lblTitle = new JLabel("Ajouter un Achat", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblTitle, gbc);

        // Client
        JLabel lblClient = new JLabel("Choisir le Client :");
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lblClient, gbc);

        clientComboBox = new JComboBox<>();
        chargerClients();
        gbc.gridx = 1;
        mainPanel.add(clientComboBox, gbc);

        // Livre
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

        // Ajouter
        JButton btnAjouter = new JButton("Ajouter Achat");
        btnAjouter.addActionListener(e -> ajouterAchat());
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(btnAjouter, gbc);
    }

    private void chargerClients() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idClient, nom, prenom FROM client";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                clientComboBox.addItem(resultSet.getInt("idClient") + " - " + resultSet.getString("nom") + " " + resultSet.getString("prenom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage());
        }
    }

    private void chargerLivres() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idLivre, titre, quantiteDisponible FROM livre WHERE quantiteDisponible > 0";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                livreComboBox.addItem(resultSet.getInt("idLivre") + " - " + resultSet.getString("titre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    private void ajouterAchat() {
        String clientSelection = (String) clientComboBox.getSelectedItem();
        String livreSelection = (String) livreComboBox.getSelectedItem();
        String quantiteText = quantiteField.getText();

        if (clientSelection == null || livreSelection == null || quantiteText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis !");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(quantiteText);
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La quantité doit être un nombre entier positif !");
            return;
        }

        int idClient = Integer.parseInt(clientSelection.split(" - ")[0]);
        int idLivre = Integer.parseInt(livreSelection.split(" - ")[0]);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            // Vérifier la quantité disponible
            String queryStock = "SELECT quantiteDisponible FROM livre WHERE idLivre = ?";
            PreparedStatement stockStatement = connection.prepareStatement(queryStock);
            stockStatement.setInt(1, idLivre);
            ResultSet resultSet = stockStatement.executeQuery();

            if (resultSet.next()) {
                int quantiteDisponible = resultSet.getInt("quantiteDisponible");
                if (quantite > quantiteDisponible) {
                    JOptionPane.showMessageDialog(this, "Quantité insuffisante en stock !");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Livre introuvable !");
                return;
            }

            // Ajouter l'achat
            String queryAchat = "INSERT INTO achat (idClient, idLivre, quantite, statutValidation) VALUES (?, ?, ?, 'en attente')";
            PreparedStatement achatStatement = connection.prepareStatement(queryAchat);
            achatStatement.setInt(1, idClient);
            achatStatement.setInt(2, idLivre);
            achatStatement.setInt(3, quantite);
            achatStatement.executeUpdate();

            // Mettre à jour le stock
            String queryUpdateStock = "UPDATE livre SET quantiteDisponible = quantiteDisponible - ? WHERE idLivre = ?";
            PreparedStatement updateStockStatement = connection.prepareStatement(queryUpdateStock);
            updateStockStatement.setInt(1, quantite);
            updateStockStatement.setInt(2, idLivre);
            updateStockStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Achat ajouté avec succès !");
            this.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'achat : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AchatCreationFrame().setVisible(true));
    }
}
