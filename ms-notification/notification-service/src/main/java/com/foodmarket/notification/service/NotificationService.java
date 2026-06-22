package com.foodmarket.notification.service;

import com.foodmarket.notification.model.Notification;
import com.foodmarket.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notifRepo;

    public Notification create(Long recipientId, String type, String message) {
        Notification notif = Notification.builder()
                .recipientId(recipientId)
                .type(type)
                .message(message)
                .read(false)
                .build();
        Notification saved = notifRepo.save(notif);
        log.info("[NOTIFICATION] Notificacion creada para usuario {}: {}", recipientId, type);
        return saved;
    }

    public List<Notification> getByUser(Long userId) {
        log.info("[NOTIFICATION] Consultando notificaciones para usuario {}", userId);
        return notifRepo.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnread(Long userId) {
        log.info("[NOTIFICATION] Consultando no leidas para usuario {}", userId);
        return notifRepo.findByRecipientIdAndReadFalse(userId);
    }

    public void markAsRead(Long id) {
        notifRepo.findById(id).ifPresent(n -> {
            n.setRead(true);
            notifRepo.save(n);
            log.info("[NOTIFICATION] Notificacion {} marcada como leida", id);
        });
    }
}
