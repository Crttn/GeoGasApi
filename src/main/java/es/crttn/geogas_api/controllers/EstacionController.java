package es.crttn.geogas_api.controllers;

import es.crttn.geogas_api.projection.*;
import es.crttn.geogas_api.repository.EstacionServicioRepository;
import es.crttn.geogas_api.repository.PrecioCombustibleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gasolineras")
public class EstacionController {

    private final EstacionServicioRepository estacionRepository;
    private final PrecioCombustibleRepository precioRepository;

    public EstacionController(EstacionServicioRepository estacionRepository, PrecioCombustibleRepository precioRepository) {
        this.estacionRepository = estacionRepository;
        this.precioRepository = precioRepository;
    }

    @GetMapping("/area")
    public ResponseEntity<List<EstacionCercanaProjection>> obtenerPorArea(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radio) {
        return ResponseEntity.ok(estacionRepository.findEstacionesEnRadio(lat, lon, radio));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<BusquedaFiltroProjection>> buscarGasolineras(
            @RequestParam String provincia,
            @RequestParam String combustible,
            @RequestParam Double precioMax,
            @RequestParam(defaultValue = "0") int page) {

        int limit = 20;
        int offset = page * limit;
        return ResponseEntity.ok(precioRepository.buscarConFiltros(provincia, combustible, precioMax, limit, offset));
    }

    @GetMapping("/{id}/calcular")
    public ResponseEntity<CalculoTanqueProjection> calcularTanque(
            @PathVariable Long id,
            @RequestParam String combustible,
            @RequestParam Double litros) {
        return ResponseEntity.ok(precioRepository.calcularLlenadoTanque(id, combustible, litros));
    }

    @GetMapping("/cercanas/top")
    public ResponseEntity<List<TopBarataProjection>> obtenerTopBaratasCercanas(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radio,
            @RequestParam String combustible) {
        return ResponseEntity.ok(precioRepository.findTopBaratasCercanas(lat, lon, radio, combustible));
    }
}