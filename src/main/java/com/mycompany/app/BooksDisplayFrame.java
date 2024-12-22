package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class BooksDisplayFrame extends JFrame {

    private JPanel booksPanel;

    public BooksDisplayFrame() {
        // Configuration de la fenêtre principale
        setTitle("Livres Disponibles");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Livres", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Panneau des livres
        booksPanel = new JPanel();
        booksPanel.setLayout(new GridLayout(0, 3, 15, 15)); // 3 colonnes
        booksPanel.setBackground(new Color(45, 52, 54));

        // Ajouter un JScrollPane pour permettre le défilement
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        scrollPane.setBackground(new Color(45, 52, 54));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bouton pour ajouter un livre
        JButton btnAddBook = new JButton("Ajouter un Livre");
        btnAddBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddBook.setForeground(Color.WHITE);
        btnAddBook.setBackground(new Color(0, 184, 148));
        btnAddBook.setFocusPainted(false);
        btnAddBook.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAddBook.addActionListener(e -> ouvrirFormulaireAjoutLivre());
        mainPanel.add(btnAddBook, BorderLayout.SOUTH);

        // Charger les livres
        chargerLivres();
    }

    private void chargerLivres() {
        booksPanel.removeAll(); // Vider les livres actuels
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre WHERE statut = 1";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                JPanel bookPanel = new JPanel();
                bookPanel.setLayout(new BorderLayout(10, 10));
                bookPanel.setBackground(new Color(64, 74, 76));
                bookPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));

                // Ajouter la photo
                String photoPath = resultSet.getString("photo");
                JLabel photoLabel;
                if (photoPath != null && !photoPath.isEmpty() && new File(photoPath).exists()) {
                    ImageIcon bookImage = new ImageIcon(photoPath);
                    Image scaledImage = bookImage.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                    photoLabel = new JLabel(new ImageIcon(scaledImage));
                } else {
                    photoLabel = new JLabel("Aucune image", SwingConstants.CENTER);
                    photoLabel.setPreferredSize(new Dimension(150, 200));
                    photoLabel.setOpaque(true);
                    photoLabel.setBackground(new Color(220, 220, 220));
                }
                bookPanel.add(photoLabel, BorderLayout.CENTER);

                // Ajouter les informations du livre
                String bookInfo = "<html><b>" + resultSet.getString("titre") + "</b><br>" +
                        "Auteur : " + resultSet.getString("auteur") + "<br>" +
                        "ISBN : " + resultSet.getString("isbn") + "</html>";
                JLabel infoLabel = new JLabel(bookInfo, SwingConstants.CENTER);
                infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                infoLabel.setForeground(new Color(241, 242, 246));
                bookPanel.add(infoLabel, BorderLayout.SOUTH);

                booksPanel.add(bookPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }

        booksPanel.revalidate();
        booksPanel.repaint();
    }

    private void ouvrirFormulaireAjoutLivre() {
        new BookManagementFrame().setVisible(true); // Formulaire d'ajout de livre
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BooksDisplayFrame frame = new BooksDisplayFrame();
            frame.setVisible(true);
        });
    }
}
