package br.senai.sp.cfp138.restaguide.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.senai.sp.cfp138.restaguide.model.TipoRestaurante;

public interface TipoRestRepository extends PagingAndSortingRepository<TipoRestaurante, Long> {
	@Query("SELECT tipo FROM TipoRestaurante tipo WHERE tipo.nome LIKE %:t% OR tipo.descricao LIKE %:t% OR tipo.palavrasChave LIKE %:t%")
	public List<TipoRestaurante> procurarPorTodos(@Param("t") String todos);

	public List<TipoRestaurante> findAllByOrderByNomeAsc();
	
}
