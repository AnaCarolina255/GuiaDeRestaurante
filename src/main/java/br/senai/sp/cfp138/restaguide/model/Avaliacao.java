package br.senai.sp.cfp138.restaguide.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Data
@Entity
public class Avaliacao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JsonProperty(access = Access.WRITE_ONLY)
	private Restaurante restaurante;
	@JsonFormat(pattern = "dd-MM-yyyy")
	private Calendar dataVisita;
	private String comentario;
	private double nota;
	@ManyToOne
	private Usuario usuario;
}
