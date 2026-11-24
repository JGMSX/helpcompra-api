package com.helpcompra.controller;

import com.helpcompra.dto.ListaRequest;
import com.helpcompra.dto.ListaResponse;
import com.helpcompra.service.ListaCompraService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/listas")
@Tag(name = "Listas de compra")
public class ListaCompraController {

    private final ListaCompraService service;

    public ListaCompraController(ListaCompraService service) {
        this.service = service;
    }

    @GetMapping
    public List<ListaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ListaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ListaResponse> criar(@Valid @RequestBody ListaRequest request) {
        ListaResponse criada = service.criar(request);
        return ResponseEntity.created(URI.create("/api/listas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    public ListaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ListaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    /** Relacionamento N:N: adiciona um produto à lista. */
    @PutMapping("/{id}/produtos/{produtoId}")
    public ListaResponse adicionarProduto(@PathVariable Long id, @PathVariable Long produtoId) {
        return service.adicionarProduto(id, produtoId);
    }

    /** Relacionamento N:N: remove um produto da lista. */
    @DeleteMapping("/{id}/produtos/{produtoId}")
    public ListaResponse removerProduto(@PathVariable Long id, @PathVariable Long produtoId) {
        return service.removerProduto(id, produtoId);
    }
}
