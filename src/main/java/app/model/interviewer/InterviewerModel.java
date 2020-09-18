package app.model.interviewer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "interviewer")
public class InterviewerModel {
    @Id
    private String name;

    @JsonIgnore
    @OneToOne(mappedBy = "interviewerModel", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private InterviewerAvailabilityModel interviewerAvailabilityModel;

    public InterviewerModel() {
    }

    public InterviewerModel(String name) {
        this.name = name;
    }

    public InterviewerModel(Builder builder) {
        this.name = builder.name;
        this.interviewerAvailabilityModel = builder.interviewerAvailabilityModel;
    }

    public String getName() {
        return name;
    }

    public InterviewerAvailabilityModel getInterviewerAvailabilityModel() {
        return interviewerAvailabilityModel;
    }

    public static class Builder {
        private String name;
        private InterviewerAvailabilityModel interviewerAvailabilityModel;

        public static Builder interviewerModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Builder withInterviewerAvailabilityModel(InterviewerAvailabilityModel interviewerAvailabilityModel) {
            this.interviewerAvailabilityModel = interviewerAvailabilityModel;

            return this;
        }

        public InterviewerModel build() {
            return new InterviewerModel(this);
        }
    }
}
