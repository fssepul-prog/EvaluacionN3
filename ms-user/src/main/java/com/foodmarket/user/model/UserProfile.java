package com.foodmarket.user.model;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
/** Entidad JPA: perfil extendido del usuario */
@Entity @Table(name="user_profiles") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="user_id", unique=true, nullable=false) private Long userId;
    @Column(name="full_name", nullable=false, length=100) private String fullName;
    @Column(length=20) private String phone;
    @Column(length=150) private String email;
    @Column(length=50) private String role;
    @Column(name="created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() { this.createdAt = LocalDateTime.now(); }
}
