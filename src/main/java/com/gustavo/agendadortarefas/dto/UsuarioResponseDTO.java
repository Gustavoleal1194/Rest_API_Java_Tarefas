package com.gustavo.agendadortarefas.dto;

public record UsuarioResponseDTO(
	Long id,
	String nome,
	String email,
	Boolean ativo
) {
}
