package app.service.interviewslots;

import app.exception.BusinessException;
import app.model.candidate.CandidateAvailabilityModel;
import app.model.candidate.CandidateModel;
import app.model.interviewer.InterviewerAvailabilityModel;
import app.model.interviewer.InterviewerModel;
import app.model.interviewslots.InterviewSlotsQueryModel;
import app.model.interviewslots.InterviewSlotsReturnModel;
import app.model.utils.AvailabilitySlot;
import app.repository.candidate.CandidateAvailabilityRepository;
import app.repository.candidate.CandidateRepository;
import app.repository.interviewer.InterviewerAvailabilityRepository;
import app.repository.interviewer.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterviewSlotsServiceImpl implements InterviewSlotsService {
    private final CandidateRepository candidateRepository;
    private final CandidateAvailabilityRepository candidateAvailabilityRepository;
    private final InterviewerRepository interviewerRepository;
    private final InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    @Autowired
    public InterviewSlotsServiceImpl(CandidateRepository candidateRepository,
                                     CandidateAvailabilityRepository candidateAvailabilityRepository,
                                     InterviewerRepository interviewerRepository,
                                     InterviewerAvailabilityRepository interviewerAvailabilityRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateAvailabilityRepository = candidateAvailabilityRepository;
        this.interviewerRepository = interviewerRepository;
        this.interviewerAvailabilityRepository = interviewerAvailabilityRepository;
    }

    @Override
    public InterviewSlotsReturnModel getInterviewSlots(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        verifyCandidateExists(interviewSlotsQueryModel);
        verifyInterviewersExist(interviewSlotsQueryModel);

        List<AvailabilitySlot> candidateAvailabilitySlots = getCandidateAvailabilitySlots(interviewSlotsQueryModel);
        List<AvailabilitySlot> interviewersAvailabilitySlots = getInterviewersAvailabilities(interviewSlotsQueryModel);

        getCommonAvailabilitySlots(candidateAvailabilitySlots, interviewersAvailabilitySlots);

        return null;
    }

    private void verifyCandidateExists(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        String candidateName = interviewSlotsQueryModel.getCandidate().getName();

        Optional<CandidateModel> existingCandidate = candidateRepository.findById(candidateName);

        if (existingCandidate.isEmpty()) {
            throw new BusinessException("Candidate does not exist!", candidateName);
        }
    }

    private void verifyInterviewersExist(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        List<InterviewerModel> interviewersList = interviewSlotsQueryModel.getInterviewersList();

        for (InterviewerModel interviewer : interviewersList) {
            String interviewerName = interviewer.getName();

            Optional<InterviewerModel> existingInterviewer = interviewerRepository.findById(interviewerName);

            if (existingInterviewer.isEmpty()) {
                throw new BusinessException("Interviewer does not exist!", interviewerName);
            }
        }
    }

    private List<AvailabilitySlot> getCandidateAvailabilitySlots(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        String candidateName = interviewSlotsQueryModel.getCandidate().getName();

        CandidateAvailabilityModel candidateAvailability =
                candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName);

        List<AvailabilitySlot> candidateAvailabilitySlots;

        if (candidateAvailability == null) {
            throw new BusinessException("Candidate has no availability defined!", candidateName);
        } else {
            candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        }

        return candidateAvailabilitySlots;
    }

    private List<AvailabilitySlot> getInterviewersAvailabilities(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        List<InterviewerModel> interviewersList = interviewSlotsQueryModel.getInterviewersList();
        List<AvailabilitySlot> interviewersAvailabilitySlots = new ArrayList<>();

        for (InterviewerModel interviewer : interviewersList) {
            String interviewerName = interviewer.getName();

            InterviewerAvailabilityModel interviewerAvailability = interviewerAvailabilityRepository
                    .getInterviewerAvailabilityByInterviewerName(interviewerName);

            if (interviewerAvailability == null) {
                throw new BusinessException("Interviewer has no availability defined!", interviewerName);
            } else {
                interviewersAvailabilitySlots.addAll(interviewerAvailability.getAvailabilitySlotList());
            }
        }

        return interviewersAvailabilitySlots;
    }

    private void getCommonAvailabilitySlots(List<AvailabilitySlot> candidateAvailabilitySlots,
                                            List<AvailabilitySlot> interviewersAvailabilitySlots) {

        List<AvailabilitySlot> commonCandidateAvailabilitySlots = new ArrayList<>(); // All slots that have a common day
        List<AvailabilitySlot> commonInterviewersAvailabilitySlots =
                new ArrayList<>(); // All slots that have a common day

        List<LocalDate> candidateAvailabilityDays = candidateAvailabilitySlots.stream().map(AvailabilitySlot::getDay)
                                                                              .collect(Collectors.toList());

        List<LocalDate> interviewersAvailabilityDays = interviewersAvailabilitySlots.stream().map(
                AvailabilitySlot::getDay).collect(
                Collectors.toList());

        List<LocalDate> commonDays = candidateAvailabilityDays.stream().distinct().filter(
                interviewersAvailabilityDays::contains).collect(
                Collectors.toList());


        for (LocalDate commonDay : commonDays) {
            for (AvailabilitySlot candidateAvailabilitySlot : candidateAvailabilitySlots) {
                if (candidateAvailabilitySlot.getDay().isEqual(commonDay)) {
                    commonCandidateAvailabilitySlots.add(candidateAvailabilitySlot);
                }
            }

            for (AvailabilitySlot interviewersAvailabilitySlot : interviewersAvailabilitySlots) {
                if (interviewersAvailabilitySlot.getDay().isEqual(commonDay)) {
                    commonInterviewersAvailabilitySlots.add(interviewersAvailabilitySlot);
                }
            }
        }

        System.out.println(1);
    }
}
