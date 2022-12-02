package ch.martinelli.sakila.endpoints;

import ch.martinelli.sakila.backend.entity.SakilaUser;
import ch.martinelli.sakila.security.UserContext;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@Endpoint
@PermitAll
public class UserEndpoint {

    private final UserContext userContext;

    public UserEndpoint(UserContext userContext) {
        this.userContext = userContext;
    }

    public Optional<SakilaUser> getUser() {
        return userContext.get();
    }
}
