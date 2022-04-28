package br.senai.sp.cfp138.restaguide.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.senai.sp.cfp138.restaguide.model.Restaurante;
import br.senai.sp.cfp138.restaguide.model.TipoRestaurante;
import br.senai.sp.cfp138.restaguide.repository.RestauranteRepository;
import br.senai.sp.cfp138.restaguide.repository.TipoRestRepository;
import br.senai.sp.cfp138.restaguide.util.FirebaseUtil;

@Controller
public class RestauranteController {
	@Autowired
	private TipoRestRepository repTipo;
	@Autowired
	private RestauranteRepository repRest;
	@Autowired
	private FirebaseUtil firebaseUtil;
	
	@RequestMapping("formRestaurante")
	public String form(Model model) {
		model.addAttribute("tipos", repTipo.findAll());
		return "restaurante/formRest";
	}
	
	@RequestMapping("alterarRestaurante")
	public String alterarRestaurante(Model model, Long id) {
		Restaurante restaurante = repRest.findById(id).get();
		model.addAttribute("restaurantes", restaurante);	
		return "forward:formRestaurante";
	}
	
	@RequestMapping("excluirRest")
	public String excluirRestaurante(Long id) {
		Restaurante rest = repRest.findById(id).get();
		if(rest.getFotos().length() > 0) {
			for(String foto : rest.verFotos()) {
				firebaseUtil.deletar(foto);
			}
		}
		repRest.delete(rest);
		return "redirect:listaRestaurante/1";
	}
	
	@RequestMapping("salvarRestaurante")
	public String salvarRestaurante(Restaurante restaurante, @RequestParam("fileFotos") MultipartFile[] fileFotos) {
		// String para a URL das fotos
		String fotos = restaurante.getFotos();
		// percorrer cada arquivo que foi submetido no formulário
		for(MultipartFile arquivo : fileFotos) {
			// verificar se o arquivo está vazio
			if(arquivo.getOriginalFilename().isEmpty()) {
				// vai para o próximo arquivo
				continue;
			}
			// faz o upload para a nuvem e obtém a url gerada
			try {
				fotos += firebaseUtil.uploadFile(arquivo)+";";
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		// atribui a String fotos ao objeto restaurante
		restaurante.setFotos(fotos);
		repRest.save(restaurante);
		return "redirect:formRestaurante";
	}
	
	@RequestMapping("listaRestaurante/{page}")
	public String listaRestaurante(Model model,@PathVariable("page") int page) {
		PageRequest pageable = PageRequest.of(page-1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		Page<Restaurante> pagina = repRest.findAll(pageable);
		int totalPages = pagina.getTotalPages();
		List<Integer> PageNumbers = new ArrayList<Integer>();
		for(int i = 0; i< totalPages; i++) {
			PageNumbers.add(i+1);
		}
		// adiciona as variáveis na Model
		model.addAttribute("restaurantes", pagina.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", totalPages);
		model.addAttribute("numPaginas", PageNumbers);
		// retorna para o HTML da lista
		return "restaurante/listaRestaurante";
	}
	
	@RequestMapping("/excluirFotoRestaurante")
	public String excluirFoto(Long idRestaurante, int numFoto, Model model) {
		// busca o restaurante no banco de dados
		Restaurante rest = repRest.findById(idRestaurante).get();
		// pegar a String da foto a ser excluida
		String fotoUrl = rest.verFotos()[numFoto];
		// excluir do firebase
		firebaseUtil.deletar(fotoUrl);
		// "arranca" a foto da String fotos
		rest.setFotos(rest.getFotos().replace(fotoUrl+";", ""));
		// salva no BD o objeto rest
		repRest.save(rest);
		// adiciona o rest na Model
		model.addAttribute("restaurantes", rest);
		// encaminhar para o form
		return "forward:/formRestaurante";
	}
}
