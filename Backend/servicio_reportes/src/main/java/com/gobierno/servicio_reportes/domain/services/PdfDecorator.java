package com.gobierno.servicio_reportes.domain.services;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PdfDecorator extends ReporteBaseDecorator {

    private static final float MARGIN = 40;
    private static final float SECTION_SIZE = 11;
    private static final float BODY_SIZE = 8;
    private static final float LINE_HEIGHT = 11;

    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    private PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private PDType1Font fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    public PdfDecorator(ReporteComponent reporte) {
        super(reporte);
    }

    @Override
    public byte[] generar(ReporteData datos) {
        byte[] contenidoTexto = reporte.generar(datos);
        return renderizarTextoAPdf(new String(contenidoTexto, StandardCharsets.UTF_8));
    }

    private byte[] renderizarTextoAPdf(String texto) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            List<String> lineas = Arrays.asList(texto.split("\n"));

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yPosition = PAGE_HEIGHT - MARGIN;
            int numeroPagina = 1;

            boolean esEncabezado = false;

            for (String linea : lineas) {

                // Salto de página seguro
                if (yPosition < MARGIN + LINE_HEIGHT) {
                    agregarFooter(contentStream, numeroPagina++);
                    contentStream.close();

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);

                    yPosition = PAGE_HEIGHT - MARGIN;
                }

                String lineaTrim = linea.trim();

                if (linea.contains("===")) {
                    yPosition = dibujarSeparador(contentStream, yPosition);
                }
                else if (linea.startsWith("|")) {
                    yPosition = dibujarTabla(contentStream, linea, yPosition);
                }
                else if (lineaTrim.matches("^(RESUMEN|DETALLE|ENCABEZADO|PIE|FIN DEL REPORTE|REPORTE).*")) {
                    yPosition = dibujarTextoMultilinea(contentStream, lineaTrim, fontBold, SECTION_SIZE, yPosition);
                    if (lineaTrim.contains("ENCABEZADO")) esEncabezado = true;
                }
                else if (lineaTrim.matches("^\\s*-\\s+.*")) {
                    yPosition = dibujarTextoMultilinea(contentStream, lineaTrim, fontNormal, BODY_SIZE, yPosition);
                }
                else if (lineaTrim.matches("^(Generado|Total de).*") || esEncabezado) {
                    yPosition = dibujarTextoMultilinea(contentStream, lineaTrim, fontItalic, BODY_SIZE, yPosition);
                    esEncabezado = false;
                }
                else if (!lineaTrim.isBlank()) {
                    yPosition = dibujarTextoMultilinea(contentStream, lineaTrim, fontNormal, BODY_SIZE, yPosition);
                }
                else {
                    yPosition -= LINE_HEIGHT * 0.5f;
                }
            }

            agregarFooter(contentStream, numeroPagina);
            contentStream.close();

            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    // ===========================
    // 🔹 TEXTO MULTILINEA
    // ===========================
    private float dibujarTextoMultilinea(PDPageContentStream contentStream,
                                         String texto,
                                         PDType1Font font,
                                         float fontSize,
                                         float yPosition) throws IOException {

        float maxWidth = PAGE_WIDTH - 2 * MARGIN;
        List<String> lineas = dividirTexto(texto, font, fontSize, maxWidth);

        for (String linea : lineas) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(linea);
            contentStream.endText();

            yPosition -= LINE_HEIGHT;
        }

        return yPosition;
    }

    private List<String> dividirTexto(String texto, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> resultado = new ArrayList<>();
        String[] palabras = texto.split(" ");

        StringBuilder lineaActual = new StringBuilder();

        for (String palabra : palabras) {
            String prueba = lineaActual.length() == 0 ? palabra : lineaActual + " " + palabra;
            float ancho = font.getStringWidth(prueba) / 1000 * fontSize;

            if (ancho <= maxWidth) {
                lineaActual = new StringBuilder(prueba);
            } else {
                resultado.add(lineaActual.toString());
                lineaActual = new StringBuilder(palabra);
            }
        }

        if (!lineaActual.isEmpty()) {
            resultado.add(lineaActual.toString());
        }

        return resultado;
    }

    // ===========================
    // 🔹 TABLAS
    // ===========================
    private float dibujarTabla(PDPageContentStream contentStream, String linea, float yPosition) throws IOException {

        String[] columnas = linea.split("\\|");
        float anchoTotal = PAGE_WIDTH - 2 * MARGIN;
        float anchoColumna = anchoTotal / (columnas.length - 1);

        float x = MARGIN;

        for (int i = 1; i < columnas.length; i++) {

            List<String> lineasCelda = dividirTexto(columnas[i].trim(), fontNormal, BODY_SIZE, anchoColumna - 5);
            float yTemp = yPosition;

            for (String l : lineasCelda) {
                contentStream.beginText();
                contentStream.setFont(fontNormal, BODY_SIZE);
                contentStream.newLineAtOffset(x, yTemp);
                contentStream.showText(l);
                contentStream.endText();

                yTemp -= LINE_HEIGHT;
            }

            x += anchoColumna;
        }

        return yPosition - LINE_HEIGHT;
    }

    // ===========================
    // 🔹 SEPARADOR
    // ===========================
    private float dibujarSeparador(PDPageContentStream contentStream, float yPosition) throws IOException {

        String linea = "------------------------------------------------------------";

        contentStream.beginText();
        contentStream.setFont(fontNormal, BODY_SIZE);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(linea);
        contentStream.endText();

        return yPosition - LINE_HEIGHT;
    }

    // ===========================
    // 🔹 FOOTER
    // ===========================
    private void agregarFooter(PDPageContentStream contentStream, int pagina) throws IOException {
        contentStream.beginText();
        contentStream.setFont(fontItalic, 7);
        contentStream.newLineAtOffset(MARGIN, MARGIN - 10);
        contentStream.showText("Sistema de Identidad Digital Gubernamental - Página " + pagina);
        contentStream.endText();
    }
}