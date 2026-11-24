package com.helpcompra.service;

import com.helpcompra.dto.ListaRequest;
import com.helpcompra.dto.ListaResponse;
import com.helpcompra.exception.ApiException;
import com.helpcompra.model.ListaCompra;
import com.helpcompra.model.Produto;
import com.helpcompra.repository.ListaCompraRepository;
import com.helpcompra.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ListaCompraService {

    private final ListaCompraRepository listaRepository;
    private final ProdutoRepository produtoRepository;

    public ListaCompraService(ListaCompraRepository listaRepository, ProdutoRepository produtoRepository) {
        this.listaRepository = listaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<ListaResponse> listar() {
        return listaRepository.findAllByOrderByNomeAsc().stream()
                .map(ListaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ListaResponse buscar(Long id) {
        return ListaResponse.from(obter(id));
    }

    public ListaResponse criar(ListaRequest request) {
        ListaCompra lista = new ListaCompra();
        preencher(lista, request);
        return ListaResponse.from(listaRepository.save(lista));
    }

    public ListaResponse atualizar(Long id, ListaRequest request) {
        ListaCompra lista = obter(id);
        preencher(lista, request);
        return ListaResponse.from(lista);
    }

    public void excluir(Long id) {
        listaRepository.delete(obter(id));
    }

    /** N:N - adiciona um produto existente à lista. */
    public ListaResponse adicionarProduto(Long listaId, Long produtoId) {
        ListaCompra lista = obter(listaId);
        lista.getProdutos().add(obterProduto(produtoId));
        return ListaResponse.from(lista);
    }

    /** N:N - remove o produto da lista (o produto continua existindo). */
    public ListaResponse removerProduto(Long listaId, Long produtoId) {
        ListaCompra lista = obter(listaId);
        boolean removido = lista.getProdutos().removeIf(p -> p.getId().equals(produtoId));
        if (!removido) {
            throw ApiException.naoEncontrado("Produto " + produtoId + " não está na lista " + listaId);
        }
        return ListaResponse.from(lista);
    }

    private void preencher(ListaCompra lista, ListaRequest request) {
        lista.setNome(request.nome().trim());
        lista.setLimiteGastos(request.limiteGastos());

        Set<Long> ids = request.produtoIds() == null ? Set.of() : request.produtoIds();
        List<Produto> produtos = produtoRepository.findAllById(ids);
        if (produtos.size() != ids.size()) {
            Set<Long> faltando = new HashSet<>(ids);
            produtos.forEach(p -> faltando.remove(p.getId()));
            throw ApiException.naoEncontrado("Produtos não encontrados: " + faltando);
        }
        lista.getProdutos().clear();
        lista.getProdutos().addAll(produtos);
    }

    private ListaCompra obter(Long id) {
        return listaRepository.findById(id)
                .orElseThrow(() -> ApiException.naoEncontrado("Lista " + id + " não encontrada"));
    }

    private Produto obterProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> ApiException.naoEncontrado("Produto " + id + " não encontrado"));
    }
}
