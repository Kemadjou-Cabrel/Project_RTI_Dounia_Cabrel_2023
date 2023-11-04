package GUI;
import GUI.Interface;
import Controller.controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class marche extends JFrame {
    public JTable tableau;
    private JButton supprimerArticleButton;
    private JButton confirmerAchatButton;
    private JButton viderLePanierButton;
    private JTextField Total;
    private JTextField Nom;
    private JTextField Mdp;// apres changer
    private JButton loginButton;
    private JButton logoutButton;
    private JCheckBox nouveauClientCheckBox;
    private JTextField Article;
    private JTextField Prix;
    private JButton PrecedentButton;
    private JButton SuivantButton;
    private JSpinner Quantite;
    private JTextField bienvenueSurLeMaraicherTextField;
    public JPanel fenetreprincipale;
    private JScrollPane table;
    private JTextField Stock;
    private JButton AcheterButton;
    private JLabel photo;



    public marche(Interface inter)
    {

        setSize(800,600);
        setTitle("Apllication Maraichier ");
        setContentPane(fenetreprincipale);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /// Creation ici de notre panier
        this.tableau = new JTable();
        DefaultTableModel tableModelEmp = (DefaultTableModel)this.tableau.getModel();
        String[] nomsColonnes = new String[]{"Article", "Prix à l'unité", "Quantite"};
        tableModelEmp.setColumnIdentifiers(nomsColonnes);
        table.setViewportView(tableau);
        ButtonDesactive();


        ///------------------------------------------------Different action des boutons de notre maraicher--------------------------------------------------------


        controller controller =new controller(inter,this);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                controller.login();

            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                controller.logout();

            }
        });

        PrecedentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.Precedent();

            }
        });

        SuivantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.Suivant();

            }
        });

        AcheterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.Acheter();

            }
        });
        supprimerArticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.supprimer();

            }
        });
        viderLePanierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.viderPanier();

            }
        });
        confirmerAchatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.confirmerAchat();

            }
        });
        ///-----------------------------------------------------------------------------------------------------------------------------------------
    }

    //-------------------------------------------------------------------Different getter-----------------------------------------------------------
    public JTextField getNom() {
        return Nom;
    }

    public JTextField getMdp() {
        return Mdp;
    }

    public JButton getLoginButton() {
        return loginButton;
    }
    public JCheckBox getNouveauClientCheckBox() {
        return nouveauClientCheckBox;
    }

    public JTextField getArticle() {
        return Article;
    }
    public JTextField getPrix() {
        return Prix;
    }
    public JTextField getStock() {
        return Stock;
    }
    public JLabel getPhoto() {
        return photo;
    }






















    //---------------------------------------------------------------------------------------------------------------------------------------------

    // nos methode pour l'activation et la desactivation des boutons
    public void ButtonActive()
    {
        loginButton.setEnabled(false);

        Nom.setEnabled(false);

        Mdp.setEnabled(false);

        nouveauClientCheckBox.setEnabled(false);

        logoutButton.setEnabled(true);
        PrecedentButton.setEnabled(true);
        SuivantButton.setEnabled(true);
        AcheterButton.setEnabled(true);
        supprimerArticleButton.setEnabled(true);
        viderLePanierButton.setEnabled(true);
        confirmerAchatButton.setEnabled(true);
        Quantite.setEnabled(true);


    }
    public void ButtonDesactive()
    {
        loginButton.setEnabled(true);
        Nom.setEnabled(true);
        Mdp.setEnabled(true);
        nouveauClientCheckBox.setEnabled(true);

        logoutButton.setEnabled(false);
        PrecedentButton.setEnabled(false);
        SuivantButton.setEnabled(false);
        AcheterButton.setEnabled(false);
        supprimerArticleButton.setEnabled(false);
        viderLePanierButton.setEnabled(false);
        confirmerAchatButton.setEnabled(false);
        Quantite.setEnabled(false);

        Article.setText(null);
        Stock.setText(null);
        Prix.setText(null);
        photo.setText(null);


    }


}
