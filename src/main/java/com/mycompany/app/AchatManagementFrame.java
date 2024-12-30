package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
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

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JLabel lblTitle = new JLabel("Gestion des Achats", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        achatsTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(achatsTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        chargerAchats();

        JButton btnEnregistrerAchat = new JButton("Enregistrer un Achat");
        btnEnregistrerAchat.addActionListener(e -> afficherFormulaireAchat());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnEnregistrerAchat);
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
            String query = "INSERT INTO achat (idLivre, idClient, dateAchat, quantite, statutValidation, idBibliotheque) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idLivre);
                statement.setInt(2, idClient);
                statement.setDate(3, dateAchat);
                statement.setInt(4, quantite);
                statement.setInt(5, statutValidation);
                statement.setInt(6, idBibliotheque);

                statement.executeUpdate();

                chargerAchats();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'achat : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        int idBibliotheque = 1; // Exemple d'ID bibliothèque
        SwingUtilities.invokeLater(() -> new AchatManagementFrame(idBibliotheque).setVisible(true));
    }
}
