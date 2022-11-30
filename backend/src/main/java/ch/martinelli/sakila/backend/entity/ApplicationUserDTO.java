package ch.martinelli.sakila.backend.entity;

import java.util.UUID;

public record ApplicationUserDTO(UUID id, String username, String name, String hashedPassword, byte[] profilePicture) {
}
