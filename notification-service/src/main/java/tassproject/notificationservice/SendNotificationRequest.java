package tassproject.notificationservice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SendNotificationRequest(@NotNull UUID recipientId, @NotBlank String message, @NotNull Type type) {
    public enum Type { INFO, WARNING, ERROR }
}