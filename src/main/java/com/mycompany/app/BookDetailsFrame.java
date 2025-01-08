package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class BookDetailsFrame extends JFrame {

    public BookDetailsFrame(String titre, String auteur, String annee, String isbn, String description, String photoPath, String quantiteDisponible) {
        
        setTitle("Détails du Livre");
        setSize(800, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        add(mainPanel);

        JLabel lblTitle = new JLabel(titre, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(45, 52, 54));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel photoLabel;
        if (photoPath != null && !photoPath.isEmpty() && new File(photoPath).exists()) {
            ImageIcon bookImage = new ImageIcon(photoPath);
            Image scaledImage = bookImage.getImage().getScaledInstance(250, 350, Image.SCALE_SMOOTH);
            photoLabel = new JLabel(new ImageIcon(scaledImage));
        } else {
            photoLabel = new JLabel("Aucune image", SwingConstants.CENTER);
            photoLabel.setPreferredSize(new Dimension(250, 350));
            photoLabel.setOpaque(true);
            photoLabel.setBackground(new Color(220, 220, 220));
        }
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        centerPanel.add(photoLabel, BorderLayout.WEST);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout(10, 10));
        detailsPanel.setBackground(new Color(45, 52, 54));
        centerPanel.add(detailsPanel, BorderLayout.CENTER);

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setForeground(new Color(241, 242, 246));
        descriptionArea.setBackground(new Color(45, 52, 54));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)), "Description", 0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(241, 242, 246)));
        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(new Color(45, 52, 54));
        infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)), "Informations", 0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(241, 242, 246)));

        JLabel lblAuteur = new JLabel("Auteur : " + auteur, SwingConstants.LEFT);
        lblAuteur.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAuteur.setForeground(new Color(241, 242, 246));
        infoPanel.add(lblAuteur);

        JLabel lblAnnee = new JLabel("Année : " + annee, SwingConstants.LEFT);
        lblAnnee.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAnnee.setForeground(new Color(241, 242, 246));
        infoPanel.add(lblAnnee);

        JLabel lblIsbn = new JLabel("ISBN : " + isbn, SwingConstants.LEFT);
        lblIsbn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblIsbn.setForeground(new Color(241, 242, 246));
        infoPanel.add(lblIsbn);

        detailsPanel.add(infoPanel, BorderLayout.SOUTH);
    }
}