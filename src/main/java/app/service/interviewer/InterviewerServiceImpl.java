package app.service.interviewer;

import app.exception.BusinessException;
import app.model.AvailabilitySlot;
import app.model.interviewer.InterviewerAvailabilityModel;
import app.model.interviewer.InterviewerModel;
import app.repository.interviewer.InterviewerAvailabilityRepository;
import app.repository.interviewer.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewerServiceImpl implements InterviewerService {
    private final InterviewerRepository interviewerRepository;
    private final InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    @Autowired
    public InterviewerServiceImpl(InterviewerRepository interviewerRepository,
                                  InterviewerAvailabilityRepository interviewerAvailabilityRepository) {
        this.interviewerRepository = interviewerRepository;
        this.interviewerAvailabilityRepository = interviewerAvailabilityRepository;
    }

    @Override
    public InterviewerModel createInterviewer(InterviewerModel interviewerModel) {
        verifyValidityOfInterviewer(interviewerModel);

        return interviewerRepository.save(interviewerModel);
    }

    @Override
    public List<InterviewerModel> getAllInterviewers() {
        return interviewerRepository.findAll();
    }

    @Override
    public Optional<InterviewerModel> getInterviewerByName(String name) {
        return interviewerRepository.findById(name);
    }

    @Override
    public void deleteInterviewerByName(String name) {
        interviewerRepository.deleteById(name);
    }

    @Override
    public InterviewerAvailabilityModel createInterviewerAvailability(
            InterviewerAvailabilityModel interviewerAvailabilityModel) {
        verifyValidityOfInterviewerAvailability(interviewerAvailabilityModel);

        InterviewerAvailabilityModel interviewerExistingAvailability = verifyIfInterviewerHasAvailabilityCreated(
                interviewerAvailabilityModel);

        if (interviewerExistingAvailability != null) {
            addNewAvailability(interviewerExistingAvailability, interviewerAvailabilityModel);

            return interviewerAvailabilityRepository.save(interviewerExistingAvailability);
        }

        return interviewerAvailabilityRepository.save(interviewerAvailabilityModel);
    }

    @Override
    public List<InterviewerAvailabilityModel> getAllInterviewersAvailability() {
        return interviewerAvailabilityRepository.findAll();
    }

    @Override
    public InterviewerAvailabilityModel getInterviewerAvailabilityByName(String name) {
        return interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(name);
    }

    @Override
    public void deleteInterviewerAvailabilityByName(String name) {
        Long interviewerAvailabilityIdToBeDeleted =
                interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(name).getId();

        interviewerAvailabilityRepository.deleteById(interviewerAvailabilityIdToBeDeleted);
    }

    private void verifyValidityOfInterviewer(InterviewerModel interviewerModel) {
        verifyNameIsFilled(interviewerModel);
        verifyUniqueName(interviewerModel);
    }

    private void verifyNameIsFilled(InterviewerModel interviewerModel) {
        String nameOfInterviewerToBeCreated = interviewerModel.getName();

        if (nameOfInterviewerToBeCreated == null || nameOfInterviewerToBeCreated.isBlank()) {
            throw new BusinessException("You must provide a name!",
                                        interviewerModel.getName() != null ? interviewerModel.getName() : null);
        }
    }

    private void verifyUniqueName(InterviewerModel interviewerModel) {
        String nameOfInterviewerToBeCreated = interviewerModel.getName();
        List<String> existingNames = interviewerRepository.getAllNames();

        if (existingNames.contains(nameOfInterviewerToBeCreated)) {
            throw new BusinessException("Name already exists!", interviewerModel.getName());
        }
    }

    private void verifyValidityOfInterviewerAvailability(InterviewerAvailabilityModel interviewerAvailabilityModel) {
        verifyInterviewerExists(interviewerAvailabilityModel);
        verifyPeriodOfAvailabilityIsValid(interviewerAvailabilityModel);
    }

    private void verifyInterviewerExists(InterviewerAvailabilityModel interviewerAvailabilityModel) {
        Optional<InterviewerModel> interviewerModel = interviewerRepository.findById(
                interviewerAvailabilityModel.getInterviewerModel().getName());

        if (interviewerModel.isEmpty()) {
            throw new BusinessException("Interviewer does not exist!",
                                        interviewerAvailabilityModel.getInterviewerModel().getName());
        }
    }

    private void verifyPeriodOfAvailabilityIsValid(InterviewerAvailabilityModel interviewerAvailabilityModel) {
        List<AvailabilitySlot> availabilitySlotList = interviewerAvailabilityModel.getAvailabilitySlotList();

        for (AvailabilitySlot availabilitySlot : availabilitySlotList) {
            LocalDateTime newAvailabilitySlotFromDateTime = availabilitySlot.getFrom();
            LocalDateTime newAvailabilitySlotToDateTime = availabilitySlot.getTo();

            if (newAvailabilitySlotFromDateTime.isAfter(newAvailabilitySlotToDateTime)
                || newAvailabilitySlotFromDateTime.isEqual(
                    newAvailabilitySlotToDateTime)) {
                throw new BusinessException("Start hour of slot must be before end hour of slot!",
                                            "From: " + newAvailabilitySlotFromDateTime,
                                            "To: " + newAvailabilitySlotToDateTime);
            }

            if (availabilitySlot.getFrom().getMinute() != 0 || availabilitySlot.getTo().getMinute() != 0) {
                throw new BusinessException(
                        "Availability slot must be from the beginning of the hour until the beginning of the next "
                        + "hour.",
                        "From: " + newAvailabilitySlotFromDateTime, "To: " + newAvailabilitySlotToDateTime);
            }
        }
    }

    private InterviewerAvailabilityModel verifyIfInterviewerHasAvailabilityCreated(
            InterviewerAvailabilityModel interviewerAvailabilityModel) {
        return interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(
                interviewerAvailabilityModel.getInterviewerModel().getName());
    }

    private void addNewAvailability(InterviewerAvailabilityModel interviewerExistingAvailabilityModel,
                                    InterviewerAvailabilityModel interviewerAvailabilityModel) {
        interviewerExistingAvailabilityModel.getAvailabilitySlotList().addAll(
                interviewerAvailabilityModel.getAvailabilitySlotList());
    }
}
