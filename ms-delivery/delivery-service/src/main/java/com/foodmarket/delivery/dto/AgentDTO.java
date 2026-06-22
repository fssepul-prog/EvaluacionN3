package com.foodmarket.delivery.dto;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AgentDTO { @NotNull private Long userId; @NotBlank private String zone; @NotBlank private String vehicleType; @NotBlank private String name; @NotBlank @Email private String email; }