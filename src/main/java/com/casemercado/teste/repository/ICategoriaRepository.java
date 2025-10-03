package com.casemercado.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemercado.teste.model.Categoria;

public interface ICategoriaRepository extends JpaRepository<Categoria, Long>{
	
	 boolean existsByNome(String nome);

}
