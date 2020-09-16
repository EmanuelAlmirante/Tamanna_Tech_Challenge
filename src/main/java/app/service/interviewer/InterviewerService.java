package app.service.interviewer;

import app.model.interviewer.InterviewerModel;

import java.util.List;
import java.util.Optional;

public interface InterviewerService {
    InterviewerModel createInterviewer(InterviewerModel interviewerModel);

    List<InterviewerModel> getAllInterviewers();

    Optional<InterviewerModel> getInterviewerByName(String name);

    void deleteInterviewerByName(String name);
}
