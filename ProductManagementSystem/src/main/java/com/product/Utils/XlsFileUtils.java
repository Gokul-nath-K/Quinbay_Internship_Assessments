package com.product.Utils;

import com.product.Entity.Orders;
import com.product.Services.Impl.OrderDbServiceImpl;
import com.product.Services.OrderDbServices;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.List;

public class XlsFileUtils {

    String xls_fileName = "/Users/gokulnathk/Documents/Inventory.xls";
    String csvFileName = "/Users/gokulnathk/Documents/Inventory.csv";

    OrderDbServices orderDbServices = new OrderDbServiceImpl();

    public void exportDatabaseToExcel() {

        try {

            File file = new File(xls_fileName);

            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (!created) {
                    throw new IOException("Failed to create new file: " + xls_fileName);
                }
            }

            // Open file input stream
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook;

            // Check if the file is empty
            if (file.length() == 0) {
                workbook = new HSSFWorkbook(); // Create a new workbook if file is empty (for .xls format)
            } else {
                workbook = WorkbookFactory.create(fileInputStream);
            }
            boolean sheetExists = false;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (workbook.getSheetAt(i).getSheetName().equalsIgnoreCase("Orders")) {
                    sheetExists = true;
                    break;
                }
            }

            Sheet orderSheet;
            if (!sheetExists) {
                orderSheet = workbook.createSheet("Orders");
                Row headerRow = orderSheet.createRow(0);
                String[] headers = {"Order ID", "Total Price", "Number of Items", "Order Status", "Ordered On", "Updated On"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }
            } else {
                orderSheet = workbook.getSheet("Orders");
            }

            List<Orders> ordersList = orderDbServices.getOrderList();

            int rowNum = 1;
            for (Orders order : ordersList) {
                Row dataRow = orderSheet.createRow(rowNum++);

                dataRow.createCell(0).setCellValue(order.getId());
                dataRow.createCell(1).setCellValue(order.getTotalPrice());
                dataRow.createCell(2).setCellValue(order.getNumberOfItems());
                dataRow.createCell(3).setCellValue(order.getOrderStatus());
                dataRow.createCell(4).setCellValue(order.getOrderedOn().toString()); // Convert Date to String as needed
                dataRow.createCell(5).setCellValue(order.getUpdatedOn().toString()); // Convert Date to String as needed
            }

            // Save changes to the workbook
            try (FileOutputStream outputStream = new FileOutputStream(xls_fileName)) {
                workbook.write(outputStream);
            }

            System.out.println("Excel file exported successfully.");
            excelToCSV(xls_fileName, csvFileName);
            fileInputStream.close();

        } catch (IOException exception) {

            System.out.println(exception.getMessage());
        }
    }

    public void excelToCSV(String excel, String csvFile) {

        try {
            FileInputStream excelFile = new FileInputStream(new File(excel));

            Workbook workbook = WorkbookFactory.create(excelFile);

            Sheet sheet = workbook.getSheet("Orders");

            FileWriter csvWriter = new FileWriter(csvFile);

            for (Row row : sheet) {
                StringBuilder csvLine = new StringBuilder();
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            csvLine.append(cell.getStringCellValue()).append(",");
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                csvLine.append(cell.getDateCellValue()).append(",");
                            } else {
                                csvLine.append(cell.getNumericCellValue()).append(",");
                            }
                            break;
                        case BOOLEAN:
                            csvLine.append(cell.getBooleanCellValue()).append(",");
                            break;
                        case FORMULA:
                            csvLine.append(cell.getCellFormula()).append(",");
                            break;
                        default:
                            csvLine.append(",");
                    }
                }
                csvWriter.append("\n");
                csvWriter.append(csvLine.toString());
            }

            csvWriter.close();

            System.out.println("Excel file converted to CSV successfully.");

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }

    }
}
