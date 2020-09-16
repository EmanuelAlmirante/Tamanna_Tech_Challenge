package app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AvailabilitySlot implements Serializable {
    private final LocalDateTime from;
    private final LocalDateTime to;

    public AvailabilitySlot(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public AvailabilitySlot(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public static class Builder {
        private LocalDateTime from;
        private LocalDateTime to;

        public static Builder availabilitySlotWith() {
            return new Builder();
        }

        public Builder withFrom(LocalDateTime from) {
            this.from = from;

            return this;
        }

        public Builder withTo(LocalDateTime to) {
            this.to = to;

            return this;
        }

        public AvailabilitySlot build() {
            return new AvailabilitySlot(this);
        }
    }
}
