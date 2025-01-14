package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibrarianDashboardFrame extends JFrame {

    private int bibliothequeId;

    private String getBibliothequeName(int bibliothequeId) {
        String bibliothequeName = "Bibliothèque Inconnue";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", ""); PreparedStatement stmt = conn.prepareStatement("SELECT nom FROM bibliotheque WHERE idBibliotheque = ?")) {
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

    public LibrarianDashboardFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;

        String bibliothequeName = getBibliothequeName(bibliothequeId);

        setTitle("Tableau de Bord - Bibliothécaire");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblTitle = new JLabel("Tableau de Bord - " + bibliothequeName, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 20, 20));
        buttonPanel.setBackground(new Color(45, 52, 54));

        // Boutons pour les fonctionnalités
        JButton btnManageClients = createButton("Gérer les Clients", e -> openManagementFrame("clients"));
        JButton btnManageBooks = createButton("Gérer les Livres", e -> openManagementFrame("books"));
        JButton btnConsultBorrowings = createButton("Consulter les Emprunts", e -> openManagementFrame("borrowings"));
        JButton btnConsultPurchases = createButton("Consulter les Achats", e -> openManagementFrame("purchases"));

        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogout.setBackground(new Color(255, 71, 87));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        buttonPanel.add(btnManageClients);
        buttonPanel.add(btnManageBooks);
        buttonPanel.add(btnConsultBorrowings);
        buttonPanel.add(btnConsultPurchases);
        buttonPanel.add(btnLogout);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(0, 184, 148));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        return button;
    }

    private void openManagementFrame(String type) {
        JFrame managementFrame;
        switch (type) {
            case "clients":
                managementFrame = new ClientManagementFrame(bibliothequeId);
                break;
            case "books":
                managementFrame = new BooksDisplayForLibrarianFrame(bibliothequeId);
                break;
            case "borrowings":
                managementFrame = new LoanManagementFrame(bibliothequeId);
                break;
            case "purchases":
                managementFrame = new AchatManagementFrame(bibliothequeId);
                break;
            default:
                return;
        }
        managementFrame.setVisible(true);
    }

    public static void main(String[] args) {

        int bibliothequeId = 1;
        SwingUtilities.invokeLater(() -> new LibrarianDashboardFrame(bibliothequeId).setVisible(true));
    }
}
