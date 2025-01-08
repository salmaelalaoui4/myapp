package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GestionAchatsAdminFrame extends JFrame {

    private int bibliothequeId;
    private JTable achatTable;
    private DefaultTableModel tableModel;

    public GestionAchatsAdminFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;

        setTitle("Gestion des Achats (Admin)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblTitle = new JLabel("Gestion des Achats", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID Achat", "ID Client", "ID Livre", "Quantité", "Statut"}, 0);
        achatTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(achatTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnValidate = new JButton("Valider");
        JButton btnReject = new JButton("Rejeter");

        btnValidate.addActionListener(e -> changerStatutAchat("validé"));
        btnReject.addActionListener(e -> changerStatutAchat("rejeté"));

        buttonPanel.add(btnValidate);
        buttonPanel.add(btnReject);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerAchats();
    }

    private void chargerAchats() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM achat WHERE statutValidation = 0 AND idBibliotheque = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, bibliothequeId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    tableModel.setRowCount(0); 

                    while (resultSet.next()) {
                        int idAchat = resultSet.getInt("idAchat");
                        int idClient = resultSet.getInt("idClient");
                        int idLivre = resultSet.getInt("idLivre");
                        int quantite = resultSet.getInt("quantite");
                        int statutValue = resultSet.getInt("statutValidation");

                        String statut = "En attente";
                        if (statutValue == 1) {
                            statut = "Validé";
                        } else if (statutValue == 2) {
                            statut = "Rejeté";
                        }

                        tableModel.addRow(new Object[]{idAchat, idClient, idLivre, quantite, statut});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des achats : " + e.getMessage());
        }
    }

    private void changerStatutAchat(String nouveauStatut) {
        int selectedRow = achatTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un achat !");
            return;
        }

        int idAchat = (int) tableModel.getValueAt(selectedRow, 0);
        int idLivre = (int) tableModel.getValueAt(selectedRow, 2); 
        int quantite = (int) tableModel.getValueAt(selectedRow, 3); 
        int statutValue = 0;

        switch (nouveauStatut.toLowerCase()) {
            case "validé":
                statutValue = 1;
                break;
            case "rejeté":
                statutValue = 2;
                break;
            case "en attente":
            default:
                statutValue = 0;
                break;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            
            connection.setAutoCommit(false);

            String updateAchatQuery = "UPDATE achat SET statutValidation = ? WHERE idAchat = ?";
            try (PreparedStatement updateAchatStmt = connection.prepareStatement(updateAchatQuery)) {
                updateAchatStmt.setInt(1, statutValue);
                updateAchatStmt.setInt(2, idAchat);
                updateAchatStmt.executeUpdate();
            }

            if (statutValue == 1) { 
                String updateLivreQuantiteQuery = "UPDATE livre SET quantiteDisponible = quantiteDisponible - ? WHERE idLivre = ?";
                String updateLivreStatutQuery = "UPDATE livre SET statut = IF(quantiteDisponible = 0, 'non disponible', 'disponible') WHERE idLivre = ?";

                try (PreparedStatement updateLivreQuantiteStmt = connection.prepareStatement(updateLivreQuantiteQuery); PreparedStatement updateLivreStatutStmt = connection.prepareStatement(updateLivreStatutQuery)) {

                    updateLivreQuantiteStmt.setInt(1, quantite);
                    updateLivreQuantiteStmt.setInt(2, idLivre);
                    int rowsUpdated = updateLivreQuantiteStmt.executeUpdate();

                    if (rowsUpdated == 0) {
                        throw new SQLException("Échec de la mise à jour de la quantité pour le livre ID " + idLivre);
                    }

                    updateLivreStatutStmt.setInt(1, idLivre);
                    updateLivreStatutStmt.executeUpdate();
                }
            }

            connection.commit();

            JOptionPane.showMessageDialog(this, "Achat " + nouveauStatut + " avec succès !");
            chargerAchats();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionAchatsAdminFrame(1).setVisible(true)); 
    }
}
