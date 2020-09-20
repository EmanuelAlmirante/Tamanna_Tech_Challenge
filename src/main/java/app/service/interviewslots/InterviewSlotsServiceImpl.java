package app.service.interviewslots;

import app.exception.BusinessException;
import app.model.candidate.CandidateAvailabilityModel;
import app.model.candidate.CandidateModel;
import app.model.interviewer.InterviewerAvailabilityModel;
import app.model.interviewer.InterviewerModel;
import app.model.interviewslots.InterviewSlotsQueryModel;
import app.model.interviewslots.InterviewSlotsReturnModel;
import app.model.utils.AvailabilitySlot;
import app.model.utils.TimeSlot;
import app.repository.candidate.CandidateAvailabilityRepository;
import app.repository.candidate.CandidateRepository;
import app.repository.interviewer.InterviewerAvailabilityRepository;
import app.repository.interviewer.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
        verifyCandidateAndInterviewersExist(interviewSlotsQueryModel);

        String candidateName = interviewSlotsQueryModel.getCandidateName();
        List<String> interviewersNames = interviewSlotsQueryModel.getInterviewersNames();
        List<AvailabilitySlot> interviewAvailabilitySlots = getInterviewAvailabilitySlots(interviewSlotsQueryModel);

        InterviewSlotsReturnModel interviewSlotsReturnModel =
                InterviewSlotsReturnModel.Builder.interviewSlotsReturnModelWith()
                                                 .withCandidateName(candidateName)
                                                 .withInterviewerNameList(interviewersNames)
                                                 .withInterviewAvailabilitySlotList(interviewAvailabilitySlots)
                                                 .build();

        return interviewSlotsReturnModel;
    }

    private void verifyCandidateAndInterviewersExist(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        String candidateName = interviewSlotsQueryModel.getCandidateName();
        List<String> interviewersNames = interviewSlotsQueryModel.getInterviewersNames();

        verifyCandidateExists(candidateName);
        verifyInterviewersExist(interviewersNames);
    }

    private void verifyCandidateExists(String candidateName) {
        Optional<CandidateModel> existingCandidate = candidateRepository.findById(candidateName);

        if (existingCandidate.isEmpty()) {
            throw new BusinessException("Candidate does not exist!", candidateName);
        }
    }

    private void verifyInterviewersExist(List<String> interviewersNames) {
        for (String interviewerName : interviewersNames) {
            Optional<InterviewerModel> existingInterviewer = interviewerRepository.findById(interviewerName);

            if (existingInterviewer.isEmpty()) {
                throw new BusinessException("Interviewer does not exist!", interviewerName);
            }
        }
    }

    private List<AvailabilitySlot> getInterviewAvailabilitySlots(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        String candidateName = interviewSlotsQueryModel.getCandidateName();
        CandidateAvailabilityModel candidateAvailability = getCandidateAvailability(candidateName);

        List<String> interviewersNames = interviewSlotsQueryModel.getInterviewersNames();
        List<InterviewerAvailabilityModel> interviewersAvailabilities = new ArrayList<>();

        for (String interviewerName : interviewersNames) {
            InterviewerAvailabilityModel interviewerAvailability = getInterviewerAvailability(interviewerName);

            interviewersAvailabilities.add(interviewerAvailability);
        }

        Set<LocalDate> candidateAndInterviewersAvailabilitiesCommonDays =
                getCandidateAndInterviewersAvailabilitiesCommonDays(
                        candidateAvailability,
                        interviewersAvailabilities);

        List<AvailabilitySlot> interviewAvailabilitySlots = getCommonAvailabilitySlots(
                candidateAndInterviewersAvailabilitiesCommonDays,
                candidateAvailability,
                interviewersAvailabilities);

        return interviewAvailabilitySlots;
    }


    private CandidateAvailabilityModel getCandidateAvailability(String candidateName) {
        CandidateAvailabilityModel candidateAvailability =
                candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName);

        if (candidateAvailability == null) {
            throw new BusinessException("Candidate has no availability defined!", candidateName);
        }

        return candidateAvailability;
    }

    private InterviewerAvailabilityModel getInterviewerAvailability(String interviewerName) {
        InterviewerAvailabilityModel interviewerAvailability = interviewerAvailabilityRepository
                .getInterviewerAvailabilityByInterviewerName(interviewerName);

        if (interviewerAvailability == null) {
            throw new BusinessException("Interviewer has no availability defined!", interviewerName);
        }

        return interviewerAvailability;
    }

    private Set<LocalDate> getCandidateAndInterviewersAvailabilitiesCommonDays(
            CandidateAvailabilityModel candidateAvailability,
            List<InterviewerAvailabilityModel> interviewersAvailabilities) {
        List<AvailabilitySlot> candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();

        Set<LocalDate> candidateAndInterviewersAvailabilitiesCommonDays = new HashSet<>();

        for (AvailabilitySlot candidateAvailabilitySlot : candidateAvailabilitySlots) {
            LocalDate candidateAvailabilityDay = candidateAvailabilitySlot.getDay();

            for (InterviewerAvailabilityModel interviewerAvailability : interviewersAvailabilities) {
                List<AvailabilitySlot> interviewerAvailabilitySlots = interviewerAvailability.getAvailabilitySlotList();

                for (AvailabilitySlot interviewerAvailabilitySlot : interviewerAvailabilitySlots) {
                    LocalDate interviewerAvailabilityDay = interviewerAvailabilitySlot.getDay();

                    if (candidateAvailabilityDay.compareTo(interviewerAvailabilityDay) == 0) {
                        candidateAndInterviewersAvailabilitiesCommonDays.add(candidateAvailabilityDay);
                    }
                }
            }
        }

        return candidateAndInterviewersAvailabilitiesCommonDays;
    }

    private List<AvailabilitySlot> getCommonAvailabilitySlots(Set<LocalDate> commonDays,
                                                              CandidateAvailabilityModel candidateAvailability,
                                                              List<InterviewerAvailabilityModel> interviewersAvailabilities) {
        List<AvailabilitySlot> candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> interviewAvailabilitySlots = getCommonDayAvailabilitySlots(commonDays,
                                                                                          candidateAvailabilitySlots);

        for (InterviewerAvailabilityModel interviewerAvailability : interviewersAvailabilities) {
            List<AvailabilitySlot> interviewerAllAvailabilitySlots = interviewerAvailability.getAvailabilitySlotList();

            List<AvailabilitySlot> interviewerCommonAvailabilitySlots = getCommonDayAvailabilitySlots(
                    commonDays, interviewerAllAvailabilitySlots);

            interviewAvailabilitySlots = getOverlappingAvailabilitySlots(interviewAvailabilitySlots,
                                                                         interviewerCommonAvailabilitySlots);
        }

        return interviewAvailabilitySlots;
    }

    private List<AvailabilitySlot> getCommonDayAvailabilitySlots(Set<LocalDate> commonDays,
                                                                 List<AvailabilitySlot> availabilitySlots) {
        List<AvailabilitySlot> commonAvailabilitySlots = new ArrayList<>();

        for (AvailabilitySlot availabilitySlot : availabilitySlots) {
            for (LocalDate localDate : commonDays) {
                if (availabilitySlot.getDay().compareTo(localDate) == 0) {
                    commonAvailabilitySlots.add(availabilitySlot);
                }
            }
        }

        return commonAvailabilitySlots;
    }

    private List<AvailabilitySlot> getOverlappingAvailabilitySlots(
            List<AvailabilitySlot> candidateCommonAvailabilitySlots,
            List<AvailabilitySlot> interviewerCommonAvailabilitySlots) {
        List<AvailabilitySlot> overlappingAvailabilitySlots = calculateOverlappingAvailabilitySlots(
                candidateCommonAvailabilitySlots, interviewerCommonAvailabilitySlots);

        return overlappingAvailabilitySlots;
    }

    private List<AvailabilitySlot> calculateOverlappingAvailabilitySlots(
            List<AvailabilitySlot> firstAvailabilitySlots,
            List<AvailabilitySlot> secondAvailabilitySlots) {
        List<AvailabilitySlot> overlappingAvailabilitySlots = new ArrayList<>();

        for (AvailabilitySlot firstAvailabilitySlot : firstAvailabilitySlots) {
            for (AvailabilitySlot secondAvailabilitySlot : secondAvailabilitySlots) {
                LocalDate firstAvailabilitySlotDay = firstAvailabilitySlot.getDay();
                LocalDate secondAvailabilitySlotDay = secondAvailabilitySlot.getDay();

                if (firstAvailabilitySlotDay.compareTo(secondAvailabilitySlotDay) == 0) {
                    List<TimeSlot> firstAvailabilityTimeSlots = firstAvailabilitySlot.getTimeSlotList();
                    List<TimeSlot> secondAvailabilityTimeSlots = secondAvailabilitySlot.getTimeSlotList();

                    List<TimeSlot> overlappingTimeSlots = new ArrayList<>();

                    for (TimeSlot firstTimeSlot : firstAvailabilityTimeSlots) {
                        for (TimeSlot secondTimeSlot : secondAvailabilityTimeSlots) {
                            LocalTime firstTimeSlotFrom = firstTimeSlot.getFrom();
                            LocalTime firstTimeSlotTo = firstTimeSlot.getTo();
                            LocalTime secondTimeSlotFrom = secondTimeSlot.getFrom();
                            LocalTime secondTimeSlotTo = secondTimeSlot.getTo();

                            TimeSlot overlappingTimeSlot = getOverlappingTimeSlot(firstTimeSlotFrom, firstTimeSlotTo,
                                                                                  secondTimeSlotFrom,
                                                                                  secondTimeSlotTo);

                            if (overlappingTimeSlot.getFrom() != null && overlappingTimeSlot.getTo() != null) {
                                overlappingTimeSlots.add(overlappingTimeSlot);
                            }
                        }
                    }

                    if (!overlappingTimeSlots.isEmpty()) {
                        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith()
                                                                                    .withDay(secondAvailabilitySlot
                                                                                                     .getDay())
                                                                                    .withTimeSlotList(
                                                                                            overlappingTimeSlots)
                                                                                    .build();

                        overlappingAvailabilitySlots.add(availabilitySlot);
                    }
                }
            }
        }

        return overlappingAvailabilitySlots;
    }

    private TimeSlot getOverlappingTimeSlot(LocalTime candidateFrom, LocalTime candidateTo, LocalTime interviewerFrom,
                                            LocalTime interviewerTo) {
        TimeSlot overlappingTimeSlot = new TimeSlot();
        LocalTime overlapTimeSlotFrom;
        LocalTime overlapTimeSlotTo;

        if (candidateFrom.isBefore(interviewerTo) && interviewerFrom.isBefore(candidateTo)) {
            if (candidateFrom.isBefore(interviewerFrom)) {
                overlapTimeSlotFrom = interviewerFrom;
            } else {
                overlapTimeSlotFrom = candidateFrom;
            }

            if (candidateTo.isBefore(interviewerTo)) {
                overlapTimeSlotTo = candidateTo;
            } else {
                overlapTimeSlotTo = interviewerTo;
            }

            overlappingTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(overlapTimeSlotFrom).withTo(
                    overlapTimeSlotTo).build();
        }

        return overlappingTimeSlot;
    }
}
