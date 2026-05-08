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

import com.gustavo.agendadortarefas.dto.TarefaResponseDTO;
import com.gustavo.agendadortarefas.dto.UsuarioRequestDTO;
import com.gustavo.agendadortarefas.dto.UsuarioResponseDTO;
import com.gustavo.agendadortarefas.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	public List<UsuarioResponseDTO> listar() {
		return usuarioService.listar();
	}

	@GetMapping("/{id}")
	public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
		return usuarioService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(request));
	}

	@PutMapping("/{id}")
	public UsuarioResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {
		return usuarioService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		usuarioService.remover(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/tarefas")
	public List<TarefaResponseDTO> listarTarefasDoUsuario(@PathVariable Long id) {
		return usuarioService.listarTarefasDoUsuario(id);
	}
}
