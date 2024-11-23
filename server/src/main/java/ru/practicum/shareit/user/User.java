package ru.practicum.shareit.user;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String email;

    String name;

    Instant registrationDate = Instant.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(name, user.name) &&
                Objects.equals(registrationDate, user.registrationDate);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, email, name, registrationDate);
    }
}