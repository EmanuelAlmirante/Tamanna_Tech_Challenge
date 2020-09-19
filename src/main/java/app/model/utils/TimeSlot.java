package app.model.utils;

import java.io.Serializable;
import java.time.LocalTime;

public class TimeSlot implements Serializable {
    private final LocalTime from;
    private final LocalTime to;

    public TimeSlot(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public TimeSlot(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
    }

    public LocalTime getFrom() {
        return from;
    }

    public LocalTime getTo() {
        return to;
    }

    public static class Builder {
        private LocalTime from;
        private LocalTime to;

        public static Builder timeSlotWith() {
            return new Builder();
        }

        public Builder withFrom(LocalTime from) {
            this.from = from;

            return this;
        }

        public Builder withTo(LocalTime to) {
            this.to = to;

            return this;
        }

        public TimeSlot build() {
            return new TimeSlot(this);
        }
    }
}
