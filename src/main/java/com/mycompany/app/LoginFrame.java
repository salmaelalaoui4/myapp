package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame() {
        // Configuration de la fenêtre principale
        setTitle("Connexion Utilisateur");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(45, 52, 54));

        // Titre
        JLabel titleLabel = new JLabel("Connexion", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(241, 242, 246));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 52, 54));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email :");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(241, 242, 246));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(241, 242, 246));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.RED);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(statusLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Bouton de connexion
        JButton loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(0, 184, 148));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(45, 52, 54));
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajouter le panel principal à la fenêtre
        add(mainPanel);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/biblio";
            String user = "root";
            String pass = "";
            connection = DriverManager.getConnection(url, user, pass);

            String query = "SELECT * FROM utilisateur WHERE email = ? AND motDePasse = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                int bibliothequeId = resultSet.getInt("idBibliotheque"); // Récupération de l'ID de la bibliothèque

                if ("admin".equalsIgnoreCase(role)) {
                    JOptionPane.showMessageDialog(this, "Bienvenue, Administrateur !");
                    dispose(); // Ferme la fenêtre de connexion
                    new AdminDashboardFrameBiblio(bibliothequeId).setVisible(true); // Redirige vers le tableau de bord admin
                } else if ("bibliothecaire".equalsIgnoreCase(role)) {
                    JOptionPane.showMessageDialog(this, "Bienvenue, Bibliothécaire !");
                    dispose(); // Ferme la fenêtre de connexion
                    new LibrarianDashboardFrame(bibliothequeId).setVisible(true); // Redirige vers le tableau de bord bibliothécaire
                } else {
                    statusLabel.setText("Rôle inconnu.");
                }
            } else {
                statusLabel.setText("Email ou mot de passe incorrect.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Erreur de connexion à la base de données.");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
