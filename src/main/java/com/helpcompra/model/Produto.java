package com.helpcompra.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade base da hierarquia (herança JOINED): cada subclasse tem sua própria
 * tabela, ligada à tabela "produtos" pela chave primária.
 *
 * <ul>
 *   <li>{@link ProdutoUnidade}: preço por unidade x quantidade</li>
 *   <li>{@link ProdutoKg}: preço por quilo x peso</li>
 * </ul>
 */
@Entity
@Table(name = "produtos")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    @Column(nullable = false, length = 120)
    @ToString.Include
    private String nome;

    /** Preço por unidade (ProdutoUnidade) ou por quilo (ProdutoKg). */
    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    /** Lado "N" da associação 1:N com Categoria. */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /** Lado inverso da associação N:N com ListaCompra. */
    @ManyToMany(mappedBy = "produtos")
    private Set<ListaCompra> listas = new HashSet<>();

    /** Polimorfismo: cada tipo de produto calcula seu total de um jeito. */
    public abstract BigDecimal calcularTotal();

    public abstract String getTipo();
}
