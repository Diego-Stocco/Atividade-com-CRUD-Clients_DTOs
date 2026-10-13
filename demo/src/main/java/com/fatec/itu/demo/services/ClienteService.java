package com.fatec.itu.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.itu.demo.dtos.ClienteRequestDTO;
import com.fatec.itu.demo.dtos.ClienteResponseDTO;
import com.fatec.itu.demo.entites.Cliente;
import com.fatec.itu.demo.repositories.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(this::converterParaResposta)
                .toList();
    }

    public ClienteResponseDTO findById(Long id) {
        return converterParaResposta(obterCliente(id));
    }

    public ClienteResponseDTO save(ClienteRequestDTO dados) {
        validarDados(dados);

        if (clienteRepository.existsByCpf(dados.getCpf())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }

        Cliente novoCliente = new Cliente();
        copiarDados(dados, novoCliente);
        return converterParaResposta(clienteRepository.save(novoCliente));
    }

    public ClienteResponseDTO update(ClienteRequestDTO dados, Long id) {
        validarDados(dados);
        Cliente cliente = obterCliente(id);

        boolean cpfFoiAlterado = !dados.getCpf().equals(cliente.getCpf());
        if (cpfFoiAlterado && clienteRepository.existsByCpf(dados.getCpf())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }

        copiarDados(dados, cliente);
        return converterParaResposta(clienteRepository.save(cliente));
    }

    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não cadastrado.");
        }
        clienteRepository.deleteById(id);
    }

    private Cliente obterCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não cadastrado."));
    }

    private void validarDados(ClienteRequestDTO dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Os dados do cliente são obrigatórios.");
        }
        if (vazio(dados.getNome())) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }
        if (vazio(dados.getEmail())) {
            throw new IllegalArgumentException("O e-mail do cliente é obrigatório.");
        }
        if (vazio(dados.getCpf())) {
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
        }

        dados.setNome(dados.getNome().trim());
        dados.setEmail(dados.getEmail().trim());
        dados.setCpf(dados.getCpf().trim());
        if (dados.getTelefone() != null) {
            dados.setTelefone(dados.getTelefone().trim());
        }
    }

    private void copiarDados(ClienteRequestDTO origem, Cliente destino) {
        destino.setNome(origem.getNome());
        destino.setEmail(origem.getEmail());
        destino.setTelefone(origem.getTelefone());
        destino.setCpf(origem.getCpf());
    }

    private ClienteResponseDTO converterParaResposta(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCpf());
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
