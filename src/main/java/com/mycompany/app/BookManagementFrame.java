package com.mycompany.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.io.File;

public class BookManagementFrame extends JFrame {

    // Composants de l'interface
    private JTextField txtTitle, txtAuthor, txtISBN, txtYear, txtPhoto;
    private JButton btnAdd, btnAfficherLivres, btnChoosePhoto;
    private JTable tableBooks;
    private DefaultTableModel tableModel;

    public BookManagementFrame() {
        setTitle("Gestion des Livres - Administrateur");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Création de l'interface
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblTitle = new JLabel("Gestion des Livres", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Champs de formulaire
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails du Livre"));
        formPanel.add(new JLabel("Titre :"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);
        formPanel.add(new JLabel("Auteur :"));
        txtAuthor = new JTextField();
        formPanel.add(txtAuthor);
        formPanel.add(new JLabel("ISBN :"));
        txtISBN = new JTextField();
        formPanel.add(txtISBN);
        formPanel.add(new JLabel("Année de publication :"));
        txtYear = new JTextField();
        formPanel.add(txtYear);
        formPanel.add(new JLabel("Photo :"));
        JPanel photoPanel = new JPanel(new BorderLayout());
        txtPhoto = new JTextField();
        txtPhoto.setEditable(false);
        btnChoosePhoto = new JButton("Choisir...");
        btnChoosePhoto.addActionListener(e -> choisirPhoto());
        photoPanel.add(txtPhoto, BorderLayout.CENTER);
        photoPanel.add(btnChoosePhoto, BorderLayout.EAST);
        formPanel.add(photoPanel);
        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table des livres
        tableModel = new DefaultTableModel(new String[]{"ID", "Titre", "Auteur", "ISBN", "Année", "Photo"}, 0);
        tableBooks = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableBooks);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Ajouter");
        btnAfficherLivres = new JButton("Voir Livres Disponibles");

        btnAdd.addActionListener(e -> ajouterLivre());
        btnAfficherLivres.addActionListener(e -> new BooksDisplayFrame().setVisible(true));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnAfficherLivres);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        chargerLivres();
    }

    private void choisirPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtPhoto.setText(selectedFile.getAbsolutePath());
        }
    }

    private void ajouterLivre() {
        String titre = txtTitle.getText();
        String auteur = txtAuthor.getText();
        String isbn = txtISBN.getText();
        String annee = txtYear.getText();
        String photoPath = txtPhoto.getText();
        String idBibliotheque = "1"; // Par défaut

        if (titre.isEmpty() || auteur.isEmpty() || isbn.isEmpty() || annee.isEmpty() || photoPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO livre (titre, auteur, isbn, anneePublication, statut, idBibliotheque, photo) VALUES (?, ?, ?, ?, 1, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, titre);
            statement.setString(2, auteur);
            statement.setString(3, isbn);
            statement.setInt(4, Integer.parseInt(annee));
            statement.setInt(5, Integer.parseInt(idBibliotheque));
            statement.setString(6, photoPath);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Livre ajouté avec succès !");
            chargerLivres();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + e.getMessage());
        }
    }

    private void chargerLivres() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT * FROM livre WHERE statut = 1";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            tableModel.setRowCount(0);
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("idLivre"));
                row.add(resultSet.getString("titre"));
                row.add(resultSet.getString("auteur"));
                row.add(resultSet.getString("isbn"));
                row.add(resultSet.getInt("anneePublication"));
                row.add(resultSet.getString("photo"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookManagementFrame().setVisible(true));
    }
}
