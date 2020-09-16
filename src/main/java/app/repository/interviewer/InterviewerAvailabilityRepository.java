package app.repository.interviewer;

import app.model.interviewer.InterviewerAvailabilityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewerAvailabilityRepository extends JpaRepository<InterviewerAvailabilityModel, Long> {
    @Query("select ia from InterviewerAvailabilityModel ia where ia.interviewerModel.name = :name")
    InterviewerAvailabilityModel getInterviewerAvailabilityByInterviewerName(String name);
}
