package dev.netho.planner.trip;

import dev.netho.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository repository;

    @Autowired
    private ParticipantService participantService;

    public Optional<TripCreateResponse> registerTrip(TripRequestPayload payload) {

        LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

        if (startsAt.isAfter(endsAt)) {
            return Optional.empty();
        }

        Trip newTrip = new Trip(payload);
        this.repository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return Optional.of(new TripCreateResponse(newTrip.getId()));

    }

    public Optional<TripData> getTripFromId(UUID id) {
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();
            TripData tripData = new TripData(rawTrip.getId(), rawTrip.getDestination(), rawTrip.getStartsAt(), rawTrip.getEndsAt(), rawTrip.getOwner_email(), rawTrip.getOwner_name(), rawTrip.getIsConfirmed());
            return Optional.of(tripData);
        }

        return Optional.empty();
    }

    public Optional<TripData> updateTrip(Trip trip, TripRequestPayload payload) {

        LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

        if (startsAt.isAfter(endsAt)) {
            return Optional.empty();
        }

        trip.setEndsAt(startsAt);
        trip.setStartsAt(endsAt);
        trip.setDestination(payload.destination());

        this.repository.save(trip);

        return Optional.of(new TripData(trip.getId(), trip.getDestination(), trip.getStartsAt(), trip.getEndsAt(), trip.getOwner_email(), trip.getOwner_name(), trip.getIsConfirmed()));
    }

    public TripData confirmTrip(Trip trip) {
        trip.setIsConfirmed(true);

        this.repository.save(trip);

        return new TripData(trip.getId(), trip.getDestination(), trip.getStartsAt(), trip.getEndsAt(), trip.getOwner_email(), trip.getOwner_name(), trip.getIsConfirmed());
    }
}
