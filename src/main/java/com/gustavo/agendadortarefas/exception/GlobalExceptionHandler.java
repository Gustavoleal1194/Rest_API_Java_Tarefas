package com.gustavo.agendadortarefas.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
		return buildResponse(HttpStatus.NOT_FOUND, "Recurso nao encontrado", List.of(exception.getMessage()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Erro de regra de negocio", List.of(exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		List<String> mensagens = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> fieldError.getDefaultMessage())
			.toList();

		return buildResponse(HttpStatus.BAD_REQUEST, "Erro de validacao", mensagens);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException exception) {
		return buildResponse(
			HttpStatus.BAD_REQUEST,
			"Erro na requisicao",
			List.of("Verifique o JSON enviado e os valores dos campos")
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
		return buildResponse(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"Erro inesperado",
			List.of("Ocorreu um erro inesperado no servidor")
		);
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String erro, List<String> mensagens) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), status.value(), erro, mensagens);
		return ResponseEntity.status(status).body(response);
	}
}
