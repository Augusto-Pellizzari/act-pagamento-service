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
public class PagamentoConfirmadoEvent {

    private Long pedidoId;
    private String status;
    private OffsetDateTime quando;
}