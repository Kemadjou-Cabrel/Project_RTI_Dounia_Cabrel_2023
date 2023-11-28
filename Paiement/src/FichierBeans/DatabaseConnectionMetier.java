package FichierBeans;

import Modele.Facture;
import Modele.Vente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class DatabaseConnectionMetier {
    private DatabaseConnectionGenrique ConnectionGenerique;
    public DatabaseConnectionMetier()
    {
        ConnectionGenerique = new DatabaseConnectionGenrique();
    }
    public int Login(String login, String MDP)
    {
        try {
            String query = "SELECT * FROM utilisateurs WHERE login = '" + login + "'";

            ResultSet rs = ConnectionGenerique.executeQuery(query);

            int id = 0;

            if(rs.next()){

                if(MDP.equals(rs.getString("password")))
                {
                    id = rs.getInt("id");
                }

            }

            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void close()
    {
        try
        {
            ConnectionGenerique.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public LinkedList<Facture> Factures(boolean paye, int idClient)
    {
        try {
            int id;

            if(paye)
            {
                id = 1;
            }
            else
                id = 0;

            String query = "SELECT * FROM factures WHERE paye = " + id + " AND idClient = " + idClient + " AND dateFacture IS NOT NULL";

            ResultSet res = ConnectionGenerique.executeQuery(query);

            LinkedList<Facture> factures = new LinkedList<>();

            while (res.next())
            {
                int idFacture = res.getInt("idFacture");
                String dateFacture = res.getString("dateFacture");
                Float montant = res.getFloat("montant");

                Facture facture = new Facture(idFacture, dateFacture, montant);

                factures.add(facture);
            }

            return factures;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int Paiement(int idFacture)
    {
        try {
            String query = "UPDATE factures SET paye = 1 where idFacture = " + idFacture;

            int payement = ConnectionGenerique.executeUpdate(query);

            if (payement > 0)
            {
                System.out.println("Votre paiement a ete  effectué avec succès.");
                return 1;
            }
            else
            {
                System.out.println("Paiement pas effectuer");
                return 0;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
