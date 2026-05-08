package com.gustavo.agendadortarefas.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gustavo.agendadortarefas.dto.EtiquetaResponseDTO;
import com.gustavo.agendadortarefas.dto.TarefaRequestDTO;
import com.gustavo.agendadortarefas.dto.TarefaResponseDTO;
import com.gustavo.agendadortarefas.dto.UsuarioResumoDTO;
import com.gustavo.agendadortarefas.exception.BusinessException;
import com.gustavo.agendadortarefas.exception.ResourceNotFoundException;
import com.gustavo.agendadortarefas.model.Etiqueta;
import com.gustavo.agendadortarefas.model.Tarefa;
import com.gustavo.agendadortarefas.model.Usuario;
import com.gustavo.agendadortarefas.repository.TarefaRepository;

@Service
public class TarefaService {

	private final TarefaRepository tarefaRepository;
	private final EtiquetaService etiquetaService;
	private final UsuarioService usuarioService;

	public TarefaService(TarefaRepository tarefaRepository, EtiquetaService etiquetaService, UsuarioService usuarioService) {
		this.tarefaRepository = tarefaRepository;
		this.etiquetaService = etiquetaService;
		this.usuarioService = usuarioService;
	}

	@Transactional
	public TarefaResponseDTO criar(TarefaRequestDTO request) {
		Tarefa tarefa = new Tarefa();
		preencherDados(tarefa, request);
		tarefa.setAtivo(true);

		return toResponse(tarefaRepository.save(tarefa));
	}

	@Transactional(readOnly = true)
	public List<TarefaResponseDTO> listar() {
		return tarefaRepository.findAllByAtivoTrue()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public TarefaResponseDTO buscarPorId(Long id) {
		return toResponse(buscarTarefaAtiva(id));
	}

	@Transactional
	public TarefaResponseDTO atualizar(Long id, TarefaRequestDTO request) {
		Tarefa tarefa = buscarTarefaAtiva(id);
		preencherDados(tarefa, request);

		return toResponse(tarefaRepository.save(tarefa));
	}

	@Transactional
	public void remover(Long id) {
		Tarefa tarefa = buscarTarefaAtiva(id);
		tarefa.setAtivo(false);
		tarefa.getEtiquetas().clear();
		tarefaRepository.save(tarefa);
	}

	@Transactional
	public TarefaResponseDTO vincularEtiqueta(Long tarefaId, Long etiquetaId) {
		Tarefa tarefa = buscarTarefaAtiva(tarefaId);
		Etiqueta etiqueta = etiquetaService.buscarEtiquetaAtiva(etiquetaId);

		boolean jaVinculada = tarefa.getEtiquetas()
			.stream()
			.anyMatch(etiquetaAtual -> etiquetaAtual.getId().equals(etiquetaId));

		if (jaVinculada) {
			throw new BusinessException("A etiqueta ja esta vinculada a esta tarefa");
		}

		tarefa.getEtiquetas().add(etiqueta);
		return toResponse(tarefaRepository.save(tarefa));
	}

	@Transactional
	public TarefaResponseDTO removerEtiqueta(Long tarefaId, Long etiquetaId) {
		Tarefa tarefa = buscarTarefaAtiva(tarefaId);
		etiquetaService.buscarEtiquetaAtiva(etiquetaId);

		boolean removeu = tarefa.getEtiquetas().removeIf(etiqueta -> etiqueta.getId().equals(etiquetaId));

		if (!removeu) {
			throw new BusinessException("A etiqueta nao esta vinculada a esta tarefa");
		}

		return toResponse(tarefaRepository.save(tarefa));
	}

	@Transactional(readOnly = true)
	public List<EtiquetaResponseDTO> listarEtiquetasDaTarefa(Long tarefaId) {
		return buscarTarefaAtiva(tarefaId).getEtiquetas()
			.stream()
			.filter(Etiqueta::getAtivo)
			.sorted(Comparator.comparing(Etiqueta::getNome))
			.map(etiquetaService::toResponse)
			.toList();
	}

	private void preencherDados(Tarefa tarefa, TarefaRequestDTO request) {
		Usuario usuario = usuarioService.buscarUsuarioAtivo(request.usuarioId());
		tarefa.setTitulo(request.titulo().trim());
		tarefa.setDescricao(request.descricao());
		tarefa.setDataLimite(request.dataLimite());
		tarefa.setStatus(request.status());
		tarefa.setPrioridade(request.prioridade());
		tarefa.setUsuario(usuario);
	}

	private Tarefa buscarTarefaAtiva(Long id) {
		return tarefaRepository.findByIdAndAtivoTrue(id)
			.orElseThrow(() -> new ResourceNotFoundException("Tarefa nao encontrada com id " + id));
	}

	TarefaResponseDTO toResponse(Tarefa tarefa) {
		List<EtiquetaResponseDTO> etiquetas = tarefa.getEtiquetas()
			.stream()
			.filter(Etiqueta::getAtivo)
			.sorted(Comparator.comparing(Etiqueta::getNome))
			.map(etiquetaService::toResponse)
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
