package com.helpcompra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record ListaRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @PositiveOrZero(message = "O limite de gastos não pode ser negativo")
        BigDecimal limiteGastos,

        /** Ids dos produtos da lista (opcional). */
        Set<Long> produtoIds) {
}
