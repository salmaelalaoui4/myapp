package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;


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

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

       
        JLabel lblTitle = new JLabel("Gestion des Emprunts et Retours", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

       
        tableModel = new DefaultTableModel(new String[] {
            "ID Emprunt", "ID Livre", "ID Client", "Date Emprunt",
            "Date Retour Prévue", "Date Retour Effective", "Statut"
        }, 0);
        loanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

      
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnLoan = new JButton("Enregistrer un Emprunt");
        JButton btnReturn = new JButton("Valider un Retour");

        btnLoan.addActionListener(e -> afficherFormulaireEmprunt());
        btnReturn.addActionListener(e -> validerRetour());

        buttonPanel.add(btnLoan);
        buttonPanel.add(btnReturn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerEmprunts();  
    }

    private void chargerEmprunts() {
    System.out.println("Chargement des emprunts pour la bibliothèque ID: " + bibliothequeId);
    tableModel.setRowCount(0); 

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
        
        String query = "SELECT * FROM emprunt WHERE idBibliotheque = ? AND dateRetourEffective IS NULL";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bibliothequeId); 
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
                        statutEmprunt  
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
    String statut = "en cours"; 
    Date currentDate = new Date(System.currentTimeMillis());

    
    if (dateRetourPrevue.before(currentDate)) {
        statut = "en retard";
    }

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
        connection.setAutoCommit(false); 

        try {
          
            String checkQuery = "SELECT quantiteDisponible, statut FROM livre WHERE idLivre = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, idLivre);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int quantiteDisponible = resultSet.getInt("quantiteDisponible");
                        String statutLivre = resultSet.getString("statut");

                        if (quantiteDisponible <= 0 || "non disponible".equalsIgnoreCase(statutLivre)) {
                            throw new SQLException("Le livre n'est pas disponible pour l'emprunt.");
                        }
                    } else {
                        throw new SQLException("Livre introuvable.");
                    }
                }
            }

            
            String query = "INSERT INTO emprunt (idLivre, idClient, idBibliotheque, dateEmprunt, dateRetourPrevue, statut) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idLivre);
                statement.setInt(2, idClient);
                statement.setInt(3, bibliothequeId);
                statement.setDate(4, dateEmprunt);
                statement.setDate(5, dateRetourPrevue);
                statement.setString(6, statut);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted <= 0) {
                    throw new SQLException("Erreur lors de l'insertion de l'emprunt.");
                }
            }

            String updateQuery = "UPDATE livre SET quantiteDisponible = quantiteDisponible - 1, statut = IF(quantiteDisponible = 1, 'non disponible', 'disponible') WHERE idLivre = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, idLivre);
                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated <= 0) {
                    throw new SQLException("Quantité insuffisante pour le livre sélectionné.");
                }
            }

            connection.commit();
            JOptionPane.showMessageDialog(null, "Emprunt enregistré avec succès !");
            chargerEmprunts(); 
        } catch (SQLException e) {
            connection.rollback(); 
            throw e;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement de l'emprunt : " + e.getMessage());
    }
}


    private void afficherFormulaireEmprunt() {
       
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
    int idLivre = (int) tableModel.getValueAt(selectedRow, 1); // Récupérer l'ID du livre
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
        connection.setAutoCommit(false); 

        try {
            
            String query = "UPDATE emprunt SET dateRetourEffective = NOW(), statut = 'retourné' WHERE idEmprunt = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idEmprunt);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated <= 0) {
                    throw new SQLException("Erreur lors de la mise à jour de l'emprunt.");
                }
            }

          
            String updateQuery = "UPDATE livre SET quantiteDisponible = quantiteDisponible + 1 WHERE idLivre = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, idLivre);
                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated <= 0) {
                    throw new SQLException("Erreur lors de la mise à jour de la quantité du livre.");
                }
            }

            connection.commit();
            JOptionPane.showMessageDialog(this, "Retour validé avec succès !");
            chargerEmprunts(); 
        } catch (SQLException e) {
            connection.rollback(); 
            throw e;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors de la validation du retour : " + e.getMessage());
    }
}


    public static void main(String[] args) {
        int bibliothequeId = 1; 
        SwingUtilities.invokeLater(() -> {
            LoanManagementFrame frame = new LoanManagementFrame(bibliothequeId);
            frame.setVisible(true);
        });
    }
}
