package itmo.infsec.lab1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "note_title", nullable = false)
    private String noteTitle;

    @Column(name = "note_content", nullable = false)
    private String noteContent;

    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;
}
