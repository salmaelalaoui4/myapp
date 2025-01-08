package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AchatManagementFrame extends JFrame {

    private JTable achatsTable;
    private int idBibliotheque;

    public AchatManagementFrame(int idBibliotheque) {
        this.idBibliotheque = idBibliotheque;

        setTitle("Gestion des Achats");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(45, 52, 54));  // Fond sombre
        add(mainPanel);

        // Titre
        JLabel lblTitle = new JLabel("Gestion des Achats", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(241, 242, 246));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Panel pour la table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(45, 52, 54));  // Fond sombre
        achatsTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(achatsTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        chargerAchats();

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(45, 52, 54));  // Fond sombre

        JButton btnEnregistrerAchat = new JButton("Enregistrer un Achat");
        btnEnregistrerAchat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEnregistrerAchat.setForeground(Color.WHITE);
        btnEnregistrerAchat.setBackground(new Color(0, 184, 148));
        btnEnregistrerAchat.addActionListener(e -> afficherFormulaireAchat());

        JButton btnModifierAchat = new JButton("Modifier");
        btnModifierAchat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnModifierAchat.setForeground(Color.WHITE);
        btnModifierAchat.setBackground(new Color(0, 184, 148));
        btnModifierAchat.addActionListener(e -> modifierAchat());

        JButton btnSupprimerAchat = new JButton("Supprimer");
        btnSupprimerAchat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSupprimerAchat.setForeground(Color.WHITE);
        btnSupprimerAchat.setBackground(new Color(0, 184, 148));
        btnSupprimerAchat.addActionListener(e -> supprimerAchat());

        buttonPanel.add(btnEnregistrerAchat);
        buttonPanel.add(btnModifierAchat);
        buttonPanel.add(btnSupprimerAchat);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerAchats() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "SELECT idAchat, idLivre, dateAchat, quantite, statutValidation FROM achat WHERE idBibliotheque = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idBibliotheque);
                try (ResultSet resultSet = statement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    model.addColumn("ID Achat");
                    model.addColumn("ID Livre");
                    model.addColumn("Date Achat");
                    model.addColumn("Quantité");
                    model.addColumn("Statut Validation");

                    while (resultSet.next()) {
                        model.addRow(new Object[]{
                            resultSet.getInt("idAchat"),
                            resultSet.getInt("idLivre"),
                            resultSet.getDate("dateAchat"),
                            resultSet.getInt("quantite"),
                            resultSet.getInt("statutValidation") == 1 ? "Validé" : "En attente"
                        });
                    }
                    achatsTable.setModel(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des achats : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherFormulaireAchat() {
        AchatFormulaireFrame formulaireFrame = new AchatFormulaireFrame(idBibliotheque, this);
        formulaireFrame.setVisible(true);
    }

    public void enregistrerAchat(int idLivre, int idClient, java.sql.Date dateAchat, int quantite, int statutValidation) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            connection.setAutoCommit(false); // Démarrer une transaction

            try {
                // Vérifier si le livre existe et est disponible pour achat
                String checkQuery = "SELECT quantiteDisponible FROM livre WHERE idLivre = ?";
                try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                    checkStatement.setInt(1, idLivre);
                    try (ResultSet resultSet = checkStatement.executeQuery()) {
                        if (resultSet.next()) {
                            int quantiteDisponible = resultSet.getInt("quantiteDisponible");

                            if (quantiteDisponible < quantite) {
                                throw new SQLException("Quantité insuffisante en stock pour effectuer l'achat.");
                            }
                        } else {
                            throw new SQLException("Livre introuvable.");
                        }
                    }
                }

                // Insérer l'achat dans la table "achat"
                String query = "INSERT INTO achat (idLivre, idClient, dateAchat, quantite, statutValidation, idBibliotheque) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, idLivre);
                    statement.setInt(2, idClient);
                    statement.setDate(3, dateAchat);
                    statement.setInt(4, quantite);
                    statement.setInt(5, statutValidation);
                    statement.setInt(6, idBibliotheque);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted <= 0) {
                        throw new SQLException("Échec de l'insertion de l'achat.");
                    }
                }

                // Mettre à jour la quantité disponible et le statut dans la table "livre"
                if (statutValidation == 1) { // Si l'achat est validé
                    String updateQuery = "UPDATE livre "
                            + "SET quantiteDisponible = quantiteDisponible + ?, "
                            + "    statut = IF(quantiteDisponible + ? > 0, 'disponible', 'non disponible') "
                            + "WHERE idLivre = ?";

                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setInt(1, quantite);
                        updateStatement.setInt(2, quantite);
                        updateStatement.setInt(3, idLivre);

                        int rowsUpdated = updateStatement.executeUpdate();
                        if (rowsUpdated <= 0) {
                            throw new SQLException("Échec de la mise à jour des informations du livre.");
                        }
                    }
                }

                connection.commit(); // Valider la transaction
                JOptionPane.showMessageDialog(this, "Achat enregistré avec succès !");
                chargerAchats(); // Recharger les achats après insertion
            } catch (SQLException e) {
                connection.rollback(); // Annuler la transaction en cas d'erreur
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'achat : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierAchat() {
        int selectedRow = achatsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un achat à modifier !");
            return;
        }

        int idAchat = (int) achatsTable.getValueAt(selectedRow, 0);
        int quantiteActuelle = (int) achatsTable.getValueAt(selectedRow, 3);

        String nouvelleQuantiteStr = JOptionPane.showInputDialog(this, "Entrez la nouvelle quantité :", quantiteActuelle);
        if (nouvelleQuantiteStr == null || nouvelleQuantiteStr.isEmpty()) {
            return;
        }

        try {
            int nouvelleQuantite = Integer.parseInt(nouvelleQuantiteStr);

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
                String query = "UPDATE achat SET quantite = ? WHERE idAchat = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, nouvelleQuantite);
                    statement.setInt(2, idAchat);
                    statement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Achat modifié avec succès !");
                    chargerAchats();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantité invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'achat : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerAchat() {
        int selectedRow = achatsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un achat à supprimer !");
            return;
        }

        int idAchat = (int) achatsTable.getValueAt(selectedRow, 0);

        int confirmation = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet achat ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "DELETE FROM achat WHERE idAchat = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idAchat);
                statement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Achat supprimé avec succès !");
                chargerAchats();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'achat : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        int idBibliotheque = 1; // Exemple d'ID bibliothèque
        SwingUtilities.invokeLater(() -> new AchatManagementFrame(idBibliotheque).setVisible(true));
    }
}
