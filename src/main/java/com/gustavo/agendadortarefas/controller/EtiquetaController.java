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

import com.gustavo.agendadortarefas.dto.EtiquetaRequestDTO;
import com.gustavo.agendadortarefas.dto.EtiquetaResponseDTO;
import com.gustavo.agendadortarefas.dto.TarefaResponseDTO;
import com.gustavo.agendadortarefas.service.EtiquetaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/etiquetas")
public class EtiquetaController {

	private final EtiquetaService etiquetaService;

	public EtiquetaController(EtiquetaService etiquetaService) {
		this.etiquetaService = etiquetaService;
	}

	@GetMapping
	public List<EtiquetaResponseDTO> listar() {
		return etiquetaService.listar();
	}

	@GetMapping("/{id}")
	public EtiquetaResponseDTO buscarPorId(@PathVariable Long id) {
		return etiquetaService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<EtiquetaResponseDTO> criar(@Valid @RequestBody EtiquetaRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(etiquetaService.criar(request));
	}

	@PutMapping("/{id}")
	public EtiquetaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody EtiquetaRequestDTO request) {
		return etiquetaService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		etiquetaService.remover(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{etiquetaId}/tarefas")
	public List<TarefaResponseDTO> listarTarefasDaEtiqueta(@PathVariable Long etiquetaId) {
		return etiquetaService.listarTarefasDaEtiqueta(etiquetaId);
	}
}
