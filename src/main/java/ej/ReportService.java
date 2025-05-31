package ej;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import net.sf.jasperreports.export.*;

@Service
public class ReportService {

    // Método auxiliar para cargar y llenar el informe, reutilizado por todos los exportadores
    private JasperPrint fillReportFromJson(String jsonData, String reportTitle) throws Exception {
        File templateFile = ResourceUtils.getFile("classpath:templates/report_template.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(templateFile.getAbsolutePath());

        InputStream jsonStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
        // El segundo parámetro de JsonDataSource es la expresión de selección.
        // Si el JSON es un array en la raíz, se puede usar null, "" o "."
        JsonDataSource jsonDataSource = new JsonDataSource(jsonStream);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", reportTitle);
        parameters.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, "yyyy-MM-dd");
        parameters.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#,##0.##");
        parameters.put(JsonQueryExecuterFactory.JSON_LOCALE, java.util.Locale.US);
        parameters.put(JRParameter.REPORT_LOCALE, java.util.Locale.US);

        return JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);
    }

    public byte[] generateExcelReportFromJson(String jsonData) throws Exception {
        JasperPrint jasperPrint = fillReportFromJson(jsonData, "Lista de Productos (JSON) - Excel");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        configuration.setWhitePageBackground(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setRemoveEmptySpaceBetweenColumns(true);

        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return byteArrayOutputStream.toByteArray();
    }

    public byte[] generatePdfReportFromJson(String jsonData) throws Exception {
        JasperPrint jasperPrint = fillReportFromJson(jsonData, "Lista de Productos (JSON) - PDF");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));

        // Configuración opcional para PDF
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setMetadataAuthor("Mi Aplicacion");
        configuration.setMetadataTitle("Reporte Productos PDF");
        configuration.setCreatingBatchModeBookmarks(true); // Si usas bookmarks en tu JRXML
        // configuration.setPermissions(PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY); // Ejemplo de permisos
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] generateCsvReportFromJson(String jsonData) throws Exception {
        JasperPrint jasperPrint = fillReportFromJson(jsonData, "Lista de Productos (JSON) - CSV");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRCsvExporter exporter = new JRCsvExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        // Para CSV, es común usar un Writer, pero también funciona con OutputStream
        exporter.setExporterOutput(new SimpleWriterExporterOutput(byteArrayOutputStream, StandardCharsets.UTF_8.name()));

        // Configuración opcional para CSV
        SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
        configuration.setFieldDelimiter(","); // Delimitador (coma por defecto)
        configuration.setRecordDelimiter("\n"); // Delimitador de registro (salto de línea)
        // configuration.setWriteBOM(Boolean.TRUE); // Para asegurar compatibilidad con Excel en algunos casos con UTF-8
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        return byteArrayOutputStream.toByteArray();
    }
}
