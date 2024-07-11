package dev.netho.planner.activity;

import dev.netho.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public Optional<ActivityResponse> registerActivity(ActivityRequestPayload payload, Trip trip) {

        LocalDateTime occursAt = LocalDateTime.parse(payload.occurs_at(), DateTimeFormatter.ISO_DATE_TIME);

        if (occursAt.isBefore(trip.getStartsAt()) || occursAt.isAfter(trip.getEndsAt())) {
            return Optional.empty();
        }

        Activity newActivity = new Activity(payload.title(), payload.occurs_at(), trip);

        this.activityRepository.save(newActivity);

        return Optional.of(new ActivityResponse(newActivity.getId()));
    }

    public List<ActivityData> getAllActivitiesFromId(UUID id) {

        return this.activityRepository.findByTripId(id).stream().map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();
    }
}
