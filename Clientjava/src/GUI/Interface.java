package GUI;


import Controller.controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class Interface extends JFrame {
    private  JTextField Adresse;
    private  JTextField Port;
    private  JButton connecterButton;
    private JPanel Connection;



    public Socket socket;

    public Interface()
    {

        setSize(400,400);
        setTitle("Interface de connection ");
        setContentPane(Connection);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        controller controller =new controller(this,null);




        connecterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == connecterButton)
                {
                    socket=controller.connection();
                }


            }
        });
    }
    public  JButton getConnecter() {
        return connecterButton;
    }

    public  JTextField getAdresse() {
        return Adresse;
    }

    public  JTextField getPort() {
        return Port;
    }

    public Socket getSocket() {


        return socket;
    }

}
