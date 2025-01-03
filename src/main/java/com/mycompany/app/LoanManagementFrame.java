package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanManagementFrame extends JFrame {

    private int bibliothequeId;
    private JTable loanTable;
    private DefaultTableModel tableModel;

    public LoanManagementFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;

        setTitle("Gestion des Emprunts et Retours");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Emprunts et Retours", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Tableau des emprunts
        tableModel = new DefaultTableModel(new String[] {
            "ID Emprunt", "ID Livre", "ID Client", "Date Emprunt",
            "Date Retour Prévue", "Date Retour Effective", "Statut"
        }, 0);
        loanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnLoan = new JButton("Enregistrer un Emprunt");
        JButton btnReturn = new JButton("Valider un Retour");

        btnLoan.addActionListener(e -> afficherFormulaireEmprunt());
        btnReturn.addActionListener(e -> validerRetour());

        buttonPanel.add(btnLoan);
        buttonPanel.add(btnReturn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerEmprunts();  // Charger les emprunts au démarrage de l'interface
    }

    private void chargerEmprunts() {
        System.out.println("Chargement des emprunts pour la bibliothèque ID: " + bibliothequeId);
        tableModel.setRowCount(0); // Réinitialiser le tableau avant d'ajouter les nouvelles lignes

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM emprunt WHERE idBibliotheque = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, bibliothequeId);  // Passer l'ID de la bibliothèque
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String statutEmprunt = resultSet.getString("statut");
                        tableModel.addRow(new Object[] {
                            resultSet.getInt("idEmprunt"),
                            resultSet.getInt("idLivre"),
                            resultSet.getInt("idClient"),
                            resultSet.getDate("dateEmprunt"),
                            resultSet.getDate("dateRetourPrevue"),
                            resultSet.getDate("dateRetourEffective"),
                            statutEmprunt  // Afficher le statut
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des emprunts : " + e.getMessage());
        }
    }

    public void enregistrerEmprunt(int idLivre, int idClient, Date dateEmprunt, Date dateRetourPrevue) {
        String statut = "en cours"; // Par défaut, le statut est "en cours"
        Date currentDate = new Date(System.currentTimeMillis());

        // Si la date de retour prévue est dépassée, changer le statut à "en retard"
        if (dateRetourPrevue.before(currentDate)) {
            statut = "en retard";
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO emprunt (idLivre, idClient, idBibliotheque, dateEmprunt, dateRetourPrevue, statut) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idLivre);
                statement.setInt(2, idClient);
                statement.setInt(3, bibliothequeId);
                statement.setDate(4, dateEmprunt);
                statement.setDate(5, dateRetourPrevue);
                statement.setString(6, statut);  // Insérer le statut calculé

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Emprunt enregistré avec succès !");
                    chargerEmprunts();  // Recharger les emprunts après insertion
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement de l'emprunt.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement de l'emprunt : " + e.getMessage());
        }
    }

    private void afficherFormulaireEmprunt() {
        // Ouvrir un formulaire pour enregistrer un emprunt (à implémenter)
         EmpruntFormulaireFrame formulaireFrame = new EmpruntFormulaireFrame(this, bibliothequeId);
         formulaireFrame.setVisible(true);
    }

    private void validerRetour() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt !");
            return;
        }

        int idEmprunt = (int) tableModel.getValueAt(selectedRow, 0);
        String statutEmprunt = (String) tableModel.getValueAt(selectedRow, 6);
        if ("Retourné".equals(statutEmprunt)) {
            JOptionPane.showMessageDialog(this, "Cet emprunt a déjà été retourné.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment valider ce retour ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE emprunt SET dateRetourEffective = NOW(), statut = 'retourné' WHERE idEmprunt = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idEmprunt);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Retour validé avec succès !");
                    chargerEmprunts();  // Recharger les emprunts après le retour
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la validation du retour.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la validation du retour : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int bibliothequeId = 1;  // Exemple : bibliothèque avec ID 1
        SwingUtilities.invokeLater(() -> {
            LoanManagementFrame frame = new LoanManagementFrame(bibliothequeId);
            frame.setVisible(true);
        });
    }
}
