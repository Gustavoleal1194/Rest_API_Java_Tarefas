package com.gustavo.agendadortarefas.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gustavo.agendadortarefas.dto.EtiquetaRequestDTO;
import com.gustavo.agendadortarefas.dto.EtiquetaResponseDTO;
import com.gustavo.agendadortarefas.dto.TarefaResponseDTO;
import com.gustavo.agendadortarefas.exception.BusinessException;
import com.gustavo.agendadortarefas.exception.ResourceNotFoundException;
import com.gustavo.agendadortarefas.model.Etiqueta;
import com.gustavo.agendadortarefas.model.Tarefa;
import com.gustavo.agendadortarefas.repository.EtiquetaRepository;
import com.gustavo.agendadortarefas.repository.TarefaRepository;

@Service
public class EtiquetaService {

	private final EtiquetaRepository etiquetaRepository;
	private final TarefaRepository tarefaRepository;

	public EtiquetaService(EtiquetaRepository etiquetaRepository, TarefaRepository tarefaRepository) {
		this.etiquetaRepository = etiquetaRepository;
		this.tarefaRepository = tarefaRepository;
	}

	@Transactional
	public EtiquetaResponseDTO criar(EtiquetaRequestDTO request) {
		validarNomeDuplicado(request.nome());

		Etiqueta etiqueta = new Etiqueta();
		etiqueta.setNome(request.nome().trim());
		etiqueta.setDescricao(request.descricao());
		etiqueta.setAtivo(true);

		return toResponse(etiquetaRepository.save(etiqueta));
	}

	@Transactional(readOnly = true)
	public List<EtiquetaResponseDTO> listar() {
		return etiquetaRepository.findAllByAtivoTrue()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public EtiquetaResponseDTO buscarPorId(Long id) {
		return toResponse(buscarEtiquetaAtiva(id));
	}

	@Transactional
	public EtiquetaResponseDTO atualizar(Long id, EtiquetaRequestDTO request) {
		Etiqueta etiqueta = buscarEtiquetaAtiva(id);

		if (etiquetaRepository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
			throw new BusinessException("Ja existe uma etiqueta com esse nome");
		}

		etiqueta.setNome(request.nome().trim());
		etiqueta.setDescricao(request.descricao());

		return toResponse(etiquetaRepository.save(etiqueta));
	}

	@Transactional
	public void remover(Long id) {
		Etiqueta etiqueta = buscarEtiquetaAtiva(id);
		etiqueta.setAtivo(false);
		etiqueta.getTarefas().forEach(tarefa -> tarefa.getEtiquetas().remove(etiqueta));
		etiquetaRepository.save(etiqueta);
	}

	@Transactional(readOnly = true)
	public List<TarefaResponseDTO> listarTarefasDaEtiqueta(Long etiquetaId) {
		buscarEtiquetaAtiva(etiquetaId);
		return tarefaRepository.findByEtiquetasIdAndAtivoTrue(etiquetaId)
			.stream()
			.map(this::toTarefaResponse)
			.toList();
	}

	private void validarNomeDuplicado(String nome) {
		if (etiquetaRepository.existsByNomeIgnoreCase(nome)) {
			throw new BusinessException("Ja existe uma etiqueta com esse nome");
		}
	}

	Etiqueta buscarEtiquetaAtiva(Long id) {
		return etiquetaRepository.findByIdAndAtivoTrue(id)
			.orElseThrow(() -> new ResourceNotFoundException("Etiqueta nao encontrada com id " + id));
	}

	EtiquetaResponseDTO toResponse(Etiqueta etiqueta) {
		return new EtiquetaResponseDTO(
			etiqueta.getId(),
			etiqueta.getNome(),
			etiqueta.getDescricao(),
			etiqueta.getAtivo()
		);
	}

	private TarefaResponseDTO toTarefaResponse(Tarefa tarefa) {
		List<EtiquetaResponseDTO> etiquetas = tarefa.getEtiquetas()
			.stream()
			.filter(Etiqueta::getAtivo)
			.sorted(Comparator.comparing(Etiqueta::getNome))
			.map(this::toResponse)
			.toList();

		return new TarefaResponseDTO(
			tarefa.getId(),
			tarefa.getTitulo(),
			tarefa.getDescricao(),
			tarefa.getDataLimite(),
			tarefa.getStatus(),
			tarefa.getPrioridade(),
			tarefa.getAtivo(),
			etiquetas
		);
	}
}
