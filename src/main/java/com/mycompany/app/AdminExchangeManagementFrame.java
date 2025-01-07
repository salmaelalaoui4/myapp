package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AdminExchangeManagementFrame extends JFrame {

    private int bibliothequeId;

    public AdminExchangeManagementFrame(int bibliothequeId) {
        this.bibliothequeId = bibliothequeId;
        setTitle("Gestion des Échanges - Bibliothèque " + bibliothequeId);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Échanges", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(241, 242, 246));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ajouter une Demande", createAddExchangePanel());
        tabbedPane.addTab("Voir les Demandes", createViewRequestsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    // Panneau pour ajouter une demande
    private JPanel createAddExchangePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBackground(new Color(45, 52, 54));

        JLabel lblBook = new JLabel("Livre à demander:");
        lblBook.setForeground(Color.WHITE);
        JComboBox<String> cbBooks = new JComboBox<>();

        JLabel lblDestination = new JLabel("Bibliothèque destination:");
        lblDestination.setForeground(Color.WHITE);
        JComboBox<String> cbLibraries = new JComboBox<>();

        JLabel lblQuantity = new JLabel("Quantité demandée:");
        lblQuantity.setForeground(Color.WHITE);
        JTextField txtQuantity = new JTextField();

        JButton btnSubmit = new JButton("Ajouter la Demande");
        btnSubmit.setBackground(new Color(0, 184, 148));
        btnSubmit.setForeground(Color.WHITE);

        // Remplir les livres disponibles dans la bibliothèque actuelle
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT titre FROM livre WHERE idBibliotheque = ?")) {
            stmt.setInt(1, bibliothequeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cbBooks.addItem(rs.getString("titre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Remplir la liste des bibliothèques destination (ne pas inclure la bibliothèque source)
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT nom FROM bibliotheque WHERE idBibliotheque != ?")) {
            stmt.setInt(1, bibliothequeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cbLibraries.addItem(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnSubmit.addActionListener(e -> {
            String book = (String) cbBooks.getSelectedItem();
            String destination = (String) cbLibraries.getSelectedItem();
            int quantity;
            try {
                quantity = Integer.parseInt(txtQuantity.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une quantité valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (destination == null || destination.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une bibliothèque destination.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Récupérer le nom de la bibliothèque source
            String sourceLibraryName = null;
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("SELECT nom FROM bibliotheque WHERE idBibliotheque = ?")) {

                stmt.setInt(1, bibliothequeId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    sourceLibraryName = rs.getString("nom");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du nom de la bibliothèque source.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (sourceLibraryName == null) {
                JOptionPane.showMessageDialog(this, "Erreur : Bibliothèque source introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ajouter la demande dans la base de données
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO echange (idLivre, nomBibliothequeSource, nomBibliothequeDestination, dateDemande, statut, quantite) " +
                                 "VALUES ((SELECT idLivre FROM livre WHERE titre = ? AND idBibliotheque = ?), ?, ?, NOW(), 'en attente', ?)");) {

                stmt.setString(1, book); // Titre du livre
                stmt.setInt(2, bibliothequeId); // ID de la bibliothèque source
                stmt.setString(3, sourceLibraryName); // Nom de la bibliothèque source
                stmt.setString(4, destination); // Nom de la bibliothèque destination
                stmt.setInt(5, quantity); // Quantité demandée

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Demande ajoutée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la demande.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(lblBook);
        panel.add(cbBooks);
        panel.add(lblDestination);
        panel.add(cbLibraries);
        panel.add(lblQuantity);
        panel.add(txtQuantity);
        panel.add(new JLabel());
        panel.add(btnSubmit);

        return panel;
    }

    // Panneau pour voir les demandes
    private JPanel createViewRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(45, 52, 54));

        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnAccept = new JButton("Accepter");
        JButton btnReject = new JButton("Rejeter");

        btnAccept.addActionListener(e -> handleRequestAction(table, true));
        btnReject.addActionListener(e -> handleRequestAction(table, false));

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAccept);
        buttonPanel.add(btnReject);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Charger les demandes des autres bibliothèques
        loadRequests(table);

        return panel;
    }

    private void handleRequestAction(JTable table, boolean accept) {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Veuillez sélectionner une demande.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Récupérer les données de la ligne sélectionnée
    int exchangeId = (int) table.getValueAt(selectedRow, 0);
    int bookId = (int) table.getValueAt(selectedRow, 1); // Récupérer l'ID du livre
    int quantity = (int) table.getValueAt(selectedRow, 5); // Quantité demandée

    // Vérifier si le livre existe dans la bibliothèque du demandeur
    String bookTitle = null;
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
         PreparedStatement stmt = conn.prepareStatement("SELECT titre FROM livre WHERE idLivre = ? AND idBibliotheque = ?")) {
        
        stmt.setInt(1, bookId);
        stmt.setInt(2, bibliothequeId);  // Assurez-vous de passer l'ID de la bibliothèque du demandeur
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            bookTitle = rs.getString("titre"); // Récupérer le titre du livre de la bibliothèque du demandeur
        } else {
            JOptionPane.showMessageDialog(this, "Le livre demandé n'existe pas dans la bibliothèque du demandeur.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;  // Sortir si le livre n'existe pas dans la bibliothèque source
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors de la vérification du livre dans la bibliothèque du demandeur.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Vérifier si le livre existe dans votre bibliothèque
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
         PreparedStatement stmt = conn.prepareStatement("SELECT quantiteDisponible FROM livre WHERE titre = ? AND idBibliotheque = ?")) {

        stmt.setString(1, bookTitle);  // Utiliser le titre récupéré précédemment
        stmt.setInt(2, bibliothequeId);  // Vérifier dans votre propre bibliothèque
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int availableQuantity = rs.getInt("quantiteDisponible");

            // Si la quantité est 0, le livre n'est pas disponible
            if (availableQuantity == 0) {
                JOptionPane.showMessageDialog(this, "Le livre n'est pas disponible dans votre bibliothèque.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            } 

            // Si la quantité disponible est inférieure à la quantité demandée
            else if (availableQuantity < quantity) {
                JOptionPane.showMessageDialog(this, "La quantité disponible est insuffisante. Quantité disponible: " + availableQuantity, "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Le livre demandé n'existe pas dans votre bibliothèque.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors de la vérification de la disponibilité du livre dans votre bibliothèque.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Traiter l'action (accepter ou rejeter)
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
         PreparedStatement stmt = conn.prepareStatement("UPDATE echange SET statut = ? WHERE idEchange = ?")) {

        if (accept) {
            stmt.setString(1, "accepté");
        } else {
            stmt.setString(1, "rejeté");
        }
        stmt.setInt(2, exchangeId);
        stmt.executeUpdate();

        if (accept) {
            // Si la demande est acceptée, mettre à jour la quantité disponible dans la bibliothèque
            if (updateQuantities(bookId, quantity)) {
                JOptionPane.showMessageDialog(this, "Demande acceptée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Quantité insuffisante pour accepter la demande.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Demande rejetée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }

        loadRequests(table); // Recharge les demandes après modification

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors du traitement de la demande.", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}



private boolean updateQuantities(int bookId, int quantity) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "")) {
        conn.setAutoCommit(false);

        // Mettre à jour la quantité dans la bibliothèque source
        PreparedStatement updateSource = conn.prepareStatement("UPDATE livre SET quantiteDisponible = quantiteDisponible - ? WHERE idLivre = ? AND idBibliotheque = ?");
        updateSource.setInt(1, quantity);
        updateSource.setInt(2, bookId);
        updateSource.setInt(3, bibliothequeId);
        int rowsUpdated = updateSource.executeUpdate();

        if (rowsUpdated == 0) {
            conn.rollback();
            return false;
        }

        // Mettre à jour la quantité dans la bibliothèque destination
        PreparedStatement updateDestination = conn.prepareStatement("UPDATE livre SET quantiteDisponible = quantiteDisponible + ? WHERE idLivre = ? AND idBibliotheque != ?");
        updateDestination.setInt(1, quantity);
        updateDestination.setInt(2, bookId);
        updateDestination.setInt(3, bibliothequeId);
        updateDestination.executeUpdate();

        conn.commit();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

private void loadRequests(JTable table) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
         PreparedStatement stmt = conn.prepareStatement("SELECT idEchange, idLivre, nomBibliothequeSource, nomBibliothequeDestination, dateDemande, quantite, statut FROM echange WHERE nomBibliothequeDestination = ? AND statut = 'en attente'")) {

        stmt.setString(1, getBibliothequeName()); // Utiliser la méthode pour obtenir le nom
        ResultSet rs = stmt.executeQuery();
        table.setModel(buildTableModel(rs));
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    private String getBibliothequeName() {
        String libraryName = null;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT nom FROM bibliotheque WHERE idBibliotheque = ?")) {

            stmt.setInt(1, bibliothequeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                libraryName = rs.getString("nom");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libraryName;
    }

    private static TableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Noms des colonnes
        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Données
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int column = 1; column <= columnCount; column++) {
                row.add(rs.getObject(column));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }
}