package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AdminLibrarianManagementFrame extends JFrame {

    private static int bibliothequeId;
    private JTable librarianTable;
    private DefaultTableModel tableModel;

    public AdminLibrarianManagementFrame(int bibliothequeId) {
         this.bibliothequeId = bibliothequeId;
        setTitle("Gestion des Bibliothécaires");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Bibliothécaires", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Tableau des bibliothécaires
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Email", "Téléphone", "Statut"}, 0);
        librarianTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(librarianTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnAdd = new JButton("Ajouter");
        JButton btnDelete = new JButton("Supprimer");// Nouveau bouton pour revenir au Dashboard

    btnAdd.addActionListener(e -> {
    // Ouvrir le formulaire pour ajouter un bibliothécaire et passer la référence à la fenêtre admin
    AddLibrarianFormulaire formulaire = new AddLibrarianFormulaire(bibliothequeId, this);  // Passer "this" (la fenêtre actuelle) à AddLibrarianFormulaire
    formulaire.setVisible(true);
});


        btnDelete.addActionListener(e -> supprimerBibliothecaire());

         // Action pour le bouton Retour au Dashboard
        
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerBibliothecaires();
    }

    void chargerBibliothecaires() {
    tableModel.setRowCount(0);  // Effacer les anciennes lignes
    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
        // Utilisation de l'ID de la bibliothèque de l'administrateur pour filtrer les bibliothécaires
        String query = "SELECT * FROM utilisateur WHERE role = 'bibliothecaire' AND idBibliotheque = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, bibliothequeId);  // Paramétrage de l'ID de la bibliothèque
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            tableModel.addRow(new Object[]{
                resultSet.getInt("idUtilisateur"),
                resultSet.getString("nom"),
                resultSet.getString("prenom"), // Ajout du prénom
                resultSet.getString("email"),
                resultSet.getString("telephone"),
                resultSet.getBoolean("statut") ? "Actif" : "Désactivé"
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors du chargement des bibliothécaires : " + e.getMessage());
    }
}



    

    private void supprimerBibliothecaire() {
        int selectedRow = librarianTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un bibliothécaire !");
            return;
        }

        int idUtilisateur = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "DELETE FROM utilisateur WHERE idUtilisateur = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idUtilisateur);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bibliothécaire supprimé avec succès !");
            chargerBibliothecaires();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du bibliothécaire : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLibrarianManagementFrame(bibliothequeId).setVisible(true));
    }
}
