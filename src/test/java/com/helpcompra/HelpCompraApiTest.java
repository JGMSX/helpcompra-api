package com.helpcompra;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelpCompraApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void fluxoCompleto() throws Exception {
        // Categoria (lado 1 do 1:N)
        long categoriaId = criar("/api/categorias", "{\"nome\":\"Hortifruti\"}");

        // Nome de categoria repetido -> 409
        mvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"hortifruti\"}"))
                .andExpect(status().isConflict());

        // Corpo inválido -> 400 e recurso inexistente -> 404
        mvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/produtos/999999")).andExpect(status().isNotFound());

        // Produtos (herança): um por unidade e um por quilo
        long leiteId = criar("/api/produtos", """
                {"tipo":"UNIDADE","nome":"Leite","preco":4.00,"categoriaId":%d,"quantidade":3}
                """.formatted(categoriaId));
        long tomateId = criar("/api/produtos", """
                {"tipo":"KG","nome":"Tomate","preco":8.50,"categoriaId":%d,"pesoKg":1.5}
                """.formatted(categoriaId));

        // Lista (N:N) com limite abaixo do total
        long listaId = criar("/api/listas", """
                {"nome":"Compra do mês","limiteGastos":20.00,"produtoIds":[%d,%d]}
                """.formatted(leiteId, tomateId));

        mvc.perform(get("/api/listas/" + listaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtos", hasSize(2)))
                .andExpect(jsonPath("$.total").value(24.75))
                .andExpect(jsonPath("$.dentroDoLimite").value(false));

        // Consulta geral de cada entidade
        mvc.perform(get("/api/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/produtos")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/listas")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        // Alterar produto
        mvc.perform(put("/api/produtos/" + leiteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"UNIDADE","nome":"Leite integral","preco":5.00,"categoriaId":%d,"quantidade":2}
                                """.formatted(categoriaId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10.00));

        // Não pode mudar o tipo
        mvc.perform(put("/api/produtos/" + leiteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"KG","nome":"Leite","preco":5.00,"categoriaId":%d,"pesoKg":1}
                                """.formatted(categoriaId)))
                .andExpect(status().isBadRequest());

        // Validação de campos obrigatórios por tipo
        mvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"KG","nome":"Banana","preco":6.00,"categoriaId":%d}
                                """.formatted(categoriaId)))
                .andExpect(status().isBadRequest());

        // Categoria com produtos não pode ser excluída
        mvc.perform(delete("/api/categorias/" + categoriaId)).andExpect(status().isConflict());

        // Remover da lista mantém o produto; excluir o produto o tira de todas as listas
        mvc.perform(delete("/api/listas/" + listaId + "/produtos/" + leiteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtos", hasSize(1)));
        mvc.perform(get("/api/produtos/" + leiteId)).andExpect(status().isOk());

        mvc.perform(delete("/api/produtos/" + tomateId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/listas/" + listaId))
                .andExpect(jsonPath("$.produtos", hasSize(0)))
                .andExpect(jsonPath("$.dentroDoLimite").value(true));

        // Exclusões finais
        mvc.perform(delete("/api/listas/" + listaId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/listas/" + listaId)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/produtos/" + leiteId)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/categorias/" + categoriaId)).andExpect(status().isNoContent());
    }

    private long criar(String url, String json) throws Exception {
        String corpo = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(corpo, "$.id")).longValue();
    }
}
