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

        String firstInterviewerName = interviewersNames.get(0);

        InterviewerAvailabilityModel firstInterviewerAvailability = getInterviewerAvailability(firstInterviewerName);

        InterviewerAvailabilityModel secondInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith().build();

        if (interviewersNames.size() > 1) {
            String secondInterviewerName = interviewersNames.get(1);
            secondInterviewerAvailability = getInterviewerAvailability(secondInterviewerName);
        }

        List<AvailabilitySlot> commonAvailabilitySlots;

        if (interviewSlotsQueryModel.getInterviewersNames().size() == 1) {
            List<LocalDate> commonDaysOneInterviewer = getAvailabilitiesCommonDaysOneInterviewer(candidateAvailability,
                                                                                                 firstInterviewerAvailability);

            commonAvailabilitySlots = getCommonAvailabilitySlotsOneInterviewer(commonDaysOneInterviewer,
                                                                               candidateAvailability,
                                                                               firstInterviewerAvailability);
        } else {
            List<LocalDate> commonDaysTwoInterviewers = getAvailabilitiesCommonDaysTwoInterviewers(
                    candidateAvailability, firstInterviewerAvailability,
                    secondInterviewerAvailability);

            commonAvailabilitySlots = getCommonAvailabilitySlotsTwoInterviewers(commonDaysTwoInterviewers,
                                                                                candidateAvailability,
                                                                                firstInterviewerAvailability,
                                                                                secondInterviewerAvailability);
        }

        return commonAvailabilitySlots;
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

    private List<LocalDate> getAvailabilitiesCommonDaysOneInterviewer(CandidateAvailabilityModel candidateAvailability,
                                                                      InterviewerAvailabilityModel interviewerAvailability) {
        List<AvailabilitySlot> candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> interviewerAvailabilitySlots = interviewerAvailability.getAvailabilitySlotList();

        List<LocalDate> candidateAvailabilityDays = candidateAvailabilitySlots.stream().map(AvailabilitySlot::getDay)
                                                                              .collect(Collectors.toList());

        List<LocalDate> interviewerAvailabilityDays = interviewerAvailabilitySlots.stream().map(
                AvailabilitySlot::getDay).collect(Collectors.toList());

        List<LocalDate> commonDays = new ArrayList<>();

        for (LocalDate candidateAvailabilityDay : candidateAvailabilityDays) {
            for (LocalDate interviewerAvailabilityDay : interviewerAvailabilityDays) {
                if (candidateAvailabilityDay.compareTo(interviewerAvailabilityDay) == 0) {
                    commonDays.add(candidateAvailabilityDay);
                }
            }
        }

        return commonDays;
    }

    private List<LocalDate> getAvailabilitiesCommonDaysTwoInterviewers(CandidateAvailabilityModel candidateAvailability,
                                                                       InterviewerAvailabilityModel firstInterviewerAvailability,
                                                                       InterviewerAvailabilityModel secondInterviewerAvailability) {
        List<AvailabilitySlot> candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> firstInterviewerAvailabilitySlots =
                firstInterviewerAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> secondInterviewerAvailabilitySlots =
                secondInterviewerAvailability.getAvailabilitySlotList();

        List<LocalDate> candidateAvailabilityDays = candidateAvailabilitySlots.stream().map(AvailabilitySlot::getDay)
                                                                              .collect(Collectors.toList());

        List<LocalDate> firstInterviewerAvailabilityDays = firstInterviewerAvailabilitySlots.stream().map(
                AvailabilitySlot::getDay).collect(Collectors.toList());

        List<LocalDate> secondInterviewerAvailabilityDays = secondInterviewerAvailabilitySlots.stream().map(
                AvailabilitySlot::getDay).collect(Collectors.toList());

        List<LocalDate> commonDays = new ArrayList<>();

        for (LocalDate candidateAvailabilityDay : candidateAvailabilityDays) {
            for (LocalDate firstInterviewerAvailabilityDay : firstInterviewerAvailabilityDays) {
                for (LocalDate secondInterviewerAvailabilityDay : secondInterviewerAvailabilityDays) {
                    if (candidateAvailabilityDay.compareTo(firstInterviewerAvailabilityDay) == 0
                        && candidateAvailabilityDay.compareTo(secondInterviewerAvailabilityDay) == 0) {
                        commonDays.add(candidateAvailabilityDay);
                    }
                }
            }
        }

        return commonDays;
    }

    private List<AvailabilitySlot> getCommonAvailabilitySlotsOneInterviewer(List<LocalDate> commonDays,
                                                                            CandidateAvailabilityModel candidateAvailability,
                                                                            InterviewerAvailabilityModel interviewerAvailability) {
        List<AvailabilitySlot> candidateAllAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> candidateCommonAvailabilitySlots = getCandidateCommonDayAvailabilitySlots(commonDays,
                                                                                                         candidateAllAvailabilitySlots);

        List<AvailabilitySlot> interviewerAllAvailabilitySlots =
                interviewerAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> interviewerCommonAvailabilitySlots = getInterviewerCommonDayAvailabilitySlots(
                commonDays, interviewerAllAvailabilitySlots);

        List<AvailabilitySlot> commonAvailabilitySlots = getFinalAvailabilitySlotsOneInterviewer(
                candidateCommonAvailabilitySlots, interviewerCommonAvailabilitySlots);

        return commonAvailabilitySlots;
    }

    private List<AvailabilitySlot> getCommonAvailabilitySlotsTwoInterviewers(List<LocalDate> commonDays,
                                                                             CandidateAvailabilityModel candidateAvailability,
                                                                             InterviewerAvailabilityModel firstInterviewerAvailability,
                                                                             InterviewerAvailabilityModel secondInterviewerAvailability) {
        List<AvailabilitySlot> candidateAllAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> candidateCommonAvailabilitySlots = getCandidateCommonDayAvailabilitySlots(commonDays,
                                                                                                         candidateAllAvailabilitySlots);

        List<AvailabilitySlot> firstInterviewerAllAvailabilitySlots =
                firstInterviewerAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> firstInterviewerCommonAvailabilitySlots = getInterviewerCommonDayAvailabilitySlots(
                commonDays, firstInterviewerAllAvailabilitySlots);

        List<AvailabilitySlot> secondInterviewerAllAvailabilitySlots =
                secondInterviewerAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> secondInterviewerCommonAvailabilitySlots = getInterviewerCommonDayAvailabilitySlots(
                commonDays, secondInterviewerAllAvailabilitySlots);

        List<AvailabilitySlot> commonAvailabilitySlots = getFinalAvailabilitySlotsTwoInterviewers(
                candidateCommonAvailabilitySlots, firstInterviewerCommonAvailabilitySlots,
                secondInterviewerCommonAvailabilitySlots);

        return commonAvailabilitySlots;
    }

    private List<AvailabilitySlot> getCandidateCommonDayAvailabilitySlots(List<LocalDate> commonDays,
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

    private List<AvailabilitySlot> getInterviewerCommonDayAvailabilitySlots(List<LocalDate> commonDays,
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
        List<AvailabilitySlot> availabilitySlots = calculateAvailabilityTimeSlotsOverlap(
                candidateCommonAvailabilitySlots, interviewerCommonAvailabilitySlots);

        return availabilitySlots;
    }

    private List<AvailabilitySlot> getFinalAvailabilitySlotsTwoInterviewers(
            List<AvailabilitySlot> candidateCommonAvailabilitySlots,
            List<AvailabilitySlot> firstInterviewerCommonAvailabilitySlots,
            List<AvailabilitySlot> secondInterviewerCommonAvailabilitySlots) {
        List<AvailabilitySlot> interviewersCommonAvailabilitySlots = calculateAvailabilityTimeSlotsOverlap(
                firstInterviewerCommonAvailabilitySlots, secondInterviewerCommonAvailabilitySlots);
        List<AvailabilitySlot> candidateAndInterviewersCommonAvailabilitySlots = calculateAvailabilityTimeSlotsOverlap(
                candidateCommonAvailabilitySlots, interviewersCommonAvailabilitySlots);

        return candidateAndInterviewersCommonAvailabilitySlots;
    }

    private List<AvailabilitySlot> calculateAvailabilityTimeSlotsOverlap(List<AvailabilitySlot> firstAvailabilitySlots,
                                                                         List<AvailabilitySlot> secondAvailabilitySlots) {
        List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

        for (AvailabilitySlot firstAvailabilitySlot : firstAvailabilitySlots) {
            for (AvailabilitySlot secondAvailabilitySlot : secondAvailabilitySlots) {
                if (firstAvailabilitySlot.getDay().compareTo(secondAvailabilitySlot.getDay())
                    == 0) {
                    List<TimeSlot> firstTimeSlots = firstAvailabilitySlot.getTimeSlotList();
                    List<TimeSlot> secondTimeSlots = secondAvailabilitySlot.getTimeSlotList();

                    List<TimeSlot> timeSlots = new ArrayList<>();

                    for (TimeSlot firstTimeSlot : firstTimeSlots) {
                        for (TimeSlot secondTimeSlot : secondTimeSlots) {
                            LocalTime firstFrom = firstTimeSlot.getFrom();
                            LocalTime firstTo = firstTimeSlot.getTo();
                            LocalTime secondFrom = secondTimeSlot.getFrom();
                            LocalTime secondTo = secondTimeSlot.getTo();

                            TimeSlot newTimeSlot = overlappingTimeSlot(firstFrom, firstTo, secondFrom,
                                                                       secondTo);

                            if (newTimeSlot.getFrom() != null && newTimeSlot.getTo() != null) {
                                timeSlots.add(newTimeSlot);
                            }
                        }
                    }

                    if (!timeSlots.isEmpty()) {
                        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith()
                                                                                    .withDay(
                                                                                            secondAvailabilitySlot
                                                                                                    .getDay())
                                                                                    .withTimeSlotList(timeSlots)
                                                                                    .build();

                        availabilitySlots.add(availabilitySlot);
                    }
                }
            }
        }

        return availabilitySlots;
    }

    private TimeSlot overlappingTimeSlot(LocalTime candidateFrom, LocalTime candidateTo, LocalTime interviewerFrom,
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
