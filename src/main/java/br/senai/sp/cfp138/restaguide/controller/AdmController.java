package br.senai.sp.cfp138.restaguide.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
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

import br.senai.sp.cfp138.restaguide.annotation.Publico;
import br.senai.sp.cfp138.restaguide.model.Administrador;
import br.senai.sp.cfp138.restaguide.repository.AdminRepository;
import br.senai.sp.cfp138.restaguide.util.HashUtil;

@Controller
public class AdmController {
	// repository para persistência do administrador
	// Autowired para injetar a dependência
	@Autowired
	private AdminRepository repository;
	
	//request mapping para o formulário
	@RequestMapping("formAdm")
	public String formAdm() {
		return "administrador/admform";
	}
	
	@RequestMapping("alterarAdm")
	public String alterarAdm(Model model, Long id) {
		Administrador administrador = repository.findById(id).get();
		model.addAttribute("admin", administrador);	
		return "forward:formAdm";
	}
	
	@Publico
	@RequestMapping("login")
	public String login(Administrador admLogin, RedirectAttributes attr, HttpSession session) {
		// buscar o Administrador no BD através do email e da senha
		Administrador admin = repository.findByEmailAndSenha(admLogin.getEmail(), admLogin.getSenha());
		// verifica se existe o admin
		if(admin == null) {
			// se for nulo, avisa ao usuário
			attr.addFlashAttribute("mensagemErro", "login e/ou senha inválido(s)");
			return "redirect:/";
		}else {
			// se não for nulo, salva a sessão e acessa o sistema
			session.setAttribute("usuarioLogado", admin);
			return "redirect:listarAdmin/1";
		}
	}
	
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		// elimina o usuário da session
		session.invalidate();
		// retorna para a página inicial
		return "redirect:/";
	}
	
	@RequestMapping("excluirAdm")
	public String excluirAdm(Long id) {
		repository.deleteById(id);
		return "redirect:listarAdmin/1";
	}
	
	//request mapping para salvar o administrador
	@RequestMapping(value = "salvarAdministrador", method = RequestMethod.POST)
	public String salvarAdmin(@Valid Administrador admin, BindingResult result, RedirectAttributes attr) {
		// verifica se houve erro na validação do objeto
		if(result.hasErrors()) {
			// envia mensagem de erro via requisição
			attr.addFlashAttribute("mensagemErro", "Verifique os campos...");
			return "redirect:formAdm";
		}
		
		//verifica se está sendo feita uma alteração ao invés de uma inserção
		boolean alteracao = admin.getId() != null ? true : false;
		
		//verifica se a senha está vazia
		if(admin.getSenha().equals(HashUtil.hash256(""))) {
			//se não for alteração, eu defino a primeira parte do email como a senha
			if (!alteracao) {
				//extrai a parte do email antes do @
				String parte = admin.getEmail().substring(0, admin.getEmail().indexOf("@"));
			//define a senha do admin
				admin.setSenha(parte);
			}else {
				//busca a senha atual
				String hash = repository.findById(admin.getId()).get().getSenha();
				//"seta" a senha com hash
				admin.setSenhaCommHash(hash);
			}
		}
		
		try {
		// salva o Administrador
		repository.save(admin);
		attr.addFlashAttribute("mensagemSucesso", "Admnistrador salvo com sucesso. Caso a senha não tenha sido informada no cadastro, será a parte do email antes do @. ID do admin:"+admin.getId());
		} catch (Exception e) {
			// caso ocorra uma Exception informa o usuário
			attr.addFlashAttribute("mensagemErro", "Houve um erro ao cadastrar o Administrador: "+e.getMessage());
		}
		return "redirect:formAdm";
	}
	
	// request mapping para listar, informando a página desejada
	@RequestMapping("listarAdmin/{page}")
	public String listar(Model model,@PathVariable("page") int page) {
		// cria um pageable com 6 elementos por página, ordenando os objetos pelo nome de forma ascendente
		PageRequest pageable = PageRequest.of(page-1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		// cria a página atual através do repository (busca no banco de dados)
		Page<Administrador> pagina = repository.findAll(pageable);
		// descobrir o total de páginas
		int totalPages = pagina.getTotalPages();
		// cria uma lista de inteiros para representar as páginas
		List<Integer> PageNumbers = new ArrayList<Integer>();
		// preecher a lista com as páginas
		for(int i = 0; i< totalPages; i++) {
			PageNumbers.add(i+1);
		}
		// adiciona as variáveis na Model
		model.addAttribute("admins", pagina.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", totalPages);
		model.addAttribute("numPaginas", PageNumbers);
		// retorna para o HTML da lista
		return "administrador/listaAdm";
	}
	
	@RequestMapping("index")
	public String index() {
		return "index";
	}
}
