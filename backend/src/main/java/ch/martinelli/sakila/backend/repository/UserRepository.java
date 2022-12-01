package ch.martinelli.sakila.backend.repository;

import ch.martinelli.sakila.backend.entity.Role;
import ch.martinelli.sakila.backend.entity.SakilaUser;
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

    public SakilaUser findByUsername(String username) {
        SakilaUser sakilaUser = dsl
                .select(APPLICATION_USER.ID, APPLICATION_USER.USERNAME, APPLICATION_USER.NAME, APPLICATION_USER.HASHED_PASSWORD, APPLICATION_USER.PROFILE_PICTURE)
                .from(APPLICATION_USER)
                .where(APPLICATION_USER.USERNAME.eq(username))
                .fetchOneInto(SakilaUser.class);
        if (sakilaUser != null) {
            getRoles(sakilaUser.getId())
                    .forEach(role -> sakilaUser.getRoles().add(Role.valueOf(role.getRoles())));
        }
        return sakilaUser;
    }

    public Result<UserRolesRecord> getRoles(UUID id) {
        return dsl.selectFrom(USER_ROLES).where(USER_ROLES.USER_ID.eq(id)).fetch();
    }
}
