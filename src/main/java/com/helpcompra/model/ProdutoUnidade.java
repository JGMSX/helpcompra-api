package com.helpcompra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Produto vendido por unidade (ex.: lata de milho, caixa de leite). */
@Entity
@Table(name = "produtos_unidade")
@Getter
@Setter
@NoArgsConstructor
public class ProdutoUnidade extends Produto {

    @Column(nullable = false)
    private Integer quantidade;

    @Override
    public BigDecimal calcularTotal() {
        return getPreco().multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getTipo() {
        return "UNIDADE";
    }
}
