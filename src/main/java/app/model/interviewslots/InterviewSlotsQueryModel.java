package app.model.interviewslots;

import app.model.candidate.CandidateModel;
import app.model.interviewer.InterviewerModel;

import java.util.List;

public class InterviewSlotsQueryModel {
    private final String candidateName;
    private final List<String> interviewersNames;

    public InterviewSlotsQueryModel(String candidateName, List<String> interviewersNames) {
        this.candidateName = candidateName;
        this.interviewersNames = interviewersNames;
    }

    public InterviewSlotsQueryModel(Builder builder) {
        this.candidateName = builder.candidateName;
        this.interviewersNames = builder.interviewersNames;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public List<String> getInterviewersNames() {
        return interviewersNames;
    }

    public static class Builder {
        private String candidateName;
        private List<String> interviewersNames;

        public static Builder interviewSlotsQueryModelWith() {
            return new Builder();
        }

        public Builder withCandidateName(String candidateName) {
            this.candidateName = candidateName;

            return this;
        }

        public Builder withInterviewersNames(List<String> interviewersNames) {
            this.interviewersNames = interviewersNames;

            return this;
        }

        public InterviewSlotsQueryModel build() {
            return new InterviewSlotsQueryModel(this);
        }
    }
}
