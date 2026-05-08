package com.gustavo.agendadortarefas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EtiquetaRequestDTO(
	@NotBlank(message = "O nome da etiqueta e obrigatorio")
	@Size(min = 3, max = 80, message = "O nome da etiqueta deve ter entre 3 e 80 caracteres")
	String nome,

	@Size(max = 300, message = "A descricao da etiqueta deve ter no maximo 300 caracteres")
	String descricao
) {
}
