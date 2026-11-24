package com.helpcompra.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Um único formato de entrada para os dois tipos de produto.
 * <ul>
 *   <li>tipo = UNIDADE: informe "quantidade"</li>
 *   <li>tipo = KG: informe "pesoKg" (o "preco" é o preço por quilo)</li>
 * </ul>
 */
public record ProdutoRequest(
        @NotBlank(message = "O tipo é obrigatório")
        @Pattern(regexp = "UNIDADE|KG", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "O tipo deve ser UNIDADE ou KG")
        String tipo,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 2 casas decimais")
        BigDecimal preco,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId,

        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @Positive(message = "O peso deve ser maior que zero")
        @Digits(integer = 7, fraction = 3, message = "O peso deve ter no máximo 3 casas decimais")
        BigDecimal pesoKg) {
}
