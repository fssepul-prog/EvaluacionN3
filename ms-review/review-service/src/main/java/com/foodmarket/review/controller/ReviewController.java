package com.foodmarket.review.controller;
import com.foodmarket.review.dto.*; import com.foodmarket.review.model.TargetType; import com.foodmarket.review.service.ReviewService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/reviews") @RequiredArgsConstructor @Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping public ResponseEntity<ReviewResponseDTO> create(@Valid @RequestBody ReviewDTO dto) { log.info("Creando resena para orden {}", dto.getOrderId()); return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(dto)); }
    @GetMapping("/restaurant/{id}") public ResponseEntity<List<ReviewResponseDTO>> getByRestaurant(@PathVariable Long id) { return ResponseEntity.ok(reviewService.getByRestaurant(id)); }
    @GetMapping("/agent/{id}") public ResponseEntity<List<ReviewResponseDTO>> getByAgent(@PathVariable Long id) { return ResponseEntity.ok(reviewService.getByAgent(id)); }
    @GetMapping("/restaurant/{id}/average") public ResponseEntity<Double> getAvgRestaurant(@PathVariable Long id) { return ResponseEntity.ok(reviewService.getAvgRating(id,TargetType.RESTAURANT)); }
}