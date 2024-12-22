package com.mycompany.app;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame() {
        setTitle("Tableau de Bord - Administrateur");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Tableau de Bord - Administrateur", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Boutons pour les fonctionnalités
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBackground(new Color(45, 52, 54));

        // Bouton pour gérer les bibliothécaires
        JButton btnManageLibrarians = new JButton("Gérer les Bibliothécaires");
        btnManageLibrarians.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageLibrarians.setBackground(new Color(0, 184, 148));
        btnManageLibrarians.setForeground(Color.WHITE);
        btnManageLibrarians.setFocusPainted(false);
        btnManageLibrarians.addActionListener(e -> new AdminLibrarianManagementFrame().setVisible(true));

        // Bouton pour valider les achats
        JButton btnValidatePurchases = new JButton("Valider les Achats");
        btnValidatePurchases.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnValidatePurchases.setBackground(new Color(0, 184, 148));
        btnValidatePurchases.setForeground(Color.WHITE);
        btnValidatePurchases.setFocusPainted(false);
        btnValidatePurchases.addActionListener(e -> JOptionPane.showMessageDialog(this, "Validation des achats à implémenter !"));

        // Bouton pour gérer les livres
        JButton btnManageBooks = new JButton("Gérer les Livres");
        btnManageBooks.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageBooks.setBackground(new Color(0, 184, 148));
        btnManageBooks.setForeground(Color.WHITE);
        btnManageBooks.setFocusPainted(false);
        btnManageBooks.addActionListener(e -> new BookManagementFrame().setVisible(true));

        // Ajouter les boutons au panneau
        buttonPanel.add(btnManageLibrarians);
        buttonPanel.add(btnValidatePurchases);
        buttonPanel.add(btnManageBooks);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboardFrame frame = new AdminDashboardFrame();
            frame.setVisible(true);
        });
    }
}
