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
        verifyCandidateExists(interviewSlotsQueryModel);
        verifyInterviewersExist(interviewSlotsQueryModel);

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

    private void verifyCandidateExists(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        String candidateName = interviewSlotsQueryModel.getCandidateName();

        Optional<CandidateModel> existingCandidate = candidateRepository.findById(candidateName);

        if (existingCandidate.isEmpty()) {
            throw new BusinessException("Candidate does not exist!", candidateName);
        }
    }

    private void verifyInterviewersExist(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        List<String> interviewersNames = interviewSlotsQueryModel.getInterviewersNames();

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

        List<InterviewerAvailabilityModel> interviewerAvailabilities = new ArrayList<>();

        for (String interviewerName : interviewersNames) {
            InterviewerAvailabilityModel interviewerAvailability = getInterviewerAvailability(interviewerName);

            interviewerAvailabilities.add(interviewerAvailability);
        }

        List<AvailabilitySlot> interviewAvailabilitySlots;

        Set<LocalDate> commonDaysOneInterviewer = getAvailabilitiesCommonDays(candidateAvailability,
                                                                              interviewerAvailabilities);

        interviewAvailabilitySlots = getInterviewAvailabilitySlots(commonDaysOneInterviewer,
                                                                   candidateAvailability,
                                                                   interviewerAvailabilities);

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

    private Set<LocalDate> getAvailabilitiesCommonDays(CandidateAvailabilityModel candidateAvailability,
                                                       List<InterviewerAvailabilityModel> interviewersAvailabilities) {
        List<AvailabilitySlot> candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();

        Set<LocalDate> commonDays = new HashSet<>();

        for (AvailabilitySlot candidateAvailabilitySlot : candidateAvailabilitySlots) {
            LocalDate candidateAvailabilityDay = candidateAvailabilitySlot.getDay();

            for (InterviewerAvailabilityModel interviewerAvailability : interviewersAvailabilities) {
                List<AvailabilitySlot> interviewerAvailabilitySlots = interviewerAvailability.getAvailabilitySlotList();

                for (AvailabilitySlot interviewerAvailabilitySlot : interviewerAvailabilitySlots) {
                    LocalDate interviewerAvailabilityDay = interviewerAvailabilitySlot.getDay();

                    if (candidateAvailabilityDay.compareTo(interviewerAvailabilityDay) == 0) {
                        commonDays.add(candidateAvailabilityDay);
                    }
                }
            }
        }

        return commonDays;
    }

    private List<AvailabilitySlot> getInterviewAvailabilitySlots(Set<LocalDate> commonDays,
                                                                 CandidateAvailabilityModel candidateAvailability,
                                                                 List<InterviewerAvailabilityModel> interviewersAvailabilities) {
        List<AvailabilitySlot> candidateAllAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> interviewAvailabilitySlots = getCandidateCommonDayAvailabilitySlots(commonDays,
                                                                                                   candidateAllAvailabilitySlots);

        for (InterviewerAvailabilityModel interviewerAvailability : interviewersAvailabilities) {
            List<AvailabilitySlot> interviewerAllAvailabilitySlots = interviewerAvailability.getAvailabilitySlotList();

            List<AvailabilitySlot> interviewerCommonAvailabilitySlots = getInterviewerCommonDayAvailabilitySlots(
                    commonDays, interviewerAllAvailabilitySlots);

            List<AvailabilitySlot> test = getFinalAvailabilitySlotsOneInterviewer(interviewAvailabilitySlots,
                                                                                  interviewerCommonAvailabilitySlots);

            interviewAvailabilitySlots.clear();
            interviewAvailabilitySlots.addAll(test);
        }

        return interviewAvailabilitySlots;
    }

    private List<AvailabilitySlot> getCandidateCommonDayAvailabilitySlots(Set<LocalDate> commonDays,
                                                                          List<AvailabilitySlot> candidateAllAvailabilitySlots) {
        List<AvailabilitySlot> candidateCommonAvailabilitySlots = new ArrayList<>();

        for (AvailabilitySlot availabilitySlot : candidateAllAvailabilitySlots) {
            for (LocalDate localDate : commonDays) {
                if (availabilitySlot.getDay().compareTo(localDate) == 0) {
                    candidateCommonAvailabilitySlots.add(availabilitySlot);
                }
            }
        }

        return candidateCommonAvailabilitySlots;
    }

    private List<AvailabilitySlot> getInterviewerCommonDayAvailabilitySlots(Set<LocalDate> commonDays,
                                                                            List<AvailabilitySlot> interviewerAllAvailabilitySlots) {
        List<AvailabilitySlot> interviewerCommonAvailabilitySlots = new ArrayList<>();

        for (AvailabilitySlot availabilitySlot : interviewerAllAvailabilitySlots) {
            for (LocalDate localDate : commonDays) {
                if (availabilitySlot.getDay().compareTo(localDate) == 0) {
                    interviewerCommonAvailabilitySlots.add(availabilitySlot);
                }
            }
        }

        return interviewerCommonAvailabilitySlots;
    }

    private List<AvailabilitySlot> getFinalAvailabilitySlotsOneInterviewer(
            List<AvailabilitySlot> candidateCommonAvailabilitySlots,
            List<AvailabilitySlot> interviewerCommonAvailabilitySlots) {
        List<AvailabilitySlot> availabilitySlots = calculateInterviewAvailabilitySlotsOverlap(
                candidateCommonAvailabilitySlots, interviewerCommonAvailabilitySlots);

        return availabilitySlots;
    }

    private List<AvailabilitySlot> calculateInterviewAvailabilitySlotsOverlap(
            List<AvailabilitySlot> firstAvailabilitySlots,
            List<AvailabilitySlot> secondAvailabilitySlots) {
        List<AvailabilitySlot> interviewAvailabilitySlots = new ArrayList<>();

        for (AvailabilitySlot firstAvailabilitySlot : firstAvailabilitySlots) {
            for (AvailabilitySlot secondAvailabilitySlot : secondAvailabilitySlots) {
                LocalDate firstAvailabilitySlotDay = firstAvailabilitySlot.getDay();
                LocalDate secondAvailabilitySlotDay = secondAvailabilitySlot.getDay();

                if (firstAvailabilitySlotDay.compareTo(secondAvailabilitySlotDay)
                    == 0) {
                    List<TimeSlot> firstTimeSlots = firstAvailabilitySlot.getTimeSlotList();
                    List<TimeSlot> secondTimeSlots = secondAvailabilitySlot.getTimeSlotList();

                    List<TimeSlot> overlappingTimeSlots = new ArrayList<>();

                    for (TimeSlot firstTimeSlot : firstTimeSlots) {
                        for (TimeSlot secondTimeSlot : secondTimeSlots) {
                            LocalTime firstFrom = firstTimeSlot.getFrom();
                            LocalTime firstTo = firstTimeSlot.getTo();
                            LocalTime secondFrom = secondTimeSlot.getFrom();
                            LocalTime secondTo = secondTimeSlot.getTo();

                            TimeSlot overlappingTimeSlot = getOverlappingTimeSlot(firstFrom, firstTo, secondFrom,
                                                                                  secondTo);

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

                        interviewAvailabilitySlots.add(availabilitySlot);
                    }
                }
            }
        }

        return interviewAvailabilitySlots;
    }

    private TimeSlot getOverlappingTimeSlot(LocalTime candidateFrom, LocalTime candidateTo, LocalTime interviewerFrom,
                                            LocalTime interviewerTo) {
        TimeSlot timeSlot = new TimeSlot();
        LocalTime newFrom;
        LocalTime newTo;

        if (candidateFrom.isBefore(interviewerTo) && interviewerFrom.isBefore(candidateTo)) {
            if (candidateFrom.isBefore(interviewerFrom)) {
                newFrom = interviewerFrom;
            } else {
                newFrom = candidateFrom;
            }

            if (candidateTo.isBefore(interviewerTo)) {
                newTo = candidateTo;
            } else {
                newTo = interviewerTo;
            }

            timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(newFrom).withTo(newTo).build();
        }

        return timeSlot;
    }
}
