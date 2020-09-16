package app.model.candidate;

import app.model.AvailabilitySlot;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "candidate_availability")
public class CandidateAvailabilityModel {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonProperty("candidateName")
    @JoinColumn(name = "name", nullable = false)
    private CandidateModel candidateModel;

    @NotNull
    @ElementCollection
    private List<AvailabilitySlot> availabilitySlot;

    public CandidateAvailabilityModel() {
    }

    public CandidateAvailabilityModel(Builder builder) {
        this.candidateModel = builder.candidateModel;
        this.availabilitySlot = builder.availabilitySlot;
    }

    public Long getId() {
        return id;
    }

    public CandidateModel getCandidateModel() {
        return candidateModel;
    }

    public List<AvailabilitySlot> getAvailabilitySlot() {
        return availabilitySlot;
    }

    public static class Builder {
        private CandidateModel candidateModel;
        private List<AvailabilitySlot> availabilitySlot;

        public static Builder candidateAvailabilityModelWith() {
            return new Builder();
        }

        public Builder withCandidateModel(CandidateModel candidateModel) {
            this.candidateModel = candidateModel;

            return this;
        }

        public Builder withAvailabilitySlot(List<AvailabilitySlot> availabilitySlot) {
            this.availabilitySlot = availabilitySlot;

            return this;
        }

        public CandidateAvailabilityModel build() {
            return new CandidateAvailabilityModel(this);
        }
    }
}
