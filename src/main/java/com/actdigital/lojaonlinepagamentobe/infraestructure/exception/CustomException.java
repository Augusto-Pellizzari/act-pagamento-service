package com.actdigital.lojaonlinepagamentobe.infraestructure.exception;


import org.springframework.http.HttpStatus;

/**
 * Agrupa as exceções especializadas da aplicação.
 */
public class CustomException {

    public static class NotFoundException extends BusinessException {
        public NotFoundException(ErrorCode code, String detail) {
            super(new SimpleMessage(code.name(), detail), HttpStatus.NOT_FOUND);
        }
    }

    public static class RepositoryException extends BusinessException {
        public RepositoryException(ErrorCode code, String detail) {
            super(new SimpleMessage(code.name(), detail), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static class ServiceException extends BusinessException {
        public ServiceException(ErrorCode code, String detail) {
            super(new SimpleMessage(code.name(), detail), HttpStatus.BAD_GATEWAY);
        }
    }

    public static class BrokerException extends BusinessException {
        public BrokerException(ErrorCode code, String detail) {
            super(new SimpleMessage(code.name(), detail), HttpStatus.BAD_GATEWAY);
        }
    }
}
