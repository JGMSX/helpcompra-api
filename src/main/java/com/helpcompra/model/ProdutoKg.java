package com.helpcompra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Produto vendido por peso (ex.: carne, frutas, legumes). O preço é por quilo. */
@Entity
@Table(name = "produtos_kg")
@Getter
@Setter
@NoArgsConstructor
public class ProdutoKg extends Produto {

    @Column(name = "peso_kg", nullable = false, precision = 10, scale = 3)
    private BigDecimal pesoKg;

    @Override
    public BigDecimal calcularTotal() {
        return getPreco().multiply(pesoKg).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getTipo() {
        return "KG";
    }
}
