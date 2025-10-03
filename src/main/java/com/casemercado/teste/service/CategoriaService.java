package com.casemercado.teste.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.casemercado.teste.repository.ICategoriaRepository;
import com.casemercado.teste.model.Categoria;

public class CategoriaService {
	
	@Autowired
	private ICategoriaRepository categoriaRepository;
	
	public List<Categoria> listarCategorias(){
		return categoriaRepository.findAll();
	}
	
	public Categoria buscarPorId(Long id) {
		return categoriaRepository.findById(id).orElse(null);
	}

	public Categoria salvarCategoria(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}
	
	public Categoria atualizarCategoria(Long id, Categoria categoria) {
		if(categoriaRepository.existsById(id)) {
			categoria.setId(id);
			return categoriaRepository.save(categoria);
		}
		return null;
	}
	
	public void deletarCategoria(Long id) {
		categoriaRepository.deleteById(id);
	}
}
