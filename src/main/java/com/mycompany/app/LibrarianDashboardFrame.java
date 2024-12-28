package com.mycompany.app;

import javax.swing.*;
import java.awt.*;

public class LibrarianDashboardFrame extends JFrame {

    public LibrarianDashboardFrame() {
        setTitle("Tableau de Bord - Bibliothécaire");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblTitle = new JLabel("Tableau de Bord - Bibliothécaire", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBackground(new Color(45, 52, 54));

        JButton btnManageClients = new JButton("Gérer les Clients");
        btnManageClients.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageClients.setBackground(new Color(0, 184, 148));
        btnManageClients.setForeground(Color.WHITE);
        btnManageClients.setFocusPainted(false);
        btnManageClients.addActionListener(e -> new ClientManagementFrame().setVisible(true));

        JButton btnManageBooks = new JButton("Gérer les Livres");
        btnManageBooks.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnManageBooks.setBackground(new Color(0, 184, 148));
        btnManageBooks.setForeground(Color.WHITE);
        btnManageBooks.setFocusPainted(false);
        btnManageBooks.addActionListener(e -> new BookManagementFrame().setVisible(true));

        buttonPanel.add(btnManageClients);
        buttonPanel.add(btnManageBooks);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }
}
