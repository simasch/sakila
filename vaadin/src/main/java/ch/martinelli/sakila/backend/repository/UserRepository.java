package ch.martinelli.sakila.backend.repository;

import ch.martinelli.sakila.backend.entity.ApplicationUser;
import ch.martinelli.sakila.db.tables.records.UserRolesRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static ch.martinelli.sakila.db.tables.ApplicationUser.APPLICATION_USER;
import static ch.martinelli.sakila.db.tables.UserRoles.USER_ROLES;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public ApplicationUser findByUsername(String username) {
        return dsl.selectFrom(APPLICATION_USER).where(APPLICATION_USER.USERNAME.eq(username)).fetchOneInto(ApplicationUser.class);
    }

    public Result<UserRolesRecord> getRoles(UUID id) {
        return dsl.selectFrom(USER_ROLES).where(USER_ROLES.USER_ID.eq(id)).fetch();
    }
}
