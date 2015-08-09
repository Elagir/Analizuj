package com.jaar.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelParser {

	public List<String> parseExcel(String fileName, List<String> parsedNames) {
		List<String> parsedValues = new ArrayList<String>();

		try {

			FileInputStream file = new FileInputStream(new File(fileName));

			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			Integer columnNo = new Integer(1);
			// output all not null values to the list
			List<Cell> cells = new ArrayList<Cell>();

			for (Row row : sheet) {
				Cell cell = row.getCell(columnNo);
				if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
					// Nothing in the cell in this row, skip it
				} else {
					cells.add(cell);

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						parsedValues.add(String.valueOf(cell.getBooleanCellValue()));
						parsedNames.add(String.valueOf(cell.getBooleanCellValue()));
						break;
					case Cell.CELL_TYPE_NUMERIC:
						parsedValues.add(String.valueOf(cell.getNumericCellValue()));
						parsedNames.add(String.valueOf(cell.getNumericCellValue()));
						break;
					case Cell.CELL_TYPE_STRING:
						String value = cell.getStringCellValue();
						String code = value;
						String name = value;
						int lastSpaceIndex = value.lastIndexOf(" ");
						if (lastSpaceIndex >= 0) {
							code = value.substring(lastSpaceIndex + 1);
							if (code.length() <= 3) {
								int prevlastSpaceIndex = value.substring(0, lastSpaceIndex).lastIndexOf(" ");
								if (prevlastSpaceIndex >= 0) {
									if (value.substring(prevlastSpaceIndex + 1, lastSpaceIndex).equalsIgnoreCase("X")) {
										prevlastSpaceIndex = value.substring(0, prevlastSpaceIndex).lastIndexOf(" ");
									}
									code = value.substring(prevlastSpaceIndex + 1);
								}
							}
							parsedValues.add(code);
							parsedNames.add(name);
						}
						break;
					}
				}
			}
			file.close();
			workbook.close();

		} catch (FileNotFoundException e1) {
			System.out.println("Excel czytanie - Blad w plikach:" + e1.getMessage());
		} catch (IOException e2) {
			System.out.println("Excel czytanie - Blad w plikach:" + e2.getMessage());
		}
		return parsedValues;
	}

}
