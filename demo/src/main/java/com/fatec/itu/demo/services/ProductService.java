package com.fatec.itu.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.itu.demo.entites.Product;
import com.fatec.itu.demo.repositories.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não cadastrado."));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void update(Product dados, Long id) {
        Product produtoSalvo = findById(id);
        produtoSalvo.setName(dados.getName());
        produtoSalvo.setDescription(dados.getDescription());
        produtoSalvo.setPrice(dados.getPrice());
        productRepository.save(produtoSalvo);
    }

    public void deleteById(long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não cadastrado.");
        }
        productRepository.deleteById(id);
    }
}
