package com.gustavo.agendadortarefas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gustavo.agendadortarefas.dto.EtiquetaResponseDTO;
import com.gustavo.agendadortarefas.dto.TarefaResponseDTO;
import com.gustavo.agendadortarefas.dto.UsuarioRequestDTO;
import com.gustavo.agendadortarefas.dto.UsuarioResponseDTO;
import com.gustavo.agendadortarefas.dto.UsuarioResumoDTO;
import com.gustavo.agendadortarefas.exception.BusinessException;
import com.gustavo.agendadortarefas.exception.ResourceNotFoundException;
import com.gustavo.agendadortarefas.model.Etiqueta;
import com.gustavo.agendadortarefas.model.Tarefa;
import com.gustavo.agendadortarefas.model.Usuario;
import com.gustavo.agendadortarefas.repository.TarefaRepository;
import com.gustavo.agendadortarefas.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final TarefaRepository tarefaRepository;

	public UsuarioService(UsuarioRepository usuarioRepository, TarefaRepository tarefaRepository) {
		this.usuarioRepository = usuarioRepository;
		this.tarefaRepository = tarefaRepository;
	}

	@Transactional
	public UsuarioResponseDTO criar(UsuarioRequestDTO request) {
		validarEmailDuplicado(request.email());

		Usuario usuario = new Usuario();
		usuario.setNome(request.nome().trim());
		usuario.setEmail(request.email().trim());
		usuario.setAtivo(true);

		return toResponse(usuarioRepository.save(usuario));
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponseDTO> listar() {
		return usuarioRepository.findAllByAtivoTrue()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarPorId(Long id) {
		return toResponse(buscarUsuarioAtivo(id));
	}

	@Transactional
	public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO request) {
		Usuario usuario = buscarUsuarioAtivo(id);

		if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
			throw new BusinessException("Ja existe um usuario com esse email");
		}

		usuario.setNome(request.nome().trim());
		usuario.setEmail(request.email().trim());

		return toResponse(usuarioRepository.save(usuario));
	}

	@Transactional
	public void remover(Long id) {
		Usuario usuario = buscarUsuarioAtivo(id);
		boolean possuiTarefaAtiva = tarefaRepository.findByUsuarioIdAndAtivoTrue(id).stream().findAny().isPresent();

		if (possuiTarefaAtiva) {
			throw new BusinessException("Nao e possivel remover usuario com tarefas ativas");
		}

		usuario.setAtivo(false);
		usuarioRepository.save(usuario);
	}

	@Transactional(readOnly = true)
	public List<TarefaResponseDTO> listarTarefasDoUsuario(Long usuarioId) {
		buscarUsuarioAtivo(usuarioId);
		return tarefaRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
			.stream()
			.map(this::toTarefaResponse)
			.toList();
	}

	Usuario buscarUsuarioAtivo(Long id) {
		return usuarioRepository.findByIdAndAtivoTrue(id)
			.orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id " + id));
	}

	private void validarEmailDuplicado(String email) {
		if (usuarioRepository.existsByEmailIgnoreCase(email)) {
			throw new BusinessException("Ja existe um usuario com esse email");
		}
	}

	private UsuarioResponseDTO toResponse(Usuario usuario) {
		return new UsuarioResponseDTO(
			usuario.getId(),
			usuario.getNome(),
			usuario.getEmail(),
			usuario.getAtivo()
		);
	}

	private TarefaResponseDTO toTarefaResponse(Tarefa tarefa) {
		List<EtiquetaResponseDTO> etiquetas = tarefa.getEtiquetas()
			.stream()
			.filter(Etiqueta::getAtivo)
			.map(etiqueta -> new EtiquetaResponseDTO(
				etiqueta.getId(),
				etiqueta.getNome(),
				etiqueta.getDescricao(),
				etiqueta.getAtivo()
			))
			.toList();

		UsuarioResumoDTO usuario = new UsuarioResumoDTO(
			tarefa.getUsuario().getId(),
			tarefa.getUsuario().getNome(),
			tarefa.getUsuario().getEmail()
		);

		return new TarefaResponseDTO(
			tarefa.getId(),
			tarefa.getTitulo(),
			tarefa.getDescricao(),
			tarefa.getDataLimite(),
			tarefa.getStatus(),
			tarefa.getPrioridade(),
			tarefa.getAtivo(),
			usuario,
			etiquetas
		);
	}
}
