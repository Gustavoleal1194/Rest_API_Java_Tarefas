package com.gustavo.agendadortarefas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
	@NotBlank(message = "O nome do usuario e obrigatorio")
	@Size(min = 3, max = 120, message = "O nome do usuario deve ter entre 3 e 120 caracteres")
	String nome,

	@NotBlank(message = "O email do usuario e obrigatorio")
	@Email(message = "O email informado e invalido")
	@Size(max = 120, message = "O email do usuario deve ter no maximo 120 caracteres")
	String email
) {
}
