package es.crttn.geogas_api.projections;

import java.time.LocalDate;
public interface EvolucionHistoricaProjection {
    LocalDate getDia(); // Importante manejar la fecha limpia sin horas
    Double getPrecio();
}
