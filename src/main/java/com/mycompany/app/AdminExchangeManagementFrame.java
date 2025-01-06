package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class AdminExchangeManagementFrame extends JFrame {

    private JTable exchangeTable;
    private DefaultTableModel tableModel;
    private int bibliothequeId;
    private JButton btnAccept, btnReject;
    private int selectedExchangeId;

    public AdminExchangeManagementFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;
        setTitle("Gestion des Échanges - Bibliothèque " + bibliothequeId);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Échanges - Bibliothèque " + bibliothequeId, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Table pour afficher les échanges
        tableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"ID Échange", "Livre", "Source", "Destination", "Quantité", "Date Demande", "Date Réception", "Statut"});
        exchangeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(exchangeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(45, 52, 54));

        // Bouton pour voir les échanges demandés par l'administrateur
        JButton btnMyExchanges = new JButton("Mes Échanges");
        btnMyExchanges.setBackground(new Color(0, 184, 148));
        btnMyExchanges.setForeground(Color.WHITE);
        btnMyExchanges.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMyExchanges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadExchanges("my");
            }
        });

        // Bouton pour voir les échanges avec les autres bibliothèques
        JButton btnOtherLibraryExchanges = new JButton("Échanges avec Autres Bibliothèques");
        btnOtherLibraryExchanges.setBackground(new Color(0, 123, 255));
        btnOtherLibraryExchanges.setForeground(Color.WHITE);
        btnOtherLibraryExchanges.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnOtherLibraryExchanges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadExchanges("other");
            }
        });

        buttonPanel.add(btnMyExchanges);
        buttonPanel.add(btnOtherLibraryExchanges);

        // Boutons d'acceptation et de rejet
        btnAccept = new JButton("Accepter");
        btnAccept.setBackground(new Color(0, 184, 148));
        btnAccept.setForeground(Color.WHITE);
        btnAccept.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAccept.setEnabled(false); // Désactivé au départ
        btnAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExchangeStatus("accepté");
            }
        });

        btnReject = new JButton("Rejeter");
        btnReject.setBackground(new Color(255, 99, 71));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnReject.setEnabled(false); // Désactivé au départ
        btnReject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExchangeStatus("rejeté");
            }
        });

        buttonPanel.add(btnAccept);
        buttonPanel.add(btnReject);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Charger les échanges par défaut (demandés par l'administrateur)
        loadExchanges("my");

        // Écouter les sélections de la table
        exchangeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = exchangeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedExchangeId = (int) tableModel.getValueAt(selectedRow, 0);
                    String sourceLibrary = (String) tableModel.getValueAt(selectedRow, 2);
                    String destinationLibrary = (String) tableModel.getValueAt(selectedRow, 3);

                    // Activer les boutons seulement si l'échange provient d'une autre bibliothèque
                    if (!destinationLibrary.equals(getBibliothequeName(bibliothequeId))) {
                        btnAccept.setEnabled(true);
                        btnReject.setEnabled(true);
                    } else {
                        btnAccept.setEnabled(false);
                        btnReject.setEnabled(false);
                    }
                }
            }
        });
    }

    private void loadExchanges(String type) {
        tableModel.setRowCount(0);
        String query = "";

        if ("my".equals(type)) {
            // Charger les échanges faits par l'administrateur
            query = "SELECT e.idEchange, l.titre, e.nomBibliothequeSource, e.nomBibliothequeDestination, e.quantite, e.dateDemande, e.dateReception, e.statut " +
                    "FROM echange e JOIN livre l ON e.idLivre = l.idLivre " +
                    "WHERE e.nomBibliothequeSource = ?";
        } else if ("other".equals(type)) {
            // Charger les échanges faits avec d'autres bibliothèques
            query = "SELECT e.idEchange, l.titre, e.nomBibliothequeSource, e.nomBibliothequeDestination, e.quantite, e.dateDemande, e.dateReception, e.statut " +
                    "FROM echange e JOIN livre l ON e.idLivre = l.idLivre " +
                    "WHERE e.nomBibliothequeDestination = ?";
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if ("my".equals(type)) {
                stmt.setString(1, getBibliothequeName(bibliothequeId));
            } else if ("other".equals(type)) {
                stmt.setString(1, getBibliothequeName(bibliothequeId));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getInt("idEchange");
                row[1] = rs.getString("titre");
                row[2] = rs.getString("nomBibliothequeSource");
                row[3] = rs.getString("nomBibliothequeDestination");
                row[4] = rs.getInt("quantite");
                row[5] = rs.getDate("dateDemande");
                row[6] = rs.getDate("dateReception");
                row[7] = rs.getString("statut");

                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des échanges : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExchangeStatus(String status) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE echange SET statut = ? WHERE idEchange = ?")) {
            stmt.setString(1, status);
            stmt.setInt(2, selectedExchangeId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Échange " + status + " avec succès.");
            loadExchanges("other"); // Recharger la liste des échanges après mise à jour
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de l'échange : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getBibliothequeName(int idBibliotheque) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT nom FROM bibliotheque WHERE idBibliotheque = ?")) {
            stmt.setInt(1, idBibliotheque);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Inconnu";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminExchangeManagementFrame frame = new AdminExchangeManagementFrame(1);
            frame.setVisible(true);
        });
    }
}
