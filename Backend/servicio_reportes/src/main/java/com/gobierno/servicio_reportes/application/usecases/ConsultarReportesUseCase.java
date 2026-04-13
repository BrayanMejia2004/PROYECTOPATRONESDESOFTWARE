package com.gobierno.servicio_reportes.application.usecases;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.in.ConsultarReportesPort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteRepositoryPort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultarReportesUseCase implements ConsultarReportesPort {
    
    private final ReporteRepositoryPort reporteRepository;
    
    public ConsultarReportesUseCase(ReporteRepositoryPort reporteRepository) {
        this.reporteRepository = reporteRepository;
    }
    
    @Override
    public List<Reporte> obtenerHistorial() {
        return reporteRepository.findAll();
    }
    
    @Override
    public List<Reporte> obtenerHistorialPorTipo(String tipo) {
        return reporteRepository.findByTipoOrderByFechaGeneracionDesc(tipo.toUpperCase());
    }
    
    @Override
    public Optional<Reporte> obtenerReportePorId(Long id) {
        return reporteRepository.findById(id);
    }
}
