# Data Converter API

To convert CSV to XML files, there needs to be a converter. This API converts CSV to XML and vica versa. When there is a need for validation in between conversion, this API can do that to! <br>

To test this API, look at [instructions](#instructions) for using API with embeded implementation.<br>
To use this API for other purposes, please check little [documentation](#documentation) that I have createt.

## Table of content
- [Quick overview of API](#overview)
- [Instructions for running this project](#instructions)
  - [Using desktop app to convert](#desktop)
  - [Using client-server to convert](#client-server)
  
## Quick overview of API <a name="overview"></a>

**Notice :** This repository contains all nessesery dependencies for running.

Imagine there is XML files that contains data, but your requirenments are to convert that data into CSV file. Converting on its own is very simple, but what if there is some validation whitch you have to take before you write it into new file. To solve that isue, I came up with idea to first pull data from file, XML or CSV, put that data into individual objects, validate that objects and then write that validated data into new file.

This API consist of three parts, reading data, validating data and finally, writing data. Depending on data that you need to convert, there is great posibility that you need to create your own data transfer objects. In the background, there is object creator service. Object creator service will create and populate your object with data that is passed into service. Currently, object creator service an only create DTO who has String variables. Every variable needs to have getter and setter method so that creator service can call those methods to get, or to set values into object. After creation of all objects, next up is validation. Currently there are two types of validator, exclude validator and exception validator. Exclude validator will just exclude object that does not pass the validation. Exception validation will throw exception and stop furthure validation. You can implement your own validation rules. After validation is complete, time is to write data into new file. 

If there is a need to rearange data and to store it into same file type, this API can do that for you. To check how to test current API with some embeded data, please go to the next section.

Below will be presented UML Class diagram so that you can understend better what is going on here :D<br><br>
![Class diagram](/Application_1_class_diagram.jpg)

## Instructions for running this project <a name="instructions"></a>

This project includes two applications. First is Swing desktop application and the second is simple socket application. Both applications should be run using executable jars provided in the folder [runningJars](https://github.com/kakarot94/Data_Converter_API/tree/main/runningJars).

### Using desktop app to convert <a name="desktop"></a>

To use swing demo application, you can clone this repository or you can download exe jar. To download exe jar, click on 'View raw' on this [page](https://github.com/kakarot94/Data_Converter_API/blob/main/runningJars/desktopApp/application_1.jar) and save the file and follow commands below. <br>

To use executable JAR affter cloning repository, find desktop exe jar inside folder runningJars/desktopApp.<br>

To start the application, open terminal or command line and enter next comand:
```bash
$ java -jar application_1.jar /input/dir/path/ /output/dir/path/
```
/input/dir/path is directory path where application can find XML and/or CSV files. If you do not have any, you can use provided files. Provided input XML and CSV files can be found in [sample/input](https://github.com/kakarot94/Data_Converter_API/tree/main/sample/input).

### Using client-server to convert <a name="client-server"></a>

To use client-server demo application, you can clone this repository or you can download exe jar. To download exe jar, click on 'View raw' on this [page](https://github.com/kakarot94/Data_Converter_API/blob/main/runningJars/desktopApp/application_1.jar) and save the file and follow commands below. <br>

To use executable JAR affter cloning repository, find desktop exe jar inside folder runningJars/desktopApp.<br>

**Important : ** server part needs to be run before client part!

To start the server part, open terminal or command line and enter next comand:
```bash
$ java -jar server_app.jar 1234 /output/dir/path/output_file_name.xml
```
When starting server part, you need to specify the name and the type of the file to create. After that, server is up and running. As soon as you run client part corectly, conversion will begin.

To start the client part, open terminal or command line and enter next comand:
```bash
$ java -jar client_app.jar localhost 1234 /input/dir/path/input_file.xml
```

/input/dir/path is directory path where application can find XML and/or CSV files. If you do not have any, you can use provided files. Provided input XML and CSV files can be found in [sample/input](https://github.com/kakarot94/Data_Converter_API/tree/main/sample/input). 
