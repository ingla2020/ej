package ej;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String jsonContent = """
            [{"nombre":"Producto Alfa","cantidad":10,"precio":15.99,"total":159.90},{"nombre":"Producto Beta","cantidad":5,"precio":100.00,"total":500.00}]
            """;

    @Test
    public void testGeneratePdfReport() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/reports/pdf-from-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andReturn();

        byte[] pdfBytes = result.getResponse().getContentAsByteArray();
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            assertTrue(text.contains("Producto Alfa"));
            assertTrue(text.contains("Producto Beta"));
        }
    }

    @Test
    public void testGenerateExcelReport() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/reports/excel-from-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andReturn();

        byte[] excelBytes = result.getResponse().getContentAsByteArray();
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            // The data starts at row 2, after the title and headers.
            String productName = sheet.getRow(2).getCell(0).getStringCellValue();
            assertEquals("Producto Alfa", productName);
        }
    }

    @Test
    public void testGenerateCsvReport() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/reports/csv-from-json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andReturn();

        String csvContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertTrue(csvContent.contains("Producto Alfa,10,15.99 $,159.90 $"));
        assertTrue(csvContent.contains("Producto Beta,5,100.00 $,500.00 $"));
    }
}
