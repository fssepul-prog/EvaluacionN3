package com.foodmarket.search.controller;
import com.foodmarket.search.model.RestaurantIndex;
import com.foodmarket.search.repository.RestaurantIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CAPA CONTROLLER: delega al repositorio
 * @Slf4j para log de busquedas y resultados
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final RestaurantIndexRepository indexRepo;

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantIndex>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String category) {

        log.info("[SEARCH] Busqueda iniciada: name={} zone={} category={}", name, zone, category);

        List<RestaurantIndex> result;
        if (name != null) {
            result = indexRepo.searchByName(name);
            log.info("[SEARCH] Por nombre '{}': {} resultados", name, result.size());
        } else if (zone != null) {
            result = indexRepo.findByZoneOrderByRating(zone);
            log.info("[SEARCH] Por zona '{}': {} resultados", zone, result.size());
        } else if (category != null) {
            result = indexRepo.findByCategoryAndStatus(category, "OPEN");
            log.info("[SEARCH] Por categoria '{}': {} resultados", category, result.size());
        } else {
            result = indexRepo.findAll();
            log.info("[SEARCH] Busqueda general: {} restaurantes", result.size());
        }
        return ResponseEntity.ok(result);
    }
}
