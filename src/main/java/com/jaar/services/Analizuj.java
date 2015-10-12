package com.jaar.services;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.client.ClientConfig;

public class Analizuj {
	public static final String FILE_NAME_PARSED = "./wynik";

	public static void main(String[] args) {

		String excelName = "";
		String fileNameCode = "";
		String fileNameParsed = FILE_NAME_PARSED;

		if (args.length > 0) {
			excelName = args[0];
		} else {
			System.out.println("Usage: analizuj plik wejsciowy [plik wyjsciowy] [plik z kodami] ");
			return;
		}
		if (args.length > 1) {
			fileNameParsed = args[1];
		}
		if (args.length > 2) {
			fileNameCode = args[2];
		}

		List<String> parsedCodes;

		if (fileNameCode.length() > 0) {
			parsedCodes = readCodeFile(fileNameCode);
		} else {
			ExcelParser parser = new ExcelParser();
			List<String> parsedNames = new ArrayList<String>();
			parsedCodes = parser.parseExcel(excelName, parsedNames);
			writeCodeFile(parsedCodes, parsedNames, fileNameParsed+"_kody");
		}

		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		List<String> prodCeneo = new ArrayList<String>();
		List<String> prodJaar = new ArrayList<String>();
		List<String> prodArmadeo = new ArrayList<String>();
		List<String> priceCeneo = new ArrayList<String>();
		List<String> priceJaar = new ArrayList<String>();
		List<String> priceArmadeo = new ArrayList<String>();

		CallService service = new CallService();
		service.callCeneoService(client, parsedCodes, Defines.URL_CENEO, prodCeneo, priceCeneo);
		service.callJAARService(client, parsedCodes, Defines.URL_JAAR, prodJaar, priceJaar);
		service.callArmadeoService(client, parsedCodes, Defines.URL_ARMADEO, prodArmadeo, priceArmadeo);

		writeParsedFile(parsedCodes, prodCeneo, prodJaar, prodArmadeo, priceCeneo, priceJaar, priceArmadeo,
				fileNameParsed);
		System.out.println("wyszukiwanie zakonczone");
	}

	private static void writeParsedFile(List<String> parsedCodes, List<String> prodCeneo, List<String> prodJaar,
			List<String> prodArmadeo, List<String> priceCeneo, List<String> priceJaar, List<String> priceArmadeo,
			String fileName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

			out.println(
					"Kod szukany\tProdukt w Ceneo\tCena Ceneo\tProdukt w Jaar\tCena Jaar\tProdukt w Armadeo\tCena Armadeo");
			int idx = 0;
			for (String code : parsedCodes) {

				out.println(code + "\t" + prodCeneo.get(idx) + "\t" + priceCeneo.get(idx) + "\t" + prodJaar.get(idx)
						+ "\t" + priceJaar.get(idx) + "\t" + prodArmadeo.get(idx) + "\t" + priceArmadeo.get(idx));
				idx++;
			}
			out.flush();
			out.close();

		} catch (IOException e) {
			System.out.println("Plik z produktami pisanie - Blad w plikach:" + e.getMessage());
		}
	}

	private static void writeCodeFile(List<String> parsedCodes, List<String> parsedNames, String fileName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

			out.println("Kod szukany\tProdukt");
			int idx = 0;
			for (String code : parsedCodes) {
				out.println(code+"\t"+"PRODUKT: "+parsedNames.get(idx++));
			}
			out.flush();
			out.close();

		} catch (IOException e) {
			System.out.println("Plik z kodami pisanie - Blad w plikach:" + e.getMessage());
		}
	}

	private static List<String> readCodeFile(String fileName) {
		List<String> parsedCodes = new ArrayList<String>();
		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.ISO_8859_1);
			boolean first = true;
			for (String line : lines) 
			{ 
				if(!first)
				{
					parsedCodes.add(line.split("\t")[0]); 
				}
				first = false;
			} 
		} catch (IOException e) {
			System.out.println("Plik z kodami czytanie - Blad w plikach:" + e.getMessage());
		}
		return parsedCodes;
	}

}
