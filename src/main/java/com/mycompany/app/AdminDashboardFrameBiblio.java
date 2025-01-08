package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardFrameBiblio extends JFrame {

  
    private String getBibliothequeName(int bibliothequeId) {
        String bibliothequeName = "Bibliothèque Inconnue";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT nom FROM bibliotheque WHERE idBibliotheque = ?")) {
            stmt.setInt(1, bibliothequeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                bibliothequeName = rs.getString("nom");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bibliothequeName;
    }

    public AdminDashboardFrameBiblio(int bibliothequeId) {
      
        String bibliothequeName = getBibliothequeName(bibliothequeId);

        setTitle("Tableau de Bord - Administrateur Bibliothèque " + bibliothequeId);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

       
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblTitle = new JLabel("Tableau de Bord - " + bibliothequeName, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

       
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 20, 20)); // Mise à jour pour 5 boutons
        buttonPanel.setBackground(new Color(45, 52, 54));

       
        JButton btnManageLibrarians = new JButton("Gérer les Bibliothécaires");
        btnManageLibrarians.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageLibrarians.setBackground(new Color(0, 184, 148));
        btnManageLibrarians.setForeground(Color.WHITE);
        btnManageLibrarians.setFocusPainted(false);
        btnManageLibrarians.addActionListener(e -> new AdminLibrarianManagementFrame(bibliothequeId).setVisible(true));

       
        JButton btnConsultPurchases = new JButton("Consulter les Achats");
        btnConsultPurchases.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConsultPurchases.setBackground(new Color(0, 184, 148));
        btnConsultPurchases.setForeground(Color.WHITE);
        btnConsultPurchases.setFocusPainted(false);
        btnConsultPurchases.addActionListener(e -> new GestionAchatsAdminFrame(bibliothequeId).setVisible(true));

 
        JButton btnManageBooks = new JButton("Gérer les Livres");
        btnManageBooks.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageBooks.setBackground(new Color(0, 184, 148));
        btnManageBooks.setForeground(Color.WHITE);
        btnManageBooks.setFocusPainted(false);
        btnManageBooks.addActionListener(e -> new BooksDisplayFrame(bibliothequeId).setVisible(true));

       
        JButton btnConsultExchanges = new JButton("Consulter les Échanges");
        btnConsultExchanges.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConsultExchanges.setBackground(new Color(0, 184, 148));
        btnConsultExchanges.setForeground(Color.WHITE);
        btnConsultExchanges.setFocusPainted(false);
        btnConsultExchanges.addActionListener(e -> new AdminExchangeManagementFrame(bibliothequeId).setVisible(true));

        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogout.setBackground(new Color(255, 71, 87));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true); 
        });

        buttonPanel.add(btnManageLibrarians);
        buttonPanel.add(btnConsultPurchases);
        buttonPanel.add(btnManageBooks);
        buttonPanel.add(btnConsultExchanges);
        buttonPanel.add(btnLogout);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboardFrameBiblio frame = new AdminDashboardFrameBiblio(1); 
            frame.setVisible(true);
        });
    }
}
