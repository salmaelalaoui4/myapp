package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GestionAchatsAdminFrame extends JFrame {

    private JTable achatTable;
    private DefaultTableModel tableModel;

    public GestionAchatsAdminFrame() {
        setTitle("Gestion des Achats (Admin)");
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
        tableModel = new DefaultTableModel(new String[]{"ID Achat", "ID Client", "ID Livre", "Quantité", "Statut"}, 0);
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
            String query = "SELECT * FROM achat WHERE statutValidation = 'en attente'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            tableModel.setRowCount(0); // Effacer les anciennes données

            while (resultSet.next()) {
                int idAchat = resultSet.getInt("idAchat");
                int idClient = resultSet.getInt("idClient");
                int idLivre = resultSet.getInt("idLivre");
                int quantite = resultSet.getInt("quantite");
                String statut = resultSet.getString("statutValidation");

                tableModel.addRow(new Object[]{idAchat, idClient, idLivre, quantite, statut});
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

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE achat SET statutValidation = ? WHERE idAchat = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nouveauStatut);
            statement.setInt(2, idAchat);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Achat " + nouveauStatut + " avec succès !");
            chargerAchats();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionAchatsAdminFrame().setVisible(true));
    }
}
