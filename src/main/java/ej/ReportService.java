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


@Service
public class ReportService {

    public byte[] generateReportFromJson(String jsonData) throws Exception {
        // Cargar y compilar el archivo JRXML
        File templateFile = ResourceUtils.getFile("classpath:templates/report_template.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(templateFile.getAbsolutePath());

        // Crear el JsonDataSource a partir del String JSON
        // El 'selectExpression' (segundo parámetro) indica la ruta a la lista de objetos en el JSON.
        // Si el JSON es directamente un array, puedes usar null o "" o ".".
        // Si es un objeto que contiene un array, por ejemplo {"data": [...]}, usarías "data".
        // Para este ejemplo, asumimos que el JSON es un array de objetos directamente.
        InputStream jsonStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
        JsonDataSource jsonDataSource = new JsonDataSource(jsonStream);

        // Parámetros para el informe (si los hubiera)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Lista de Productos (JSON)");
        // Necesario para que JsonDataSource funcione correctamente con sub-reports o listas anidadas.
        parameters.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, "yyyy-MM-dd");
        parameters.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#,##0.##");
        parameters.put(JsonQueryExecuterFactory.JSON_LOCALE, java.util.Locale.US); // O tu Locale
        parameters.put(JRParameter.REPORT_LOCALE, java.util.Locale.US);


        // Llenar el informe
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);

        // Exportar a Excel (XLSX)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true); // Detecta automáticamente el tipo de celda (número, texto, etc.)
        configuration.setCollapseRowSpan(false);
        configuration.setWhitePageBackground(false); // Evita fondos blancos innecesarios
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setRemoveEmptySpaceBetweenColumns(true);

        exporter.setConfiguration(configuration);

        exporter.exportReport();

        return byteArrayOutputStream.toByteArray();
    }
}
