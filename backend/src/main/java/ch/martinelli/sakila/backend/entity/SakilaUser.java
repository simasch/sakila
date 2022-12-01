package ch.martinelli.sakila.backend.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SakilaUser {

    private final UUID id;
    private final String username;
    private final String name;
    private final String hashedPassword;
    private final byte[] profilePicture;

    private Set<Role> roles = new HashSet<>();

    public SakilaUser(UUID id, String username, String name, String hashedPassword, byte[] profilePicture) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.profilePicture = profilePicture;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
