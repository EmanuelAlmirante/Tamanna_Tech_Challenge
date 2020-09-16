package app.model.candidate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "candidate")
public class CandidateModel {
    @Id
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "candidateModel", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<CandidateAvailabilityModel> candidateAvailabilityModelList;

    public CandidateModel() {
    }

    public CandidateModel(String name) {
        this.name = name;
    }

    public CandidateModel(Builder builder) {
        this.name = builder.name;
        this.candidateAvailabilityModelList = builder.candidateAvailabilityModelList;
    }

    public String getName() {
        return name;
    }

    public List<CandidateAvailabilityModel> getCandidateAvailabilityModelList() {
        return candidateAvailabilityModelList;
    }

    public static class Builder {
        private String name;
        private List<CandidateAvailabilityModel> candidateAvailabilityModelList;

        public static Builder candidateModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Builder withCandidateAvailabilityModelList(
                List<CandidateAvailabilityModel> candidateAvailabilityModelList) {
            this.candidateAvailabilityModelList = candidateAvailabilityModelList;

            return this;
        }

        public CandidateModel build() {
            return new CandidateModel(this);
        }
    }
}
