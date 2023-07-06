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
package net.ausiasmarch.wildcart.repository;

import java.util.List;
import net.ausiasmarch.wildcart.entity.CompraEntity;
import net.ausiasmarch.wildcart.entity.FacturaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompraRepository extends JpaRepository<CompraEntity, Long> {

    Page<CompraEntity> findByFacturaId(Long factura, Pageable oPageable);

    Page<CompraEntity> findByProductoId(Long producto, Pageable oPageable);

    List<CompraEntity> findByFacturaIdOrderByFechaDesc(Long id_factura);

    @Query(value = "SELECT * FROM compra WHERE id_factura = ?1 AND (cantidad LIKE  %?2% OR precio LIKE %?3% OR fecha LIKE %?4% OR descuento_usuario LIKE %?5% OR descuento_producto LIKE %?6%)", nativeQuery = true)
    Page<CompraEntity> findByFacturaIdAndCantidadContainingOrPrecioContainingOrFechaContainingOrDescuento_usuarioContainingOrDescuento_productoContaining(long IdFactura, String cantidad, String precio, String fecha, String descuentoUsuario, String descuentoProducto, Pageable oPageable);

    @Query(value = "SELECT * FROM compra WHERE id LIKE  %?1% OR cantidad LIKE  %?2% OR precio LIKE %?3% OR fecha LIKE %?4% OR descuento_usuario LIKE %?5% OR descuento_producto LIKE %?6%", nativeQuery = true)
    Page<CompraEntity> findByIdContainingOrCantidadContainingOrPrecioContainingOrFechaContainingOrDescuento_usuarioContainingOrDescuento_productoContaining(String IdCompra, String cantidad, String precio, String fecha, String descuentoUsuario, String descuentoProducto, Pageable oPageable);

    @Query(value = "SELECT * FROM compra WHERE id_producto = ?1 AND (cantidad LIKE  %?2% OR precio LIKE %?3% OR fecha LIKE %?4% OR descuento_usuario LIKE %?5% OR descuento_producto LIKE %?6%)", nativeQuery = true)
    Page<CompraEntity> findByProductoIdAndCantidadContainingOrPrecioContainingOrFechaContainingOrDescuento_usuarioContainingOrDescuento_productoContaining(long IdProducto, String cantidad, String precio, String fecha, String descuentoUsuario, String descuentoProducto, Pageable oPageable);

    @Query(value = "SELECT * FROM compra WHERE id_factura = ?1 AND id_factura IN (SELECT id FROM factura WHERE id_usuario = ?2) AND (cantidad LIKE  %?3% OR precio LIKE %?4% OR fecha LIKE %?5% OR descuento_usuario LIKE %?6% OR descuento_producto LIKE %?7%)", nativeQuery = true)
    Page<CompraEntity> findByFacturaIdAndUsuarioIdAndCantidadContainingOrPrecioContainingOrFechaContainingOrDescuento_usuarioContainingOrDescuento_productoContaining(long id_factura, long id_usuario, String cantidad, String precio, String fecha, String descuentoUsuario, String descuentoProducto, Pageable oPageable);

    @Query(value = "SELECT * FROM compra WHERE id_factura = ?1 AND id_factura IN (SELECT id FROM factura WHERE id_usuario = ?2)", nativeQuery = true)
    Page<CompraEntity> findByFacturaIdAndUsuarioId(Long id_factura, Long id_usuario, Pageable oPageable);

    @Query(value = "SELECT * FROM compra WHERE id_producto=?1 AND id_factura IN (SELECT id FROM factura WHERE id_usuario = ?2)", nativeQuery = true)
    Page<CompraEntity> findByProductoIdAndUsuarioId(long id_producto, long id_usuario, Pageable oPageable);

    @Query(value = "SELECT * FROM compra WHERE id_producto=?1 AND id_factura IN (SELECT id FROM factura WHERE id_usuario = ?2) AND (cantidad LIKE  %?3% OR precio LIKE %?4% OR fecha LIKE %?5% OR descuento_usuario LIKE %?6% OR descuento_producto LIKE %?7%)", nativeQuery = true)
    Page<CompraEntity> findByProductoIdAndUsuarioIdAndCantidadContainingOrPrecioContainingOrFechaContainingOrDescuento_usuarioContainingOrDescuento_productoContaining(long id_producto, long id_usuario, String cantidad, String precio, String fecha, String descuentoUsuario, String descuentoProducto, Pageable oPageable);

    @Query(value = "SELECT * FROM compra where id_factura IN (SELECT id FROM factura WHERE id_usuario = ?1)", nativeQuery = true)
    Page<CompraEntity> findByUsuarioId(long id_usuario, Pageable oPageable);

    @Query(value = "SELECT * FROM compra where id_factura IN (SELECT id FROM factura WHERE id_usuario = ?1) AND (cantidad LIKE  %?2% OR precio LIKE %?3% OR fecha LIKE %?4% OR descuento_usuario LIKE %?5% OR descuento_producto LIKE %?6%)", nativeQuery = true)
    Page<CompraEntity> findByUsuarioIdAndCantidadContainingOrPrecioContainingOrFechaContainingOrDescuento_usuarioContainingOrDescuento_productoContaining(long id_usuario, String cantidad, String precio, String fecha, String descuentoUsuario, String descuentoProducto, Pageable oPageable);
}
