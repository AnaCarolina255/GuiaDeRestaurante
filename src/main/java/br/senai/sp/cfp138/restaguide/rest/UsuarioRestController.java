package br.senai.sp.cfp138.restaguide.rest;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.cfp138.restaguide.annotation.Privado;
import br.senai.sp.cfp138.restaguide.annotation.Publico;
import br.senai.sp.cfp138.restaguide.model.Erro;
import br.senai.sp.cfp138.restaguide.model.Usuario;
import br.senai.sp.cfp138.restaguide.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioRestController {
	@Autowired
	private UsuarioRepository repository;

	@Publico
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> criarUsuario(@RequestBody Usuario usuario) {
		try {
			// salvar o usuário no banco de dados
			repository.save(usuario);
			// retorna código 201, com a URL para acesso no Location e o usuário inserido no
			// corpo da resposta
			return ResponseEntity.created(URI.create("/" + usuario.getId())).body(usuario);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			Erro erro = new Erro();
			erro.setStatusCode(500);
			erro.setMensagem("Erro de Constraint: Registro Duplicado");
			erro.setException(e.getClass().getName());
			return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			Erro erro = new Erro();
			erro.setStatusCode(500);
			erro.setMensagem("Erro: " + e.getMessage());
			erro.setException(e.getClass().getName());
			return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> atualizarUsuario(@RequestBody Usuario usuario, @PathVariable("id") Long id){
		// valida o ID
		if(id != usuario.getId()) {
			throw new RuntimeException("ID Inválido");
		}
		// salva o usuário no banco
		repository.save(usuario);
		// criar um cabeçalho  http
		HttpHeaders header = new HttpHeaders();
		header.setLocation(URI.create("/api/usuario/"));
		return new ResponseEntity<Void>(header, HttpStatus.OK);
	}

	@Privado
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Usuario> findUsuario(@PathVariable("id") Long idUsuario) {
		// busca o usuário
		Optional<Usuario> usuario = repository.findById(idUsuario);
		if (usuario.isPresent()) {
			return ResponseEntity.ok(usuario.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@Privado
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> excluirUsuario(@PathVariable("id") Long idUsuario){
		repository.deleteById(idUsuario);
		return ResponseEntity.noContent().build();
	}
}
