package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class BooksDisplayFrame extends JFrame {

    public BooksDisplayFrame() {
        setTitle("Livres Disponibles");
        setSize(1000, 700); // Taille ajustée
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre en haut
        JLabel lblTitle = new JLabel("Livres Disponibles", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 150)); // Couleur personnalisée
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Panneau contenant les livres
        JPanel booksPanel = new JPanel();
        booksPanel.setLayout(new GridLayout(0, 3, 15, 15)); // 3 colonnes avec des espacements
        booksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Charger les livres disponibles
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre WHERE statut = 1";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                // Panneau individuel pour chaque livre
                JPanel bookPanel = new JPanel();
                bookPanel.setLayout(new BorderLayout(10, 10));
                bookPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));

                // Ajouter la photo
                String photoPath = resultSet.getString("photo");
                JLabel photoLabel;
                if (photoPath != null && !photoPath.isEmpty() && new File(photoPath).exists()) {
                    ImageIcon bookImage = new ImageIcon(photoPath);
                    Image scaledImage = bookImage.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                    photoLabel = new JLabel(new ImageIcon(scaledImage));
                } else {
                    // Icône par défaut si l'image est absente
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
                bookPanel.add(infoLabel, BorderLayout.SOUTH);

                // Ajouter le panneau du livre au panneau principal
                booksPanel.add(bookPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }

        // Ajouter un JScrollPane pour permettre le défilement
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    // Méthode main pour exécuter cette classe
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BooksDisplayFrame frame = new BooksDisplayFrame();
            frame.setVisible(true);
        });
    }
}
