package com.gustavo.agendadortarefas.dto;

public record EtiquetaResponseDTO(
	Long id,
	String nome,
	String descricao,
	Boolean ativo
) {
}
