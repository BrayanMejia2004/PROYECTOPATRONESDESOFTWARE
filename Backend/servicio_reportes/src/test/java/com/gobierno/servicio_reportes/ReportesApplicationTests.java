package com.gobierno.servicio_reportes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteDataProviderPort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteRepositoryPort;

@SpringBootTest
class ReportesApplicationTests {

    @SuppressWarnings("removal")
    @MockBean
    private ReporteDataProviderPort dataProviderPort;

    @SuppressWarnings("removal")
    @MockBean
    private ReporteRepositoryPort repositoryPort;

    @Test
    void contextLoads() {
    }
}
