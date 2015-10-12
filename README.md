# Analizuj
This is module for researching and comparing products and their prices from 3 different web sources

## Quick start

Download and Install Analizuj packet

The software requires java 1.8 and gradle installed.

# Properties file

There is a .properies file used to define parameters and properties located in Analizuj/src/main/resources/app.properties

Adjust your parameters before building jar file or make sure that properties file is in the classpath.

Here are parameters and their meanings:

Name used for the producent column

    producent=Cersanit
    
List of spreadsheet workbooks being processed

    workbooks=3 
    
Columns numbers on the spreadsheet beeing processed (column start from 0)

First is for Kod producenta (katalogowy), second Opis asortymentu, third Cena producenta (katalogowa) 

    columns=5 6 9
    
Column number on the spreadsheet containg product code    

    code_indeks=5
    
Input file including path    

    plik_wejsciowy=C:\\Lista cenowa Rovese_PL_TT_ 13 05 2015.xlsx
    
Output file path name, workdook name is attached to this file name    

    plik_wyjsciowy=C:\\output

## Building a 'fat' Jar

To include all dependencies in one jar module gradle-one-jar is used.
In order to build 'fat' jar run command in your command window:

    gradle analizujJar

## Running Analizuj module

    java -jar analizuj.jar 
    
The output result file contains 6 tab delimited columns in the following order:

    Producent\t    Kod producenta (katalogowy)\t  Opis asortymentu\t Cena producenta (katalogowa)\t Cena Ceneo\t   Cena Jaar\t Cena Armadeo
    
The first product found is selected, assuming it is a best match.
If the row value is empty, no product was found using a code provided.
The output can be then imported into an excel spreadsheet for analysis.
