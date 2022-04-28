package br.senai.sp.cfp138.restaguide.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import br.senai.sp.cfp138.restaguide.util.HashUtil;
import lombok.Data;

// para gerar o get e o set
@Data
// para mapear como uma entidade JPA
@Entity
public class Administrador {
	// chave primária e auto-incremento
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// o campo não pode ser vazio
	@NotEmpty
	private String nome;
	// campo não vazio e específico para email
	@Email
	@Column(unique = true)
	private String email;
	@NotEmpty
	private String senha;
	
	// método para "setar" a senha aplicando hash
	public void setSenha(String senha) {	
		// aplica o hash e "seta" a senha no objeto
		this.senha = HashUtil.hash256(senha);
	}
	
	// método para "setar" a senha sem aplicar o hash
	public void setSenhaCommHash(String hash) {
		// "seta" o hash na senha
		this.senha = hash;
	}
}
