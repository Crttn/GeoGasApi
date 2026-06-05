package es.crttn.geogas_api.repository;

import es.crttn.geogas_api.models.PrecioCombustible;
import es.crttn.geogas_api.projections.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface PrecioCombustibleRepository extends JpaRepository<PrecioCombustible, Long> {
    // Busca si ya existe un precio registrado para esa gasolinera y ese tipo de combustible
    Optional<PrecioCombustible> findByEstacionServicioIdAndTipoCombustible(Long estacionId, String tipoCombustible);

    // Filtros de datos con Paginación nativa
    @Query(value = "SELECT e.rotulo, e.municipio, p.tipo_combustible, p.precio " +
            "FROM estaciones_servicio e " +
            "JOIN precios_combustible p ON e.id = p.estacion_id " +
            "WHERE e.provincia_id = :provinciaId " +
            "  AND p.tipo_combustible = :tipoCombustible " +
            "  AND p.precio <= :precioMaximo " +
            "ORDER BY p.precio ASC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<BusquedaFiltroProjection> buscarConFiltros(
            @Param("provinciaId") String provinciaId,
            @Param("tipoCombustible") String tipoCombustible,
            @Param("precioMaximo") Double precioMaximo,
            @Param("limit") int limit,
            @Param("offset") int offset);

    // Calculadora de Tanque
    @Query(value = "SELECT p.precio AS precio_por_litro, " +
            "(p.precio * :litros) AS coste_total_llenado " +
            "FROM precios_combustible p " +
            "WHERE p.estacion_id = :estacionId " +
            "  AND p.tipo_combustible = :tipoCombustible", nativeQuery = true)
    CalculoTanqueProjection calcularLlenadoTanque(
            @Param("estacionId") Long estacionId,
            @Param("tipoCombustible") String tipoCombustible,
            @Param("litros") Double litros);

    // Top 10 Baratas Cercanas
    @Query(value = "SELECT e.rotulo, e.direccion, p.precio, " +
            "ST_Distance(e.ubicacion::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography) AS distancia_metros " +
            "FROM estaciones_servicio e " +
            "JOIN precios_combustible p ON e.id = p.estacion_id " +
            "WHERE p.tipo_combustible = :tipoCombustible " +
            "  AND ST_DWithin(e.ubicacion::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radioMetros) " +
            "ORDER BY p.precio ASC LIMIT 10", nativeQuery = true)
    List<TopBarataProjection> findTopBaratasCercanas(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radioMetros") double radioMetros,
            @Param("tipoCombustible") String tipoCombustible);

    // Estadísticas de Mercado (Media, Min, Max)
    @Query(value = "SELECT ROUND(AVG(p.precio), 3) AS media_provincia, " +
            "MIN(p.precio) AS precio_mas_barato, " +
            "MAX(p.precio) AS precio_mas_caro " +
            "FROM estaciones_servicio e " +
            "JOIN precios_combustible p ON e.id = p.estacion_id " +
            "WHERE e.provincia_id = :provinciaId " +
            "  AND p.tipo_combustible = :tipoCombustible", nativeQuery = true)
    EstadisticaMercadoProjection obtenerEstadisticasProvincia(
            @Param("provinciaId") String provinciaId,
            @Param("tipoCombustible") String tipoCombustible);

    // Evolución Histórica (Últimos 7 días)
    @Query(value = "SELECT DATE(fecha_registro) AS dia, precio " +
            "FROM historico_precios " +
            "WHERE estacion_id = :estacionId " +
            "  AND tipo_combustible = :tipoCombustible " +
            "  AND fecha_registro >= CURRENT_DATE - INTERVAL '7 days' " +
            "ORDER BY fecha_registro ASC", nativeQuery = true)
    List<EvolucionHistoricaProjection> obtenerEvolucionHistorica(
            @Param("estacionId") Long estacionId,
            @Param("tipoCombustible") String tipoCombustible);
}
