package ch.martinelli.sakila.backend.repository;

import ch.martinelli.sakila.backend.entity.FilmListEntry;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.martinelli.sakila.db.tables.FilmList.FILM_LIST;

@Repository
public class FilmRepository {

    private final DSLContext dsl;

    public FilmRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<FilmListEntry> findAll(int offset, int limit) {
        return dsl
                .select(FILM_LIST.TITLE, FILM_LIST.ACTORS, FILM_LIST.DESCRIPTION, FILM_LIST.CATEGORY)
                .from(FILM_LIST).offset(offset).limit(limit)
                .fetchInto(FilmListEntry.class);
    }
}
