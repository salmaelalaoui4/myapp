package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class BookManagementFrame extends JFrame {

    private JTable bookTable;
    private DefaultTableModel tableModel;
    private int currentUserLibraryId;
    private int userLibraryId;

    public BookManagementFrame(int userLibraryId) {
       this.currentUserLibraryId = userLibraryId;
        setTitle("Gestion des Livres");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblTitle = new JLabel("Gestion des Livres", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(new Object[]{"ID", "Titre", "Auteur", "Année", "ISBN"}, 0);  // Correction: Object[] au lieu de String[]
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        buttonPanel.setBackground(new Color(45, 52, 54));

        JButton btnAddBook = new JButton("Ajouter un Livre");
        btnAddBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAddBook.setBackground(new Color(0, 184, 148));
        btnAddBook.setForeground(Color.WHITE);
        btnAddBook.setFocusPainted(false);
        btnAddBook.addActionListener(e -> {
            AddBookFrame addBookFrame = new AddBookFrame(this,currentUserLibraryId);  // Passe la référence du parent
            addBookFrame.setVisible(true);
        });

        JButton btnEditBook = new JButton("Modifier un Livre");
        btnEditBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEditBook.setBackground(new Color(0, 123, 255));
        btnEditBook.setForeground(Color.WHITE);
        btnEditBook.setFocusPainted(false);
        btnEditBook.addActionListener(e -> modifierLivre());

        JButton btnDeleteBook = new JButton("Supprimer un Livre");
        btnDeleteBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDeleteBook.setBackground(new Color(255, 0, 0));
        btnDeleteBook.setForeground(Color.WHITE);
        btnDeleteBook.setFocusPainted(false);
        btnDeleteBook.addActionListener(e -> supprimerLivre());

        buttonPanel.add(btnAddBook);
        buttonPanel.add(btnEditBook);
        buttonPanel.add(btnDeleteBook);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerLivres(); 
    }

    
    public void chargerLivres() {
        tableModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre WHERE idBibliotheque = ?";   
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, currentUserLibraryId);
            ResultSet resultSet = statement.executeQuery();

            
            while (resultSet.next()) {
                int idLivre = resultSet.getInt("idLivre");
                String titre = resultSet.getString("titre");
                String auteur = resultSet.getString("auteur");
                String isbn = resultSet.getString("isbn");
                String annee = resultSet.getString("anneePublication");
                int quantiteDisponible = resultSet.getInt("quantiteDisponible");

                
                tableModel.addRow(new Object[]{idLivre, titre, auteur, annee, isbn});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    
    private void modifierLivre() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            
            int idLivre = (int) tableModel.getValueAt(selectedRow, 0);
            
            EditBookFrame editBookFrame = new EditBookFrame(this, idLivre);
            editBookFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à modifier.");
        }
    }

    
    private void supprimerLivre() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            
            int idLivre = (int) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce livre ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
                    
                    String query = "DELETE FROM livre WHERE idLivre = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, idLivre);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                       
                        tableModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Livre supprimé avec succès.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du livre.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du livre : " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à supprimer.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookManagementFrame(1).setVisible(true));
    }
}