package com.helpcompra.repository;

import com.helpcompra.model.ListaCompra;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListaCompraRepository extends JpaRepository<ListaCompra, Long> {

    @EntityGraph(attributePaths = {"produtos", "produtos.categoria"})
    List<ListaCompra> findAllByOrderByNomeAsc();

    @Override
    @EntityGraph(attributePaths = {"produtos", "produtos.categoria"})
    Optional<ListaCompra> findById(Long id);
}
