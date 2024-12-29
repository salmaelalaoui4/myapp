package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AchatManagementFrame extends JFrame {

    private JTable achatTable;
    private DefaultTableModel tableModel;
    private int bibliothequeId;

    public AchatManagementFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;

        setTitle("Gestion des Achats");
        setSize(1000, 600); // Augmenté pour garantir l'affichage
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Achats", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Tableau des achats
        tableModel = new DefaultTableModel(new String[]{
                "ID Achat", "ID Livre", "Date Achat", "Quantité", "Statut", "ID Bibliothécaire"
        }, 0);
        achatTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(achatTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // 3 colonnes pour les boutons
        JButton btnValidate = new JButton("Valider");
        JButton btnReject = new JButton("Rejeter");
        JButton btnBackToDashboard = new JButton("Retour au Dashboard");

        btnValidate.addActionListener(e -> changerStatutAchat("validé"));
        btnReject.addActionListener(e -> changerStatutAchat("rejeté"));

        btnBackToDashboard.addActionListener(e -> {
            this.dispose(); // Fermer la fenêtre actuelle
            AdminDashboardFrameBiblio dashboard = new AdminDashboardFrameBiblio(bibliothequeId);
            dashboard.setVisible(true); // Ouvrir le tableau de bord
        });

        buttonPanel.add(btnValidate);
        buttonPanel.add(btnReject);
        buttonPanel.add(btnBackToDashboard);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerAchats();
    }

    private void chargerAchats() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM achat WHERE idBibliotheque = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, bibliothequeId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    tableModel.setRowCount(0);
                    while (resultSet.next()) {
                        tableModel.addRow(new Object[]{
                                resultSet.getInt("idAchat"),
                                resultSet.getInt("idLivre"),
                                resultSet.getDate("dateAchat"),
                                resultSet.getInt("quantite"),
                                resultSet.getString("statutValidation"),
                                resultSet.getInt("idBibliothecaire")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des achats : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void changerStatutAchat(String nouveauStatut) {
        int selectedRow = achatTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un achat !", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAchat = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE achat SET statutValidation = ? WHERE idAchat = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nouveauStatut);
                statement.setInt(2, idAchat);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "L'achat a été " + nouveauStatut + " avec succès !");
                    chargerAchats();
                } else {
                    JOptionPane.showMessageDialog(this, "Aucun achat trouvé pour mettre à jour.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AchatManagementFrame(1).setVisible(true)); // Remplacez 1 par l'ID de votre bibliothèque
    }
}
