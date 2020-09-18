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

    @OneToOne
    @JsonProperty("candidateName")
    @JoinColumn(name = "name", nullable = false)
    private CandidateModel candidateModel;

    @NotNull
    @ElementCollection
    @Column(length = Integer.MAX_VALUE)
    private List<AvailabilitySlot> availabilitySlotList;

    public CandidateAvailabilityModel() {
    }

    public CandidateAvailabilityModel(Builder builder) {
        this.candidateModel = builder.candidateModel;
        this.availabilitySlotList = builder.availabilitySlotList;
    }

    public Long getId() {
        return id;
    }

    public CandidateModel getCandidateModel() {
        return candidateModel;
    }

    public List<AvailabilitySlot> getAvailabilitySlotList() {
        return availabilitySlotList;
    }

    public static class Builder {
        private CandidateModel candidateModel;
        private List<AvailabilitySlot> availabilitySlotList;

        public static Builder candidateAvailabilityModelWith() {
            return new Builder();
        }

        public Builder withCandidateModel(CandidateModel candidateModel) {
            this.candidateModel = candidateModel;

            return this;
        }

        public Builder withAvailabilitySlotList(List<AvailabilitySlot> availabilitySlotList) {
            this.availabilitySlotList = availabilitySlotList;

            return this;
        }

        public CandidateAvailabilityModel build() {
            return new CandidateAvailabilityModel(this);
        }
    }
}
