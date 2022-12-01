package ch.martinelli.sakila.endpoints;

import ch.martinelli.sakila.backend.entity.FilmListEntry;
import ch.martinelli.sakila.backend.repository.FilmRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import org.springframework.data.domain.Pageable;

import java.util.List;

@AnonymousAllowed
@Endpoint
public class FilmEndpoint {

    private final FilmRepository filmRepository;

    public FilmEndpoint(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Nonnull
    public List<@Nonnull FilmListEntry> findAll(Pageable pageable) {
        return filmRepository.findAll((int) pageable.getOffset(), pageable.getPageSize());
    }
}
