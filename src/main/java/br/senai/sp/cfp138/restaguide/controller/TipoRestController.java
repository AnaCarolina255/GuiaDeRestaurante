package br.senai.sp.cfp138.restaguide.controller;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.senai.sp.cfp138.restaguide.model.TipoRestaurante;
import br.senai.sp.cfp138.restaguide.repository.TipoRestRepository;

@Controller
public class TipoRestController {
	
	@Autowired
	private TipoRestRepository repository;
	
	@RequestMapping("formTipo")
	public String formTipo() {
		return "tipo/formTipo";
	}
	
	@RequestMapping("alterarTipo")
	public String alterarTipo(Model model, Long id) {
		TipoRestaurante tipo = repository.findById(id).get();
		model.addAttribute("tipos", tipo);	
		return "forward:formTipo";
	}
	
	@RequestMapping("excluirTipo")
	public String excluirTipo(Long id) {
		repository.deleteById(id);
		return "redirect:listaTipo/1";
	}
	
	@RequestMapping(value = "salvarTipo", method = RequestMethod.POST)
	public String salvarTipo(@Valid TipoRestaurante tipo, BindingResult result, RedirectAttributes attr) {

		 if(result.hasErrors()) {
			attr.addFlashAttribute("mensagemErro", "Verifique os campos...");
			return "redirect:formTipo";
		}
	
	try {
		repository.save(tipo);
		attr.addFlashAttribute("mensagemSucesso", "Tipo salvo com sucesso. ID do tipo:" + tipo.getId());
		} catch (Exception e) {
			attr.addFlashAttribute("mensagemErro", "Houve um erro ao cadastrar o Tipo: " + e.getMessage());
		}
		return "redirect:formTipo";
	}

	
	@RequestMapping("listaTipo/{page}")
	public String listarTipoRest(Model model,@PathVariable("page") int page) {
		PageRequest pageable = PageRequest.of(page-1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		Page<TipoRestaurante> pagina = repository.findAll(pageable);
		int totalPages = pagina.getTotalPages();
		List<Integer> PageNumbers = new ArrayList<Integer>();
		for(int i = 0; i< totalPages; i++) {
			PageNumbers.add(i+1);
		}
		// adiciona as variáveis na Model
		model.addAttribute("tipos", pagina.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", totalPages);
		model.addAttribute("numPaginas", PageNumbers);
		// retorna para o HTML da lista
		return "tipo/listaTipo";
	}
	
	@RequestMapping("buscarTodos")
	public String buscarPorTodos(String todos, Model model) {
		model.addAttribute("tipos", repository.procurarPorTodos("%"+todos+"%"));
		return "/tipo/listaTipo";
	}
}
