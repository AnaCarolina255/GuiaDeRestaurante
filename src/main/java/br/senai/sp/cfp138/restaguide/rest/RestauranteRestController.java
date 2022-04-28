package br.senai.sp.cfp138.restaguide.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.cfp138.restaguide.annotation.Publico;
import br.senai.sp.cfp138.restaguide.model.Restaurante;
import br.senai.sp.cfp138.restaguide.repository.RestauranteRepository;

@RequestMapping("/api/restaurante")
@RestController
public class RestauranteRestController {
	@Autowired
	private RestauranteRepository repository;
	
	@Publico
	@RequestMapping(value="", method = RequestMethod.GET)
	public Iterable<Restaurante> getRestaurantes(){
		return repository.findAll();
	}
	
	@Publico
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	// método que devolve um restaurante
	public ResponseEntity<Restaurante> findRestaurante(@PathVariable("id") Long idRestaurante){
		// busca o restaurante
		Optional<Restaurante> restaurante = repository.findById(idRestaurante);
		// verifica se está presente
		if(restaurante.isPresent()) {
			return ResponseEntity.ok(restaurante.get());
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@Publico
	@RequestMapping(value="/tipo/{id}", method = RequestMethod.GET)
	public Iterable<Restaurante> getRestaurantesByTipo(@PathVariable("id") Long idTipo){
		return repository.findByTipoRestauranteId(idTipo);

	}
	
	@RequestMapping(value="/estacionamento/{estacionamento}", method = RequestMethod.GET)
	public Iterable<Restaurante> getRestaurantesByEstacionamento(@PathVariable("estacionamento") Boolean estacionamento){
		return repository.findByEstacionamento(estacionamento);
	}
	
	@RequestMapping(value="/estado/{uf}", method = RequestMethod.GET)
	public List<Restaurante> getRestaurantesByEstado(@PathVariable("uf") String uf){
		return repository.findByEstado(uf);
	}
	
}
