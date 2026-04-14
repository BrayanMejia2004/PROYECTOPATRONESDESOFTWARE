package com.gobierno.servicio_reportes.application.usecases;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.in.GenerarReportePort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteDataProviderPort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteRepositoryPort;
import com.gobierno.servicio_reportes.domain.services.CsvDecorator;
import com.gobierno.servicio_reportes.domain.services.PdfDecorator;
import com.gobierno.servicio_reportes.domain.services.ReporteConcreteComponent;
import com.gobierno.servicio_reportes.domain.services.ReporteComponent;
import com.gobierno.servicio_reportes.domain.services.ZipDecorator;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;

@Service
public class GenerarReporteUseCase implements GenerarReportePort {
    
    private final ReporteRepositoryPort reporteRepository;
    private final ReporteDataProviderPort dataProvider;
    
    public GenerarReporteUseCase(
            ReporteRepositoryPort reporteRepository,
            ReporteDataProviderPort dataProvider) {
        this.reporteRepository = reporteRepository;
        this.dataProvider = dataProvider;
    }
    
    @Override
    @Transactional
    public byte[] generarReporte(String tipo, String formato, String usuarioSolicitante) {
        ReporteData datos = dataProvider.obtenerDatos(tipo);
        byte[] contenido = generarContenido(datos, formato, tipo);
        guardarReporte(tipo, datos.getTitulo(), datos.getDescripcion(), contenido, formato, usuarioSolicitante);
        return contenido;
    }
    
    @Override
    @Transactional
    public Reporte guardarReporte(String tipo, String titulo, String descripcion,
                                   byte[] contenido, String formato, String usuarioSolicitante) {
        String contenidoBase64 = Base64.getEncoder().encodeToString(contenido);
        Reporte reporte = new Reporte(
            tipo.toUpperCase(), titulo, descripcion, contenidoBase64,
            formato.toUpperCase(), usuarioSolicitante
        );
        return reporteRepository.guardar(reporte);
    }
    
    private byte[] generarContenido(ReporteData datos, String formato, String tipo) {
        ReporteComponent reporte = new ReporteConcreteComponent();
        reporte = aplicarDecorators(reporte, formato, tipo);
        return reporte.generar(datos);
    }
    
    private ReporteComponent aplicarDecorators(ReporteComponent reporte, String formato, String tipo) {
        String formatoUpper = formato.toUpperCase();
        boolean conZip = formatoUpper.contains("ZIP");
        
        if (formatoUpper.contains("CSV")) {
            reporte = new CsvDecorator(reporte);
        } else if (formatoUpper.contains("PDF")) {
            reporte = new PdfDecorator(reporte);
        }
        if (conZip) {
            reporte = new ZipDecorator(reporte, tipo);
        }
        return reporte;
    }
}
