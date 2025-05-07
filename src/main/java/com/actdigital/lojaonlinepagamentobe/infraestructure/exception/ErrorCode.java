package com.actdigital.lojaonlinepagamentobe.infraestructure.exception;

public enum ErrorCode {

    UNEXPECTED_ERROR,
    ORDER_NOT_FOUND,
    ORDER_CREATION_FAILED,
    ORDER_UPDATE_FAILED,
    PAYMENT_PROCESS_FAILED,
    PAYMENT_CONFIRM_FAILED,
    DB_ERROR,
    BROKER_PUBLISH_ERROR
}