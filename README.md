# Analizuj
This is module for researching and comparing products and their prices from 3 different web sources

## Quick start

Download and Install Analizuj packet

The software requires java 1.7 and gradle installed.

## Building a 'fat' Jar

To include all dependencies in one jar module gradle-one-jar is used.
In order to build 'fat' jar run command in your command window:

    gradle analizujJar

## Running Analizuj module

The program can have up to 3 input parameters.
First parameter is required and points to input spreadsheet file (xls).
Second parameter is the output file, if not specified default is used.
Third parameter is optional and points to file with product codes, if not specified product codes are parsed from the original input spreadsheet and a code file is created.


    java -jar analizuj.jar ./input.xls [./output] [./codes]
    
Second column is used from the input spreadsheet to pull product name.
Codes are parsed from the last part of a product name.
If the last code component is shorter than 3 characters, one more proceeding section is added to a code.

To get better search results open generated code file and check which auto generated codes need corrections.
You can edit this file and after corrections use it for searches.
