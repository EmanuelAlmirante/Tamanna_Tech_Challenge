package app.model.interviewslots;

import app.model.candidate.CandidateModel;
import app.model.interviewer.InterviewerModel;
import app.model.utils.AvailabilitySlot;

import java.util.List;

public class InterviewSlotsReturnModel {
    private final List<InterviewerModel> interviewersList;
    private final CandidateModel candidate;
    private final List<AvailabilitySlot> interviewAvailabilitySlotList;

    public InterviewSlotsReturnModel(Builder builder) {
        this.interviewersList = builder.interviewersList;
        this.candidate = builder.candidate;
        this.interviewAvailabilitySlotList = builder.interviewAvailabilitySlotList;
    }

    public List<InterviewerModel> getInterviewersList() {
        return interviewersList;
    }

    public CandidateModel getCandidate() {
        return candidate;
    }

    public List<AvailabilitySlot> getInterviewAvailabilitySlotList() {
        return interviewAvailabilitySlotList;
    }

    public static class Builder {
        private List<InterviewerModel> interviewersList;
        private CandidateModel candidate;
        private List<AvailabilitySlot> interviewAvailabilitySlotList;

        public static Builder interviewSlotsReturnModelWith() {
            return new Builder();
        }

        public Builder withInterviewerNameList(List<InterviewerModel> interviewersList) {
            this.interviewersList = interviewersList;

            return this;
        }

        public Builder withCandidateName(CandidateModel candidate) {
            this.candidate = candidate;

            return this;
        }

        public Builder withInterviewAvailabilitySlotList(List<AvailabilitySlot> interviewAvailabilitySlotList) {
            this.interviewAvailabilitySlotList = interviewAvailabilitySlotList;

            return this;
        }

        public InterviewSlotsReturnModel build() {
            return new InterviewSlotsReturnModel(this);
        }
    }
}
