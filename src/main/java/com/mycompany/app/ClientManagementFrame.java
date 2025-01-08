package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ClientManagementFrame extends JFrame {

    private JTable clientTable;
    private DefaultTableModel tableModel;
    private int bibliothequeId; // ID de la bibliothèque

    public ClientManagementFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;

        setTitle("Gestion des Clients - Bibliothèque " + bibliothequeId);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Clients - Bibliothèque " + bibliothequeId, SwingConstants.CENTER);
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
        JButton btnDelete = new JButton("Supprimer");

        btnAdd.addActionListener(e -> ajouterClient());
        btnUpdate.addActionListener(e -> mettreAJourClient());
        btnDelete.addActionListener(e -> supprimerClient());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Charger les clients au démarrage
        chargerClients();
    }

    private void chargerClients() {
        tableModel.setRowCount(0); // Efface les données existantes dans le tableau

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idClient, nom, prenom, email, telephone FROM client WHERE statut = 1 AND idBibliotheque = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, bibliothequeId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int idClient = resultSet.getInt("idClient");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                String telephone = resultSet.getString("telephone");

                tableModel.addRow(new Object[]{idClient, nom, prenom, email, telephone});
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
        String adresse = JOptionPane.showInputDialog(this, "Adresse du client :");
        String telephone = JOptionPane.showInputDialog(this, "Téléphone du client :");

        if (nom == null || prenom == null || email == null || adresse == null || telephone == null ||
                nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO client (nom, prenom, email, adresse, telephone, statut, dateInscription, idBibliotheque) " +
                           "VALUES (?, ?, ?, ?, ?, 1, NOW(), ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, email);
            statement.setString(4, adresse);
            statement.setString(5, telephone);
            statement.setInt(6, bibliothequeId);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
            chargerClients(); // Recharger la liste des clients après l'ajout
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

        // Récupérer l'ID du client sélectionné
        int idClient = (int) tableModel.getValueAt(selectedRow, 0);

        // Demander les nouvelles valeurs
        String nouveauNom = JOptionPane.showInputDialog(this, "Nouveau nom :", tableModel.getValueAt(selectedRow, 1));
        String nouveauPrenom = JOptionPane.showInputDialog(this, "Nouveau prénom :", tableModel.getValueAt(selectedRow, 2));
        String nouveauTelephone = JOptionPane.showInputDialog(this, "Nouveau téléphone :", tableModel.getValueAt(selectedRow, 4));

        if (nouveauNom == null || nouveauPrenom == null || nouveauTelephone == null ||
                nouveauNom.isEmpty() || nouveauPrenom.isEmpty() || nouveauTelephone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE client SET nom = ?, prenom = ?, telephone = ? WHERE idClient = ? AND idBibliotheque = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nouveauNom);
            statement.setString(2, nouveauPrenom);
            statement.setString(3, nouveauTelephone);
            statement.setInt(4, idClient);
            statement.setInt(5, bibliothequeId);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Client mis à jour avec succès !");
            chargerClients(); // Rafraîchir la table après la mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du client : " + e.getMessage());
        }
    }

    private void supprimerClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client !");
            return;
        }

        int idClient = (int) tableModel.getValueAt(selectedRow, 0);

        // Demander confirmation
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce client ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE client SET statut = 0 WHERE idClient = ? AND idBibliotheque = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idClient);
            statement.setInt(2, bibliothequeId);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Client supprimé avec succès !");
                chargerClients(); // Recharger la liste après la suppression
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientManagementFrame(1).setVisible(true)); // Exemple avec bibliothequeId = 1
    }
}
