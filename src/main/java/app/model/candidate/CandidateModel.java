package app.model.candidate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "candidate")
public class CandidateModel {
    @Id
    private String name;

    @JsonIgnore
    @OneToOne(mappedBy = "candidateModel", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private CandidateAvailabilityModel candidateAvailabilityModel;

    public CandidateModel() {
    }

    public CandidateModel(String name) {
        this.name = name;
    }

    public CandidateModel(Builder builder) {
        this.name = builder.name;
        this.candidateAvailabilityModel = builder.candidateAvailabilityModel;
    }

    public String getName() {
        return name;
    }

    public CandidateAvailabilityModel getCandidateAvailabilityModel() {
        return candidateAvailabilityModel;
    }

    public static class Builder {
        private String name;
        private CandidateAvailabilityModel candidateAvailabilityModel;

        public static Builder candidateModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Builder withCandidateAvailabilityModel(CandidateAvailabilityModel candidateAvailabilityModel) {
            this.candidateAvailabilityModel = candidateAvailabilityModel;

            return this;
        }

        public CandidateModel build() {
            return new CandidateModel(this);
        }
    }
}
