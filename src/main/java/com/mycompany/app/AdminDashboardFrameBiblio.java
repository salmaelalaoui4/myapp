package com.mycompany.app;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrameBiblio extends JFrame {

    public AdminDashboardFrameBiblio(int bibliothequeId) {
        setTitle("Tableau de Bord - Administrateur Bibliothèque " + bibliothequeId);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Tableau de Bord - Administrateur Bibliothèque " + bibliothequeId, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Boutons pour les fonctionnalités
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 20, 20)); // Mise à jour pour 5 boutons
        buttonPanel.setBackground(new Color(45, 52, 54));

        // Bouton pour gérer les bibliothécaires
        JButton btnManageLibrarians = new JButton("Gérer les Bibliothécaires");
        btnManageLibrarians.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageLibrarians.setBackground(new Color(0, 184, 148));
        btnManageLibrarians.setForeground(Color.WHITE);
        btnManageLibrarians.setFocusPainted(false);
        btnManageLibrarians.addActionListener(e -> new AdminLibrarianManagementFrame(bibliothequeId).setVisible(true));

        // Bouton pour consulter les achats
        JButton btnConsultPurchases = new JButton("Consulter les Achats");
        btnConsultPurchases.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConsultPurchases.setBackground(new Color(0, 184, 148));
        btnConsultPurchases.setForeground(Color.WHITE);
        btnConsultPurchases.setFocusPainted(false);
        btnConsultPurchases.addActionListener(e -> new GestionAchatsAdminFrame(bibliothequeId).setVisible(true));

        // Bouton pour gérer les livres
        JButton btnManageBooks = new JButton("Gérer les Livres");
        btnManageBooks.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageBooks.setBackground(new Color(0, 184, 148));
        btnManageBooks.setForeground(Color.WHITE);
        btnManageBooks.setFocusPainted(false);
        btnManageBooks.addActionListener(e -> new BooksDisplayFrame(bibliothequeId).setVisible(true));


        // Bouton pour consulter les échanges
        JButton btnConsultExchanges = new JButton("Consulter les Échanges");
        btnConsultExchanges.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConsultExchanges.setBackground(new Color(0, 184, 148));
        btnConsultExchanges.setForeground(Color.WHITE);
        btnConsultExchanges.setFocusPainted(false);
        btnConsultExchanges.addActionListener(e -> new AdminExchangeManagementFrame(bibliothequeId).setVisible(true));

        // Ajouter les boutons au panneau
        buttonPanel.add(btnManageLibrarians);
        buttonPanel.add(btnConsultPurchases);
        buttonPanel.add(btnManageBooks);
        
        buttonPanel.add(btnConsultExchanges);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboardFrameBiblio frame = new AdminDashboardFrameBiblio(1); // Changez 1 par 2 selon la bibliothèque
            frame.setVisible(true);
        });
    }

}
