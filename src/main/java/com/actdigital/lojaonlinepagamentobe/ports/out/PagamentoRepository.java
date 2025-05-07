package com.actdigital.lojaonlinepagamentobe.ports.out;

import com.actdigital.lojaonlinepagamentobe.domain.model.Pagamento;
import com.actdigital.lojaonlinepagamentobe.domain.model.PagamentoStatus;
import java.util.Optional;

public interface PagamentoRepository {

    Pagamento salvar(Pagamento p);
    Optional<Pagamento> findByPedidoId(Long pedidoId);
    Optional<Pagamento> findByCorrelationId(String correlationId);
    void updateStatus(Long pedidoId, PagamentoStatus status);
}