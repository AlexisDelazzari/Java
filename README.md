# Projet Banque Qualite Dev

Ce projet à été mis à jour par :
    
    Burnel Justin
    Couto Joshua
    De-lazzari Alexis
    Engelmann Quentin

Bienvenue dans le nouveau projet où nous redonnerons vie à une application bancaire délaissée. En tant qu'expert JAVA, notre mission comprend la correction des bugs, l'ajout de fonctionnalités, la documentation technique, la création d'un diagramme de classe métier, et le développement d'une documentation fonctionnelle. Nous adoptons GitHub pour le développement. L'objectif ultime est d'assurer une excellence technique à travers des métriques précis, tout en respectant un planning de livraison bien défini. C'est un engagement envers la qualité et la robustesse de l'application.



## Initialisation du git 

> `git clone https://github.com/AlexisDelazzari/Java`

Au cas ou vous ne seriez pas sur la branche principale vous pouvez faire :
> `git checkout master `
> 
>
> `git pull ` 

## Installation  

Pour compiler notre projet Java il nous faut installer la version de 17 de JDK (Java Developement Kit), 
ensuite nous devont ajouter la variables d'environnement JAVA_HOME avec le lien vers le JDK puis mettre dans la variable d'environnement PATH le lien vers JAVA_HOME.

Il nous fallu aussi installer le fichier bin de maven, pour cela nous avons télécharger {https://maven.apache.org/download.cgi}, puis nous allons aussi créé une variables d'environnement relatif. {../Maven\apache-maven-3.9.4\bin}

## Compilation du projet 

Pour installer les dépendances nécessaires au sein du dossier on compile avec la commande :  
& mvn compile -f {lien vers le pom.xml}

## Installation de MySQL

Vous devez installer la version 5.1.44. de [MySQL](https://downloads.mysql.com/archives/community/)

Lors de l'installation laissez toutes les options par défauts. Mettez root pour l'utilisateur et pour le mot de passe.

## Lancement de MySQL

Lancer MySQL Command Line Client, saisissez ensuite le mot de passer pour accéder à l'invite de commande MySQL.

Créez la base de données en utilisant la commande :

- CREATE DATABASE banque;

puis selectionner cette base de données avec la commande :

- USE banque;

Importez les tables et les données, en utilisant le fichier "dumpSQL.sql" dans le dossier "script", copiez tout son contenue {controle A puis controle C} puis collez-le dans l'invite de commande MySQL {controle V}

Il faut ensuite installer la base de test : 

- Create database banqueiuttest;
- use banqueiuttest;
- Copier le contenue du fichier "src/dumpSQL_JUnitTest.sql"

## Installation du projet

Pour installer notre projet Java nous allons utiliser la ligne de commande suivante : 
- mvn clean install

Cette commande va crée un dossier "target" avec notre projet java a l'intérieur ainsi qu'un .war qui va nous permettre d'importer notre projet sur tomcat.

Une fois cela fait, ouvrir le fichier "applicationContext" situé dans "WebContent/WEB-INF", aller a la lige 49 et remplacer cette igne par :

- `<property name="url" value="jdbc:mysql://localhost:3306/banque?useSSL=false" />`

En haut d'IntelliJ, appuyez sur la flèche bleue, puis sur "Edit Configurations".
Si Tomcat n'est pas affiché, appuyez sur le bouton "+" en haut à gauche et sélectionnez Tomcat.

Dans le déploiement, appuyez sur le "+" et sélectionnez l'artifact : "war:exploded".

Changer la configurations, pour cela mettez :

- HTPP port: 8080
- JMX port : 1099

Puis lancer tomcat et conecter-vous avec les identifiants : "rootpass" et "root".

## Initialisation de Tomcat

Il vous faut installer [Tomcat](https://tomcat.apache.org/download-90.cgi), de lancer l'exécutable. Nous auront besoin de deux lignes de commande qui vont nous permettre de lancer et femer le serveur.

- Startup 
- Shutdown 

Une fois tomcat installer nous nous rendont dans le fichier tomcat-user.xml qui se trouve dans le fichier conf, une fois dans ce fichier on décommente et modifier la partie uername et password. Une fois cela fait nous allons pouvoir ajouter le .war préalablement générer ce qui va crée un répertoire au sein du serveur.



## Différence poids

La différence de poids entre les sources compressées est attribuable au dossier 'target'. La suppression de ce dossier dans la nouvelle source est justifiée, car 'target' est un répertoire de build du projet. Il est créé localement à chaque compilation pour stocker les fichiers temporaires et les résultats de la compilation. Par conséquent, inclure le dossier 'target' dans la source n'est pas nécessaire et peut augmenter la taille de l'archive inutilement. Il est préférable de se concentrer sur les fichiers de configuration essentiels, car ces fichiers sont suffisants pour déployer et exécuter le projet. Cela permet également de réduire la taille des fichiers sources, ce qui est utile pour le stockage et le partage du projet.

Ainsi, la suppression du dossier 'target' dans la source comprimée est une pratique recommandée pour optimiser l'utilisation des ressources et maintenir des archives de projet plus légères.

De plus le dossier .idea est un répertoire spécifique aux projets créés avec des outils JetBrains tels qu'IntelliJ IDEA et WebStorm. Il contient des fichiers de configuration et de paramétrage spécifiques au projet. Inclure le dossier .idea dans une source comprimée n'est généralement pas recommandé, car cela peut entraîner des problèmes de confidentialité et de compatibilité.

Confidentialité : Le dossier .idea peut contenir des informations sensibles telles que les paramètres de connexion à la base de données, les préférences personnelles de l'utilisateur, ou d'autres données confidentielles liées au projet. L'inclusion de ces informations dans une source compressée peut poser des problèmes de sécurité et de confidentialité, surtout si le projet est partagé ou publié publiquement.

Compatibilité : Les fichiers de configuration contenus dans le dossier .idea sont spécifiques à l'environnement de développement local de l'utilisateur. Les inclure dans la source peut entraîner des problèmes de compatibilité lorsque d'autres développeurs tentent de travailler sur le projet, car ces fichiers peuvent ne pas correspondre à leurs propres configurations.

### Screenshot analyse de code hebdomadaire

Les screens de l'analyse de code hebdomadaire sont dans le dossier "screenshot" du projet ranger par semaine.

### Auteur

- [Justin burnel](https://www.github.com/Dakuken)
- [Couto Joshua](https://github.com/Narga1299)
- [Engelmann Quentin](https://github.com/Yahzog)
- [De-Lazzari Alexis](https://github.com/AlexisDelazzari)





