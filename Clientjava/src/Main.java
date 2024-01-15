import Controller.controller;
import GUI.Interface;
import GUI.marche;

public class Main {

    public static void main(String[] args)
    {


        Interface inter =new Interface();

        marche marc = new marche(inter);

        controller controller =new controller(inter,marc);

        inter.setVisible(true);

    }
}