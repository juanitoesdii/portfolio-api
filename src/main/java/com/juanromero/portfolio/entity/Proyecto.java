package com.juanromero.portfolio.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 250)
    private String ubicacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_proyecto_id", nullable = false)
    private TipoProyecto tipoProyecto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_servicio_id", nullable = false)
    private TipoServicio tipoServicio;

    private LocalDate fechaPrevistaInicio;

    private LocalDate fechaPrevistaFinalizacion;

    private LocalDate fechaRealInicio;

    private LocalDate fechaRealFinalizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoProyecto estado = EstadoProyecto.NEGOCIACION;

    public Proyecto() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoProyecto getTipoProyecto() {
        return tipoProyecto;
    }

    public void setTipoProyecto(TipoProyecto tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public LocalDate getFechaPrevistaInicio() {
        return fechaPrevistaInicio;
    }

    public void setFechaPrevistaInicio(LocalDate fechaPrevistaInicio) {
        this.fechaPrevistaInicio = fechaPrevistaInicio;
    }

    public LocalDate getFechaPrevistaFinalizacion() {
        return fechaPrevistaFinalizacion;
    }

    public void setFechaPrevistaFinalizacion(LocalDate fechaPrevistaFinalizacion) {
        this.fechaPrevistaFinalizacion = fechaPrevistaFinalizacion;
    }

    public LocalDate getFechaRealInicio() {
        return fechaRealInicio;
    }

    public void setFechaRealInicio(LocalDate fechaRealInicio) {
        this.fechaRealInicio = fechaRealInicio;
    }

    public LocalDate getFechaRealFinalizacion() {
        return fechaRealFinalizacion;
    }

    public void setFechaRealFinalizacion(LocalDate fechaRealFinalizacion) {
        this.fechaRealFinalizacion = fechaRealFinalizacion;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
    }
}