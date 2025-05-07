package com.actdigital.lojaonlinepagamentobe.infraestructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Collections;

@Getter
public class BusinessException extends RuntimeException {

    private final Collection<SimpleMessage> simpleMessages;
    private final HttpStatus httpStatus;

    public BusinessException(Collection<SimpleMessage> messages, HttpStatus status) {
        super(messages.iterator().next().getMessage());
        this.simpleMessages = messages;
        this.httpStatus = status;
    }

    public BusinessException(SimpleMessage msg, HttpStatus status) {
        this(Collections.singletonList(msg), status);
    }

    public BusinessException(ErrorCode code, HttpStatus status) {
        this(new SimpleMessage(code.name(), code.name()), status);
    }

    public BusinessException(ErrorCode code, String detail, HttpStatus status) {
        this(new SimpleMessage(code.name(), detail), status);
    }

    public BusinessException(SimpleMessage msg) {
        this(msg, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(ErrorCode code) {
        this(new SimpleMessage(code.name(), code.name()), HttpStatus.BAD_REQUEST);
    }
}