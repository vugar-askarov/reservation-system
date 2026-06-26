package com.example.reservation.reservations;
import com.example.reservation.reservations.availability.ReservationAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);       //Logger
    private final ReservationRepository repository;
    private final ReservationMapper mapper;
    private final ReservationAvailabilityService reservationAvailabilityService;
    public ReservationService(ReservationRepository repository, ReservationMapper mapper, ReservationAvailabilityService reservationAvailabilityService) {this.repository = repository; this.mapper = mapper; this.reservationAvailabilityService = reservationAvailabilityService;}


    public Reservation getReservationById(Long id) {
        ReservationEntity reservationEntity =  repository.findById(id).orElseThrow(()-> new EntityNotFoundException("Not found reservation by id = " + id));
        return mapper.toDomain(reservationEntity);
    }//getReservationById


    public List<Reservation> searchAllByFilter (ReservationSearchFilter filter) {
        int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        List<ReservationEntity> allEntities = repository.searchAllByFilter(filter.roomId(),filter.userId(),pageable);
        return allEntities.stream().map(mapper::toDomain).toList();
    }//findAllReservation


    public Reservation createReservation(Reservation reservation) {
        if (reservation.status() != null){throw new IllegalArgumentException("Status should be empty");}
        if(!reservation.endDate().isAfter(reservation.startDate())) {throw new IllegalArgumentException("Start date must be 1 day earlier than end date");}

        var entityToSave = mapper.toEntity(reservation);
        entityToSave.setStatus(ReservationStatus.PENDING);

        var savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }//createReservation


    public Reservation updateReservation(Long id, Reservation reservation) {
        if(!reservation.endDate().isAfter(reservation.startDate())) {throw new IllegalArgumentException("Start date must be 1 day earlier than end date");}
        if (reservation.status() != null){throw new IllegalArgumentException("Status should be empty");}
        var reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING){throw new IllegalStateException("Cannot modify reservation: status=" + reservationEntity.getStatus());}

        var reservationToSave = mapper.toEntity(reservation);
        reservationToSave.setId(reservationEntity.getId());
        reservationToSave.setStatus(ReservationStatus.PENDING);

        var updatedReservation = repository.save(reservationToSave);
        return mapper.toDomain(updatedReservation);
    }//updateReservation

    @Transactional
    public void cancelReservation(Long id) {
        var reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        if(reservationEntity.getStatus().equals(ReservationStatus.APPROVED)){throw new IllegalStateException("Cannot cancel approved reservation. Contact with manager please");}
        if(reservationEntity.getStatus().equals(ReservationStatus.CANCELED)){throw new IllegalStateException("Cannot cancel the reservation. Reservation was already cancelled");}
        repository.setStatus(id, ReservationStatus.CANCELED);          //Тут можно было и как в approveReservation сделать: reservationEntity.setStatus(ReservationStatus.CANCELED)
        log.info("Successfully cancelled reservation: id={}", id);
    }//deleteReservation


    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING){throw new IllegalStateException("Cannot approve reservation: status=" + reservationEntity.getStatus());}
        var isAvailableApprove = reservationAvailabilityService.isReservationAvailable(reservationEntity.getRoomId(),reservationEntity.getStartDate(), reservationEntity.getEndDate());
        if (!isAvailableApprove){throw new IllegalStateException("Cannot approve reservation because of conflict");}
        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        return mapper.toDomain(reservationEntity);
    }//approveReservation


}//ReservationService (ALL)

