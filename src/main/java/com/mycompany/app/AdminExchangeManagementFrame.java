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

    // Récupérer les données de l'échange
    int exchangeId = (int) table.getValueAt(selectedRow, 0);
    int bookId = (int) table.getValueAt(selectedRow, 1); // ID du livre dans la bibliothèque source
    int quantity = (int) table.getValueAt(selectedRow, 5);

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "")) {
        conn.setAutoCommit(false);

        if (accept) {
            // Étape 1 : Récupérer le titre du livre à partir de l'ID dans la bibliothèque source
            String bookTitle = null;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT titre FROM livre WHERE idLivre = ?")) {
                stmt.setInt(1, bookId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    bookTitle = rs.getString("titre");
                } else {
                    JOptionPane.showMessageDialog(this, "Le livre n'existe pas dans la bibliothèque source.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return; // Arrêter si le titre ne peut pas être récupéré
                }
            }

            // Étape 2 : Vérifier si le livre existe dans la bibliothèque destination
            int availableQuantity = -1;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT quantiteDisponible FROM livre WHERE titre = ? AND idBibliotheque = ?")) {
                stmt.setString(1, bookTitle);
                stmt.setInt(2, bibliothequeId); // Bibliothèque destination
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    availableQuantity = rs.getInt("quantiteDisponible");
                } else {
                    JOptionPane.showMessageDialog(this, "Le livre n'existe pas dans la bibliothèque destination.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return; // Arrêter si le livre n'existe pas
                }
            }

            // Étape 3 : Vérifier si la quantité est suffisante
            if (availableQuantity < quantity) {
                JOptionPane.showMessageDialog(this, "Quantité insuffisante pour accepter la demande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return; // Arrêter si la quantité est insuffisante
            }

            // Étape 4 : Mettre à jour les quantités et marquer l'échange comme accepté
            updateQuantities(bookTitle, quantity, conn);

            // Marquer l'échange comme accepté
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE echange SET statut = 'accepté', dateReception = ? WHERE idEchange = ?")) {
                stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                stmt.setInt(2, exchangeId);
                stmt.executeUpdate();
            }

            // Mettre à jour le statut du livre
            updateBookStatus(conn, bookTitle);

            JOptionPane.showMessageDialog(this, "Demande acceptée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Rejeter l'échange sans condition
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE echange SET statut = 'rejeté' WHERE idEchange = ?")) {
                stmt.setInt(1, exchangeId);
                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Demande rejetée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }

        conn.commit(); // Valider les changements
        loadRequests(table); // Recharger les demandes
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors du traitement de la demande.", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}

// Méthode pour mettre à jour les quantités
private void updateQuantities(String bookTitle, int quantity, Connection conn) throws SQLException {
    try {
        // Réduire la quantité dans la bibliothèque source
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE livre SET quantiteDisponible = quantiteDisponible - ? WHERE titre = ? AND idBibliotheque = ?")) {
            stmt.setInt(1, quantity);
            stmt.setString(2, bookTitle);
            stmt.setInt(3, bibliothequeId); // Bibliothèque source
            stmt.executeUpdate();
        }

        // Augmenter la quantité dans la bibliothèque destination
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE livre SET quantiteDisponible = quantiteDisponible + ? WHERE titre = ? AND idBibliotheque != ?")) {
            stmt.setInt(1, quantity);
            stmt.setString(2, bookTitle);
            stmt.setInt(3, bibliothequeId); // Exclure la bibliothèque source
            stmt.executeUpdate();
        }
    } catch (SQLException e) {
        conn.rollback();
        throw e;
    }
}

// Méthode pour mettre à jour le statut du livre
private void updateBookStatus(Connection conn, String bookTitle) throws SQLException {
    String query = "UPDATE livre SET statut = CASE WHEN quantiteDisponible > 0 THEN 'disponible' ELSE 'non disponible' END WHERE titre = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, bookTitle);
        stmt.executeUpdate();
    }
}




    private void loadRequests(JTable table) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/biblio", "root", "");
         PreparedStatement stmt = conn.prepareStatement(
             "SELECT idEchange, idLivre, nomBibliothequeSource, nomBibliothequeDestination, dateDemande, quantite, statut " +
             "FROM echange " +
             "WHERE nomBibliothequeDestination = ? AND statut = 'en attente'")) { // Filtrer par statut 'en attente'

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