package itmo.infsec.lab1.repository;

import itmo.infsec.lab1.model.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNoteRepository extends JpaRepository<UserNote, Long> {
    List<UserNote> findAllByUsername(String username);
    Optional<UserNote> findByIdAndUsername(Long id, String username);
    boolean existsByNoteTitle (String noteTitle);
}
