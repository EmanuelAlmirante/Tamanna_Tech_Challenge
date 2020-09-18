package app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class AvailabilitySlot implements Serializable {
    private final LocalDate day;
    private final List<TimeSlot> timeSlotList;

    public AvailabilitySlot(LocalDate day, List<TimeSlot> timeSlotList) {
        this.day = day;
        this.timeSlotList = timeSlotList;
    }

    public AvailabilitySlot(Builder builder) {
        this.day = builder.day;
        this.timeSlotList = builder.timeSlotList;
    }

    public LocalDate getDay() {
        return day;
    }

    public List<TimeSlot> getTimeSlotList() {
        return timeSlotList;
    }

    public static class Builder {
        private LocalDate day;
        private List<TimeSlot> timeSlotList;

        public static Builder availabilitySlotWith() {
            return new Builder();
        }

        public Builder withDay(LocalDate day) {
            this.day = day;

            return this;
        }

        public Builder withTimeSlotList(List<TimeSlot> timeSlotList) {
            this.timeSlotList = timeSlotList;

            return this;
        }

        public AvailabilitySlot build() {
            return new AvailabilitySlot(this);
        }
    }
}
