package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfDecorator extends ReporteBaseDecorator {
    
    private static final float MARGIN = 50;
    private static final float TITLE_SIZE = 18;
    private static final float HEADER_SIZE = 12;
    private static final float BODY_SIZE = 10;
    private static final float LINE_HEIGHT = 14;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    
    public PdfDecorator(ReporteComponent reporte) {
        super(reporte);
    }
    
    @Override
    public byte[] generar(ReporteData datos) {
        return convertirAPdf(datos);
    }
    
    private byte[] convertirAPdf(ReporteData datos) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            List<String[]> todasLasFilas = new ArrayList<>();
            if (datos.getFilas() != null) {
                for (String[] fila : datos.getFilas()) {
                    todasLasFilas.add(fila);
                }
            }
            
            String titulo = datos.getTitulo();
            String descripcion = datos.getDescripcion();
            String[] headers = datos.getHeaders();
            
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            float yPosition = PAGE_HEIGHT - MARGIN;
            
            yPosition = dibujarTitulo(contentStream, titulo, yPosition);
            yPosition -= LINE_HEIGHT;
            
            if (descripcion != null && !descripcion.isBlank()) {
                yPosition = dibujarLinea(contentStream, descripcion, new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), BODY_SIZE, yPosition);
                yPosition -= LINE_HEIGHT * 0.5f;
            }
            
            yPosition -= LINE_HEIGHT;
            
            if (headers != null && headers.length > 0) {
                yPosition = dibujarHeaders(contentStream, headers, yPosition);
                yPosition -= LINE_HEIGHT * 0.5f;
                yPosition = dibujarLineaSeparadora(contentStream, yPosition);
                yPosition -= LINE_HEIGHT;
            }
            
            if (todasLasFilas.isEmpty()) {
                yPosition = dibujarLinea(contentStream, "No hay datos para mostrar", new PDType1Font(Standard14Fonts.FontName.HELVETICA), BODY_SIZE, yPosition);
            } else {
                for (String[] fila : todasLasFilas) {
                    if (yPosition < MARGIN + LINE_HEIGHT * 2) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = PAGE_HEIGHT - MARGIN;
                    }
                    yPosition = dibujarFila(contentStream, fila, yPosition);
                }
            }
            
            contentStream.close();
            document.save(baos);
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF con PDFBox", e);
        }
    }
    
    private float dibujarTitulo(PDPageContentStream contentStream, String titulo, float yPosition) throws IOException {
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        contentStream.beginText();
        contentStream.setFont(font, TITLE_SIZE);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(titulo != null ? titulo : "Reporte");
        contentStream.endText();
        return yPosition - TITLE_SIZE;
    }
    
    private float dibujarHeaders(PDPageContentStream contentStream, String[] headers, float yPosition) throws IOException {
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        float[] columnWidths = calcularAnchosColumnas(headers);
        float xPosition = MARGIN;
        
        contentStream.setFont(font, HEADER_SIZE);
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition, yPosition);
            String headerText = headers[i] != null ? headers[i] : "";
            contentStream.showText(headerText);
            contentStream.endText();
            xPosition += columnWidths[i];
        }
        
        return yPosition - HEADER_SIZE;
    }
    
    private float dibujarFila(PDPageContentStream contentStream, String[] fila, float yPosition) throws IOException {
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        float[] columnWidths = calcularAnchosColumnas(fila);
        float xPosition = MARGIN;
        
        contentStream.setFont(font, BODY_SIZE);
        for (int i = 0; i < fila.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition, yPosition);
            String cellText = fila[i] != null ? fila[i] : "";
            contentStream.showText(cellText);
            contentStream.endText();
            xPosition += columnWidths[i];
        }
        
        return yPosition - BODY_SIZE;
    }
    
    private float dibujarLinea(PDPageContentStream contentStream, String texto, PDType1Font font, float fontSize, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(texto);
        contentStream.endText();
        return yPosition - fontSize;
    }
    
    private float dibujarLineaSeparadora(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN, yPosition);
        contentStream.lineTo(PAGE_WIDTH - MARGIN, yPosition);
        contentStream.stroke();
        return yPosition;
    }
    
    private float[] calcularAnchosColumnas(String[] datos) {
        int numColumns = datos.length > 0 ? datos.length : 1;
        float availableWidth = PAGE_WIDTH - 2 * MARGIN;
        float columnWidth = availableWidth / numColumns;
        float[] widths = new float[numColumns];
        for (int i = 0; i < numColumns; i++) {
            widths[i] = columnWidth;
        }
        return widths;
    }
}
