package com.actdigital.lojaonlinepagamentobe.domain.model;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Pagamento {

    private Long id;
    private Long pedidoId;
    private PagamentoStatus status;
    private OffsetDateTime criadoEm;
    private OffsetDateTime confirmadoEm;
    private String correlationId;

}
