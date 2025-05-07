package com.actdigital.lojaonlinepagamentobe.application.impl;



import com.actdigital.lojaonlinepagamentobe.infraestructure.exception.CustomException;
import com.actdigital.lojaonlinepagamentobe.infraestructure.exception.ErrorCode;
import com.actdigital.lojaonlinepagamentobe.ports.in.PagamentoService;
import com.actdigital.lojaonlinepagamentobe.domain.model.Pagamento;
import com.actdigital.lojaonlinepagamentobe.domain.model.PagamentoStatus;
import com.actdigital.lojaonlinepagamentobe.infraestructure.publisher.PagamentoPublisher;
import com.actdigital.lojaonlinepagamentobe.infraestructure.event.PedidoCriadoEvent;
import com.actdigital.lojaonlinepagamentobe.ports.out.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.dao.DataAccessException;
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
            throw new CustomException.ServiceException(
                    ErrorCode.PAYMENT_PROCESS_FAILED,
                    "CorrelationId é obrigatório"
            );
        }

        try {
            if (repository.findByCorrelationId(correlationId).isPresent()) {
                log.debug("Evento já processado, corrId={}", correlationId);
                return;
            }
        } catch (DataAccessException ex) {
            log.error("Erro no DB ao verificar correlationId={}", correlationId, ex);
            throw new CustomException.RepositoryException(
                    ErrorCode.DB_ERROR,
                    "Falha ao verificar pagamento existente"
            );
        }

        try {
            Pagamento p = new Pagamento();
            p.setPedidoId(event.getId());
            p.setStatus(PagamentoStatus.PENDENTE);
            p.setCriadoEm(OffsetDateTime.now());
            p.setCorrelationId(correlationId);
            repository.salvar(p);
            log.info("Pagamento PENDENTE para pedidoId={} corrId={}", event.getId(), correlationId);
        } catch (DataAccessException ex) {
            log.error("Erro ao salvar pagamento no DB, corrId={}", correlationId, ex);
            throw new CustomException.RepositoryException(
                    ErrorCode.DB_ERROR,
                    "Falha ao criar registro de pagamento"
            );
        }
    }

    @Override
    public void confirmarPagamento(Long pedidoId, PagamentoStatus status) {

        try {
            repository.updateStatus(pedidoId, status);
        } catch (DataAccessException ex) {
            log.error("Erro ao atualizar status de pagamento para pedidoId={}", pedidoId, ex);
            throw new CustomException.RepositoryException(
                    ErrorCode.DB_ERROR,
                    "Falha ao atualizar status de pagamento"
            );
        }

        try {
            publisher.publicarConfirmacao(pedidoId, status.name());
            log.info("Pagamento {} para pedidoId={}", status, pedidoId);
        } catch (AmqpException ex) {
            log.error("Erro ao notificar broker do pagamento para pedidoId={}", pedidoId, ex);
            throw new CustomException.BrokerException(
                    ErrorCode.PAYMENT_CONFIRM_FAILED,
                    "Falha ao notificar confirmação de pagamento"
            );
        }
    }
}