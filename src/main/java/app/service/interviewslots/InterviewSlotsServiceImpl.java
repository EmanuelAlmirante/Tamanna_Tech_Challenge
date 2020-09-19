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

        CandidateModel candidate = interviewSlotsQueryModel.getCandidate();
        List<InterviewerModel> interviewers = interviewSlotsQueryModel.getInterviewersList();

        List<AvailabilitySlot> availabilitySlots = getCommonAvailabilitySlots(interviewSlotsQueryModel);

        InterviewSlotsReturnModel interviewSlotsReturnModel =
                InterviewSlotsReturnModel.Builder.interviewSlotsReturnModelWith()
                                                 .withCandidateName(candidate)
                                                 .withInterviewerNameList(interviewers)
                                                 .withInterviewAvailabilitySlotList(availabilitySlots)
                                                 .build();

        return interviewSlotsReturnModel;
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

    private List<AvailabilitySlot> getCommonAvailabilitySlots(InterviewSlotsQueryModel interviewSlotsQueryModel) {
        CandidateModel candidate = interviewSlotsQueryModel.getCandidate();
        CandidateAvailabilityModel candidateAvailability = getCandidateAvailability(candidate);

        InterviewerModel firstInterviewer = interviewSlotsQueryModel.getInterviewersList().get(0);
        InterviewerAvailabilityModel firstInterviewerAvailability = getInterviewerAvailability(firstInterviewer);

        InterviewerAvailabilityModel secondInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith().build();

        if (interviewSlotsQueryModel.getInterviewersList().size() > 1) {
            InterviewerModel secondInterviewer = interviewSlotsQueryModel.getInterviewersList().get(1);
            secondInterviewerAvailability = getInterviewerAvailability(secondInterviewer);
        }

        List<AvailabilitySlot> commonAvailabilitySlots;

        if (interviewSlotsQueryModel.getInterviewersList().size() == 1) {
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

    private CandidateAvailabilityModel getCandidateAvailability(CandidateModel candidate) {
        String candidateName = candidate.getName();

        CandidateAvailabilityModel candidateAvailability =
                candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName);

        if (candidateAvailability == null) {
            throw new BusinessException("Candidate has no availability defined!", candidateName);
        }

        return candidateAvailability;
    }

    private InterviewerAvailabilityModel getInterviewerAvailability(InterviewerModel interviewer) {
        String interviewerName = interviewer.getName();

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

        List<AvailabilitySlot> commonAvailabilitySlots = new ArrayList<>();

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
        List<TimeSlot> timeSlots = new ArrayList<>();
        List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

        for (AvailabilitySlot candidateCommonAvailabilitySlot : candidateCommonAvailabilitySlots) {
            for (AvailabilitySlot interviewerCommonAvailabilitySlot : interviewerCommonAvailabilitySlots) {
                if (candidateCommonAvailabilitySlot.getDay().compareTo(interviewerCommonAvailabilitySlot.getDay())
                    == 0) {
                    List<TimeSlot> candidateTimeSlots = candidateCommonAvailabilitySlot.getTimeSlotList();
                    List<TimeSlot> interviewerTimeSlots = interviewerCommonAvailabilitySlot.getTimeSlotList();

                    for (TimeSlot candidateTimeSlot : candidateTimeSlots) {
                        for (TimeSlot interviewerTimeSlot : interviewerTimeSlots) {
                            LocalTime candidateFrom = candidateTimeSlot.getFrom();
                            LocalTime candidateTo = candidateTimeSlot.getTo();
                            LocalTime interviewerFrom = interviewerTimeSlot.getFrom();
                            LocalTime interviewerTo = interviewerTimeSlot.getTo();

                            TimeSlot newTimeSlot = timeOverlapping(candidateFrom, candidateTo, interviewerFrom,
                                                                   interviewerTo);

                            timeSlots.add(newTimeSlot);
                        }
                    }

                    AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith()
                                                                                .withDay(candidateCommonAvailabilitySlot
                                                                                                 .getDay())
                                                                                .withTimeSlotList(timeSlots)
                                                                                .build();

                    availabilitySlots.add(availabilitySlot);
                }
            }
        }

        return availabilitySlots;
    }

    // TODO: 19/09/20 change method name
    private List<AvailabilitySlot> teste2(List<AvailabilitySlot> candidateCommonAvailabilitySlots,
                                          List<AvailabilitySlot> firstInterviewerCommonAvailabilitySlots,
                                          List<AvailabilitySlot> secondInterviewerCommonAvailabilitySlots) {

        return null;
    }

    private TimeSlot timeOverlapping(LocalTime candidateFrom, LocalTime candidateTo, LocalTime interviewerFrom,
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
