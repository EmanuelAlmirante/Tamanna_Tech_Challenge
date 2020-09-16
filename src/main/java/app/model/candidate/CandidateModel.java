package app.model.candidate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "candidate")
public class CandidateModel {
    @Id
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "candidateModel", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<CandidateAvailabilityModel> candidateAvailabilityModelSet;

    public CandidateModel() {
    }

    public CandidateModel(String name) {
        this.name = name;
    }

    public CandidateModel(Builder builder) {
        this.name = builder.name;
        this.candidateAvailabilityModelSet = builder.candidateAvailabilityModelSet;
    }

    public String getName() {
        return name;
    }

    public Set<CandidateAvailabilityModel> getCandidateAvailabilityModelSet() {
        return candidateAvailabilityModelSet;
    }

    public static class Builder {
        private String name;
        private Set<CandidateAvailabilityModel> candidateAvailabilityModelSet;

        public static Builder candidateModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Builder withCandidateAvailabilityModelSet(
                Set<CandidateAvailabilityModel> candidateAvailabilityModelSet) {
            this.candidateAvailabilityModelSet = candidateAvailabilityModelSet;

            return this;
        }

        public CandidateModel build() {
            return new CandidateModel(this);
        }
    }
}
