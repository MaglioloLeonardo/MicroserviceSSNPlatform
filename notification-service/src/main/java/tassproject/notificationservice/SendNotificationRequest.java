package tassproject.notificationservice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;          // 👈 nuovo import
import java.util.UUID;

public record SendNotificationRequest(
        @NotNull  UUID  recipientId,
        @NotBlank String message,
        @NotNull  Type   type)
        implements Serializable {     // 👈 implementa Serializable

    public enum Type { INFO, WARNING, ERROR }
}
