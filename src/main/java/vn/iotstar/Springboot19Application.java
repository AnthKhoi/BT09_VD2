package vn.iotstar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@SpringBootApplication
public class Springboot19Application {

    @Value("${server.port:8081}")
    private String serverPort;

    public static void main(String[] args) {
        SpringApplication.run(Springboot19Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserOnStartup() {
        String url = "http://localhost:" + serverPort + "/login";
        System.out.println(">>> [AUTO-LAUNCH] Đang tự động mở trình duyệt tại: " + url);
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", url).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception e) {
            System.err.println("Không thể mở trình duyệt: " + e.getMessage());
        }
    }

    @Bean
    CommandLineRunner init(
        RoleRepository roleRepository,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Role userRole = roleRepository
                .findByName("ROLE_USER")
                .orElseGet(() ->
                    roleRepository.save(
                        Role.builder()
                            .name("ROLE_USER")
                            .build()
                    )
                );

            Role adminRole = roleRepository
                .findByName("ROLE_ADMIN")
                .orElseGet(() ->
                    roleRepository.save(
                        Role.builder()
                            .name("ROLE_ADMIN")
                            .build()
                    )
                );

            // User 1: user01
            if (userRepository.findByUsername("user01").isEmpty()) {
                User user = User.builder()
                    .username("user01")
                    .email("user01@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Nguyễn Hữu Trung")
                    .images("/images/user.png")
                    .role(userRole)
                    .enabled(true)
                    .build();
                userRepository.save(user);
            }

            // User 2: anhkhoi (Admin)
            if (userRepository.findByUsername("anhkhoi").isEmpty()) {
                User admin = User.builder()
                    .username("anhkhoi")
                    .email("lehuynhanhkhoi2006@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Lê Huỳnh Anh Khôi")
                    .images("/images/user.png")
                    .role(adminRole)
                    .enabled(true)
                    .build();
                userRepository.save(admin);
            }
        };
    }
}
