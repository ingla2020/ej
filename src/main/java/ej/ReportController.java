package ej;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping(value = "/excel-from-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> generateExcelReportFromJson(@RequestBody String jsonData) {
        try {
            byte[] reportBytes = reportService.generateExcelReportFromJson(jsonData);
            ByteArrayResource resource = new ByteArrayResource(reportBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_productos.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // Específico para XLSX
                    .contentLength(reportBytes.length)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // Devuelve un cuerpo vacío o un mensaje de error JSON
        }
    }

    @PostMapping(value = "/pdf-from-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> generatePdfReportFromJson(@RequestBody String jsonData) {
        try {
            byte[] reportBytes = reportService.generatePdfReportFromJson(jsonData);
            ByteArrayResource resource = new ByteArrayResource(reportBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_productos.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(reportBytes.length)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(value = "/csv-from-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> generateCsvReportFromJson(@RequestBody String jsonData) {
        try {
            byte[] reportBytes = reportService.generateCsvReportFromJson(jsonData);
            ByteArrayResource resource = new ByteArrayResource(reportBytes);

            // Para CSV, el MediaType "text/csv" es común.
            MediaType csvMediaType = new MediaType("text", "csv");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_productos.csv")
                    .contentType(csvMediaType)
                    .contentLength(reportBytes.length)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
