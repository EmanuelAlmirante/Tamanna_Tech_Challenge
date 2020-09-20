package service.interviewer;

import app.exception.BusinessException;
import app.model.interviewer.InterviewerAvailabilityModel;
import app.model.interviewer.InterviewerModel;
import app.model.utils.AvailabilitySlot;
import app.model.utils.TimeSlot;
import app.repository.interviewer.InterviewerAvailabilityRepository;
import app.repository.interviewer.InterviewerRepository;
import app.service.interviewer.InterviewerServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InterviewerServiceImplTests {
    @Mock
    private InterviewerRepository interviewerRepository;
    @Mock
    private InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    @InjectMocks
    private InterviewerServiceImpl interviewerServiceImpl;

    @Test
    public void createInterviewerWithNameSuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        // Act
        when(interviewerRepository.save(interviewer)).thenReturn(interviewer);

        InterviewerModel savedInterviewer = interviewerServiceImpl.createInterviewer(interviewer);

        // Assert
        assertNotNull(savedInterviewer);
        assertNull(savedInterviewer.getInterviewerAvailabilityModel());
        assertEquals(interviewerName, savedInterviewer.getName());
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerWithEmptyNameFails() {
        // Arrange
        String interviewerName = "";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        // Act && Assert
        try {
            interviewerServiceImpl.createInterviewer(interviewer);
        } catch (BusinessException be) {
            String exceptionMessage = "You must provide a name!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of interviewer with empty name was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerWithNullNameFails() {
        // Arrange
        String interviewerName = null;
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        // Act && Assert
        try {
            interviewerServiceImpl.createInterviewer(interviewer);
        } catch (BusinessException be) {
            String exceptionMessage = "You must provide a name!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of interviewer with null name was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerWithExistingNameFails() {
        // Arrange
        String existingInterviewerName = "John Doe";

        String interviewerToBeCreatedName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(
                interviewerToBeCreatedName)
                                                               .build();

        List<String> existingInterviewers = Collections.singletonList(existingInterviewerName);

        // Act && Assert
        when(interviewerRepository.getAllNames()).thenReturn(existingInterviewers);

        try {
            interviewerServiceImpl.createInterviewer(interviewer);
        } catch (BusinessException be) {
            String exceptionMessage = "Name already exists!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of interviewer with existing name was not thrown!");
    }

    @Test
    public void getAllInterviewersSuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();
        List<InterviewerModel> interviewersToBeReturned = Collections.singletonList(interviewer);

        // Act
        when(interviewerRepository.findAll()).thenReturn(interviewersToBeReturned);

        List<InterviewerModel> interviewersReturned = interviewerServiceImpl.getAllInterviewers();

        // Assert
        assertNotNull(interviewersReturned);
        assertEquals(interviewersToBeReturned.size(), interviewersReturned.size());
        assertEquals(interviewersToBeReturned, interviewersReturned);
    }

    @Test
    public void getInterviewerByNameSuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        // Act
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));

        Optional<InterviewerModel> interviewerReturned = interviewerServiceImpl.getInterviewerByName(interviewerName);

        // Assert
        assertNotNull(interviewerReturned);
        assertEquals(interviewer.getName(), interviewerReturned.get().getName());
        assertEquals(interviewer, interviewerReturned.get());
    }

    @Test
    public void deleteInterviewerByNameSuccessfully() {
        // Arrange
        String interviewerName = "John Doe";

        // Act
        interviewerServiceImpl.deleteInterviewerByName(interviewerName);

        // Assert
        verify(interviewerRepository, times(1)).deleteById(interviewerName);
    }

    @Test
    public void createInterviewerAvailabilityNewAvailabilitySuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();

        // Act
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(interviewerName)).thenReturn(
                null);
        when(interviewerAvailabilityRepository.save(interviewerAvailability)).thenReturn(interviewerAvailability);

        InterviewerAvailabilityModel savedInterviewerAvailability =
                interviewerServiceImpl.createInterviewerAvailability(interviewerAvailability);

        // Assert
        assertNotNull(savedInterviewerAvailability);
        assertEquals(interviewerName, savedInterviewerAvailability.getInterviewerModel().getName());
        assertEquals(availabilitySlots, savedInterviewerAvailability.getAvailabilitySlotList());
    }

    @Test
    public void createInterviewerAvailabilityAddToExistingAvailabilityFromDifferentDaySuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot existingTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(11, 0))
                                                    .build();
        List<TimeSlot> existingTimeSlots = new ArrayList<>();
        existingTimeSlots.add(existingTimeSlot);

        AvailabilitySlot existingAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(existingTimeSlots).build();
        List<AvailabilitySlot> existingAvailabilitySlots = new ArrayList<>();
        existingAvailabilitySlots.add(existingAvailabilitySlot);

        InterviewerAvailabilityModel existingInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(existingAvailabilitySlots)
                                                    .build();

        TimeSlot newTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                               .build();
        List<TimeSlot> newTimeSlots = new ArrayList<>();
        newTimeSlots.add(newTimeSlot);

        AvailabilitySlot newAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 2)).withTimeSlotList(newTimeSlots).build();
        List<AvailabilitySlot> newAvailabilitySlots = new ArrayList<>();
        newAvailabilitySlots.add(newAvailabilitySlot);

        InterviewerAvailabilityModel newInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(newAvailabilitySlots)
                                                    .build();

        List<AvailabilitySlot> returnedAvailabilitySlots = new ArrayList<>();
        returnedAvailabilitySlots.addAll(existingAvailabilitySlots);
        returnedAvailabilitySlots.addAll(newAvailabilitySlots);

        InterviewerAvailabilityModel returnedInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(returnedAvailabilitySlots)
                                                    .build();

        // Act
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(interviewerName)).thenReturn(
                existingInterviewerAvailability);
        when(interviewerAvailabilityRepository.save(any(InterviewerAvailabilityModel.class))).thenReturn(
                returnedInterviewerAvailability);

        InterviewerAvailabilityModel savedInterviewerAvailability =
                interviewerServiceImpl.createInterviewerAvailability(newInterviewerAvailability);

        // Assert
        assertNotNull(savedInterviewerAvailability);
        assertEquals(returnedInterviewerAvailability.getAvailabilitySlotList().size(),
                     savedInterviewerAvailability.getAvailabilitySlotList().size());
        assertEquals(returnedInterviewerAvailability, savedInterviewerAvailability);
    }

    @Test
    public void createInterviewerAvailabilityAddToExistingAvailabilityFromSameDaySuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot existingTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(11, 0))
                                                    .build();
        List<TimeSlot> existingTimeSlots = new ArrayList<>();
        existingTimeSlots.add(existingTimeSlot);

        AvailabilitySlot existingAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(existingTimeSlots).build();
        List<AvailabilitySlot> existingAvailabilitySlots = new ArrayList<>();
        existingAvailabilitySlots.add(existingAvailabilitySlot);

        InterviewerAvailabilityModel existingInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(existingAvailabilitySlots)
                                                    .build();

        TimeSlot newTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(13, 0)).withTo(LocalTime.of(15, 0))
                                               .build();
        List<TimeSlot> newTimeSlots = new ArrayList<>();
        newTimeSlots.add(newTimeSlot);

        AvailabilitySlot newAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(newTimeSlots).build();
        List<AvailabilitySlot> newAvailabilitySlots = new ArrayList<>();
        newAvailabilitySlots.add(newAvailabilitySlot);

        InterviewerAvailabilityModel newInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(newAvailabilitySlots)
                                                    .build();

        List<AvailabilitySlot> returnedAvailabilitySlots = new ArrayList<>();
        returnedAvailabilitySlots.addAll(existingAvailabilitySlots);
        returnedAvailabilitySlots.addAll(newAvailabilitySlots);

        InterviewerAvailabilityModel returnedInterviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(returnedAvailabilitySlots)
                                                    .build();

        // Act
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(interviewerName)).thenReturn(
                existingInterviewerAvailability);
        when(interviewerAvailabilityRepository.save(any(InterviewerAvailabilityModel.class))).thenReturn(
                returnedInterviewerAvailability);

        InterviewerAvailabilityModel savedInterviewerAvailability =
                interviewerServiceImpl.createInterviewerAvailability(newInterviewerAvailability);

        // Assert
        assertNotNull(savedInterviewerAvailability);
        assertEquals(returnedInterviewerAvailability.getAvailabilitySlotList().size(),
                     savedInterviewerAvailability.getAvailabilitySlotList().size());
        assertEquals(returnedInterviewerAvailability, savedInterviewerAvailability);
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerAvailabilityWithNonExistingInterviewerFails() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();

        // Act && Assert
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.empty());

        try {
            interviewerServiceImpl.createInterviewerAvailability(interviewerAvailability);
        } catch (BusinessException be) {
            String exceptionMessage = "Interviewer does not exist!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of interviewer availability with non-existing interviewer was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerAvailabilityWithFromAfterToFails() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(13, 0)).withTo(
                LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();

        // Act && Assert
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));

        try {
            interviewerServiceImpl.createInterviewerAvailability(interviewerAvailability);
        } catch (BusinessException be) {
            String exceptionMessage = "Start hour of slot must be before end hour of slot!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of from after to was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerAvailabilityWithFromEqualsToFails() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(11, 0)).withTo(
                LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();

        // Act && Assert
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));

        try {
            interviewerServiceImpl.createInterviewerAvailability(interviewerAvailability);
        } catch (BusinessException be) {
            String exceptionMessage = "Start hour of slot must be before end hour of slot!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of from equals to was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerAvailabilityWithInvalidFromFails() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(10, 30)).withTo(
                LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();

        // Act && Assert
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));

        try {
            interviewerServiceImpl.createInterviewerAvailability(interviewerAvailability);
        } catch (BusinessException be) {
            String exceptionMessage =
                    "Availability slot must be from the beginning of the hour until the beginning of the next hour!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of invalid from was not thrown!");
    }

    @Test(expected = BusinessException.class)
    public void createInterviewerAvailabilityWithInvalidToFails() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(10, 0)).withTo(
                LocalTime.of(11, 30))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();

        // Act && Assert
        when(interviewerRepository.findById(interviewerName)).thenReturn(Optional.of(interviewer));

        try {
            interviewerServiceImpl.createInterviewerAvailability(interviewerAvailability);
        } catch (BusinessException be) {
            String exceptionMessage =
                    "Availability slot must be from the beginning of the hour until the beginning of the next hour!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of invalid to was not thrown!");
    }

    @Test
    public void getAllInterviewersAvailabilitySuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();

        List<InterviewerAvailabilityModel> interviewersAvailabilitiesToBeReturned = Collections.singletonList(
                interviewerAvailability);

        // Act
        when(interviewerAvailabilityRepository.findAll()).thenReturn(interviewersAvailabilitiesToBeReturned);


        List<InterviewerAvailabilityModel> interviewersAvailabilitiesReturned =
                interviewerServiceImpl.getAllInterviewersAvailability();

        // Assert
        assertNotNull(interviewersAvailabilitiesReturned);
        assertEquals(interviewersAvailabilitiesToBeReturned.size(), interviewersAvailabilitiesReturned.size());
        assertEquals(interviewersAvailabilitiesToBeReturned, interviewersAvailabilitiesReturned);
    }

    @Test
    public void getInterviewerAvailabilityByNameSuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();


        // Act
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(interviewerName)).thenReturn(
                interviewerAvailability);


        InterviewerAvailabilityModel interviewerAvailabilityReturned =
                interviewerServiceImpl.getInterviewerAvailabilityByName(interviewerName);

        // Assert
        assertNotNull(interviewerAvailabilityReturned);
        assertEquals(interviewerAvailability, interviewerAvailabilityReturned);
        assertEquals(interviewerAvailabilityReturned.getInterviewerModel(), interviewerAvailability.getInterviewerModel());
    }

    @Test
    public void deleteInterviewerAvailabilityByNameSuccessfully() {
        // Arrange
        String interviewerName = "John Doe";
        InterviewerModel interviewer = InterviewerModel.Builder.interviewerModelWith().withName(interviewerName)
                                                               .build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        InterviewerAvailabilityModel interviewerAvailability =
                InterviewerAvailabilityModel.Builder.interviewerAvailabilityModelWith()
                                                    .withInterviewerModel(interviewer)
                                                    .withAvailabilitySlotList(availabilitySlots)
                                                    .build();


        // Act
        when(interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(interviewerName)).thenReturn(
                interviewerAvailability);

         interviewerServiceImpl.deleteInterviewerAvailabilityByName(interviewerName);

        // Assert
        verify(interviewerAvailabilityRepository, times(1)).deleteById(any());
    }
}
