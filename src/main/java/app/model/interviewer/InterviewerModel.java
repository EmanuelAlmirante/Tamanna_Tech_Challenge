package app.model.interviewer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "interviewer")
public class InterviewerModel {
    @Id
    private String name;

    public InterviewerModel() {
    }

    public InterviewerModel(Builder builder) {
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private String name;

        public static Builder interviewerModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public InterviewerModel build() {
            return new InterviewerModel(this);
        }
    }
}
