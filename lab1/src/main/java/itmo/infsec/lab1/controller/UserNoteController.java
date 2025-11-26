package itmo.infsec.lab1.controller;

import itmo.infsec.lab1.dto.UserNoteRequestDTO;
import itmo.infsec.lab1.dto.UserNoteResponseDTO;
import itmo.infsec.lab1.service.UserNoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class UserNoteController {
    @Autowired
    private UserNoteService userNoteService;

    @PostMapping
    public ResponseEntity<UserNoteResponseDTO> create(Authentication authentication,
                                                      @Valid @RequestBody UserNoteRequestDTO request) {
        String username = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userNoteService.create(username, request));
    }

    @GetMapping
    public ResponseEntity<List<UserNoteResponseDTO>> list(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userNoteService.list(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserNoteResponseDTO> update(Authentication authentication,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody UserNoteRequestDTO request) {
        String username = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userNoteService.update(username, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        String username = (String) authentication.getPrincipal();
        userNoteService.delete(username, id);
        return ResponseEntity.noContent().build();
    }
}
