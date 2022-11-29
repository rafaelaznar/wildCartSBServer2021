/*
 * Copyright (c) 2021
 *
 * by Rafael Angel Aznar Aparici (rafaaznar at gmail dot com) & 2021 DAW students
 *
 * WILDCART: Free Open Source Shopping Site
 *
 * Sources at:                https://github.com/rafaelaznar/wildCartSBServer2021
 * Database at:               https://github.com/rafaelaznar/wildCartSBServer2021
 * POSTMAN API at:            https://github.com/rafaelaznar/wildCartSBServer2021
 * Client at:                 https://github.com/rafaelaznar/wildCartAngularClient2021
 *
 * WILDCART is distributed under the MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.ausiasmarch.wildcart.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

@Entity
@Table(name = "producto")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nombre;
    private Integer existencias;
    private Double precio;
    private Long imagen;
    private int descuento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipoproducto")
    private TipoproductoEntity tipoproducto;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private final List<CarritoEntity> carritos;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private final List<CompraEntity> compras;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private final List<CommentEntity> comments;
    
    
    public ProductoEntity() {
        this.carritos = new ArrayList<>();
        this.compras = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public ProductoEntity(Long id) {
        this.carritos = new ArrayList<>();
        this.compras = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getExistencias() {
        return existencias;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Long getImagen() {
        return imagen;
    }

    public void setImagen(Long imagen) {
        this.imagen = imagen;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public void setDescuento(Integer descuento) {
        this.descuento = descuento;
    }

    public int getCarritos() {
        return this.carritos.size();
    }

    public int getCompras() {
        return compras.size();
    }

    public int getComments() {
        return comments.size();
    }
        
    public TipoproductoEntity getTipoproducto() {
        return tipoproducto;
    }

    public void setTipoproducto(TipoproductoEntity tipoproducto) {
        this.tipoproducto = tipoproducto;
    }

    @PreRemove
    public void nullify() {
        this.carritos.forEach(c -> c.setProducto(null));
        this.compras.forEach(c -> c.setProducto(null));
        this.comments.forEach(c -> c.setProducto(null));
    }
}
