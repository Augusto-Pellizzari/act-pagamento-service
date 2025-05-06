package com.actdigital.lojaonlinepagamentobe.infraestructure.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCriadoEvent {

    private Long id;
    private String cliente;
    private String status;
    private OffsetDateTime dataCriacao;
}