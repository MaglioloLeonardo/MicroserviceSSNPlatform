package tassproject.notificationservice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tassproject.notificationservice.SendNotificationRequest;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/notifications")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void notify(@Valid @RequestBody SendNotificationRequest request) {
        rabbitTemplate.convertAndSend("notification.events", request.type().name(), request);
    }
}
