package com.actdigital.lojaonlinepagamentobe.interfaceadapter.controller;

import com.actdigital.lojaonlinepagamentobe.ports.in.PagamentoService;
import com.actdigital.lojaonlinepagamentobe.domain.model.PagamentoStatus;
import com.actdigital.lojaonlinepagamentobe.interfaceadapter.dto.ConfirmacaoPagamentoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pagamentos", description = "Chamadas sobre pagamentos")
@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Operation(summary = "Confirma ou recusa um pagamento")
    @PostMapping("/confirmar")
    public ResponseEntity<Void> confirmar(
            @Valid
            @RequestBody
            ConfirmacaoPagamentoDTO dto
    ) {
        pagamentoService.confirmarPagamento(
                dto.getPedidoId(),
                PagamentoStatus.valueOf(dto.getStatus())
        );
        return ResponseEntity.ok().build();
    }
}