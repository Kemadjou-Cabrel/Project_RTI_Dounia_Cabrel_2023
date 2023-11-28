package Controller;

import GUI.Interface;
import GUI.marche;
import Modele.Article;
import Modele.TCP;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

public class controller
{

    private  Interface inter;
    private marche marc;
    private  Socket socket;
    private  TCP tcp;
    private boolean logged;
    private Article articleCourant;
    private LinkedList<Article> Caddie;
    private int nbArticles;
    private int numFacture;

    private float totalCaddie;



    public controller(Interface inter,marche marc)
    {
        this.inter=inter;

        this.marc=marc;

        numFacture = 0;

        nbArticles = 0;

        totalCaddie = 0.0F;

        Caddie = new LinkedList<>();

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

                    numFacture = Integer.parseInt(parts[2]);

                    getCaddie();

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
                    if(nbArticles > 0) {
                        vide();
                    }

                    JOptionPane.showMessageDialog(null, "Au Plaisir !!!! ;)", "LOGOUT", JOptionPane.INFORMATION_MESSAGE);

                    marc.ButtonDesactive();


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
        ConsultArticle(articleCourant.getId() - 1);

    }
    public void Suivant()
    {
        System.out.println("Suivant");

        ConsultArticle(articleCourant.getId() + 1);


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
            if ("CONSULT".equals(command))
            {
                String reponse = parts[1];

                System.out.println(reponse);

                if (!"-1".equals(reponse))
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

        String filepath = "./Clientjava/images/" + image;

        ImageIcon imageIcon = new ImageIcon(filepath);

        marc.getPhoto().setIcon(imageIcon);


    }
    public void Acheter()
    {
        System.out.println("Acheter");

        try {
            int quantite = (int) marc.getQuantite().getValue();

            if(quantite == 0)
            {
                throw new Exception("Veuillez sélectionner une valeur supérieure à 0");
            }

            int i = 0;

            for (Article art: Caddie)
            {
                if(art.getId() == articleCourant.getId())
                {
                    break;
                }

                i++;


            }

            if(Caddie.size() == i)
            {
                i = 10;
            }


            System.out.println(i);

            if(nbArticles == 10 && i == 10)
            {
                throw new Exception("votre panier est plein,");
            }

            String texte = "ACHAT#" + articleCourant.getId() + "#" + quantite;

            System.out.println(texte);

            int nbEcrits = tcp.Send(inter.getSocket(), texte);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + texte + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            String[] parts = response.split("#");

            String command = parts[0];

            System.out.println(command);
            if ("ACHAT".equals(command))
            {
                int ID = Integer.parseInt(parts[1]);
                if(ID != -1)
                {

                    int Stock = Integer.parseInt(parts[2]);

                    if (Stock == 0)
                        throw new Exception("stock insuffisant!");
                    else
                    {
                        articleCourant.setStock(articleCourant.getStock() - quantite);

                        marc.getStock().setText(String.valueOf(articleCourant.getStock()));

                        if(i == 10)
                        {
                            Caddie.add(new Article(articleCourant.getId(), articleCourant.getIntitule(), articleCourant.getPrix(), quantite, articleCourant.getImage()));

                            totalCaddie += (quantite*articleCourant.getPrix());

                            marc.getTotal().setText(String.valueOf(totalCaddie));

                            ajouteArticleTablePanier(articleCourant.getIntitule(), articleCourant.getPrix(), quantite);

                            texte = "MISE_A_JOUR#" + numFacture + "#0#0#" + totalCaddie + "#" + articleCourant.getId() + "#" + quantite;

                            System.out.println(texte);

                            nbEcrits = tcp.Send(inter.getSocket(), texte);

                            System.out.println("NbEcrits = " + nbEcrits);

                            System.out.println("Ecrit = --" + texte + "--");

                             response = tcp.Receive(inter.getSocket());

                            System.out.println("Lu = --" + response + "--");

                            nbArticles++;
                        }
                        else
                        {
                            Caddie.get(i).setStock(Caddie.get(i).getStock() + quantite);

                            totalCaddie = 0.0F;

                            for (Article art: Caddie) {
                                totalCaddie += (art.getPrix()*art.getStock());
                            }

                            marc.getTotal().setText(String.valueOf(totalCaddie));

                            DefaultTableModel modelArticles = (DefaultTableModel) marc.getTableau().getModel();

                            modelArticles.setValueAt(Caddie.get(i).getStock(), i, 2);

                            texte = "MISE_A_JOUR#" + numFacture + "#0#0#" + totalCaddie + "#" + articleCourant.getId() + "#" + quantite;

                            System.out.println(texte);

                            nbEcrits = tcp.Send(inter.getSocket(), texte);

                            System.out.println("NbEcrits = " + nbEcrits);

                            System.out.println("Ecrit = --" + texte + "--");

                            response = tcp.Receive(inter.getSocket());

                            System.out.println("Lu = --" + response + "--");

                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Achat", JOptionPane.ERROR_MESSAGE);
        }

    }
    public void supprimer()
    {
        System.out.println("supprimer");

        try {
            int ind = marc.getTableau().getSelectedRow();

            if(ind == -1)
                throw new Exception("veuillez choisir une article pour la suppression");

            String Requete = "CANCEL#" + Caddie.get(ind).getId() + "#" + Caddie.get(ind).getStock();

            System.out.println(Requete);

            int nbEcrits = tcp.Send(inter.getSocket(), Requete);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + Requete + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            String[] parts = response.split("#");

            String command = parts[0];

            System.out.println(command);
            if ("CANCEL".equals(command))
            {
                int ID = Integer.parseInt(parts[1]);

                if(ID != -1)
                {
                    if(articleCourant.getId() == ID)
                    {
                        articleCourant.setStock(Integer.parseInt(parts[2]));

                        marc.getStock().setText(String.valueOf(articleCourant.getStock()));
                    }

                    Caddie.remove(ind);
                    nbArticles--;

                    DefaultTableModel modelArticles = (DefaultTableModel) marc.getTableau().getModel();

                    modelArticles.removeRow(ind);

                    totalCaddie = 0.0F;

                    for (Article art: Caddie)
                    {
                        totalCaddie += (art.getPrix()*art.getStock());
                    }

                    marc.getTotal().setText(String.valueOf(totalCaddie));

                    Requete = "MISE_A_JOUR#" + numFacture + "#1#" + totalCaddie + "#" + articleCourant.getId();

                    System.out.println(Requete);

                    nbEcrits = tcp.Send(inter.getSocket(), Requete);

                    System.out.println("NbEcrits = " + nbEcrits);

                    System.out.println("Ecrit = --" + Requete + "--");

                    response = tcp.Receive(inter.getSocket());

                    System.out.println("Lu = --" + response + "--");
                }
            }
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Supprimer", JOptionPane.ERROR_MESSAGE);
        }


    }
    public void viderPanier()
    {
        vide();

        ////mettre a jour le facture dans le BD
        String Requete = "SUPPRIME_CADDIE#" + numFacture;

        System.out.println(Requete);
        try {


            int nbEcrits = tcp.Send(inter.getSocket(), Requete);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + Requete + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Vider", JOptionPane.ERROR_MESSAGE);
        }



    }




    public void vide()
    {
        try {
            String Requete = "CANCEL_ALL#" + nbArticles;

            for (Article art:Caddie) {
                Requete += "#" + art.getId() + "&" + art.getStock();
            }

            System.out.println(Requete);

            int nbEcrits = tcp.Send(inter.getSocket(), Requete);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + Requete + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            String[] parts = response.split("#");

            String command = parts[0];

            System.out.println(command);

            if ("CANCEL_ALL".equals(command))
            {
                if(parts[1].equals("ko"))
                    throw new Exception("Vous n'avez rien dans votre panier !");

                totalCaddie = 0.0F;

                marc.getTotal().setText(null);

                DefaultTableModel modelArticles = (DefaultTableModel) marc.getTableau().getModel();

                int rowCount = modelArticles.getRowCount();

                for (int i = rowCount - 1; i >= 0; i--) {
                    modelArticles.removeRow(i);
                }

                for (Article art: Caddie) {
                    if(art.getId() == articleCourant.getId())
                    {
                        articleCourant.setStock(articleCourant.getStock()+art.getStock());
                        marc.getStock().setText(String.valueOf(articleCourant.getStock()));
                        break;
                    }
                }

                Caddie = new LinkedList<>();

                nbArticles = 0;
            }
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Vider", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void confirmerAchat()
    {
        System.out.println("confirmerAchat");

        try
        {
            if(nbArticles == 0)
                throw new Exception("Votre panier est vide !");

            String Requete = "CONFIRMER#" + numFacture + "#" + marc.getNom().getText();

            System.out.println(Requete);

            int nbEcrits = tcp.Send(inter.getSocket(), Requete);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + Requete + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            String[] parts = response.split("#");

            String command = parts[0];

            System.out.println(command);
            if ("CONFIRME".equals(command))
            {
                if(parts[1].equals("-1"))
                    throw new Exception("erreur");
                else
                {
                    totalCaddie = 0.0F;

                    marc.getPrix().setText(null);

                    DefaultTableModel modelArticles = (DefaultTableModel) marc.getTableau().getModel();

                    int rowCount = modelArticles.getRowCount();

                    for (int i = rowCount - 1; i >= 0; i--) {
                        modelArticles.removeRow(i);
                    }

                    Caddie = new LinkedList<>();

                    nbArticles = 0;

                    numFacture = Integer.parseInt(parts[1]);

                    JOptionPane.showMessageDialog(null, "Achat confirmer!", "Payer", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Payer", JOptionPane.ERROR_MESSAGE);
        }

    }
    public void getCaddie()
    {
        try
        {

            String Requete = "CADDIE#" + numFacture;

            System.out.println(Requete);

            int nbEcrits = tcp.Send(inter.getSocket(), Requete);

            System.out.println("NbEcrits = " + nbEcrits);

            System.out.println("Ecrit = --" + Requete + "--");

            String response = tcp.Receive(inter.getSocket());

            System.out.println("Lu = --" + response + "--");

            StringTokenizer tokenizer = new StringTokenizer(response, "#");
            String ptr = tokenizer.nextToken();

            if (ptr.equals("CADDIE")) {
                nbArticles = Integer.parseInt(tokenizer.nextToken());

                if (nbArticles > 0) {
                    int i = 0;

                    while (i < nbArticles) {


                        StringTokenizer itemTokenizer = new StringTokenizer(tokenizer.nextToken(), "$");

                        int id = Integer.parseInt(itemTokenizer.nextToken());
                        String intitule = itemTokenizer.nextToken();
                        int stock = Integer.parseInt(itemTokenizer.nextToken());
                        float prix = Float.parseFloat(itemTokenizer.nextToken());

                        Article art = new Article(id, intitule, prix, stock, "");

                        Caddie.add(art);

                        ajouteArticleTablePanier(intitule, prix, stock);

                        totalCaddie += (stock * prix);

                        i++;
                    }

                    marc.getTotal().setText(String.valueOf(totalCaddie));
                    System.out.println("cabrel");
                }
            }
        }
        catch (Exception exception)
        {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "recuperation caddie", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouteArticleTablePanier(String intitule, float prix, int stock)
    {
        DefaultTableModel modelArticles = (DefaultTableModel) marc.getTableau().getModel();

        Vector ligne = new Vector();
        ligne.add(intitule);
        ligne.add(prix);
        ligne.add(stock);

        modelArticles.addRow(ligne);
    }


}

