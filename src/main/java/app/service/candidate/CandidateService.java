package app.service.candidate;

import app.model.candidate.CandidateAvailabilityModel;
import app.model.candidate.CandidateModel;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    CandidateModel createCandidate(CandidateModel candidateModel);

    List<CandidateModel> getAllCandidates();

    Optional<CandidateModel> getCandidateByName(String name);

    void deleteCandidateByName(String name);

    CandidateAvailabilityModel createCandidateAvailability(CandidateAvailabilityModel candidateAvailabilityModel);
}
