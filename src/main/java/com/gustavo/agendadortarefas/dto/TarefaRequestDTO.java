package com.gustavo.agendadortarefas.dto;

import java.time.LocalDate;

import com.gustavo.agendadortarefas.model.PrioridadeTarefa;
import com.gustavo.agendadortarefas.model.StatusTarefa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TarefaRequestDTO(
	@NotBlank(message = "O titulo da tarefa e obrigatorio")
	@Size(min = 3, max = 120, message = "O titulo da tarefa deve ter entre 3 e 120 caracteres")
	String titulo,

	@Size(max = 500, message = "A descricao da tarefa deve ter no maximo 500 caracteres")
	String descricao,

	LocalDate dataLimite,

	@NotNull(message = "O status da tarefa e obrigatorio")
	StatusTarefa status,

	@NotNull(message = "A prioridade da tarefa e obrigatoria")
	PrioridadeTarefa prioridade,

	@NotNull(message = "O usuario da tarefa e obrigatorio")
	Long usuarioId
) {
}
