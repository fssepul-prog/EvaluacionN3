package com.foodmarket.notification.model;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="notifications") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="recipient_id") private Long recipientId;
    @Column(nullable=false,length=50) private String type;
    @Column(nullable=false,length=500) private String message;
    @Column(name="is_read") private boolean read=false;
    @Column(name="created_at") private LocalDateTime createdAt;
    @PrePersist public void prePersist() { this.createdAt=LocalDateTime.now(); }
}