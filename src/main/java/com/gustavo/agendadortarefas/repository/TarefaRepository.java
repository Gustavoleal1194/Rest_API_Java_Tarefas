package com.gustavo.agendadortarefas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavo.agendadortarefas.model.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

	@EntityGraph(attributePaths = {"etiquetas", "usuario"})
	List<Tarefa> findAllByAtivoTrue();

	@EntityGraph(attributePaths = {"etiquetas", "usuario"})
	Optional<Tarefa> findByIdAndAtivoTrue(Long id);

	@EntityGraph(attributePaths = {"etiquetas", "usuario"})
	List<Tarefa> findByEtiquetasIdAndAtivoTrue(Long etiquetaId);

	@EntityGraph(attributePaths = {"etiquetas", "usuario"})
	List<Tarefa> findByUsuarioIdAndAtivoTrue(Long usuarioId);
}
