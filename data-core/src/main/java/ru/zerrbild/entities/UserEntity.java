package ru.zerrbild.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.zerrbild.entities.enums.UserState;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "bot_users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long tgUserId;
    @CreationTimestamp
    LocalDateTime firstLoginDate;
    String firstName;
    String lastName;
    String username;
    String email;
    @Enumerated(EnumType.STRING)
    UserState state;
}
