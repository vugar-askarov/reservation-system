package com.example.reservation.web;

import com.example.reservation.reservations.ReservationController;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
                                                                                                       //Posle etoqo v postman budet pisatsya:Not found reservation by id = 200
                                                                                                       //Etot klass otlavlivayet ot service/cntroller i vikidivayet owibku kliyentu (postman)
@ControllerAdvice                                                                                      //Wtobi spring ponyal wto eto obrabot4ik owibok
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)                                                                 //Wtobi spring ponyal wto eto obrabot4ik owibok
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        log.error("Handle exception", e);
        var errorDto = new ErrorResponseDto("Internal server error",e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);                 //Mojno i 500
    }//handleGenericException

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(EntityNotFoundException e) {
        log.error("Handle entityNotFoundException", e);
        var errorDto = new ErrorResponseDto("Entity not found",e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);                             //Mojno i 404
    }//handleEntityNotFound

    @ExceptionHandler(exception = {IllegalArgumentException.class, IllegalStateException.class, MethodArgumentNotValidException.class})   //Method.. чтобы было в постман ошибка 400 а не 500
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception e) {
        log.error("Handle badRequest", e);
        var errorDto = new ErrorResponseDto("Bad request",e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);                           //Mojno i 400
    }//handleBadRequest

}//GlobalExceptionHandler
















































