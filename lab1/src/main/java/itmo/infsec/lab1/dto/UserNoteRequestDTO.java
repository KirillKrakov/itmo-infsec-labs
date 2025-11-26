package itmo.infsec.lab1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserNoteRequestDTO {
    @NotBlank
    @Size(max = 50)
    private String noteTitle;

    @NotBlank
    @Size(max = 1000)
    private String noteContent;
}
