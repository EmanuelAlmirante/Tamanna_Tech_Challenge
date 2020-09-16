package app.model.interviewer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "interviewer")
public class InterviewerModel {
    @Id
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "interviewerModel", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<InterviewerAvailabilityModel> interviewerAvailabilityModelList;

    public InterviewerModel() {
    }

    public InterviewerModel(String name) {
        this.name = name;
    }

    public InterviewerModel(Builder builder) {
        this.name = builder.name;
        this.interviewerAvailabilityModelList = builder.interviewerAvailabilityModelList;
    }

    public String getName() {
        return name;
    }

    public List<InterviewerAvailabilityModel> getInterviewerAvailabilityModelList() {
        return interviewerAvailabilityModelList;
    }

    public static class Builder {
        private String name;
        private List<InterviewerAvailabilityModel> interviewerAvailabilityModelList;

        public static Builder interviewerModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Builder withInterviewerAvailabilityModelList(
                List<InterviewerAvailabilityModel> interviewerAvailabilityModelList) {
            this.interviewerAvailabilityModelList = interviewerAvailabilityModelList;

            return this;
        }

        public InterviewerModel build() {
            return new InterviewerModel(this);
        }
    }
}
