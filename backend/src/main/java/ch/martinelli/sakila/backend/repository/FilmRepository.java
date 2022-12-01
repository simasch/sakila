package ch.martinelli.sakila.backend.repository;

import ch.martinelli.sakila.db.tables.records.FilmListRecord;
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

    public List<FilmListRecord> findAll(int offset, int limit) {
        return dsl.selectFrom(FILM_LIST).offset(offset).limit(limit).fetch();
    }
}
