# wildCart SB Server 2021

* WildCart is a project that was developed by my students & I @ 2021.
* It's an online shop.
* It's made to be simple to learn.
* This backend API was developed on Java over Spring Boot. Uses MySQL or MariaDB Database underneath.
* It's still a working in progress: I estimate grade of madurity of API server: 70%
* You can access the API spec: https://rafaelaznar.github.io/wildCartSBServer2021/  (needs to be updated with product comments and captcha)

## How to run Wildcart back-end in development mode?

In a development environment we are concerned with speed of setup and ease of configuration versus security or productivity. The development system can be set up in Linux, Windows or Mac environments, although Linux is recommended.

First of all you have to clone the repository in a folder of your local machine.

MySQL or MariaDB as a relational database management system needs to be installed and running. To access the database, the installation of an interface to the database is mandatory. It is recommend phpMyAdmin to manage the database because it works on the browser, but there are other desktop options such as DBeaver, HeidiSQL, TOAD, DBVisualizer, etc. The advantage of phpMyAdmin is that it works on a WAMP, LAMP or MAMP environment that also requires the installation of Apache. Apache will also be used to serve the font-end environment. The script for loading the database structure is in the file /docs/wildCart2021.sql. You have to create the database (by default named wildcart2021) and execute the creation script.

The Java develop environment can also be set up in any of the three Linux, Windows and Mac systems. To develop with the Java environment, you must install the Java Development Kit (JDK), which is a development environment that developers use to generate, execute, and debug Java programs. It is also needed an IDE to be able to develop with Java. The use of Visual Studio Code supported by a selection of plugins is recommended, because it has some advantages such as being able to use Github Copilot, but any of the three classic Java development environments can be used: Eclipse, NetBeans or IntelliJ. This back-end can be executed within any of these environments. The server execution opens a port on the machine where it is executed to serve the API. Through this port the data from the server is accessed from the front-end. Currently the port is 8082, but if this port is changed, it must also be done on the client side, where the queries are sent to the server. Therefore, before running the server it is convenient to review the file /src/main/resources/application.properties for two reasons: to establish the port where the server API will be opened and to establish the connection string with the database. Normally we have the database manager running on the same machine but open on a different port, 3306 by default. You also have to review the dabase name, and login and password for the database manager. If the server cannot connect to the database it will not work.

To get the web application quickly running there are a number of api endpoints to create fake data. These endpoints can be accessed from the front-end so there is no need to do anything else on the server.

To continue the start-up of WildCart web application in development mode, you can access a [how to run Wildcart front-end in development mode](https://github.com/rafaelaznar/wildCartAngularClient2021)
