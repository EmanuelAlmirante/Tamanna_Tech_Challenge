package app.repository.candidate;

import app.model.candidate.CandidateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateModel, String> {
    @Query("select c.name from CandidateModel c")
    List<String> getAllNames();
}
