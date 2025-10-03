package com.casemercado.teste.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.casemercado.teste.model.Categoria;
import com.casemercado.teste.model.Produto;
import com.casemercado.teste.repository.ICategoriaRepository;
import com.casemercado.teste.repository.IProdutoRepository;
import com.casemercado.teste.response.ApiResponse;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:62027")
public class ProdutoController {

	@Autowired
	private IProdutoRepository produtoRepository;

	@Autowired
	private ICategoriaRepository categoriaRepository;

	@GetMapping("/listarprodutos")
	public List<Produto> listarTodos() {
		return produtoRepository.findAll();
	}

	@PostMapping("/salvarproduto")
	public ResponseEntity<ApiResponse<Produto>> criarProduto(@RequestBody Produto produto) {
		
		if (produto.getNome() == null || produto.getNome().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("O nome do produto é obrigatório", null, HttpStatus.BAD_REQUEST.value()));
		}

		if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
			Optional<Categoria> categoriaOptional = categoriaRepository.findById(produto.getCategoria().getId());

			if (!categoriaOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ApiResponse<>("Categoria não encontrada", null, HttpStatus.BAD_REQUEST.value()));
			} else {
				produto.setCategoria(categoriaOptional.get());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new ApiResponse<>("A categoria do produto é obrigatória", null, HttpStatus.BAD_REQUEST.value()));
		}

		Produto produtoCriado = produtoRepository.save(produto);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse<>("Produto criado com sucesso", produtoCriado, HttpStatus.CREATED.value()));
	}

	@PutMapping("/atualizarproduto/{id}")
	public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produto) {
		return produtoRepository.findById(id).map(produtoExistente -> {
			produtoExistente.setNome(produto.getNome());
			produtoExistente.setValor(produto.getValor());
			produtoExistente.setQuantidade(produto.getQuantidade());

			if (produto.getCategoria() != null) {
				Long categoriaId = produto.getCategoria().getId();
				Optional<Categoria> categoriaOptional = categoriaRepository.findById(categoriaId);

				if (categoriaOptional.isPresent()) {
					Categoria categoria = categoriaOptional.get();
					produtoExistente.setCategoria(categoria);
				} else {
					System.out.println("Categoria não encontrada com ID: " + produto.getCategoria());
				}
			}

			Produto atualizado = produtoRepository.save(produtoExistente);
			return ResponseEntity.ok(atualizado);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}
	
	@GetMapping("/buscarporcategoria/{categoriaId}")
	public ResponseEntity<List<Produto>> buscarProdutosPorCategoria(@PathVariable Long categoriaId) {
	    List<Produto> produtos = produtoRepository.findByCategoriaId(categoriaId);
	    if (produtos.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(produtos);
	    } else {
	        return ResponseEntity.ok(produtos);
	    }
	}


	@DeleteMapping("/excluirproduto/{id}")
	public ResponseEntity<ApiResponse<Void>> deletarProduto(@PathVariable Long id) {
		if (produtoRepository.existsById(id)) {
			produtoRepository.deleteById(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body(new ApiResponse<>("Produto excluído com sucesso", null, HttpStatus.NO_CONTENT.value()));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Produto não encontrado", null, HttpStatus.NOT_FOUND.value()));
		}
	}
}