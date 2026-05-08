package com.gustavo.agendadortarefas.dto;

import java.time.LocalDate;
import java.util.List;

import com.gustavo.agendadortarefas.model.PrioridadeTarefa;
import com.gustavo.agendadortarefas.model.StatusTarefa;

public record TarefaResponseDTO(
	Long id,
	String titulo,
	String descricao,
	LocalDate dataLimite,
	StatusTarefa status,
	PrioridadeTarefa prioridade,
	Boolean ativo,
	List<EtiquetaResponseDTO> etiquetas
) {
}
