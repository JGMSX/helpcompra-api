package com.helpcompra.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Lista de compras. Associação N:N com Produto: uma lista tem vários produtos
 * e um produto pode estar em várias listas (tabela de junção "lista_produto").
 */
@Entity
@Table(name = "listas_compra")
@Getter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ListaCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    @Column(nullable = false, length = 120)
    @ToString.Include
    private String nome;

    /** Orçamento máximo da compra (opcional). */
    @Setter
    @Column(name = "limite_gastos", precision = 10, scale = 2)
    private BigDecimal limiteGastos;

    @ManyToMany
    @JoinTable(
            name = "lista_produto",
            joinColumns = @JoinColumn(name = "lista_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id"))
    private Set<Produto> produtos = new LinkedHashSet<>();

    public BigDecimal calcularTotal() {
        return produtos.stream()
                .map(Produto::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isDentroDoLimite() {
        return limiteGastos == null || calcularTotal().compareTo(limiteGastos) <= 0;
    }
}
