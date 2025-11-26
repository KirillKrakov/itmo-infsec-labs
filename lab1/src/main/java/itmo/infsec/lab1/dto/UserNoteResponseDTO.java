package itmo.infsec.lab1.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNoteResponseDTO {
    private Long id;
    private String noteTitle;
    private String noteContent;
    private Instant lastModifiedAt;
}
