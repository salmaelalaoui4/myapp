package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AddLibrarianFormulaire extends JFrame {
    private JTextField txtNom, txtPrenom, txtEmail, txtTelephone, txtPassword;
    private int idBibliotheque;
    private JButton btnAdd;
    private AdminLibrarianManagementFrame adminFrame;  

    public AddLibrarianFormulaire(int idBibliotheque, AdminLibrarianManagementFrame adminFrame) {
        this.idBibliotheque = idBibliotheque;
        this.adminFrame = adminFrame;  // 
        setTitle("Ajouter un Bibliothécaire");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
       
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));
        
     
        JLabel lblNom = new JLabel("Nom:");
        txtNom = new JTextField();
        
        JLabel lblPrenom = new JLabel("Prénom:");
        txtPrenom = new JTextField();
        
        JLabel lblEmail = new JLabel("Email:");
        txtEmail = new JTextField();
        
        JLabel lblTelephone = new JLabel("Téléphone:");
        txtTelephone = new JTextField();
        
        JLabel lblPassword = new JLabel("Mot de Passe:");
        txtPassword = new JPasswordField();
        
        btnAdd = new JButton("Ajouter");

        // Ajouter les composants au panel
        panel.add(lblNom);
        panel.add(txtNom);
        panel.add(lblPrenom);
        panel.add(txtPrenom);
        panel.add(lblEmail);
        panel.add(txtEmail);
        panel.add(lblTelephone);
        panel.add(txtTelephone);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnAdd);

        add(panel, BorderLayout.CENTER);

       
        btnAdd.addActionListener(e -> ajouterBibliothecaire());
    }

    private void ajouterBibliothecaire() {
 
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String email = txtEmail.getText();
        String telephone = txtTelephone.getText();
        String password = txtPassword.getText();


        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !");
            return;
        }

 
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio", "root", "")) {
            String query = "INSERT INTO utilisateur (nom, prenom, email, telephone, motDePasse, role, statut, idBibliotheque) VALUES (?, ?, ?, ?, ?, 'bibliothecaire', 1, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, email);
            statement.setString(4, telephone);
            statement.setString(5, password);
            statement.setInt(6, idBibliotheque);
            statement.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Bibliothécaire ajouté avec succès !");
            
           
            adminFrame.chargerBibliothecaires();  // Appeler la méthode de la classe adminFrame pour rafraîchir la liste
            
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du bibliothécaire : " + e.getMessage());
        }
    }
}
