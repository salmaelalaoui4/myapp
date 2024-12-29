package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminExchangeManagementFrame extends JFrame {

    private JTable exchangeTable;
    private DefaultTableModel tableModel;
    private int bibliothequeId;

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
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID Échange", "ID Livre", "Bibliothèque Source", "Bibliothèque Destination", "Date Demande", "Date Réception", "Statut"});
        exchangeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(exchangeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(45, 52, 54));

        // Bouton Ajouter
        JButton btnAddExchange = new JButton("Ajouter Échange");
        btnAddExchange.setBackground(new Color(0, 184, 148));
        btnAddExchange.setForeground(Color.WHITE);
        btnAddExchange.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAddExchange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddExchangeDialog();
            }
        });

        // Bouton Modifier
        JButton btnEditExchange = new JButton("Modifier Échange");
        btnEditExchange.setBackground(new Color(0, 184, 148));
        btnEditExchange.setForeground(Color.WHITE);
        btnEditExchange.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEditExchange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditExchangeDialog();
            }
        });

        // Bouton Supprimer
        JButton btnDeleteExchange = new JButton("Supprimer Échange");
        btnDeleteExchange.setBackground(new Color(255, 69, 58));
        btnDeleteExchange.setForeground(Color.WHITE);
        btnDeleteExchange.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDeleteExchange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteExchange();
            }
        });

        buttonPanel.add(btnAddExchange);
        buttonPanel.add(btnEditExchange);
        buttonPanel.add(btnDeleteExchange);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Charger les échanges depuis la base de données
        loadExchanges();
    }

    private void loadExchanges() {
        // Connexion à la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM echange")) {

            // Vider la table avant de recharger
            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("idEchange");
                row[1] = rs.getInt("idLivre");
                row[2] = getBibliothequeName(rs.getInt("nomBibliothequeSource"));
                row[3] = getBibliothequeName(rs.getInt("nomBibliothequeDestination"));
                row[4] = rs.getDate("dateDemande");
                row[5] = rs.getDate("dateReception");
                row[6] = rs.getString("statut");
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des échanges : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getBibliothequeName(int bibliothequeId) {
        // Récupérer le nom de la bibliothèque à partir de l'id
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT nomBibliotheque FROM bibliotheque WHERE idBibliotheque = ?")) {
            stmt.setInt(1, bibliothequeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nomBibliotheque");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Inconnu";
    }

    private void showAddExchangeDialog() {
        // Ajouter un échange (implémentez un formulaire pour ajouter un échange)
        String idLivre = JOptionPane.showInputDialog(this, "ID Livre:");
        String sourceLibraryName = JOptionPane.showInputDialog(this, "Nom Bibliothèque Source:");
        String destinationLibraryName = JOptionPane.showInputDialog(this, "Nom Bibliothèque Destination:");
        String dateDemande = JOptionPane.showInputDialog(this, "Date Demande (YYYY-MM-DD):");
        String statut = JOptionPane.showInputDialog(this, "Statut:");

        // Récupérer les id des bibliothèques sources et destinations
        int sourceLibraryId = getBibliothequeId(sourceLibraryName);
        int destinationLibraryId = getBibliothequeId(destinationLibraryName);

        // Insérer dans la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO echange (idLivre, nomBibliothequeSource, nomBibliothequeDestination, dateDemande, statut) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, Integer.parseInt(idLivre));
            stmt.setInt(2, sourceLibraryId);
            stmt.setInt(3, destinationLibraryId);
            stmt.setDate(4, Date.valueOf(dateDemande));
            stmt.setString(5, statut);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Échange ajouté avec succès.");
            loadExchanges(); // Recharger les échanges après l'ajout
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'échange : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getBibliothequeId(String bibliothequeName) {
        // Récupérer l'ID de la bibliothèque à partir de son nom
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT idBibliotheque FROM bibliotheque WHERE nomBibliotheque = ?")) {
            stmt.setString(1, bibliothequeName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idBibliotheque");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Retourner -1 si la bibliothèque n'est pas trouvée
    }

    private void showEditExchangeDialog() {
        int selectedRow = exchangeTable.getSelectedRow();
        if (selectedRow != -1) {
            int idEchange = (Integer) tableModel.getValueAt(selectedRow, 0);

            // Demander les nouvelles valeurs pour l'échange sélectionné
            String newStatut = JOptionPane.showInputDialog(this, "Statut actuel : " + tableModel.getValueAt(selectedRow, 6) + "\nNouvel Statut:");
            if (newStatut != null) {
                // Mettre à jour dans la base de données
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
                     PreparedStatement stmt = conn.prepareStatement("UPDATE echange SET statut = ? WHERE idEchange = ?")) {
                    stmt.setString(1, newStatut);
                    stmt.setInt(2, idEchange);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Échange mis à jour avec succès.");
                    loadExchanges(); // Recharger les échanges après modification
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'échange : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un échange à modifier.", "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteExchange() {
        int selectedRow = exchangeTable.getSelectedRow();
        if (selectedRow != -1) {
            int idEchange = (Integer) tableModel.getValueAt(selectedRow, 0);

            // Confirmer la suppression
            int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet échange ?", "Confirmer la suppression", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Supprimer de la base de données
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "");
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM echange WHERE idEchange = ?")) {
                    stmt.setInt(1, idEchange);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Échange supprimé avec succès.");
                    loadExchanges(); // Recharger les échanges après suppression
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'échange : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un échange à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminExchangeManagementFrame frame = new AdminExchangeManagementFrame(1); // Changez 1 par l'ID de la bibliothèque
            frame.setVisible(true);
        });
    }
}
