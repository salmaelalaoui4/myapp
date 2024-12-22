package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PurchaseManagementFrame extends JFrame {

    private JTable purchaseTable;
    private DefaultTableModel tableModel;

    public PurchaseManagementFrame() {
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
        tableModel = new DefaultTableModel(new String[]{"ID Achat", "ID Livre", "ID Client", "Date Achat", "Quantité", "Statut"}, 0);
        purchaseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(purchaseTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnAdd = new JButton("Enregistrer un Achat");

        btnAdd.addActionListener(e -> enregistrerAchat());

        buttonPanel.add(btnAdd);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerAchats();
    }

    private void chargerAchats() {
        tableModel.setRowCount(0); // Vider le tableau
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM achat";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("idAchat"),
                        resultSet.getInt("idLivre"),
                        resultSet.getInt("idClient"),
                        resultSet.getDate("dateAchat"),
                        resultSet.getInt("quantite"),
                        resultSet.getBoolean("statutValidation") ? "Validé" : "En Attente"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des achats : " + e.getMessage());
        }
    }

    private void enregistrerAchat() {
        try {
            String idLivre = JOptionPane.showInputDialog(this, "ID du Livre :");
            String idClient = JOptionPane.showInputDialog(this, "ID du Client :");
            String quantite = JOptionPane.showInputDialog(this, "Quantité :");

            if (idLivre == null || idClient == null || quantite == null ||
                    idLivre.isEmpty() || idClient.isEmpty() || quantite.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
                return;
            }

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
                String query = "INSERT INTO achat (idLivre, idClient, dateAchat, quantite, statutValidation) VALUES (?, ?, NOW(), ?, 0)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, Integer.parseInt(idLivre));
                statement.setInt(2, Integer.parseInt(idClient));
                statement.setInt(3, Integer.parseInt(quantite));
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Achat enregistré avec succès !");
                chargerAchats();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'achat : " + e.getMessage());
        }
    }
    private int selectionnerLivre() {
    DefaultListModel<String> listModel = new DefaultListModel<>();
    JList<String> bookList = new JList<>(listModel);
    bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
        String query = "SELECT idLivre, titre FROM livre WHERE statut = 1";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            int idLivre = resultSet.getInt("idLivre");
            String titre = resultSet.getString("titre");
            listModel.addElement(idLivre + " - " + titre);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
    }

    if (listModel.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Aucun livre disponible.");
        return -1;
    }

    int result = JOptionPane.showConfirmDialog(this, new JScrollPane(bookList), "Choisissez un Livre",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        String selectedValue = bookList.getSelectedValue();
        if (selectedValue != null) {
            return Integer.parseInt(selectedValue.split(" - ")[0]); // Retourne l'ID du livre
        }
    }

    return -1;
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PurchaseManagementFrame().setVisible(true));
    }
}
