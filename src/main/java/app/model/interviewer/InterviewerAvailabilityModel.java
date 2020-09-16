package app.model.interviewer;

import app.model.AvailabilitySlot;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "interviewer_availability")
public class InterviewerAvailabilityModel {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonProperty("interviewerName")
    @JoinColumn(name = "name", nullable = false)
    private InterviewerModel interviewerModel;

    @NotNull
    @ElementCollection
    private List<AvailabilitySlot> availabilitySlotList;

    public InterviewerAvailabilityModel() {
    }

    public InterviewerAvailabilityModel(Builder builder) {
        this.interviewerModel = builder.interviewerModel;
        this.availabilitySlotList = builder.availabilitySlotList;
    }

    public Long getId() {
        return id;
    }

    public InterviewerModel getInterviewerModel() {
        return interviewerModel;
    }

    public List<AvailabilitySlot> getAvailabilitySlotList() {
        return availabilitySlotList;
    }

    public static class Builder {
        private InterviewerModel interviewerModel;
        private List<AvailabilitySlot> availabilitySlotList;

        public static Builder interviewerAvailabilityModelWith() {
            return new Builder();
        }

        public Builder withInterviewerModel(InterviewerModel interviewerModel) {
            this.interviewerModel = interviewerModel;

            return this;
        }

        public Builder withAvailabilitySlotList(List<AvailabilitySlot> availabilitySlotList) {
            this.availabilitySlotList = availabilitySlotList;

            return this;
        }

        public InterviewerAvailabilityModel build() {
            return new InterviewerAvailabilityModel(this);
        }
    }
}
