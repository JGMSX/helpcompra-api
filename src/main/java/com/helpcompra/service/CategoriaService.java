package com.helpcompra.service;

import com.helpcompra.dto.CategoriaRequest;
import com.helpcompra.dto.CategoriaResponse;
import com.helpcompra.exception.ApiException;
import com.helpcompra.model.Categoria;
import com.helpcompra.repository.CategoriaRepository;
import com.helpcompra.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAllByOrderByNomeAsc().stream()
                .map(CategoriaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscar(Long id) {
        return CategoriaResponse.from(obter(id));
    }

    public CategoriaResponse criar(CategoriaRequest request) {
        String nome = request.nome().trim();
        garantirNomeLivre(nome, null);
        return CategoriaResponse.from(categoriaRepository.save(new Categoria(nome)));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = obter(id);
        String nome = request.nome().trim();
        garantirNomeLivre(nome, id);
        categoria.setNome(nome);
        return CategoriaResponse.from(categoria);
    }

    public void excluir(Long id) {
        Categoria categoria = obter(id);
        if (produtoRepository.existsByCategoriaId(id)) {
            throw ApiException.conflito("Não é possível excluir a categoria: existem produtos associados a ela");
        }
        categoriaRepository.delete(categoria);
    }

    private Categoria obter(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> ApiException.naoEncontrado("Categoria " + id + " não encontrada"));
    }

    private void garantirNomeLivre(String nome, Long idAtual) {
        categoriaRepository.findByNomeIgnoreCase(nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw ApiException.conflito("Já existe uma categoria chamada \"" + nome + "\"");
                });
    }
}
