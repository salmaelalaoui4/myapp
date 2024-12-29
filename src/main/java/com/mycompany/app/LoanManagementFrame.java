package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LoanManagementFrame extends JFrame {

    private static int bibliothequeId;

    private JTable loanTable;
    private DefaultTableModel tableModel;

    public LoanManagementFrame(int bibliothequeId) {
        
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
        tableModel = new DefaultTableModel(new String[]{"ID Emprunt", "ID Livre", "ID Client", "Date Emprunt", "Date Retour Prévue", "Statut"}, 0);
        loanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton btnLoan = new JButton("Enregistrer un Emprunt");
        JButton btnReturn = new JButton("Valider un Retour");

        btnLoan.addActionListener(e -> enregistrerEmprunt());
        btnReturn.addActionListener(e -> validerRetour());

        buttonPanel.add(btnLoan);
        buttonPanel.add(btnReturn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerEmprunts();
    }

    private void chargerEmprunts() {
        tableModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM emprunt";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("idEmprunt"),
                        resultSet.getInt("idLivre"),
                        resultSet.getInt("idClient"),
                        resultSet.getDate("dateEmprunt"),
                        resultSet.getDate("dateRetourPrevue"),
                        resultSet.getBoolean("statut") ? "Retourné" : "En Cours"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des emprunts : " + e.getMessage());
        }
    }

    private void enregistrerEmprunt() {
    String idLivre = JOptionPane.showInputDialog(this, "ID du Livre :");
    String idClient = JOptionPane.showInputDialog(this, "ID du Client :");
    String dateRetourPrevue = JOptionPane.showInputDialog(this, "Date Retour Prévue (YYYY-MM-DD) :");

    // Validation des entrées
    if (idLivre == null || idClient == null || dateRetourPrevue == null || idLivre.trim().isEmpty() || idClient.trim().isEmpty() || dateRetourPrevue.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.");
        return;
    }

    try {
        int livreID = Integer.parseInt(idLivre);
        int clientID = Integer.parseInt(idClient);

        // Vérification que le livre et le client existent
        if (!livreExiste(livreID) || !clientExiste(clientID)) {
            JOptionPane.showMessageDialog(this, "Le livre ou le client n'existe pas.");
            return;
        }

        // Enregistrer l'emprunt
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO emprunt (idLivre, idClient, dateEmprunt, dateRetourPrevue, statut) VALUES (?, ?, NOW(), ?, 0)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, livreID);
            statement.setInt(2, clientID);
            statement.setString(3, dateRetourPrevue);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Emprunt enregistré avec succès !");
            chargerEmprunts();
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "L'ID du livre et de l'emprunteur doivent être des entiers.");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'emprunt : " + e.getMessage());
    }
}

private boolean livreExiste(int livreID) {
    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
        String query = "SELECT COUNT(*) FROM livre WHERE idLivre = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, livreID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1) > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

private boolean clientExiste(int clientID) {
    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
        String query = "SELECT COUNT(*) FROM client WHERE idClient = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, clientID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1) > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}


    private void validerRetour() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt !");
            return;
        }

        int idEmprunt = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "UPDATE emprunt SET dateRetourEffective = NOW(), statut = 1 WHERE idEmprunt = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idEmprunt);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Retour validé avec succès !");
            chargerEmprunts();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la validation du retour : " + e.getMessage());
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
        SwingUtilities.invokeLater(() -> new LoanManagementFrame(bibliothequeId).setVisible(true));
    }
}
