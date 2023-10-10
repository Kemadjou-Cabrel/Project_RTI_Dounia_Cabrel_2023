#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <signal.h>
#include <mysql.h>
#define DERNIER_ARTICLE_ID 22 

#include "OVESP.h"

//***** Etat du protocole : liste des clients loggés ****************

int clients[NB_MAX_CLIENTS];

int nbClients = 0;

int estPresent(int socket);

void ajoute(int socket);

void retire(int socket);

// mutex qui protege la liste des clients connecte 
pthread_mutex_t mutexClients = PTHREAD_MUTEX_INITIALIZER;


//***** Parsing de la requete et creation de la reponse *************

bool OVESP(char* requete, char* reponse,int socket, MYSQL* conn)

{
    // ***** Récupération nom de la requete *****************

    char *ptr = strtok(requete,"#");
    
    // ***** LOGIN ******************************************
    if (strcmp(ptr,"LOGIN") == 0) 
    {
        char user[50], password[50];

        int nouveauClient;
        //page 66
        strcpy(user,strtok(NULL,"#"));

        strcpy(password,strtok(NULL,"#"));

        nouveauClient = atoi(strtok(NULL,"#"));


        printf("\t[THREAD %lu] LOGIN de %s\n",pthread_self(),user);
        
        if (estPresent(socket) >= 0) // client déjà loggé
        {
            sprintf(reponse,"LOGIN#ko#Client déjà loggé !");
        }
        else
        {
            if(nouveauClient == 0)
            {
                switch(OVESP_Login(user,password,conn))
                {
                    case 0:
                        sprintf(reponse,"LOGIN#ok");
                        ajoute(socket);
                        break;
                    case 1:
                        sprintf(reponse,"LOGIN#ko#Mauvais identifiants!");
                        break;
                    case 2:
                        sprintf(reponse,"LOGIN#ko#Mauvais mot de passe!");
                        break;
                }
            }
            else
            {
                if(OVESP_NouveauLogin(user,password,conn) == 0)
                {
                    sprintf(reponse,"LOGIN#ok");

                    ajoute(socket);
                }
                else
                {
                    sprintf(reponse,"LOGIN#ko#Nom utilisateur deja utilise");
                }
            }
        }
    }

    // ***** LOGOUT ******************************************
    if (strcmp(ptr,"LOGOUT") == 0)
    {
        OVESP_Logout(socket);

        sprintf(reponse,"LOGOUT#ok");
    }

    // ***** CONSULT ******************************************
    if (strcmp(ptr,"CONSULT") == 0)
    {
        int idCon = atoi(strtok(NULL,"#"));

        OVESP_Consult(idCon, reponse, conn);
    }

    // ***** ACHAT ******************************************
    if (strcmp(ptr,"ACHAT") == 0)
    {
        int idCon = atoi(strtok(NULL,"#"));

        int quant = atoi(strtok(NULL,"#"));

        OVESP_Achat(idCon, reponse, quant, conn);
    }

    // ***** CANCEL ******************************************
    if (strcmp(ptr,"CANCEL") == 0)
    {
        int id = atoi(strtok(NULL,"#"));

        int stock = atoi(strtok(NULL,"#"));

        OVESP_Cancel(id, stock, reponse, conn);
    }

    // ***** CANCEL_ALL ******************************************
    if (strcmp(ptr,"CANCEL_ALL") == 0)
    {
        int nbArti = atoi(strtok(NULL,"#"));

        if(nbArti == 0) 
        {
            sprintf(reponse, "CANCEL_ALL#ko");
        }   
        else
        {
            char requette[100] = "";

            int i = 1;

            strcat(requette, strtok(NULL,"#"));

            while(i < nbArti)
            {
                strcat(requette, "#");
                strcat(requette, strtok(NULL,"#"));

                i++;
            }

            OVESP_Cancel_All(requette, nbArti, reponse, conn);
        }
    }

    return true;
}

//***** Traitement des requetes *************************************

int OVESP_Login(const char* user,const char* password, MYSQL* connexion)
{
    char requete[100];
    MYSQL_RES  *resultat;
    MYSQL_ROW  Tuple;

    int val;
    char table[20];

    strcpy(table, "utilisateurs");

    sprintf(requete,"select * from %s where login = '%s';", table, user);


    if (mysql_query(connexion,requete) != 0)
    {
        fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
        exit(1);
    }

    printf("Requete SELECT réussie sur login.\n");

    // Affichage du Result Set

    if ((resultat = mysql_store_result(connexion)) == NULL)
    {
        fprintf(stderr, "Erreur de mysql_store_result: %s\n",mysql_error(connexion));
        exit(1);
    }

    if((Tuple = mysql_fetch_row(resultat)) != NULL)
    {
        if(strcmp(password, Tuple[1]) == 0) 
        {
            val = 0;// ok
        }
        else 
        {
            val = 2;  // erreur mot de passe 
        }    
    }
    else
    {
        val = 1;//erreur existe pas 
    }

    return val;
}

int OVESP_NouveauLogin(const char* user,const char* password,MYSQL* connexion)
{
    char requete[200];
    MYSQL_RES  *resultat;
    MYSQL_ROW  Tuple;

    int val;
    char table[20];

    strcpy(table, "utilisateurs");

    sprintf(requete,"select * from %s where login = '%s';", table, user);


    if (mysql_query(connexion,requete) != 0)
    {
        fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
        exit(1);
    }

    printf("Requete SELECT réussie sur login.\n");

    // Affichage du Result Set

    if ((resultat = mysql_store_result(connexion)) == NULL)
    {
        fprintf(stderr, "Erreur de mysql_store_result: %s\n",mysql_error(connexion));
        exit(1);
    }

    if((Tuple = mysql_fetch_row(resultat)) != NULL)
    {
        val = 1;//client deja existant avec cette identifiant
    }
    else
    {
        sprintf(requete,"insert into %s (login, MDP) values ('%s', '%s')", table, user, password);


        if (mysql_query(connexion,requete) != 0)
        {
            fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
            exit(1);
        }

        printf("Requete INSERT réussie sur login.\n");

        val = 0;//nouveau client inserer dans le BD
    }

    return val;
}

void OVESP_Logout(int sock)
{
    retire(sock);
}

void OVESP_Consult(int id, char* rep, MYSQL* connexion)
{
    char requete[100];
    MYSQL_RES  *resultat;
    MYSQL_ROW  Tuple;

    char table[20];

    strcpy(table, "articles");

    sprintf(requete,"select * from %s where id = %d;", table, id);


    if (mysql_query(connexion,requete) != 0)
    {
        fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
        exit(1);
    }

    printf("Requete SELECT réussie sur Consult.\n");

    // Affichage du Result Set

    if ((resultat = mysql_store_result(connexion)) == NULL)
    {
        fprintf(stderr, "Erreur de mysql_store_result: %s\n",mysql_error(connexion));
        exit(1);
    }

    // Preparation de la reponse 
    if((Tuple = mysql_fetch_row(resultat)) != NULL)
    {
        if (id == DERNIER_ARTICLE_ID)
        {
            sprintf(rep, "CONSULT#-1#Dernier_article ");
        }
        else
        {
            printf("%s - %s - %s - %s - %s \n", Tuple[0], Tuple[1], Tuple[2], Tuple[3], Tuple[4]);

            sprintf(rep,"CONSULT#%s#%s#%s#%s#%s", Tuple[0], Tuple[1], Tuple[3], Tuple[2], Tuple[4]);
        }

    }
    else
    {
        sprintf(rep, "CONSULT#-1#Impossible_de_retour");
        
    }
}

void OVESP_Achat(int id, char* rep, int quant, MYSQL* connexion)
{
    char requete[200];
    MYSQL_RES  *resultat;
    MYSQL_ROW  Tuple;

    char table[20];

    strcpy(table, "articles");

    sprintf(requete,"select * from %s where id = %d;", table, id);


    if (mysql_query(connexion,requete) != 0)
    {
        fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
        exit(1);
    }

    printf("Requete SELECT réussie sur Achat.\n");

    // Affichage du Result Set

    if ((resultat = mysql_store_result(connexion)) == NULL)
    {
        fprintf(stderr, "Erreur de mysql_store_result: %s\n",mysql_error(connexion));
        exit(1);
    }

    // Preparation de la reponse

    if((Tuple = mysql_fetch_row(resultat)) != NULL)
    {
        if(atoi(Tuple[3]) < quant) 
        {
            sprintf(rep,"ACHAT#%s#0", Tuple[0]);
        }
        else
        {
            int Stock = atoi(Tuple[3]) - quant;

            sprintf(requete,"update %s set stock = %d where id = %d;", table, Stock, id);

            if (mysql_query(connexion,requete) != 0)
            {
                fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
                exit(1);
            }

            printf("Requete UPDATE réussie.\n");

            sprintf(rep,"ACHAT#%s#%s#%s", Tuple[0], Tuple[3], Tuple[2]);
        }
    }
    else
    {
        sprintf(rep,"ACHAT#-1");
    }
}

void OVESP_Cancel(int id, int quant, char* rep, MYSQL* connexion)
{
    char requete[200];
    MYSQL_RES  *resultat;
    MYSQL_ROW  Tuple;

    char table[20];

    strcpy(table, "articles");

    sprintf(requete,"select * from %s where id = %d;", table, id);


    if (mysql_query(connexion,requete) != 0)
    {
        fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
        exit(1);
    }

    printf("Requete SELECT réussie sur Cancel.\n");

    // Affichage du Result Set

    if ((resultat = mysql_store_result(connexion)) == NULL)
    {
        fprintf(stderr, "Erreur de mysql_store_result: %s\n",mysql_error(connexion));
        exit(1);
    }

    // Preparation de la reponse

    if((Tuple = mysql_fetch_row(resultat)) != NULL)
    {
        int Stock = atoi(Tuple[3]) + quant;

        sprintf(requete,"update %s set stock = %d where id = %d;", table, Stock, id);

        if (mysql_query(connexion,requete) != 0)
        {
            fprintf(stderr, "Erreur de mysql_query: %s\n",mysql_error(connexion));
            exit(1);
        }

        printf("Requete UPDATE réussie.\n");

        sprintf(rep,"CANCEL#%s#%d", Tuple[0], atoi(Tuple[3]) + quant);
    }
    else
    {
        sprintf(rep,"CANCEL#-1");
    }
}

void OVESP_Cancel_All(char *requete, int nbArti, char* rep, MYSQL* connexion)
{
    int i = 0, id, stock;

    char *ptr = strtok(requete,"&");
    char idS[5];

    strcpy(idS, ptr);
    id = atoi(idS);
    stock = atoi(strtok(NULL,"#"));

    while(i < nbArti)
    {
        //autant de cancel qu'il y'a article

        OVESP_Cancel(id, stock, rep, connexion);

        if(i != nbArti-1)
        {
            strcpy(idS, strtok(NULL,"&"));
            id = atoi(idS);
            stock = atoi(strtok(NULL,"#"));
        }
        i++;
    }

    sprintf(rep, "CANCEL_ALL#ok");
}

//***** Gestion de l'état du protocole ******************************
int estPresent(int socket)
{
    int indice = -1;

    pthread_mutex_lock(&mutexClients);

    for(int i=0 ; i<nbClients ; i++)
    {
        if (clients[i] == socket) 
        { 
            indice = i; 
            break; 
        }
    }

    pthread_mutex_unlock(&mutexClients);
    
    return indice;
}

void ajoute(int socket)
{
    pthread_mutex_lock(&mutexClients);
    clients[nbClients] = socket;
    nbClients++;
    pthread_mutex_unlock(&mutexClients);
}

void retire(int socket)
{
    int pos = estPresent(socket);

    if (pos == -1) 
    {
        return;
    }

    pthread_mutex_lock(&mutexClients);

    for (int i=pos ; i<=nbClients-2 ; i++)
    {
        clients[i] = clients[i+1];
    }

    nbClients--;

    pthread_mutex_unlock(&mutexClients);
}

//***** Fin prématurée **********************************************

void OVESP_Close()
{
    pthread_mutex_lock(&mutexClients);

    for (int i=0 ; i<nbClients ; i++)
    {
        close(clients[i]);
    }

    pthread_mutex_unlock(&mutexClients);
}