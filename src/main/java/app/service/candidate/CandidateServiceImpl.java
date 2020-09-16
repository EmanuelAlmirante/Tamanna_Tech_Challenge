package app.service.candidate;

import app.exception.BusinessException;
import app.model.AvailabilitySlot;
import app.model.candidate.CandidateAvailabilityModel;
import app.model.candidate.CandidateModel;
import app.repository.candidate.CandidateAvailabilityRepository;
import app.repository.candidate.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;
    private final CandidateAvailabilityRepository candidateAvailabilityRepository;


    @Autowired
    public CandidateServiceImpl(CandidateRepository candidateRepository,
                                CandidateAvailabilityRepository candidateAvailabilityRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateAvailabilityRepository = candidateAvailabilityRepository;
    }

    @Override
    public CandidateModel createCandidate(CandidateModel candidateModel) {
        verifyValidityOfCandidate(candidateModel);

        return candidateRepository.save(candidateModel);
    }

    @Override
    public List<CandidateModel> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public Optional<CandidateModel> getCandidateByName(String name) {
        return candidateRepository.findById(name);
    }

    @Override
    public void deleteCandidateByName(String name) {
        candidateRepository.deleteById(name);
    }

    @Override
    public CandidateAvailabilityModel createCandidateAvailability(
            CandidateAvailabilityModel candidateAvailabilityModel) {
        verifyValidityOfCandidateAvailability(candidateAvailabilityModel);

        CandidateAvailabilityModel candidateExistingAvailabilityModel = verifyIfCandidateHasAvailabilityCreated(
                candidateAvailabilityModel);

        if (candidateExistingAvailabilityModel != null) {
            addNewAvailability(candidateExistingAvailabilityModel, candidateAvailabilityModel);

            return candidateAvailabilityRepository.save(candidateExistingAvailabilityModel);
        }

        return candidateAvailabilityRepository.save(candidateAvailabilityModel);
    }

    @Override
    public List<CandidateAvailabilityModel> getAllCandidatesAvailability() {
        return candidateAvailabilityRepository.findAll();
    }

    @Override
    public CandidateAvailabilityModel getCandidateAvailabilityByName(String name) {
        return candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(name);
    }

    private void verifyValidityOfCandidate(CandidateModel candidateModel) {
        verifyNameIsFilled(candidateModel);
        verifyUniqueName(candidateModel);
    }

    private void verifyNameIsFilled(CandidateModel candidateModel) {
        if (candidateModel.getName() == null || candidateModel.getName().isBlank()) {
            throw new BusinessException("You must provide a name!",
                                        candidateModel.getName() != null ? candidateModel.getName() : null);
        }
    }

    private void verifyUniqueName(CandidateModel candidateModel) {
        List<String> existingNames = candidateRepository.getAllNames();

        if (existingNames.contains(candidateModel.getName())) {
            throw new BusinessException("Name already exists!", candidateModel.getName());
        }
    }

    private void verifyValidityOfCandidateAvailability(CandidateAvailabilityModel candidateAvailabilityModel) {
        verifyCandidateExists(candidateAvailabilityModel);
        verifyPeriodOfAvailabilityIsValid(candidateAvailabilityModel);
    }

    private void verifyCandidateExists(CandidateAvailabilityModel candidateAvailabilityModel) {
        Optional<CandidateModel> candidateModel = candidateRepository.findById(
                candidateAvailabilityModel.getCandidateModel().getName());

        if (candidateModel.isEmpty()) {
            throw new BusinessException("Candidate does not exist!",
                                        candidateAvailabilityModel.getCandidateModel().getName());
        }
    }

    private void verifyPeriodOfAvailabilityIsValid(CandidateAvailabilityModel candidateAvailabilityModel) {
        List<AvailabilitySlot> availabilitySlotList = candidateAvailabilityModel.getAvailabilitySlot();

        for (AvailabilitySlot availabilitySlot : availabilitySlotList) {
            if (availabilitySlot.getFrom().isAfter(availabilitySlot.getTo()) || availabilitySlot.getFrom().isEqual(
                    availabilitySlot.getTo())) {
                throw new BusinessException("Start hour of slot must be before end hour of slot!",
                                            "From: " + availabilitySlot.getFrom(), "To: " + availabilitySlot.getTo());
            }

            if (availabilitySlot.getFrom().getMinute() != 0 || availabilitySlot.getTo().getMinute() != 0) {
                throw new BusinessException(
                        "Availability slot must be from the beginning of the hour until the beginning of the next "
                        + "hour.",
                        "From: " + availabilitySlot.getFrom(), "To: " + availabilitySlot.getTo());
            }
        }
    }

    private CandidateAvailabilityModel verifyIfCandidateHasAvailabilityCreated(
            CandidateAvailabilityModel candidateAvailabilityModel) {
        return candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(
                candidateAvailabilityModel.getCandidateModel().getName());
    }

    private void addNewAvailability(CandidateAvailabilityModel candidateExistingAvailabilityModel,
                                    CandidateAvailabilityModel candidateAvailabilityModel) {
        candidateExistingAvailabilityModel.getAvailabilitySlot().addAll(
                candidateAvailabilityModel.getAvailabilitySlot());
    }
}
