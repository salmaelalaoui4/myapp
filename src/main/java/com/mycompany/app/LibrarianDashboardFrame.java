package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LibrarianDashboardFrame extends JFrame {

    private int bibliothequeId;

    public LibrarianDashboardFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId; // Stocker l'ID de la bibliothèque

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

        // Créer une méthode générique pour gérer les clics sur les boutons
        JButton btnManageClients = createButton("Gérer les Clients", e -> openManagementFrame("clients"));
        JButton btnManageBooks = createButton("Gérer les Livres", e -> openManagementFrame("books"));

        buttonPanel.add(btnManageClients);
        buttonPanel.add(btnManageBooks);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    // Méthode générique pour créer des boutons
    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(0, 184, 148));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        return button;
    }

    // Méthode générique pour ouvrir les fenêtres de gestion
    private void openManagementFrame(String type) {
        JFrame managementFrame;
        if ("clients".equals(type)) {
            managementFrame = new ClientManagementFrame();
        } else if ("books".equals(type)) {
            // Ouvrir la fenêtre pour afficher les livres en mode lecture seule pour les bibliothécaires
            managementFrame = new BooksDisplayForLibrarianFrame(bibliothequeId); 
        } else {
            return;
        }
        managementFrame.setVisible(true);
    }

    public static void main(String[] args) {
        // Exemple d'ID de bibliothèque
        int bibliothequeId = 1; // Vous pouvez personnaliser cet ID en fonction de la bibliothèque
        SwingUtilities.invokeLater(() -> new LibrarianDashboardFrame(bibliothequeId).setVisible(true));
    }
}
