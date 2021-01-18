package com.dfq.coeffi.cbs.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ReportDownload {

    public static void writeXLSXFile() throws IOException {

        String fileName = "E:/Ashvini/download/text.xlsx";

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");

        setImageLogo(wb,sheet);
        Row row;
        Cell cell;

        for(int currentRow = 4; currentRow < 26; currentRow++){
            row = sheet.createRow(currentRow);
            row.setRowStyle(mainStyle(wb));
        }
        int rowCount = 8;
        int columnCount = 9;

        row = sheet.createRow(5);
        row.setRowStyle(mainTableHeaderFont(wb));

        cell = row.createCell(7);
        cell.setCellValue("UTILIZATION");
        cell.setCellStyle(logoTextStyle(wb));


        row = sheet.createRow(7);
        row.setRowStyle(mainTableHeaderFont(wb));

        cell = row.createCell(9);
        cell.setCellValue("Shift");
        cell.setCellStyle(setHeaderBorder(wb));

        cell = row.createCell(10);
        cell.setCellValue("Total Hours");
        cell.setCellStyle(setHeaderBorder(wb));

        cell = row.createCell(11);
        cell.setCellValue("Actual Time Travelled");
        cell.setCellStyle(setHeaderBorder(wb));

        cell = row.createCell(12);
        cell.setCellValue("AGR Utilization in (%)");
        cell.setCellStyle(setHeaderBorder(wb));

        for (int currentRow = 1; currentRow < 4; currentRow++) {
            row = sheet.createRow(rowCount);
            row.setRowStyle(mainTableRowStyle(wb));
            cell = row.createCell(columnCount+0);
            cell.setCellValue("Shift " + rowCount);
            cell.setCellStyle(setBorder(wb));
            cell = row.createCell(columnCount+1);
            cell.setCellValue(8);
            cell.setCellStyle(setBorder(wb));

            cell = row.createCell(columnCount+2);
            cell.setCellValue(5);
            cell.setCellStyle(setBorder(wb));

            cell = row.createCell(columnCount+3);
            cell.setCellValue(58 + 4 + rowCount);
            cell.setCellStyle(setBorder(wb));
            rowCount++;
        }

        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 8, 20);

        Chart chart = drawing.createChart(anchor);

        CTChart ctChart = ((XSSFChart) chart).getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
        CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
        ctBoolean.setVal(true);
        ctBarChart.addNewBarDir().setVal(STBarDir.COL);

        CTBarSer ctBarSer = ctBarChart.addNewSer();
        CTSerTx ctSerTx = ctBarSer.addNewTx();
        CTStrRef ctStrRef = ctSerTx.addNewStrRef();
        ctStrRef.setF("Sheet1!$A$" + 2);
        ctBarSer.addNewIdx().setVal(0);

        CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
        ctStrRef = cttAxDataSource.addNewStrRef();
        ctStrRef.setF("Sheet1!$J$9:$J$11");
        CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
        CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
        ctNumRef.setF("Sheet1!$M$" + 9 + ":$M$" + 11);

        //at least the border lines in Libreoffice Calc ;-)
        ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[]{0, 0, 0});


        //telling the BarChart that it has axes and giving them Ids
        ctBarChart.addNewAxId().setVal(123456);
        ctBarChart.addNewAxId().setVal(123457);

        //cat axis
        CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
        ctCatAx.addNewAxId().setVal(123456); //id of the cat axis
        CTScaling ctScaling = ctCatAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctCatAx.addNewDelete().setVal(false);
        ctCatAx.addNewAxPos().setVal(STAxPos.B);
        ctCatAx.addNewCrossAx().setVal(123457); //id of the val axis
        ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        //val axis
        CTValAx ctValAx = ctPlotArea.addNewValAx();
        ctValAx.addNewAxId().setVal(123457); //id of the val axis
        ctScaling = ctValAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctValAx.addNewDelete().setVal(false);
        ctValAx.addNewAxPos().setVal(STAxPos.L);
        ctValAx.addNewCrossAx().setVal(123456); //id of the cat axis
        ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        //legend
        CTLegend ctLegend = ctChart.addNewLegend();
        ctLegend.addNewLegendPos().setVal(STLegendPos.B);
        ctLegend.addNewOverlay().setVal(false);

        System.out.println(ctChart);

        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(12);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
    }


    private static CellStyle mainStyle(Workbook workbook){

        CellStyle style = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        style.setFont(headerFont);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);

        return style;
    }


    private static CellStyle mainTableHeaderFont(Workbook workbook){

        CellStyle style = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }

    private static CellStyle mainTableRowStyle(Workbook workbook){

        CellStyle style = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }

    private static CellStyle setBorder(Workbook workbook){

        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        return style;
    }

    private static CellStyle setHeaderBorder(Workbook workbook){

        CellStyle style = workbook.createCellStyle();
        Font headerFont = workbook.createFont();

        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        return style;
    }

    private static CellStyle logoTextStyle(Workbook workbook){

        CellStyle style = workbook.createCellStyle();
        Font headerFont = workbook.createFont();

        style.setBorderBottom(HSSFCellStyle.BORDER_THICK);
        style.setBorderTop(HSSFCellStyle.BORDER_THICK);
        style.setBorderRight(HSSFCellStyle.BORDER_THICK);
        style.setBorderLeft(HSSFCellStyle.BORDER_THICK);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(headerFont);
        return style;
    }

    private static void setImageLogo(Workbook wb, Sheet sheet) throws IOException {
        BufferedImage image = ImageIO.read(new File("E:/Ashvini/download/ht.png"));
        ByteArrayOutputStream baps = new ByteArrayOutputStream();
        ImageIO.write(image,"png",baps);
        int pictureIdx = wb.addPicture(baps.toByteArray(), Workbook.PICTURE_TYPE_PNG);

        Drawing drawing1 = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchor1 = helper.createClientAnchor();
        anchor1.setCol1(3);
        anchor1.setRow1(5);

        Picture picture = drawing1.createPicture(anchor1, pictureIdx);
        picture.resize(2);
    }

}
