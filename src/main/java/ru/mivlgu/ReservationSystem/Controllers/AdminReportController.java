package ru.mivlgu.ReservationSystem.Controllers;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mivlgu.ReservationSystem.Services.ReportService;
import ru.mivlgu.ReservationSystem.dto.ReportData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    @Autowired
    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/list")
    public String reportsPage() {
        return "admin/reports";
    }

    @GetMapping("/generate")
    @ResponseBody
    public ReportData generateReport(
            @RequestParam("type") String reportType,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return reportService.generateReport(reportType, startDate, endDate);
    }
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadReport(
            @RequestParam("type") String reportType,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        ReportData reportData = reportService.generateReport(reportType, startDate, endDate);
        byte[] excelBytes = generateExcelReport(reportData);

        String fileName = generateFileName(reportType, startDate, endDate);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(excelBytes));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    private byte[] generateExcelReport(ReportData reportData) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Отчет");

            // Создаем заголовки
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < reportData.getHeaders().size(); i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(reportData.getHeaders().get(i));
            }

            // Заполняем данные
            int rowNum = 1;
            for (List<String> rowData : reportData.getRows()) {
                XSSFRow row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.size(); i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue(rowData.get(i));
                }
            }

            // Автонастройка ширины столбцов
            for (int i = 0; i < reportData.getHeaders().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String generateFileName(String reportType, LocalDate startDate, LocalDate endDate) {
        String reportName;
        switch (reportType) {
            case "popularity": reportName = "Популярность_мероприятий"; break;
            case "utilization": reportName = "Загруженность_аудиторий"; break;
            case "equipment": reportName = "Использование_оборудования"; break;
            case "registrations": reportName = "Регистрации_пользователей"; break;
            default: reportName = "Отчет";
        }

        String dateRange = "";
        if (startDate != null && endDate != null) {
            dateRange = "_" + startDate + "_по_" + endDate;
        } else if (startDate != null) {
            dateRange = "_с_" + startDate;
        } else if (endDate != null) {
            dateRange = "_по_" + endDate;
        }

        return reportName + dateRange + ".xlsx";
    }
}
