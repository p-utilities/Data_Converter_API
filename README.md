# Data_Converter_API

To convert CSV to XML files, there needs to be a converter. This API converts CSV to XML and vica versa. When there is a need for validation in between conversion, this API can do that to! <br>

To test this API, look at [instructions](#instructions) for using API with embeded implementation.<br>
To use this API for other purposes, please check little [documentation](#documentation) that I have createt.

## Table of content
- [Quick overview of API](#overview)
- [Instructions for running this project](#instructions)
  - [Using desktop app to convert](#desktop)
  - [Using client-server to convert](#client-server)
- [Documentation](#documentation)
  - [Adding new DTOs (Data Transfer Object)](#adding-dtos)
  - [Adding new validators](#adding-validators)
  - [Creating converter](#converter)
- [What to expect next](#next)
  
## Quick overview of API <a name="overview"></a>

**Notice** This repository contains all nessesery dependencies for running.

Imagine there is XML files that contains data, but your requirenments are to convert that data into CSV file. Converting on its own is very simple, but what if there is some validation whitch you have to take before you write it into new file. To solve that isue, I came up with idea to first pull data from file, XML or CSV, put that data into individual objects, validate that objects and then write that validated data into new file.

This API consist of three parts, reading data, validating data and finally, writing data. Depending on data that you need to convert, there is great posibility that you need to create your own data transfer objects. In the background, there is object creator service. Object creator service will create and populate your object with data that is passed into service. Currently, object creator service an only create DTO who has String variables. Every variable needs to have getter and setter method so that creator service can call those methods to get, or to set values into object. After creation of all objects, next up is validation. Currently there are two types of validator, exclude validator and exception validator. Exclude validator will just exclude object that does not pass the validation. Exception validation will throw exception and stop furthure validation. You can implement your own validation rules. After validation is complete, time is to write data into new file. 

If there is a need to rearange data and to store it into same file type, this API can do that for you. To check how to test current API with some embeded data, please go to the next section.

Below will be presented UML Class diagram so that you can understend better what is going on here :D<br><br>
![Class diagram](/Application_1_class_diagram.jpg)

## Instructions for running this project <a name="instructions"></a>
### Using desktop app to convert <a name="desktop"></a>
### Using client-server to convert <a name="client-server"></a>


## Documentation <a name="documentation"></a>
### Adding new DTOs (Data Transfer Objects) <a name="adding-dtos"></a>
### Adding new validators <a name="adding-validators"></a>
### Creating converter <a name="converter"></a>
## What to expect next <a name="next"></a>

  
