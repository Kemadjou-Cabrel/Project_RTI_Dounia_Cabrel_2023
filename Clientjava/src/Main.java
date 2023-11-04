import Controller.controller;
import GUI.Interface;
import GUI.marche;

public class Main {

    public static void main(String[] args)
    {


        Interface inter =new Interface();

        marche marc = new marche(inter);

        controller controller =new controller(inter,marc);

        //on cree une  instance de la classe Controller en lui passant l'instance de inter comme argument.
        // Cela lie le contrôleur à la fenêtre.
        // Le contrôleur est responsable de gérer les événements de l'interface
        // et de coordonner les actions entre les composants graphiques et le reste de l'application.

        inter.setVisible(true);

    }
}