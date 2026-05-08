package com.gustavo.agendadortarefas.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
	LocalDateTime dataHora,
	int status,
	String erro,
	List<String> mensagens
) {
}
