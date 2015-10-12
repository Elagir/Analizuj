package com.jaar.rovese;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.client.ClientConfig;

import com.jaar.services.Defines;

public class ExcelReader {

	private static String delimiters = " \t,;.";
	private static String producent = "";
	private static Integer codeIdx = 0;

	public static void main(String[] args) throws IOException {

		String plik_wejsciowy = PropertiesCache.getInstance().getProperty("plik_wejsciowy");
		String codeIndex = PropertiesCache.getInstance().getProperty("code_indeks");
		FileInputStream fis = new FileInputStream(new File(plik_wejsciowy));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		int cnt = workbook.getNumberOfSheets();

		String plik_wyjsciowy = PropertiesCache.getInstance().getProperty("plik_wyjsciowy");
		producent = PropertiesCache.getInstance().getProperty("producent").trim();
		String workbooks = PropertiesCache.getInstance().getProperty("workbooks");
		List<XSSFSheet> workbookList = new ArrayList<XSSFSheet>();
		StringTokenizer st = new StringTokenizer(workbooks, delimiters);
		while (st.hasMoreTokens()) {
			String worbookNo = st.nextToken().toLowerCase().trim();
			Integer idx = Integer.parseInt(worbookNo);
			if (idx < cnt) {
				workbookList.add(workbook.getSheetAt(idx));
			}
		}

		String columns = PropertiesCache.getInstance().getProperty("columns");
		ArrayList<Integer> columnList = new ArrayList<Integer>();
		st = new StringTokenizer(columns, delimiters);
		int idx = 0;
		while (st.hasMoreTokens()) {
			String columnNo = st.nextToken().toLowerCase().trim();
			columnList.add(Integer.parseInt(columnNo));
			if(codeIndex == columnNo)
			{
				codeIdx = idx;
			}
			idx++;
		}

		processWorkbook(workbookList, columnList, plik_wyjsciowy);
			
		workbook.close();
		fis.close();

		System.out.println("Koniec !!!");
	}

	public static void processWorkbook(List<XSSFSheet> workbookList, List<Integer> columnList, String plik_wyjsciowy) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		workbookList.forEach(sheet -> {
		writeWorshseetFile(client, processWorksheet(sheet, columnList), plik_wyjsciowy, sheet.getSheetName());});
	}

	public static List<String> getParsedCodes(List<List<String>> data) {
		List<String> codes = new ArrayList<String>();
		data.forEach(value -> codes.add(value.get(codeIdx)));
		return codes;
	}


	public static List<List<String>> processWorksheet(XSSFSheet sheet, List<Integer> columns) {

		List<List<String>> rowumndata = new ArrayList<List<String>>();
		for (Row row : sheet) {
			// To filter column headings
			if (row.getRowNum() > 0) {
				
				List<String> columndata = new ArrayList<String>();
				for (Integer columnNo : columns) {
					Cell cell = row.getCell(columnNo);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						columndata.add(cell.getNumericCellValue() + "");
						break;
					case Cell.CELL_TYPE_STRING:
						columndata.add(cell.getStringCellValue());
						break;
					}
				}
				rowumndata.add(columndata);
			}
		}
		return rowumndata;
	}
	
	public static void writeWorshseetFile(Client client, List<List<String>> parsedData, String plikWyjsciowy, String sheetName)
	{		
//		List<String> prodCeneo = new ArrayList<String>();
//		List<String> prodJaar = new ArrayList<String>();
//		List<String> prodArmadeo = new ArrayList<String>();

		List<String> priceCeneo = new ArrayList<String>();
		List<String> priceJaar = new ArrayList<String>();
		List<String> priceArmadeo = new ArrayList<String>();
		
		List<String> parsedCodes = getParsedCodes(parsedData);
		
		
		CallServiceBasic service = new CallServiceBasic();
		Runnable r1 = () -> service.callCeneoService(client, parsedCodes, Defines.URL_CENEO, priceCeneo);
		Runnable r2 = () -> service.callJAARService(client, parsedCodes, Defines.URL_JAAR, priceJaar);
		Runnable r3 = () -> service.callArmadeoService(client, parsedCodes, Defines.URL_ARMADEO, priceArmadeo);
		Thread thread1 = new Thread(r1);
		Thread thread2 = new Thread(r2);
		Thread thread3 = new Thread(r3);
		thread1.start();
		thread2.start();
		thread3.start();
		
		try {
			thread1.join();
			thread2.join();
			thread3.join();
		} catch (InterruptedException e) {
			System.out.println("Blad w operacji szukania:" + e.getMessage());
			e.printStackTrace();
		}
		writeParsedFile(parsedData, priceCeneo, priceJaar, priceArmadeo, plikWyjsciowy+"_"+sheetName+".xls");
	}

	
	private static void writeParsedFile(List<List<String>> parsedData, List<String> priceCeneo, List<String> priceJaar, List<String> priceArmadeo,
			String fileName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

			// Producent
			// Kod producenta (katalogowy)
			// Opis asortymentu
			// Cena producenta (katalogowa)

			out.println(
					"Producent\tKod producenta (katalogowy)\tOpis asortymentu\tCena producenta (katalogowa)\tCena Ceneo\tCena Jaar\tCena Armadeo");
			
			parsedData.forEach(data -> {
				int idx = parsedData.indexOf(data);
				StringBuilder values = new StringBuilder(producent).append("\t");
				data.forEach(value -> values.append(value).append("\t"));
				
				out.println(values + priceCeneo.get(idx) + "\t" + priceJaar.get(idx) + "\t" + priceArmadeo.get(idx));
			    System.out.println(values + priceCeneo.get(idx) + "\t" + priceJaar.get(idx) + "\t" + priceArmadeo.get(idx));
			});
			out.flush();
			out.close();

		} catch (IOException e) {
			System.out.println("Plik z produktami pisanie - Blad w plikach:" + e.getMessage());
		}
	}

}
