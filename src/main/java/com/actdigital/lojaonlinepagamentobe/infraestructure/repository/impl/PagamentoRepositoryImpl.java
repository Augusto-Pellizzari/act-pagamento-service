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

    @Override
    public Pagamento salvar(Pagamento p) {
        var sql = """
            INSERT INTO pagamentos (pedido_id, status, criado_em)
            VALUES (:pedidoId, :status, :criadoEm)
        """;
        var params = new MapSqlParameterSource(Map.of(
                "pedidoId", p.getPedidoId(),
                "status", p.getStatus().name(),
                "criadoEm", p.getCriadoEm()
        ));
        var kh = new GeneratedKeyHolder();
        jdbc.update(sql, params, kh, new String[]{"id"});
        p.setId(kh.getKey().longValue());
        return p;
    }

    @Override
    public Optional<Pagamento> findByPedidoId(Long pedidoId) {
        var sql = "SELECT * FROM pagamentos WHERE pedido_id = :pedidoId";
        var list = jdbc.query(
                sql,
                Map.of("pedidoId", pedidoId),
                (ResultSet rs,int i) -> {
                    var pay = new Pagamento();
                    pay.setId(rs.getLong("id"));
                    pay.setPedidoId(rs.getLong("pedido_id"));
                    pay.setStatus(PagamentoStatus.valueOf(rs.getString("status")));
                    pay.setCriadoEm(rs.getObject("criado_em", OffsetDateTime.class));
                    pay.setConfirmadoEm(rs.getObject("confirmado_em", OffsetDateTime.class));
                    return pay;
                }
        );
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
