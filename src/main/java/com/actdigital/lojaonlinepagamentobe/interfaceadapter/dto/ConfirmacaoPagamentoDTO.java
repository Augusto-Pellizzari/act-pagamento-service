package com.actdigital.lojaonlinepagamentobe.interfaceadapter.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmacaoPagamentoDTO {
    @NotNull
    private Long pedidoId;

    @NotNull
    @Pattern(regexp="CONFIRMADO|RECUSADO",
            message="Status inválido")
    private String status;
}