package com.air.airquality.services;
import com.air.airquality.model.AirQualityData;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;
@Service
public class PdfService {
    public ByteArrayInputStream generateAQIReport(List<AirQualityData> dataList, String city) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph title = new Paragraph("Air Quality Report - " + city, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 3, 3});
            Stream.of("Timestamp", "AQI Value", "Unit").forEach(header -> {
                PdfPCell cell = new PdfPCell(new Phrase(header, tableFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            });
            for (AirQualityData data : dataList) {
                table.addCell(data.getTimestamp().toString());
                table.addCell(String.valueOf(data.getValue()));
                table.addCell(data.getUnit());
            }
            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}