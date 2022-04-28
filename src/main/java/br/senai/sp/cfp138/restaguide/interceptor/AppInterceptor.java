package br.senai.sp.cfp138.restaguide.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import br.senai.sp.cfp138.restaguide.annotation.Publico;

@Component
public class AppInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// variável para descobrir pra onde estão tentando ir
		String uri = request.getRequestURI();
		// mostrar a Uri
		System.out.println(uri);
		// verifica se o handler é um HandlerMethod, o que indica
		// que foi encontrado um método em algum Controller para a requisição
		if (handler instanceof HandlerMethod) {
			// liberar o acesso à página inicial
			if (uri.equals("/")) {
				return true;
			}
			if (uri.endsWith("error")) {
				return true;
			}
			// fazer o casting para HandlesMethod
			HandlerMethod metodoChamado = (HandlerMethod) handler;
			if (uri.startsWith("/api")) {
				// quando for API
				
				return true;
			} else {
				// se o método for público libera
				if (metodoChamado.getMethodAnnotation(Publico.class) != null) {
					return true;
				}
				// verifica se existe um usuário logado
				if (request.getSession().getAttribute("usuarioLogado") != null) {
					return true;
				} else {
					// redireciona para a página inicial
					response.sendRedirect("/");
					return false;
				}

			}
		}
		return true;
	}
}
