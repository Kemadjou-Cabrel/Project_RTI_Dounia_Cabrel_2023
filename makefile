.SILENT:


PROGRAMS = Serveur Client CreationBD

OBS = g++ -Wno-unused-parameter -c -pipe -g -std=gnu++11 -Wall -W -D_REENTRANT -fPIC -DQT_DEPRECATED_WARNINGS -DQT_QML_DEBUG -DQT_WIDGETS_LIB -DQT_GUI_LIB -DQT_CORE_LIB -I../UNIX_DOSSIER_FINAL -I. -isystem /usr/include/qt5 -isystem /usr/include/qt5/QtWidgets -isystem /usr/include/qt5/QtGui -isystem /usr/include/qt5/QtCore -I. -I. -I/usr/lib64/qt5/mkspecs/linux-g++ -o

all:	Serveur Client CreationBD

Serveur:	Serveur/Serveur.cpp Librairie/TCP.o Protocol/OVESP.o
	echo "Creation du Serveur"
	g++ Serveur/Serveur.cpp Librairie/TCP.o Protocol/OVESP.o -o Serveu -lpthread -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient

Librairie/TCP.o:	Librairie/TCP.h Librairie/TCP.cpp
	echo "Creation du TCP.o"
	g++ Librairie/TCP.cpp -c -o Librairie/TCP.o -Wall #-D DEBUG

Protocol/OVESP.o:	Protocol/OVESP.h Protocol/OVESP.cpp
	echo "Creation du OVESP.o"
	g++ Protocol/OVESP.cpp -c -o Protocol/OVESP.o -Wall -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl #-D DEBUG

Client:	ClientQt/mainclient.o ClientQt/windowclient.o ClientQt/moc_windowclient.o
	echo "Creation du Client"
	g++ -Wno-unused-parameter -o Client ClientQt/mainclient.o ClientQt/windowclient.o ClientQt/moc_windowclient.o Librairie/TCP.o  /usr/lib64/libQt5Widgets.so /usr/lib64/libQt5Gui.so /usr/lib64/libQt5Core.so /usr/lib64/libGL.so -lpthread

ClientQt/moc_windowclient.o:	ClientQt/moc_windowclient.cpp
	echo "Creation du moc_windowclient.o"
	$(OBS) ClientQt/moc_windowclient.o ClientQt/moc_windowclient.cpp

ClientQt/windowclient.o:	ClientQt/windowclient.cpp
	echo "Creation du windowclient.o"
	$(OBS) ClientQt/windowclient.o ClientQt/windowclient.cpp

ClientQt/mainclient.o:	ClientQt/mainclient.cpp
	echo "Creation du mainclient.o"
	$(OBS) ClientQt/mainclient.o ClientQt/mainclient.cpp

CreationBD:	BD/CreationBD.cpp
	echo création de la BD
	g++ -o CreationBD BD/CreationBD.cpp   -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl

clean:
	rm -f Serveur/*.o Client/*.o Client CreationBD Serveu