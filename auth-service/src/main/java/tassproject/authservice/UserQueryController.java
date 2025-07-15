package tassproject.authservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tassproject.authservice.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserRepository users;

    /* 8) ruoli associati ad un utente */
    @GetMapping("/{username}/roles")
    public List<Role> roles(@PathVariable String username) {
        return users.findByUsername(username.toLowerCase())
                .map(u -> List.of(u.getRole()))
                .orElse(List.of());
    }
}
