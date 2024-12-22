package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ClientManagementFrame extends JFrame {

    private JTable clientTable;
    private DefaultTableModel tableModel;

    public ClientManagementFrame() {
        setTitle("Gestion des Clients");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Clients", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Tableau des clients
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Email", "Téléphone"}, 0);
        clientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpdate = new JButton("Mettre à jour");

        btnAdd.addActionListener(e -> ajouterClient());
        btnUpdate.addActionListener(e -> mettreAJourClient());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerClients();
    }

    private void chargerClients() {
        tableModel.setRowCount(0); // Vider le tableau
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM client";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("idClient"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("email"),
                        resultSet.getString("telephone")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage());
        }
    }

    private void ajouterClient() {
        String nom = JOptionPane.showInputDialog(this, "Nom du client :");
        String prenom = JOptionPane.showInputDialog(this, "Prénom du client :");
        String email = JOptionPane.showInputDialog(this, "Email du client :");
        String telephone = JOptionPane.showInputDialog(this, "Téléphone du client :");

        if (nom == null || prenom == null || email == null || telephone == null ||
                nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO client (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, email);
            statement.setString(4, telephone);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
            chargerClients();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client : " + e.getMessage());
        }
    }

    private void mettreAJourClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client !");
            return;
        }

        int idClient = (int) tableModel.getValueAt(selectedRow, 0);
        String nouveauNom = JOptionPane.showInputDialog(this, "Nouveau nom :", tableModel.getValueAt(selectedRow, 1));
        String nouveauPrenom = JOptionPane.showInputDialog(this, "Nouveau prénom :", tableModel.getValueAt(selectedRow, 2));
        String nouveauTelephone = JOptionPane.showInputDialog(this, "Nouveau téléphone :", tableModel.getValueAt(selectedRow, 4));

        if (nouveauNom == null || nouveauPrenom == null || nouveauTelephone == null ||
                nouveauNom.isEmpty() || nouveauPrenom.isEmpty() || nouveauTelephone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE client SET nom = ?, prenom = ?, telephone = ? WHERE idClient = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nouveauNom);
            statement.setString(2, nouveauPrenom);
            statement.setString(3, nouveauTelephone);
            statement.setInt(4, idClient);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client mis à jour avec succès !");
            chargerClients();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du client : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientManagementFrame().setVisible(true));
    }
}
