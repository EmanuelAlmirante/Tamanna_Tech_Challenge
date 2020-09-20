package service.interviewslots;

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
import app.service.interviewslots.InterviewSlotsServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewSlotsServiceImplTests {
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private CandidateAvailabilityRepository candidateAvailabilityRepository;

    @Mock
    private InterviewerRepository interviewerRepository;
    @Mock
    private InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    @InjectMocks
    private InterviewSlotsServiceImpl interviewSlotsServiceImpl;

    private static CandidateModel candidate;
    private static CandidateAvailabilityModel candidateAvailability;

    private static InterviewerModel firstInterviewer;
    private static InterviewerAvailabilityModel firstInterviewerAvailability;

    private static InterviewerModel secondInterviewer;
    private static InterviewerAvailabilityModel secondInterviewerAvailability;

    private static InterviewerModel thirdInterviewer;
    private static InterviewerAvailabilityModel thirdInterviewerAvailability;


    @BeforeClass
    public static void setup() {
        // Arrange candidate and candidate availability
        String candidateName = "John Doe";
        candidate = CandidateModel.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot candidateFirstTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(11, 0)).build();
        List<TimeSlot> candidateFirstTimeSlots = Collections.singletonList(candidateFirstTimeSlot);
        AvailabilitySlot candidateFirstAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(candidateFirstTimeSlots).build();

        TimeSlot candidateSecondTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(18, 0)).build();
        List<TimeSlot> candidateSecondTimeSlots = Collections.singletonList(candidateSecondTimeSlot);
        AvailabilitySlot candidateSecondAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 2)).withTimeSlotList(candidateSecondTimeSlots).build();

        List<AvailabilitySlot> candidateAvailabilitySlots = Arrays.asList(candidateFirstAvailabilitySlot,
                                                                          candidateSecondAvailabilitySlot);

        candidateAvailability = CandidateAvailabilityModel.Builder.candidateAvailabilityModelWith()
                                                                  .withCandidateModel(candidate)
                                                                  .withAvailabilitySlotList(candidateAvailabilitySlots)
                                                                  .build();

        // Arrange first interviewer and first interviewer availability
        String firstInterviewerName = "Jane Doe";
        firstInterviewer = InterviewerModel.Builder.interviewerModelWith().withName(firstInterviewerName).build();

        TimeSlot firstInterviewerFirstTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(10, 0)).withTo(
                LocalTime.of(11, 0)).build();
        List<TimeSlot> firstInterviewerFirstTimeSlots = Collections.singletonList(firstInterviewerFirstTimeSlot);
        AvailabilitySlot firstInterviewerFirstAvailabilitySlot =
                AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                        LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(firstInterviewerFirstTimeSlots).build();

        TimeSlot firstInterviewerSecondTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(10, 0)).build();
        TimeSlot firstInterviewerThirdTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(12, 0)).withTo(
                LocalTime.of(14, 0)).build();
        List<TimeSlot> firstInterviewerSecondAndThirdTimeSlots = Arrays.asList(firstInterviewerSecondTimeSlot,
                                                                               firstInterviewerThirdTimeSlot);
        AvailabilitySlot firstInterviewerSecondAvailabilitySlot =
                AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                        LocalDate.of(2014, Month.JANUARY, 2)).withTimeSlotList(firstInterviewerSecondAndThirdTimeSlots)
                                        .build();

        List<AvailabilitySlot> firstInterviewerAvailabilitySlots = Arrays.asList(firstInterviewerFirstAvailabilitySlot,
                                                                                 firstInterviewerSecondAvailabilitySlot);

        firstInterviewerAvailability = InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                                           .withInterviewerModel(firstInterviewer)
                                                                           .withAvailabilitySlotList(
                                                                                   firstInterviewerAvailabilitySlots)
                                                                           .build();

        // Arrange second interviewer and second interviewer availability
        String secondInterviewerName = "John Smith";
        secondInterviewer = InterviewerModel.Builder.interviewerModelWith().withName(secondInterviewerName).build();

        TimeSlot secondInterviewerFirstTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(8, 0)).withTo(
                LocalTime.of(14, 0)).build();
        List<TimeSlot> secondInterviewerFirstTimeSlots = Collections.singletonList(secondInterviewerFirstTimeSlot);
        AvailabilitySlot secondInterviewerFirstAvailabilitySlot =
                AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                        LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(secondInterviewerFirstTimeSlots).build();

        TimeSlot secondInterviewerSecondTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(8, 0)).withTo(
                LocalTime.of(14, 0)).build();
        List<TimeSlot> secondInterviewerSecondTimeSlots = Collections.singletonList(secondInterviewerSecondTimeSlot);
        AvailabilitySlot secondInterviewerSecondAvailabilitySlot =
                AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                        LocalDate.of(2014, Month.JANUARY, 2)).withTimeSlotList(secondInterviewerSecondTimeSlots)
                                        .build();

        List<AvailabilitySlot> secondInterviewerAvailabilitySlots = Arrays.asList(
                secondInterviewerFirstAvailabilitySlot,
                secondInterviewerSecondAvailabilitySlot);

        secondInterviewerAvailability = InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                                            .withInterviewerModel(secondInterviewer)
                                                                            .withAvailabilitySlotList(
                                                                                    secondInterviewerAvailabilitySlots)
                                                                            .build();

        // Arrange third interviewer and third interviewer availability
        String thirdInterviewerName = "Debora Smith";
        thirdInterviewer = InterviewerModel.Builder.interviewerModelWith().withName(thirdInterviewerName).build();

        TimeSlot thirdInterviewerFirstTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(15, 0)).withTo(
                LocalTime.of(18, 0)).build();
        List<TimeSlot> thirdInterviewerFirstTimeSlots = Collections.singletonList(thirdInterviewerFirstTimeSlot);
        AvailabilitySlot thirdInterviewerFirstAvailabilitySlot =
                AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                        LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(thirdInterviewerFirstTimeSlots).build();

        TimeSlot thirdInterviewerSecondTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(8, 0)).withTo(
                LocalTime.of(18, 0)).build();
        List<TimeSlot> thirdInterviewerSecondTimeSlots = Collections.singletonList(thirdInterviewerSecondTimeSlot);
        AvailabilitySlot thirdInterviewerSecondAvailabilitySlot =
                AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                        LocalDate.of(2014, Month.JANUARY, 5)).withTimeSlotList(thirdInterviewerSecondTimeSlots).build();

        List<AvailabilitySlot> thirdInterviewerAvailabilitySlots = Arrays.asList(thirdInterviewerFirstAvailabilitySlot,
                                                                                 thirdInterviewerSecondAvailabilitySlot);

        thirdInterviewerAvailability = InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                                           .withInterviewerModel(thirdInterviewer)
                                                                           .withAvailabilitySlotList(
                                                                                   thirdInterviewerAvailabilitySlots)
                                                                           .build();
    }

    @Test
    public void getInterviewSlotsFromOneCandidateAndOneInterviewerSuccessfully() {
        // Arrange
        String candidateName = candidate.getName();
        String firstInterviewerName = firstInterviewer.getName();
        List<String> interviewersNames = Collections.singletonList(firstInterviewerName);

        InterviewSlotsQueryModel interviewSlotsQuery = InterviewSlotsQueryModel.Builder.interviewSlotsQueryModelWith()
                                                                                       .withCandidateName(candidateName)
                                                                                       .withInterviewersNames(
                                                                                               interviewersNames)
                                                                                       .build();

        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(interviewerRepository.findById(firstInterviewerName)).thenReturn(Optional.of(firstInterviewer));
        when(candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName)).thenReturn(
                candidateAvailability);
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(firstInterviewerName))
                .thenReturn(firstInterviewerAvailability);

        InterviewSlotsReturnModel interviewSlotsReturn = interviewSlotsServiceImpl.getInterviewSlots(
                interviewSlotsQuery);

        // Assert
        assertNotNull(interviewSlotsReturn);
        assertEquals(candidateName, interviewSlotsReturn.getCandidateName());
        assertThat(interviewSlotsReturn.getInterviewersNames(), containsInAnyOrder(firstInterviewerName));
        assertFalse(interviewSlotsReturn.getInterviewAvailabilitySlotList().isEmpty());
    }

    @Test
    public void getInterviewSlotsFromOneCandidateAndTwoInterviewersSuccessfully() {
        // Arrange
        String candidateName = candidate.getName();
        String firstInterviewerName = firstInterviewer.getName();
        String secondInterviewerName = secondInterviewer.getName();
        List<String> interviewersNames = Arrays.asList(firstInterviewerName, secondInterviewerName);

        InterviewSlotsQueryModel interviewSlotsQuery = InterviewSlotsQueryModel.Builder.interviewSlotsQueryModelWith()
                                                                                       .withCandidateName(candidateName)
                                                                                       .withInterviewersNames(
                                                                                               interviewersNames)
                                                                                       .build();

        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(interviewerRepository.findById(firstInterviewerName)).thenReturn(Optional.of(firstInterviewer));
        when(interviewerRepository.findById(secondInterviewerName)).thenReturn(Optional.of(secondInterviewer));
        when(candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName)).thenReturn(
                candidateAvailability);
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(firstInterviewerName))
                .thenReturn(firstInterviewerAvailability);
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(secondInterviewerName))
                .thenReturn(secondInterviewerAvailability);

        InterviewSlotsReturnModel interviewSlotsReturn = interviewSlotsServiceImpl.getInterviewSlots(
                interviewSlotsQuery);

        // Assert
        assertNotNull(interviewSlotsReturn);
        assertEquals(candidateName, interviewSlotsReturn.getCandidateName());
        assertThat(interviewSlotsReturn.getInterviewersNames(), containsInAnyOrder(firstInterviewerName, secondInterviewerName));
        assertFalse(interviewSlotsReturn.getInterviewAvailabilitySlotList().isEmpty());
    }

    @Test
    public void getInterviewSlotsReturnsEmptyAvailabilitySlotsSuccessfully() {
        // Arrange
        String candidateName = candidate.getName();
        String firstInterviewerName = firstInterviewer.getName();
        String secondInterviewerName = secondInterviewer.getName();
        String thirdInterviewerName = thirdInterviewer.getName();
        List<String> interviewersNames = Arrays.asList(firstInterviewerName, secondInterviewerName,
                                                       thirdInterviewerName);

        InterviewSlotsQueryModel interviewSlotsQuery = InterviewSlotsQueryModel.Builder.interviewSlotsQueryModelWith()
                                                                                       .withCandidateName(candidateName)
                                                                                       .withInterviewersNames(
                                                                                               interviewersNames)
                                                                                       .build();

        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(interviewerRepository.findById(firstInterviewerName)).thenReturn(Optional.of(firstInterviewer));
        when(interviewerRepository.findById(secondInterviewerName)).thenReturn(Optional.of(secondInterviewer));
        when(interviewerRepository.findById(thirdInterviewerName)).thenReturn(Optional.of(thirdInterviewer));
        when(candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName)).thenReturn(
                candidateAvailability);
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(firstInterviewerName))
                .thenReturn(firstInterviewerAvailability);
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(secondInterviewerName))
                .thenReturn(secondInterviewerAvailability);
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(thirdInterviewerName))
                .thenReturn(thirdInterviewerAvailability);

        InterviewSlotsReturnModel interviewSlotsReturn = interviewSlotsServiceImpl.getInterviewSlots(
                interviewSlotsQuery);

        // Assert
        assertNotNull(interviewSlotsReturn);
        assertEquals(candidateName, interviewSlotsReturn.getCandidateName());
        assertThat(interviewSlotsReturn.getInterviewersNames(),
                   containsInAnyOrder(firstInterviewerName, secondInterviewerName, thirdInterviewerName));
        assertTrue(interviewSlotsReturn.getInterviewAvailabilitySlotList().isEmpty());
    }

    @Test(expected = BusinessException.class)
    public void getInterviewSlotsWithoutCandidateCreatedFails() {
        // Arrange
        String candidateName = "Sam Wheeler";
        String interviewerName = firstInterviewer.getName();
        List<String> interviewersNames = Collections.singletonList(interviewerName);

        InterviewSlotsQueryModel interviewSlotsQuery = InterviewSlotsQueryModel.Builder.interviewSlotsQueryModelWith()
                                                                                       .withCandidateName(candidateName)
                                                                                       .withInterviewersNames(
                                                                                               interviewersNames)
                                                                                       .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.empty());

        try {
            interviewSlotsServiceImpl.getInterviewSlots(interviewSlotsQuery);
        } catch (BusinessException be) {
            String exceptionMessage = "Candidate does not exist!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of getting interview slots without candidate created to was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void getInterviewSlotsWithoutInterviewerCreatedFails() {
        // Arrange
        String candidateName = candidate.getName();
        String interviewerName = "Sam Wheeler";
        List<String> interviewersNames = Collections.singletonList(interviewerName);

        InterviewSlotsQueryModel interviewSlotsQuery = InterviewSlotsQueryModel.Builder.interviewSlotsQueryModelWith()
                                                                                       .withCandidateName(candidateName)
                                                                                       .withInterviewersNames(
                                                                                               interviewersNames)
                                                                                       .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.empty());

        try {
            interviewSlotsServiceImpl.getInterviewSlots(interviewSlotsQuery);
        } catch (BusinessException be) {
            String exceptionMessage = "Interviewer does not exist!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of getting interview slots without interviewer created to was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void getInterviewSlotsWithoutCandidateAvailabilityCreatedFails() {
        // Arrange
        String candidateName = candidate.getName();
        String interviewerName = firstInterviewer.getName();
        List<String> interviewersNames = Collections.singletonList(interviewerName);

        InterviewSlotsQueryModel interviewSlotsQuery = InterviewSlotsQueryModel.Builder.interviewSlotsQueryModelWith()
                                                                                       .withCandidateName(candidateName)
                                                                                       .withInterviewersNames(
                                                                                               interviewersNames)
                                                                                       .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(firstInterviewer));
        when(candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName)).thenReturn(null);

        try {
            interviewSlotsServiceImpl.getInterviewSlots(interviewSlotsQuery);
        } catch (BusinessException be) {
            String exceptionMessage = "Candidate has no availability defined!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of getting interview slots without candidate availability created to was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void getInterviewSlotsWithoutInterviewerAvailabilityCreatedFails() {
        // Arrange
        String candidateName = candidate.getName();
        String interviewerName = firstInterviewer.getName();
        List<String> interviewersNames = Collections.singletonList(interviewerName);

        InterviewSlotsQueryModel interviewSlotsQuery = InterviewSlotsQueryModel.Builder.interviewSlotsQueryModelWith()
                                                                                       .withCandidateName(candidateName)
                                                                                       .withInterviewersNames(
                                                                                               interviewersNames)
                                                                                       .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(firstInterviewer));
        when(candidateAvailabilityRepository.getCandidateAvailabilityByCandidateName(candidateName)).thenReturn(
                candidateAvailability);
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(interviewerName)).thenReturn(
                null);

        try {
            interviewSlotsServiceImpl.getInterviewSlots(interviewSlotsQuery);
        } catch (BusinessException be) {
            String exceptionMessage = "Interviewer has no availability defined!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of getting interview slots without interviewer availability created to was not "
             + "thrown!");
    }
}
