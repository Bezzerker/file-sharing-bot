package ru.zerrbild.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
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
@Entity(name = "documents")
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne
    BinaryDataEntity binaryData;
    String fileId;
    String fileName;
    String mimeType;
    Long fileSize;
}
