package com.leonardo.orgsync.orgsync.infra.exception;

import com.leonardo.orgsync.orgsync.presentation.dtos.ExceptionDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity threatDuplicateEntry(DataIntegrityViolationException exception){
        ExceptionDTO newException = new ExceptionDTO("Este usuário já existe.","400");
        return ResponseEntity.badRequest().body(newException);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity threatNotFound(EntityNotFoundException exception){
        ExceptionDTO newException = new ExceptionDTO(exception.getMessage(),"404");
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity threatGeneralError(Exception exception){
        ExceptionDTO newException = new ExceptionDTO(exception.getMessage(),"500");
        return ResponseEntity.internalServerError().body(newException);
    }

}
