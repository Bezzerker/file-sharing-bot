package ru.zerrbild.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "binary_data")
public class BinaryDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    byte[] bytes;
}
