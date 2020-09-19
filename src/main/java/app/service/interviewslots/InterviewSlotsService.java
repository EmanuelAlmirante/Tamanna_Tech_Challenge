package app.service.interviewslots;

import app.model.interviewslots.InterviewSlotsQueryModel;
import app.model.interviewslots.InterviewSlotsReturnModel;

public interface InterviewSlotsService {
    InterviewSlotsReturnModel getInterviewSlots(InterviewSlotsQueryModel interviewSlotsQueryModel);
}
