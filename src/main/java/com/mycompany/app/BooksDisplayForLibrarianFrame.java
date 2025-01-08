package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class BooksDisplayForLibrarianFrame extends JFrame {

    private JPanel booksPanel;

    public BooksDisplayForLibrarianFrame(int bibliothequeId) {
        
        setTitle("Livres Disponibles");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


     
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        
        JLabel lblTitle = new JLabel("Livres Disponibles - Bibliothécaire", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        
        booksPanel = new JPanel();
        booksPanel.setLayout(new GridLayout(0, 3, 15, 15)); // 3 colonnes
        booksPanel.setBackground(new Color(45, 52, 54));

        
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        scrollPane.setBackground(new Color(45, 52, 54));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

      
        chargerLivres(bibliothequeId);
    }

    private void chargerLivres(int bibliothequeId) {
        booksPanel.removeAll(); // Vider les livres actuels
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            
            String query = "SELECT * FROM livre WHERE idBibliotheque = ?"; 
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, bibliothequeId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                JPanel bookPanel = new JPanel();
                bookPanel.setLayout(new BorderLayout(10, 10));
                bookPanel.setBackground(new Color(64, 74, 76));
                bookPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));

               
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

                
                String bookInfo = "<html><b>" + resultSet.getString("titre") + "</b><br>" +
                        "Auteur : " + resultSet.getString("auteur") + "<br>" +
                        "ISBN : " + resultSet.getString("isbn")+"</b><br>" +"Quantité disponible : "+ resultSet.getString("quantiteDisponible") + "</html>";
                JLabel infoLabel = new JLabel(bookInfo, SwingConstants.CENTER);
                infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                infoLabel.setForeground(new Color(241, 242, 246));
                bookPanel.add(infoLabel, BorderLayout.SOUTH);

                
                int idLivre = resultSet.getInt("idLivre");

                
                JButton btnConsulter = new JButton("Consulter");
                btnConsulter.setBackground(new Color(0, 184, 148));
                btnConsulter.setForeground(Color.WHITE);

                
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
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre WHERE idLivre = ?";  
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
                String quantiteDisponible = resultSet.getString("quantiteDisponible");

                
                BookDetailsFrame bookDetailsFrame = new BookDetailsFrame(titre, auteur, annee, isbn, description, photoPath,quantiteDisponible);
                bookDetailsFrame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'affichage des détails du livre : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            int bibliothequeId = 1; 
            BooksDisplayForLibrarianFrame frame = new BooksDisplayForLibrarianFrame(bibliothequeId);
            frame.setVisible(true);
        });
    }
}
