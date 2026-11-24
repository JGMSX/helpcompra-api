package com.helpcompra.dto;

import com.helpcompra.model.ListaCompra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public record ListaResponse(
        Long id,
        String nome,
        BigDecimal limiteGastos,
        List<ProdutoResponse> produtos,
        BigDecimal total,
        boolean dentroDoLimite) {

    public static ListaResponse from(ListaCompra lista) {
        List<ProdutoResponse> produtos = lista.getProdutos().stream()
                .map(ProdutoResponse::from)
                .sorted(Comparator.comparing(ProdutoResponse::id))
                .toList();
        return new ListaResponse(
                lista.getId(),
                lista.getNome(),
                lista.getLimiteGastos() == null ? null : lista.getLimiteGastos().setScale(2, RoundingMode.HALF_UP),
                produtos,
                lista.calcularTotal(),
                lista.isDentroDoLimite());
    }
}
