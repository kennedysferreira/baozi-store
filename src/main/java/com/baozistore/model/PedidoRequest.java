package com.baozistore.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Corpo JSON recebido no POST/PUT de pedido: {"clienteId": 1, "produtoId": 1, "quantidade": 10}
 */
public record PedidoRequest(
        @NotNull Long clienteId,
        @NotNull Long produtoId,
        @NotNull @Positive Integer quantidade) {
}
