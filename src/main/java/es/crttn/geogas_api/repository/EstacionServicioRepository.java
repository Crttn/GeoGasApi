package es.crttn.geogas_api.repository;

import es.crttn.geogas_api.models.EstacionServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstacionServicioRepository extends JpaRepository<EstacionServicio, Long> {

    Optional<EstacionServicio> findByIdMinisterio(Integer idMinisterio);

    @Query(value = """
        SELECT e.*, p.precio, 
               ST_Distance(e.ubicacion, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geometry) as distancia_metros
        FROM estaciones_servicio e
        JOIN precios_combustible p ON e.id = p.estacion_id
        WHERE p.tipo_combustible = :tipoCombustible
          AND ST_DWithin(e.ubicacion, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geometry, :radioMetros)
        ORDER BY p.precio ASC, distancia_metros ASC
        LIMIT :limite
        """, nativeQuery = true)
    List<Object[]> findCheapestGasStationsNear(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radioMetros") double radioMetros,
            @Param("tipoCombustible") String tipoCombustible,
            @Param("limite") int limite
    );
}
