package itmo.infsec.lab1.service;

import itmo.infsec.lab1.dto.UserNoteRequestDTO;
import itmo.infsec.lab1.dto.UserNoteResponseDTO;
import itmo.infsec.lab1.model.UserNote;
import itmo.infsec.lab1.repository.UserNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNoteService {
    private final UserNoteRepository userNoteRepository;

    @Transactional
    public UserNoteResponseDTO create(String username, UserNoteRequestDTO request) {
        if (userNoteRepository.existsByNoteTitle(request.getNoteTitle())) {
            throw new IllegalArgumentException("Note with this title already exists");
        }
        UserNote note = new UserNote();
        note.setUsername(username);
        note.setNoteTitle(request.getNoteTitle());
        note.setNoteContent(request.getNoteContent());
        note.setLastModifiedAt(Instant.now());
        UserNote saved = userNoteRepository.save(note);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserNoteResponseDTO> list(String username) {
        return userNoteRepository.findAllByUsername(username)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserNoteResponseDTO update(String username, Long id, UserNoteRequestDTO request) {
        UserNote note = userNoteRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        note.setNoteTitle(request.getNoteTitle());
        note.setNoteContent(request.getNoteContent());
        note.setLastModifiedAt(Instant.now());
        UserNote saved = userNoteRepository.save(note);
        return toResponse(saved);
    }

    @Transactional
    public void delete(String username, Long id) {
        UserNote note = userNoteRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        userNoteRepository.delete(note);
    }

    private UserNoteResponseDTO toResponse(UserNote note) {
        return new UserNoteResponseDTO(
                note.getId(),
                note.getNoteTitle(),
                note.getNoteContent(),
                note.getLastModifiedAt());
    }
}
