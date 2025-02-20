package com.server.concert_reservation.application.concert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.concert_reservation.application.concert.dto.ConcertScheduleResult;
import com.server.concert_reservation.application.concert.dto.ConcertSeatResult;
import com.server.concert_reservation.application.concert.dto.ReservationCommand;
import com.server.concert_reservation.application.concert.dto.ReservationResult;
import com.server.concert_reservation.domain.concert.event.ReservationEvent;
import com.server.concert_reservation.domain.concert.model.ReservationOutbox;
import com.server.concert_reservation.domain.concert.service.ConcertCommandService;
import com.server.concert_reservation.domain.concert.service.ConcertQueryService;
import com.server.concert_reservation.domain.user.UserQueryService;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;
import com.server.concert_reservation.infrastructure.spring.ReservationSpringEventPublisher;
import com.server.concert_reservation.interfaces.web.support.uuid.UUIDManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConcertUseCase {

    private final ConcertCommandService concertCommandService;
    private final ConcertQueryService concertQueryService;
    private final UserQueryService userQueryService;
    private final UUIDManager uuidManager;
    private final ObjectMapper objectMapper;
    private final ReservationSpringEventPublisher reservationEventPublisher;

    @CachePut(value = "availableConcertSeats", key = "#command.concertScheduleId")
    @Transactional
    public ReservationResult reserveSeats(ReservationCommand command) {
        val user = userQueryService.findUser(command.userId());
        val concertSchedule = concertQueryService.findConcertSchedule(command.concertScheduleId());
        val concertSeat = concertCommandService.reserveSeats(command.seatIds());
        val reservation = concertCommandService.createReservation(user.id(), concertSchedule.id(), concertSeat);
        reservationEventPublisher.publish(new ReservationEvent(reservation.id()));

        return ReservationResult.from(reservation);
    }

    public List<ConcertScheduleResult> getAvailableConcertSchedules(Long concertId, LocalDateTime dateTime) {
        val concertSchedules = concertQueryService.findAvailableConcertSchedules(concertId, dateTime);
        return concertSchedules.stream()
                .map(ConcertScheduleResult::from)
                .collect(Collectors.toList());
    }

    public ConcertSeatResult getAvailableConcertSeats(Long concertScheduleId) {
        val concertSchedule = concertQueryService.findConcertSchedule(concertScheduleId);
        val concertSeat = concertQueryService.findAvailableConcertSeats(concertSchedule.id());

        return ConcertSeatResult.of(concertSchedule, concertSeat);
    }

    public void createReservationOutbox(String reservationId, Object payload) throws JsonProcessingException {
        val reservationOutbox = ReservationOutbox.builder()
                .messageId(uuidManager.generateUuid())
                .kafkaMessageId(reservationId)
                .status(OutboxStatus.INIT)
                .payload(objectMapper.writeValueAsString(payload))
                .retryCount(0)
                .build();
        concertCommandService.createReservationOutbox(reservationOutbox);
    }

    public void publishReservationOutbox(String key) {
        concertCommandService.publishReservationOutbox(key);
    }

}
