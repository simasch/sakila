package ch.martinelli.sakila.backend.repository;

import ch.martinelli.sakila.backend.entity.ApplicationUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername() {
        ApplicationUserDTO admin = userRepository.findByUsername("admin");
        assertThat(admin).isNotNull();
    }

    @Test
    void getRoles() {
        ApplicationUserDTO admin = userRepository.findByUsername("admin");
        assertThat(admin).isNotNull();

        userRepository.getRoles(admin.id());
    }
}
