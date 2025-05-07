package com.actdigital.lojaonlinepagamentobe.ports.in;

import com.actdigital.lojaonlinepagamentobe.domain.model.PagamentoStatus;
import com.actdigital.lojaonlinepagamentobe.infraestructure.event.PedidoCriadoEvent;

public interface PagamentoService {

    void processarPagamento(PedidoCriadoEvent pedidoCriadoEvent, String correlationId);
    void confirmarPagamento(Long pedidoId, PagamentoStatus status);
}