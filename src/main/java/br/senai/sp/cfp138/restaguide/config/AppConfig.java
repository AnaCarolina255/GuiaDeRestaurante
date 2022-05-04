package br.senai.sp.cfp138.restaguide.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.senai.sp.cfp138.restaguide.interceptor.AppInterceptor;

@Configuration
public class AppConfig implements WebMvcConfigurer {

	@Autowired
	private AppInterceptor interceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor);
	}

	// Configura a conexão da aplicação ao Banco de Dados MySql
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3307/restaguide");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	// Configura o Hibernate (ORM - Mapeamento Objeto Relacional)
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect"); // Versão do Banco de Dados que está sendo
																			// usado
		adapter.setShowSql(true); // Mostra as instruções SQL no console
		adapter.setPrepareConnection(true); // Usa o DataSource para conversar com o Banco de Dados
		adapter.setGenerateDdl(true); // Cria as tabelas
		return adapter;
	}

}
