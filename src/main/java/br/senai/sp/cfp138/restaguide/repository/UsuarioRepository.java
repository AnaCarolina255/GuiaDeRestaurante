package br.senai.sp.cfp138.restaguide.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.cfp138.restaguide.model.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

}
