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

    /* ---- ruoli associati all’utente ---- */
    @GetMapping("/{username}/roles")
    public List<String> roles(@PathVariable String username) {
        return users.findByUsername(username.toLowerCase())
                .map(u -> u.getRoles().stream()
                        .map(RoleEntity::getName)
                        .toList())
                .orElse(List.of());
    }
}
