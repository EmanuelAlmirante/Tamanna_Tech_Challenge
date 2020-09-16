package app.service.candidate;

import app.exception.BusinessException;
import app.model.AvailabilitySlot;
import app.model.candidate.CandidateAvailabilityModel;
import app.model.candidate.CandidateModel;
import app.repository.candidate.CandidateAvailabilityRepository;
import app.repository.candidate.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        CandidateAvailabilityModel candidateExistingAvailability = verifyIfCandidateHasAvailabilityCreated(
                candidateAvailabilityModel);

        if (candidateExistingAvailability != null) {
            addNewAvailability(candidateExistingAvailability, candidateAvailabilityModel);

            return candidateAvailabilityRepository.save(candidateExistingAvailability);
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

    @Override
    public void deleteCandidateAvailabilityByName(String name) {
        Long candidateAvailabilityIdToBeDeleted =
                candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(name).getId();

        candidateAvailabilityRepository.deleteById(candidateAvailabilityIdToBeDeleted);
    }

    private void verifyValidityOfCandidate(CandidateModel candidateModel) {
        verifyNameIsFilled(candidateModel);
        verifyUniqueName(candidateModel);
    }

    private void verifyNameIsFilled(CandidateModel candidateModel) {
        String nameOfCandidateToBeCreated = candidateModel.getName();

        if (nameOfCandidateToBeCreated == null || nameOfCandidateToBeCreated.isBlank()) {
            throw new BusinessException("You must provide a name!",
                                        candidateModel.getName() != null ? candidateModel.getName() : null);
        }
    }

    private void verifyUniqueName(CandidateModel candidateModel) {
        String nameOfCandidateToBeCreated = candidateModel.getName();
        List<String> existingNames = candidateRepository.getAllNames();

        if (existingNames.contains(nameOfCandidateToBeCreated)) {
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
        List<AvailabilitySlot> availabilitySlotList = candidateAvailabilityModel.getAvailabilitySlotList();

        for (AvailabilitySlot availabilitySlot : availabilitySlotList) {
            LocalDateTime newAvailabilitySlotFromDateTime = availabilitySlot.getFrom();
            LocalDateTime newAvailabilitySlotToDateTime = availabilitySlot.getTo();

            if (newAvailabilitySlotFromDateTime.isAfter(newAvailabilitySlotToDateTime)
                || newAvailabilitySlotFromDateTime
                        .isEqual(newAvailabilitySlotToDateTime)) {
                throw new BusinessException("Start hour of slot must be before end hour of slot!",
                                            "From: " + newAvailabilitySlotFromDateTime,
                                            "To: " + newAvailabilitySlotToDateTime);
            }

            if (newAvailabilitySlotFromDateTime.getMinute() != 0 || newAvailabilitySlotToDateTime.getMinute() != 0) {
                throw new BusinessException(
                        "Availability slot must be from the beginning of the hour until the beginning of the next "
                        + "hour.",
                        "From: " + newAvailabilitySlotFromDateTime, "To: " + newAvailabilitySlotToDateTime);
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
        candidateExistingAvailabilityModel.getAvailabilitySlotList().addAll(
                candidateAvailabilityModel.getAvailabilitySlotList());
    }
}
