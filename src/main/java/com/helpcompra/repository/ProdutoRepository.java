package com.helpcompra.repository;

import com.helpcompra.model.Produto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByCategoriaId(Long categoriaId);

    @EntityGraph(attributePaths = "categoria")
    List<Produto> findByNomeContainingIgnoreCaseOrderByNome(String nome);

    @EntityGraph(attributePaths = "categoria")
    List<Produto> findByCategoriaIdAndNomeContainingIgnoreCaseOrderByNome(Long categoriaId, String nome);
}
