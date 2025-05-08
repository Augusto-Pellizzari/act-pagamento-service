package com.actdigital.lojaonlinepagamentobe.interfaceadapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmacaoPagamentoDTO {

    @Schema(description = "ID do pedido a ser confirmado", example = "42")
    @NotNull
    private Long pedidoId;


    @Schema(
            description = "Status do pagamento: CONFIRMADO ou RECUSADO",
            example = "CONFIRMADO",
            allowableValues = { "CONFIRMADO", "RECUSADO" }
    )
    @NotNull
    @Pattern(regexp="CONFIRMADO|RECUSADO",
            message="Status inválido")
    private String status;
}