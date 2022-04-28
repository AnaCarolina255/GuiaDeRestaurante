package br.senai.sp.cfp138.restaguide.rest;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.cfp138.restaguide.annotation.Privado;
import br.senai.sp.cfp138.restaguide.annotation.Publico;
import br.senai.sp.cfp138.restaguide.model.Avaliacao;
import br.senai.sp.cfp138.restaguide.repository.AvaliacaoRepository;

@RestController
@RequestMapping("/api/avaliacao")
public class AvaliacaoRestController {
	@Autowired
	private AvaliacaoRepository repository;
	
	@Privado
	@RequestMapping(value="", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Avaliacao> criarAvaliacao(@RequestBody Avaliacao avaliacao){
		repository.save(avaliacao);
		return ResponseEntity.created(URI.create("/avaliacao/"+avaliacao.getId())).body(avaliacao);
	}
	
	@Publico
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public Avaliacao getAvaliacao(@PathVariable("id") Long id) {
		return repository.findById(id).get();
	}
	
	// método para todas as avaliações de um único restaurante
	@Publico
	@RequestMapping(value="/restaurante/{idRest}", method = RequestMethod.GET)
	public List<Avaliacao> listarPorRestaurante(@PathVariable("idRest") Long idRest){
		return repository.findByRestauranteId(idRest);
	}
}
