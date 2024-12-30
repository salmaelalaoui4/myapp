package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class BooksDisplayFrame extends JFrame {

    private JPanel booksPanel;

    private int currentUserLibraryId; // Assume you set this when the user logs in

    public BooksDisplayFrame(int userLibraryId) {
        // Set the current user's library ID
        this.currentUserLibraryId = userLibraryId;

        // Configuration de la fenêtre principale
        setTitle("Livres Disponibles");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        // Charger les livres
        chargerLivres();

        // Ajouter un bouton pour accéder à la gestion des livres
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(45, 52, 54));

        JButton btnAddBook = new JButton("Ajouter un Livre");
        btnAddBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAddBook.setBackground(new Color(0, 184, 148));
        btnAddBook.setForeground(Color.WHITE);
        btnAddBook.setFocusPainted(false);
        btnAddBook.addActionListener(e -> ouvrirFormulaireAjoutLivre());

        buttonPanel.add(btnAddBook);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerLivres() {
        booksPanel.removeAll(); // Vider les livres actuels
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            // Modifier la requête pour charger les livres selon l'idBibliotheque de l'utilisateur
            String query = "SELECT * FROM livre WHERE idBibliotheque = ?"; 
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, currentUserLibraryId); // Passer l'ID de la bibliothèque
            ResultSet resultSet = statement.executeQuery();

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

                // Extraire l'ID du livre
                int idLivre = resultSet.getInt("idLivre");

                // Ajouter un bouton "Consulter"
                JButton btnConsulter = new JButton("Consulter");
                btnConsulter.setBackground(new Color(0, 184, 148));
                btnConsulter.setForeground(Color.WHITE);

                // Action du bouton Consulter
                btnConsulter.addActionListener(e -> afficherDetailsLivre(idLivre));

                bookPanel.add(btnConsulter, BorderLayout.NORTH);

                booksPanel.add(bookPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }

        booksPanel.revalidate();
        booksPanel.repaint();
    }

    private void afficherDetailsLivre(int idLivre) {
        // Affichage des détails d'un livre dans une nouvelle fenêtre
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre WHERE idLivre = ?";  // Requête pour récupérer un livre par son ID
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idLivre);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String titre = resultSet.getString("titre");
                String auteur = resultSet.getString("auteur");
                String isbn = resultSet.getString("isbn");
                String annee = resultSet.getString("anneePublication");
                String description = resultSet.getString("description");
                String photoPath = resultSet.getString("photo");

                // Affichage dans une nouvelle fenêtre
                BookDetailsFrame bookDetailsFrame = new BookDetailsFrame(titre, auteur, annee, isbn, description, photoPath);
                bookDetailsFrame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'affichage des détails du livre : " + e.getMessage());
        }
    }

    private void ouvrirFormulaireAjoutLivre() {
        new BookManagementFrame().setVisible(true); // Formulaire d'ajout de livre
    }

    public static void main(String[] args) {
        // Example: Create the frame for admin of biblio 1 (ID 1) or biblio 2 (ID 2)
        SwingUtilities.invokeLater(() -> {
            BooksDisplayFrame frame = new BooksDisplayFrame(1); // Replace 1 with the actual user's library ID
            frame.setVisible(true);
        });
    }
}
