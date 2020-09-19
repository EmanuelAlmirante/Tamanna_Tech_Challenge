package app.model.interviewslots;

import app.model.candidate.CandidateModel;
import app.model.interviewer.InterviewerModel;

import java.util.List;

public class InterviewSlotsQueryModel {
    private final List<InterviewerModel> interviewersList;
    private final CandidateModel candidate;

    public InterviewSlotsQueryModel(List<InterviewerModel> interviewersList, CandidateModel candidate) {
        this.interviewersList = interviewersList;
        this.candidate = candidate;
    }

    public InterviewSlotsQueryModel(Builder builder) {
        this.interviewersList = builder.interviewerNameList;
        this.candidate = builder.candidateName;
    }

    public List<InterviewerModel> getInterviewersList() {
        return interviewersList;
    }

    public CandidateModel getCandidate() {
        return candidate;
    }

    public static class Builder {
        private List<InterviewerModel> interviewerNameList;
        private CandidateModel candidateName;

        public static Builder interviewSlotsQueryModelWith() {
            return new Builder();
        }

        public Builder withInterviewerNameList(List<InterviewerModel> interviewerNameList) {
            this.interviewerNameList = interviewerNameList;

            return this;
        }

        public Builder withCandidateName(CandidateModel candidateName) {
            this.candidateName = candidateName;

            return this;
        }

        public InterviewSlotsQueryModel build() {
            return new InterviewSlotsQueryModel(this);
        }
    }
}
