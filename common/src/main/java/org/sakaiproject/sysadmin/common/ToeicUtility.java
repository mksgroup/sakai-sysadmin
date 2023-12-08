package org.sakaiproject.sysadmin.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sakaiproject.sysadmin.common.model.Question;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FPT
 *
 */
@Slf4j
public class ToeicUtility {
//	private static final String PATH_READ = "H:\\projects\\github.com\\Tuanskiet\\sakai-sysadmin\\ETS_2022_N1_processed.xlsx";
//	private static final String PATH_WRITE = "H:\\projects\\github.com\\Tuanskiet\\sakai-sysadmin\\ETS_2022_N1_processed_W.xlsx";
	
	/**
	 * Write all data to Excel file with sample format (write 7 part in TOEIC test).
	 * @param dataToeicTest is a data you want to write into Excel file.
	 * @param path is a file path you want to write.
	 */
	public static void writeAllPart(List<List<Question>> dataToeicTest, String path, String outputPath) {
		for(int i = 0; i < dataToeicTest.size(); i ++) {
			List<Question> dataPart =  readDataToExcel(i + 1, path);
			dataToeicTest.add(dataPart);
			writeDataToExcel(i, outputPath, dataPart);
		}
	}
	
	/**
	 * Read all data from Excel file (7 part).
	 * @param path is a file path you want to read.
	 * @return data with type List<List<Question>>.
	 */
	public static List<List<Question>> readAllPart(String path) {
		List<List<Question>> dataToeicTest = new ArrayList<>();
		for(int i = 0; i < 7; i ++) {
			List<Question> dataPart =  readDataToExcel(i + 1, path);
			dataToeicTest.add(dataPart);
		}
		return dataToeicTest;
	}
	
	/**
	 * Write data to Excel file with sample format
	 * @param part is a part of the TOEIC test, equivalent to a sheet in a sample Excel file.
	 * @param path is a file path you want to save.
	 * @param data is a data you want to write into the excel file.  
	 * @exception  Exception  if write process occurs problem.
	 */
	public static void writeDataToExcel(Integer part, String path, List<Question> data) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(path));
		    try (Workbook workbook = new XSSFWorkbook(fis)) {
				Sheet sheet = workbook.getSheetAt(part - 1);
				int lastRow = sheet.getLastRowNum();
				for (int indexRow = 1; indexRow <= lastRow; indexRow++) {
					writeRow(sheet, indexRow, data);
				}
				FileOutputStream out = new FileOutputStream(new File(path)); 
				workbook.write(out); 
				out.close();
			}
		    log.info("Write data to part {}", part);
		    System.out.println("Write data to part :: " + part);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Write row data to Excel file with sample format
	 * @param sheet specify sheet to write the row.
	 * @param indexRow specify index of current row.
	 * @param data is a data you want to write into the excel file.  
	 * @exception  Exception  if write process occurs problem.
	 */
	private static void writeRow(Sheet sheet, int indexRow, List<Question> data) {
		Row row = sheet.getRow(indexRow);
	    int lastCell = row.getLastCellNum();
	    Question dataQuestion = data.get(indexRow - 1);
	    for (int indexCell = 0; indexCell < lastCell; indexCell++) {
	    	Object dataField = dataQuestion.getDataField(indexCell);
	    	if(dataField != null) {
	    		writeCell(row, indexCell, dataField);
	    	}
//    	else {
//    		Cell cell = row.getCell(indexCell);
//            if(cell == null) {
//            	Cell newCell = row.createCell(indexCell);
//            }
//    	}
	    }
		
	}

	/**
	 * Write cell data to Excel file with sample format.
	 * @param row specify sheet to write the cell.
	 * @param indexCell specify index of current cell.
	 * @param dataField is a data you want to write into the excel file.  
	 * @exception  Exception  if write process occurs problem.
	 */
	private static void writeCell(Row row, int indexCell, Object dataField) {
		Cell cell = row.getCell(indexCell);
		if(cell == null) {
        	Cell newCell = row.createCell(indexCell);
        	if(dataField instanceof Double) {
        		newCell.setCellType(CellType.NUMERIC);
        		newCell.setCellValue((Double)dataField);
        	}else if(dataField instanceof String) {
        		newCell.setCellValue((String)dataField);
        	}else {
        		newCell.setCellValue((String)dataField);
        	}
        }else {
        	try {
            	switch (cell.getCellType()) {
                case STRING:
                	cell.setCellValue((String)dataField);
                    break;
                case NUMERIC:
                	cell.setCellValue((Double)dataField);
                    break;
                default:
                	if(indexCell == 0) {
                		cell.setCellType(CellType.NUMERIC);
                		cell.setCellValue((Double)dataField);
                	}else {
                		cell.setCellValue((String)dataField);
                	}
                    break;
            	}
            }catch (Exception e) {
            	e.printStackTrace();
			}
        }
		
	}


	/**
	 * Read data from Excel file
	 * @param part is a part of the TOEIC test, equivalent to a sheet in a sample Excel file.
	 * @param path is a file path you want to read.
	 * @param data is a data you want to write into the excel file.  
	 * @exception  Exception  if read process occurs problem.
	 * @return data with type List<Question>.
	 */
	public static List<Question> readDataToExcel(Integer part, String path) {
		List<Question> listQuestions = new ArrayList<>();
		try {
		    FileInputStream fis = new FileInputStream(new File(path));
		    try (Workbook workbook = new XSSFWorkbook (fis)) {
				Sheet sheet = workbook.getSheetAt(part - 1);
				int lastRow = sheet.getLastRowNum();

				for (int indexRow = 1; indexRow <= lastRow; indexRow++) {
					Question question = readRow(workbook, sheet, indexRow);
				    listQuestions.add(question);
				}
			}
		    fis.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		log.info("Read data to part {}", part);
        System.out.println("Read data to part ::" + (part + 1));
		return listQuestions;
	}
	
	/**
	 * Read row data from Excel file with sample format.
	 * @param workbook is a workbook you want to save.
	 * @param indexRow specify index of current row.
	 * @param sheet specify sheet to read the row.
	 * @exception  Exception  if write process occurs problem.
	 */
	private static Question readRow(Workbook workbook, Sheet sheet, int indexRow) {
		Row row = sheet.getRow(indexRow);
	    int lCell = row.getLastCellNum();
	    Question question = new Question();
	    for (int indexCell = 0; indexCell < lCell; indexCell++) {
	    	readCell(workbook, row, indexCell, question);
	    }
		return question;
	}

	/**
	 * Read cell data from Excel file with sample format.
	 * @param workbook is a workbook you want to save.
	 * @param row specify sheet to write the cell.
	 * @param indexCell specify index of current cell.
	 * @param question is a data you want to write into the excel file.  
	 * @exception  Exception  if write process occurs problem.
	 */
	private static void readCell(Workbook workbook, Row row, int indexCell, Question question) {
		 Cell cell = row.getCell(indexCell);
	        try {
	        	switch (cell.getCellType()) {
	            case STRING:
	            	question.setDataField(indexCell, cell.getStringCellValue());
	                break;
	            case NUMERIC:
	            	question.setDataField(indexCell, cell.getNumericCellValue());
	                break;
	            case FORMULA:
	            	FormulaEvaluator formula = workbook.getCreationHelper().createFormulaEvaluator();
	            	question.setDataField(indexCell, formula.evaluate(cell).getNumberValue());
	                break;
	            default:
	            	question.setDataField(indexCell, cell.getStringCellValue());
	                break;
	        	}
	        }catch (NullPointerException e) {
				e.printStackTrace();
			}
		
	}

	public static void readDataToJson(String path) {
//		List<List<Question>> dataToeicTest = new ArrayList<>();
//		for(int i = 0; i < 7; i ++) {
//			List<Question> dataPart =  readDataToExcel(i, PATH_READ);
//			dataToeicTest.add(dataPart);
//		}
//	    
//	    try {
//	    	System.out.println("Writing data to ..., please waiting ...");
//	    	objectMapper objectMapper = new ObjectMapper();
//			objectMapper.writerWithDefaultPrettyPrinter().writeValue(System.out, dataToeicTest);
//			objectMapper.writeValue(new File(path), dataToeicTest);
//			log.info("Write data successfully!");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
}
