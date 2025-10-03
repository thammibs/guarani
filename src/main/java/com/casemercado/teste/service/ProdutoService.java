package com.casemercado.teste.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.casemercado.teste.model.Produto;
import com.casemercado.teste.repository.IProdutoRepository;

public class ProdutoService {
	
	@Autowired
	private IProdutoRepository produtoRepository;
	
	public List<Produto> listarProdutos(){
		return produtoRepository.findAll();
	}
	
	public Produto buscarPorId(Long id) {
		return produtoRepository.findById(id).orElse(null);
	}
	
	public Produto salvarProduto(Produto produto) {
		return produtoRepository.save(produto);
	}
	
	public Produto atualizarProduto(Long id, Produto produto) {
		if(produtoRepository.existsById(id)) {
			produto.setId(id);
			return produtoRepository.save(produto);
		}
		return null;
	}
	
	public void deletarProduto(Long id) {
		produtoRepository.deleteById(id);
	}

}
