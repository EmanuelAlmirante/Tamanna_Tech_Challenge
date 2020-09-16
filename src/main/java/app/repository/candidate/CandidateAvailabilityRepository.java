package app.repository.candidate;

import app.model.candidate.CandidateAvailabilityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateAvailabilityRepository extends JpaRepository<CandidateAvailabilityModel, Long> {
    @Query("select ca from CandidateAvailabilityModel ca where ca.candidateModel.name = :name")
    CandidateAvailabilityModel getCandidateAvailabilityByCandidateName(String name);
}
