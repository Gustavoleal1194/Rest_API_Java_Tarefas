package com.gustavo.agendadortarefas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavo.agendadortarefas.model.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {

	boolean existsByNomeIgnoreCase(String nome);

	boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

	List<Etiqueta> findAllByAtivoTrue();

	Optional<Etiqueta> findByIdAndAtivoTrue(Long id);

	@EntityGraph(attributePaths = "tarefas")
	Optional<Etiqueta> findWithTarefasByIdAndAtivoTrue(Long id);
}
