package com.gustavo.agendadortarefas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavo.agendadortarefas.dto.EtiquetaResponseDTO;
import com.gustavo.agendadortarefas.dto.TarefaRequestDTO;
import com.gustavo.agendadortarefas.dto.TarefaResponseDTO;
import com.gustavo.agendadortarefas.service.TarefaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

	private final TarefaService tarefaService;

	public TarefaController(TarefaService tarefaService) {
		this.tarefaService = tarefaService;
	}

	@GetMapping
	public List<TarefaResponseDTO> listar() {
		return tarefaService.listar();
	}

	@GetMapping("/{id}")
	public TarefaResponseDTO buscarPorId(@PathVariable Long id) {
		return tarefaService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<TarefaResponseDTO> criar(@Valid @RequestBody TarefaRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tarefaService.criar(request));
	}

	@PutMapping("/{id}")
	public TarefaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody TarefaRequestDTO request) {
		return tarefaService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		tarefaService.remover(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{tarefaId}/etiquetas/{etiquetaId}")
	public TarefaResponseDTO vincularEtiqueta(@PathVariable Long tarefaId, @PathVariable Long etiquetaId) {
		return tarefaService.vincularEtiqueta(tarefaId, etiquetaId);
	}

	@DeleteMapping("/{tarefaId}/etiquetas/{etiquetaId}")
	public TarefaResponseDTO removerEtiqueta(@PathVariable Long tarefaId, @PathVariable Long etiquetaId) {
		return tarefaService.removerEtiqueta(tarefaId, etiquetaId);
	}

	@GetMapping("/{tarefaId}/etiquetas")
	public List<EtiquetaResponseDTO> listarEtiquetasDaTarefa(@PathVariable Long tarefaId) {
		return tarefaService.listarEtiquetasDaTarefa(tarefaId);
	}
}
