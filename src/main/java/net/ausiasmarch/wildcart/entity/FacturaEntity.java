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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
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
import javax.persistence.PostLoad;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "factura")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime fecha;

    private int iva;
    private boolean pagado;

    @Transient
    private Double total;

    //@OneToMany(mappedBy = "factura", fetch = FetchType.LAZY, cascade = CascadeType.ALL )
    @OneToMany(mappedBy = "factura", fetch = FetchType.LAZY)
    private final List<CompraEntity> compras;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    public FacturaEntity() {
        this.compras = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getIva() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva = iva;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public int getCompras() {
        return compras.size();
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public Double getTotal() {
        Double dtotal = 0D;
        for (int i = 0; i < compras.size(); i++) {
            dtotal += ((compras.get(i).getPrecio() * compras.get(i).getCantidad())
                   - (compras.get(i).getPrecio() * compras.get(i).getCantidad() * ((double) compras.get(i).getDescuento_producto() / 100))
                   - (compras.get(i).getPrecio() * compras.get(i).getCantidad() * ((double) compras.get(i).getDescuento_usuario() / 100)));
        }
        return dtotal;
    }

    @PreRemove
    public void nullify() {
        this.compras.forEach(c -> c.setFactura(null));
    }
}
