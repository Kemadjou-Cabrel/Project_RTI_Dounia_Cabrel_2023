#include "windowclient.h"
#include "../Librairie/TCP.h"

#include <QApplication>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <pthread.h>

void HandlerSIGINT(int s);

WindowClient *w;
int sClient;

int main(int argc, char *argv[])
{
    if (argc != 3)
    {
        printf("Erreur...\n");
        printf("Veillez entrer le port du client \n");
        exit(1);
    }

//--------------------------------------------Armement des signaux-----------------------------------------------------------------
    struct sigaction A;
    A.sa_flags = 0;
    sigemptyset(&A.sa_mask);
    A.sa_handler = HandlerSIGINT;

    if (sigaction(SIGINT,&A,NULL) == -1)
    {
        perror("Erreur de sigaction");
        exit(1);
    }
//----------------------------------------------Creation de la socket client---------------------------------------------------------

    if ((sClient = ClientSocket(argv[1],atoi(argv[2]))) == -1)
    {
        perror("Erreur de ClientSocket");
        exit(1);
    }

    QApplication a(argc, argv);
    w = new WindowClient();
    //initialisation de la socket de service
    w->setSocket(sClient);
    w->show();
    return a.exec();
}

void HandlerSIGINT(int s)
{
    printf("\nArret du client.\n");
    
    close(sClient);
    
    exit(0);
}
