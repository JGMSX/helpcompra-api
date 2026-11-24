package com.helpcompra.dto;

import com.helpcompra.model.Produto;
import com.helpcompra.model.ProdutoKg;
import com.helpcompra.model.ProdutoUnidade;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ProdutoResponse(
        Long id,
        String tipo,
        String nome,
        BigDecimal preco,
        Long categoriaId,
        String categoria,
        Integer quantidade,
        BigDecimal pesoKg,
        BigDecimal total) {

    public static ProdutoResponse from(Produto produto) {
        Integer quantidade = null;
        BigDecimal pesoKg = null;
        if (produto instanceof ProdutoUnidade u) {
            quantidade = u.getQuantidade();
        } else if (produto instanceof ProdutoKg k) {
            // O SQLite guarda decimais como ponto flutuante; padroniza as casas ao devolver.
            pesoKg = k.getPesoKg().setScale(3, RoundingMode.HALF_UP);
        }
        return new ProdutoResponse(
                produto.getId(),
                produto.getTipo(),
                produto.getNome(),
                produto.getPreco().setScale(2, RoundingMode.HALF_UP),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                quantidade,
                pesoKg,
                produto.calcularTotal());
    }
}
