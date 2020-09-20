package app.model.interviewslots;

import app.model.utils.AvailabilitySlot;

import java.util.List;

public class InterviewSlotsReturnModel {
    private final String candidateName;
    private final List<String> interviewersNames;
    private final List<AvailabilitySlot> interviewAvailabilitySlotList;

    public InterviewSlotsReturnModel(Builder builder) {
        this.candidateName = builder.candidateName;
        this.interviewersNames = builder.interviewersNames;
        this.interviewAvailabilitySlotList = builder.interviewAvailabilitySlotList;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public List<String> getInterviewersNames() {
        return interviewersNames;
    }

    public List<AvailabilitySlot> getInterviewAvailabilitySlotList() {
        return interviewAvailabilitySlotList;
    }

    public static class Builder {
        private String candidateName;
        private List<String> interviewersNames;
        private List<AvailabilitySlot> interviewAvailabilitySlotList;

        public static Builder interviewSlotsReturnModelWith() {
            return new Builder();
        }

        public Builder withCandidateName(String candidateName) {
            this.candidateName = candidateName;

            return this;
        }

        public Builder withInterviewerNameList(List<String> interviewersNames) {
            this.interviewersNames = interviewersNames;

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
