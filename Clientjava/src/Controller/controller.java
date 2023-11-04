package Controller;

import GUI.Interface;
import GUI.marche;
import Modele.Article;
import Modele.TCP;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class controller
{

    private  Interface inter;
    private marche marc;
    private  Socket socket;
    private  TCP tcp;
    private boolean logged;
    private Article articleCourant;



    public controller(Interface inter,marche marc)
    {
        this.inter=inter;

        this.marc=marc;

        tcp = new TCP();

    }


    public Socket connection()
    {


        try {
            // Récupérer l'adresse IP et le port à partir des champs de texte

            String ipAdresse = inter.getAdresse().getText();

            System.out.println(ipAdresse);

            String portTexte = inter.getPort().getText();

            System.out.println(portTexte);

            // Vérifier si l'adresse IP contient uniquement des chiffres et des points
            if(ipAdresse.isEmpty())
            {
                throw new Exception("Veuillez remplir le champ adresse");

            }
            else if (!ipAdresse.matches("^[0-9.]+$"))
            {
                throw new Exception("Votre adresse doit etre sous format 0.0.0.0.0");
            }


            if(portTexte.isEmpty())
            {
                throw new Exception("Veuillez remplir le champ Port");

            }
            else if(!portTexte.matches("^[0-9]+$"))
            {
                throw new Exception("Le port doit etre uniquement des numeros");
            }
            // Convertir le port en entier
            int port = Integer.parseInt(portTexte);

            // Créer une connexion au serveur
            socket = tcp.ClientSocket(ipAdresse, port);
            System.out.println("cabrel"+socket);

            // Afficher un message de connexion réussie
            JOptionPane.showMessageDialog(null, "Connection au Serveur reussi!!!", "Connexion", JOptionPane.INFORMATION_MESSAGE);

            // Créer et afficher la fenêtre principale de l'application

            marche marche = new marche(inter);
            marche.setVisible(true);

            // Masquer la fenêtre de connexion
            inter.setVisible(false);
            // Effectuer d'autres actions après la connexion (PasLogger() dans votre code)
            marche.ButtonDesactive();
        }
        catch (Exception exception)
        {
            // Afficher un message d'erreur en cas d'échec de la connexion
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return socket;

    }



    //-------------------------------------Different fonction de notre mariachier-------------------------------------------------------------------
public  void login()
{
    int nouveauclient;

        if(marc.getNouveauClientCheckBox().isSelected())
        {
            nouveauclient = 1;
        }
        else {
            nouveauclient = 0;
        }
        try {

            String nom = marc.getNom().getText();

            String mdp = marc.getMdp().getText();

            if (nom.length() < 3)
            {
                JOptionPane.showMessageDialog(null, "Attention le nom de l'utilisateur doit comporter 3 caractères minimum!", "Login", JOptionPane.INFORMATION_MESSAGE);

            }

            if (mdp.length()< 5)
            {

                JOptionPane.showMessageDialog(null, "Veuillez entrer un mot de passe comportant au moins 5 caractères!", "Login", JOptionPane.INFORMATION_MESSAGE);

            }

            String texte = String.format("LOGIN#%s#%s#%d", nom, mdp, nouveauclient);

            System.out.println("cabrel"+texte);

            int nbEcrits;

            nbEcrits = tcp.Send(inter.getSocket(), texte);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + texte + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            String[] parts = response.split("#");

            String command = parts[0];

            System.out.println(command);

            if ("LOGIN".equals(command))
            {
                String reponse = parts[1];

                System.out.println(reponse);

                if ("ok".equals(reponse))
                {
                    if (nouveauclient == 1)
                    {
                        JOptionPane.showMessageDialog(null, "Vous avez été inscrit avec succès", "Login", JOptionPane.INFORMATION_MESSAGE);
                    }

                    JOptionPane.showMessageDialog(null, "Connecter avec succés!!!", "Login", JOptionPane.INFORMATION_MESSAGE);
                    marc.ButtonActive();

                    logged = true;

                    //numFacture = Integer.parseInt(parts[2]);

                    //getCaddie();

                    ConsultArticle(1);



                }else
                {
                    String message = parts[2];
                    JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);

                }


                }
            } catch (IOException e) {
            throw new RuntimeException(e);
        }

}


    public void logout()
    {
        try
        {
            String texte="LOGOUT";

            System.out.println(texte);

            int nbEcrits = tcp.Send(inter.getSocket(), texte);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + texte + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            String[] parts = response.split("#");

            String command = parts[0];

            System.out.println(command);

            if ("LOGOUT".equals(command))
            {
                String reponse = parts[1];

                System.out.println(reponse);

                if ("ok".equals(reponse))
                {
                    //if(nbArticles > 0) {
                   //     VidePanier();
                   // }

                    JOptionPane.showMessageDialog(null, "Au Plaisir !!!! ;)", "LOGOUT", JOptionPane.INFORMATION_MESSAGE);

                    marc.ButtonDesactive();

                    logged = false;/// faire la fenttre
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
    public void Precedent()
    {
        System.out.println("precedent");
        //ConsultArticle(articleCourant.getId() - 1);

    }
    public void Suivant()
    {
        System.out.println("Suivant");

        //ConsultArticle(articleCourant.getId() + 1);


    }
    private void ConsultArticle(int id)
    {
        try {
            String Requete = "CONSULT#" + id;

            System.out.println(Requete);

            int nbEcrits = tcp.Send(inter.getSocket(), Requete);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + Requete + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            String[] parts = response.split("#");

            String command = parts[0];

            System.out.println(command);
            if ("LOGOUT".equals(command))
            {
                String reponse = parts[1];

                System.out.println(reponse);

                if ("-1".equals(reponse))
                {
                    String intitule = parts[2];
                    int stock = Integer.parseInt(parts[3]);
                    float prix = Float.parseFloat(parts[4]);
                    String image = parts[5];

                    setArticle(intitule, prix, stock , image);

                    articleCourant = new Article(Integer.parseInt(reponse), intitule, prix, stock, image);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void setArticle(String intitule, float prix, int stock, String image)
    {
        // initialisation des articles
        marc.getArticle().setText(intitule);
        marc.getStock().setText(String.valueOf(stock));
        marc.getPrix().setText(String.valueOf(prix));

        String filepath = "./images/" + image;

        ImageIcon imageIcon = new ImageIcon(filepath);

        marc.getPhoto().setIcon(imageIcon);
    }
    public void Acheter()
    {
        System.out.println("Acheter");

    }
    public void supprimer()
    {
        System.out.println("supprimer");

    }
    public void viderPanier()
    {
        System.out.println("viderPanier");


    }
    public void confirmerAchat()
    {
        System.out.println("confirmerAchat");

    }


}

