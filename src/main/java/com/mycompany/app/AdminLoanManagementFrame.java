package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminLoanManagementFrame extends JFrame {

    private JTable loanTable;
    private DefaultTableModel tableModel;
    private int bibliothequeId;

    public AdminLoanManagementFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;
        setTitle("Gestion des Emprunts - Bibliothèque " + bibliothequeId);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Emprunts - Bibliothèque " + bibliothequeId, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Table pour afficher les emprunts
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID Emprunt", "ID Livre", "ID Client", "Date Emprunt", "Date Retour Prévue", "Date Retour Effective", "Statut"});
        loanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(45, 52, 54));

        // Bouton Ajouter
        JButton btnAddLoan = new JButton("Ajouter Emprunt");
        btnAddLoan.setBackground(new Color(0, 184, 148));
        btnAddLoan.setForeground(Color.WHITE);
        btnAddLoan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAddLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddLoanDialog();
            }
        });

        // Bouton Modifier
        JButton btnEditLoan = new JButton("Modifier Emprunt");
        btnEditLoan.setBackground(new Color(0, 184, 148));
        btnEditLoan.setForeground(Color.WHITE);
        btnEditLoan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEditLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditLoanDialog();
            }
        });

        // Bouton Supprimer
        JButton btnDeleteLoan = new JButton("Supprimer Emprunt");
        btnDeleteLoan.setBackground(new Color(255, 69, 58));
        btnDeleteLoan.setForeground(Color.WHITE);
        btnDeleteLoan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDeleteLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLoan();
            }
        });

        buttonPanel.add(btnAddLoan);
        buttonPanel.add(btnEditLoan);
        buttonPanel.add(btnDeleteLoan);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Charger les emprunts depuis la base de données
        loadLoans();
    }

    private void loadLoans() {
        // Connexion à la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotheque", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM emprunt WHERE bibliothequeId = " + bibliothequeId)) {

            // Vider la table avant de recharger
            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("idEmprunt");
                row[1] = rs.getInt("idLivre");
                row[2] = rs.getInt("idClient");
                row[3] = rs.getDate("dateEmprunt");
                row[4] = rs.getDate("dateRetourPrevue");
                row[5] = rs.getDate("dateRetourEffective");
                row[6] = rs.getString("statut");
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des emprunts : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddLoanDialog() {
        // Ajouter un emprunt (implémentez un formulaire pour ajouter un emprunt)
        String idLivre = JOptionPane.showInputDialog(this, "ID Livre:");
        String idClient = JOptionPane.showInputDialog(this, "ID Client:");
        String dateEmprunt = JOptionPane.showInputDialog(this, "Date Emprunt (YYYY-MM-DD):");
        String dateRetourPrevue = JOptionPane.showInputDialog(this, "Date Retour Prévue (YYYY-MM-DD):");
        String statut = JOptionPane.showInputDialog(this, "Statut:");

        // Insérer dans la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotheque", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO emprunt (idLivre, idClient, dateEmprunt, dateRetourPrevue, statut, bibliothequeId) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, Integer.parseInt(idLivre));
            stmt.setInt(2, Integer.parseInt(idClient));
            stmt.setDate(3, Date.valueOf(dateEmprunt));
            stmt.setDate(4, Date.valueOf(dateRetourPrevue));
            stmt.setString(5, statut);
            stmt.setInt(6, bibliothequeId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Emprunt ajouté avec succès.");
            loadLoans(); // Recharger les emprunts après l'ajout
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'emprunt : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditLoanDialog() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow != -1) {
            int idEmprunt = (Integer) tableModel.getValueAt(selectedRow, 0);

            // Demander les nouvelles valeurs pour l'emprunt sélectionné
            String newStatut = JOptionPane.showInputDialog(this, "Statut actuel : " + tableModel.getValueAt(selectedRow, 6) + "\nNouvel Statut:");
            if (newStatut != null) {
                // Mettre à jour dans la base de données
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotheque", "root", "");
                     PreparedStatement stmt = conn.prepareStatement("UPDATE emprunt SET statut = ? WHERE idEmprunt = ?")) {
                    stmt.setString(1, newStatut);
                    stmt.setInt(2, idEmprunt);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Emprunt mis à jour avec succès.");
                    loadLoans(); // Recharger les emprunts après modification
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'emprunt : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à modifier.", "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteLoan() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow != -1) {
            int idEmprunt = (Integer) tableModel.getValueAt(selectedRow, 0);

            // Confirmer la suppression
            int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet emprunt ?", "Confirmer la suppression", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Supprimer de la base de données
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotheque", "root", "");
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM emprunt WHERE idEmprunt = ?")) {
                    stmt.setInt(1, idEmprunt);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Emprunt supprimé avec succès.");
                    loadLoans(); // Recharger les emprunts après suppression
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'emprunt : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLoanManagementFrame frame = new AdminLoanManagementFrame(1); // Changez 1 par l'ID de la bibliothèque
            frame.setVisible(true);
        });
    }
}
