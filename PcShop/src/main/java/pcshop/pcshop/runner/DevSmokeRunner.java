package pcshop.pcshop.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pcshop.pcshop.repository.UserRepository;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevSmokeRunner implements CommandLineRunner {
    private final UserRepository users;

    @Override public void run(String... args) {
        users.findByEmail("admin@example.com")
                .ifPresent(u -> System.out.println("[SMOKE] Found admin id=" + u.getId() + " status=" + u.getStatus()));
    }
}
