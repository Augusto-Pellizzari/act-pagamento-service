package com.actdigital.lojaonlinepagamentobe.application.impl;



import com.actdigital.lojaonlinepagamentobe.ports.in.PagamentoService;
import com.actdigital.lojaonlinepagamentobe.domain.model.Pagamento;
import com.actdigital.lojaonlinepagamentobe.domain.model.PagamentoStatus;
import com.actdigital.lojaonlinepagamentobe.infraestructure.publisher.PagamentoPublisher;
import com.actdigital.lojaonlinepagamentobe.infraestructure.event.PedidoCriadoEvent;
import com.actdigital.lojaonlinepagamentobe.ports.out.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository repository;
    private final PagamentoPublisher publisher;

    @Override
    public void processarPagamento(PedidoCriadoEvent event, String correlationId) {

        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("correlationId é obrigatório");
        }

        if (repository.findByCorrelationId(correlationId).isPresent()) {
            log.debug("Evento já processado, corrId={}", correlationId);
            return;
        }

        Pagamento p = new Pagamento();
        p.setPedidoId(event.getId());
        p.setStatus(PagamentoStatus.PENDENTE);
        p.setCriadoEm(OffsetDateTime.now());
        p.setCorrelationId(correlationId);
        repository.salvar(p);

        log.debug("Pagamento PENDENTE para pedidoId={} corrId={}",
                event.getId(), correlationId);
    }

    @Override
    public void confirmarPagamento(Long pedidoId, PagamentoStatus status) {

        repository.updateStatus(pedidoId, status);
        publisher.publicarConfirmacao(pedidoId, status.name());
        log.debug("Pagamento {} para pedidoId={}", status, pedidoId);
    }
}