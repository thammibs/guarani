package com.casemercado.teste.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemercado.teste.model.Categoria;
import com.casemercado.teste.model.Produto;
import com.casemercado.teste.repository.ICategoriaRepository;
import com.casemercado.teste.repository.IProdutoRepository;
import com.casemercado.teste.response.ApiResponse;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "http://localhost:62027")
public class CategoriaController {

	@Autowired
	private ICategoriaRepository categoriaRepository;

	@Autowired
	private IProdutoRepository produtoRepository;
	

	@GetMapping("/listarcategorias")
	public List<Categoria> listarTodos() {
		return categoriaRepository.findAll();
	}

	@PostMapping("/salvarcategoria")
	public ResponseEntity<ApiResponse<Categoria>> criarCategoria(@RequestBody Categoria categoria) {

		if (categoria.getNome() == null || categoria.getNome().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("O nome da categoria é obrigatório", null, HttpStatus.BAD_REQUEST.value()));
		}

		if (categoriaRepository.existsByNome(categoria.getNome())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new ApiResponse<>("Já existe uma categoria com esse nome", null, HttpStatus.BAD_REQUEST.value()));
		}

		Categoria categoriaCriada = categoriaRepository.save(categoria);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse<>("Categoria criada com sucesso", categoriaCriada, HttpStatus.CREATED.value()));
	}

	@PutMapping("/atualizarcategoria/{id}")
	public ResponseEntity<ApiResponse<Categoria>> atualizarCategoria(@PathVariable Long id,
			@RequestBody Categoria categoria) {
		return categoriaRepository.findById(id).map(categoriaExistente -> {
			categoriaExistente.setNome(categoria.getNome());
			categoriaExistente.setDescricao(categoria.getDescricao());
			Categoria atualizada = categoriaRepository.save(categoriaExistente);
			return ResponseEntity
					.ok(new ApiResponse<>("Categoria atualizada com sucesso", atualizada, HttpStatus.OK.value()));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<>("Categoria não encontrada", null, HttpStatus.NOT_FOUND.value())));
	}

	@DeleteMapping("/excluircategoria/{id}")
	public ResponseEntity<ApiResponse<Void>> deletarCategoria(@PathVariable Long id) {
		Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);

		if (categoriaOptional.isPresent()) {
			List<Produto> produtosVinculados = produtoRepository.findByCategoriaId(id);

			if (!produtosVinculados.isEmpty()) {
				produtoRepository.deleteAll(produtosVinculados);
			}

			categoriaRepository.deleteById(id);

			return ResponseEntity.ok(new ApiResponse<>("Categoria e seus produtos excluídos com sucesso", null,
					HttpStatus.NO_CONTENT.value()));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Categoria não encontrada", null, HttpStatus.NOT_FOUND.value()));
		}
	}

}
