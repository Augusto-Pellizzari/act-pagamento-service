package com.actdigital.lojaonlinepagamentobe.infraestructure.repository.impl;

import com.actdigital.lojaonlinepagamentobe.domain.model.Pagamento;
import com.actdigital.lojaonlinepagamentobe.domain.model.PagamentoStatus;
import com.actdigital.lojaonlinepagamentobe.ports.out.PagamentoRepository;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
public class PagamentoRepositoryImpl implements PagamentoRepository {

    private final NamedParameterJdbcTemplate jdbc;
    public PagamentoRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Pagamento map(ResultSet rs, int row) throws java.sql.SQLException {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(rs.getLong("id"));
        pagamento.setPedidoId(rs.getLong("pedido_id"));
        pagamento.setStatus(PagamentoStatus.valueOf(rs.getString("status")));
        pagamento.setCriadoEm(rs.getObject("criado_em", OffsetDateTime.class));
        pagamento.setConfirmadoEm(rs.getObject("confirmado_em", OffsetDateTime.class));
        pagamento.setCorrelationId(rs.getString("correlation_id"));
        return pagamento;
    }

    @Override
    public Pagamento salvar(Pagamento p) {
        var sql = """
          INSERT INTO pagamentos
            (pedido_id, status, criado_em, correlation_id)
          VALUES
            (:pedidoId, :status, :criadoEm, :corrId)
        """;
        var params = new MapSqlParameterSource(Map.of(
                "pedidoId", p.getPedidoId(),
                "status",    p.getStatus().name(),
                "criadoEm",  p.getCriadoEm(),
                "corrId",    p.getCorrelationId()
        ));
        var kh = new GeneratedKeyHolder();
        jdbc.update(sql, params, kh, new String[]{"id"});
        p.setId(kh.getKey().longValue());
        return p;
    }


    @Override
    public Optional<Pagamento> findByPedidoId(Long pedidoId) {
        var sql = "SELECT * FROM pagamentos WHERE pedido_id = :pedidoId";
        var list = jdbc.query(sql, Map.of("pedidoId", pedidoId), this::map);
        return list.stream().findFirst();
    }


    @Override
    public Optional<Pagamento> findByCorrelationId(String correlationId) {
        var sql = "SELECT * FROM pagamentos WHERE correlation_id = :corrId";
        var list = jdbc.query(sql, Map.of("corrId", correlationId), this::map);
        return list.stream().findFirst();
    }

    @Override
    public void updateStatus(Long pedidoId, PagamentoStatus status) {
        var sql = """
            UPDATE pagamentos
            SET status = :status,
                confirmado_em = :confirmadoEm
            WHERE pedido_id = :pedidoId
        """;
        var params = new MapSqlParameterSource(Map.of(
                "pedidoId", pedidoId,
                "status", status.name(),
                "confirmadoEm", OffsetDateTime.now()
        ));
        jdbc.update(sql, params);
    }
}
