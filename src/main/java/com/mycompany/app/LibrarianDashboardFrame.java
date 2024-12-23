package com.mycompany.app;

import javax.swing.*;
import java.awt.*;

public class LibrarianDashboardFrame extends JFrame {

    public LibrarianDashboardFrame() {
        setTitle("Tableau de Bord - Bibliothécaire");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Tableau de Bord - Bibliothécaire", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Boutons pour les fonctionnalités
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBackground(new Color(45, 52, 54));

        // Bouton pour gérer les clients
        JButton btnManageClients = new JButton("Gérer les Clients");
        btnManageClients.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageClients.setBackground(new Color(0, 184, 148));
        btnManageClients.setForeground(Color.WHITE);
        btnManageClients.setFocusPainted(false);
        btnManageClients.addActionListener(e -> new ClientManagementFrame().setVisible(true)); // Ouvre la gestion des clients

        // Bouton pour gérer les achats
        JButton btnManageAchat = new JButton("Gérer les Achats");
        btnManageAchat.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageAchat.setBackground(new Color(0, 184, 148));
        btnManageAchat.setForeground(Color.WHITE);
        btnManageAchat.setFocusPainted(false);
        btnManageAchat.addActionListener(e -> {
            // Ouvre la fenêtre de gestion des achats
            new AchatCreationFrame().setVisible(true);
        });

        // Bouton pour gérer les emprunts et retours
        JButton btnManageLoans = new JButton("Gérer les Emprunts et Retours");
        btnManageLoans.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageLoans.setBackground(new Color(0, 184, 148));
        btnManageLoans.setForeground(Color.WHITE);
        btnManageLoans.setFocusPainted(false);
        btnManageLoans.addActionListener(e -> JOptionPane.showMessageDialog(this, "Gestion des emprunts à implémenter !")); // Placeholder

        buttonPanel.add(btnManageClients);
        buttonPanel.add(btnManageAchat);  // Ajoutez ce bouton pour gérer les achats
        buttonPanel.add(btnManageLoans);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }
}
