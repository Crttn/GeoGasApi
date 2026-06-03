package es.crttn.geogas_api.repository;

import es.crttn.geogas_api.models.PrecioCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PrecioCombustibleRepository extends JpaRepository<PrecioCombustible, Long> {
    // Busca si ya existe un precio registrado para esa gasolinera y ese tipo de combustible
    Optional<PrecioCombustible> findByEstacionServicioIdAndTipoCombustible(Long estacionId, String tipoCombustible);
}
