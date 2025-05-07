package com.actdigital.lojaonlinepagamentobe.infraestructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMessage {

    private String code;
    private String message;
}
