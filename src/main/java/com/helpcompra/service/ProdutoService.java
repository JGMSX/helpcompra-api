package com.helpcompra.service;

import com.helpcompra.dto.ProdutoRequest;
import com.helpcompra.dto.ProdutoResponse;
import com.helpcompra.exception.ApiException;
import com.helpcompra.model.Categoria;
import com.helpcompra.model.Produto;
import com.helpcompra.model.ProdutoKg;
import com.helpcompra.model.ProdutoUnidade;
import com.helpcompra.repository.CategoriaRepository;
import com.helpcompra.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar(Long categoriaId, String nome) {
        String filtro = nome == null ? "" : nome.trim();
        List<Produto> produtos = categoriaId == null
                ? produtoRepository.findByNomeContainingIgnoreCaseOrderByNome(filtro)
                : produtoRepository.findByCategoriaIdAndNomeContainingIgnoreCaseOrderByNome(categoriaId, filtro);
        return produtos.stream().map(ProdutoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.from(obter(id));
    }

    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = "UNIDADE".equalsIgnoreCase(request.tipo())
                ? new ProdutoUnidade()
                : new ProdutoKg();
        preencher(produto, request);
        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = obter(id);
        if (!produto.getTipo().equalsIgnoreCase(request.tipo())) {
            throw ApiException.requisicaoInvalida(
                    "Não é possível alterar o tipo do produto (atual: " + produto.getTipo() + ")");
        }
        preencher(produto, request);
        return ProdutoResponse.from(produto);
    }

    public void excluir(Long id) {
        Produto produto = obter(id);
        // Remove o produto das listas antes de excluí-lo (limpa a tabela de junção N:N).
        new ArrayList<>(produto.getListas()).forEach(lista -> lista.getProdutos().remove(produto));
        produtoRepository.delete(produto);
    }

    private void preencher(Produto produto, ProdutoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> ApiException.naoEncontrado(
                        "Categoria " + request.categoriaId() + " não encontrada"));

        produto.setNome(request.nome().trim());
        produto.setPreco(request.preco());
        produto.setCategoria(categoria);

        if (produto instanceof ProdutoUnidade unidade) {
            if (request.quantidade() == null) {
                throw ApiException.requisicaoInvalida("Informe a quantidade para produtos do tipo UNIDADE");
            }
            unidade.setQuantidade(request.quantidade());
        } else if (produto instanceof ProdutoKg kg) {
            if (request.pesoKg() == null) {
                throw ApiException.requisicaoInvalida("Informe o pesoKg para produtos do tipo KG");
            }
            kg.setPesoKg(request.pesoKg());
        }
    }

    private Produto obter(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> ApiException.naoEncontrado("Produto " + id + " não encontrado"));
    }
}
