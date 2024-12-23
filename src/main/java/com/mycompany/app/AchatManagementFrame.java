package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AchatManagementFrame extends JFrame {

    private JTable achatTable;
    private DefaultTableModel tableModel;

    public AchatManagementFrame() {
        setTitle("Gestion des Achats");
        setSize(900, 600);
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
            String query = "SELECT * FROM achat";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            tableModel.setRowCount(0); // Effacer les anciennes données

            while (resultSet.next()) {
                int idAchat = resultSet.getInt("idAchat");
                int idLivre = resultSet.getInt("idLivre");
                Date dateAchat = resultSet.getDate("dateAchat");
                int quantite = resultSet.getInt("quantite");
                String statut = resultSet.getString("statutValidation");
                int idBibliothecaire = resultSet.getInt("idBibliothecaire");

                tableModel.addRow(new Object[]{idAchat, idLivre, dateAchat, quantite, statut, idBibliothecaire});
            }
        } catch (Exception e) {
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

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE achat SET statutValidation = ? WHERE idAchat = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nouveauStatut);
            statement.setInt(2, idAchat);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "L'achat a été " + nouveauStatut + " avec succès !");
            chargerAchats();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AchatManagementFrame().setVisible(true));
    }
}
