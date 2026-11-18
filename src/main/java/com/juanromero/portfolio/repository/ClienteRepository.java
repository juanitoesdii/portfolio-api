package com.juanromero.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juanromero.portfolio.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}