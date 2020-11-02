# Docker setup

## Ordner für Sensordaten erstellen
Im Projektordner das Skript start.sh ausführen, erstellt Ordner und lädt die benötigten CSV-Dateien runter.
```
sudo ./start.sh
```

Docker und Docker compose installieren:

https://docs.docker.com/engine/install/ubuntu/

https://docs.docker.com/compose/install/

## Optional - Benutzer zur Gruppe "docker" hinzufügen
**_!!!!!_**
**_Bei sudo usermod -a -G docker <username> unbedingt -a UND -G in den command schreiben, sonst werden die Gruppen nicht hinzugefügt, sondern überschrieben_**
**_!!!!!_**

Mit
```
sudo usermod -a -G docker <username>
```
den Benutzer zur Gruppe docker hinzufügen, somit erspart man sich das sudo vor jedem Befehl.
Danach, reboot
```
reboot
```
Nach dem Neustart dann mit
```
groups
```
alle zugeordneten Gruppen anzeigen lassen, hier sollte "docker" dabei sein.

## 1 Danach ins Projektverzeichnis, bei mir "D:\Code\253\docker"
Docker initialisieren (lädt linuxkernel, usw)
```
sudo ./createDocker.sh
```
Danach können mit 
```
sudo docker image ls
```
alle erstellten images aufgelistet werden und mit 
```
sudo docker container list -a
```
der status jedes containers abgefragt werden.

## 2 Build und execute
Jetzt tipseln wir an unserem Programm und möchten die Änderungen compilieren und den Server/Client ausführen:
```
sudo ./mvn.sh
```
Buildet das Projekt und legt die target-folder an, in denen die jars liegen, welche für die server.sh oder client.sh benötigt werden.
```
sudo ./server.sh
```
den Server starten
```
sudo ./client.sh
```
den Client starten

## 3 Container Stoppen
Mit 
```
sudo docker container list -a
```
den Namen des Containers rausfinden und zum Beispiel mit 
```
sudo docker stop oop2-server
```
den Server Container stoppen.

## When shit hits the fan
Mit
```
sudo docker-compose down
```
werden alle Images gelöscht, danach zurück zu Schritt 1.
