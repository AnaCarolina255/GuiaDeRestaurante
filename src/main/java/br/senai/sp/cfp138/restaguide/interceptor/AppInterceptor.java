package br.senai.sp.cfp138.restaguide.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.senai.sp.cfp138.restaguide.annotation.Privado;
import br.senai.sp.cfp138.restaguide.annotation.Publico;
import br.senai.sp.cfp138.restaguide.rest.UsuarioRestController;

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
			// verifica se a request é para a API
			if (uri.startsWith("/api")) {
				// variável para o token
				String token = null;
				// se for um método privado
				if (metodoChamado.getMethodAnnotation(Privado.class) != null)
					try {
						// obtém o token da request
						token = request.getHeader("Authorization");
						// algoritmo para descriptografar
						Algorithm algoritmo = Algorithm.HMAC256(UsuarioRestController.SECRET);
						// objeto para verificar o token
						JWTVerifier verifier = JWT.require(algoritmo).withIssuer(UsuarioRestController.EMISSOR).build();
						// validar o token
						DecodedJWT jwt = verifier.verify(token);
						// extrair os dados do payload
						Map<String, Claim> payload = jwt.getClaims();
						System.out.println(payload.get("nome_usuario"));
						return true;
					} catch (Exception e) {
						if (token == null) {
							response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
						} else {
							response.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
						}
						return false;
					}
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
