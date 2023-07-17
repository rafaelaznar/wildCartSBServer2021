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

import javax.transaction.Transactional;
import net.ausiasmarch.wildcart.entity.FacturaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FacturaRepository extends JpaRepository<FacturaEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM factura where id not in (select distinct(id_factura) from compra)", nativeQuery = true)
    int purgeFacturas();

    @Query(value = "SELECT * FROM factura WHERE id_usuario = ?1", nativeQuery = true)
    Page<FacturaEntity> findByUsuarioId(Long id_usuario, Pageable pageable);

    @Query(value = "SELECT * FROM  factura f, usuario u  WHERE  f.id_usuario = u.id", nativeQuery = true)
    @Override
    Page<FacturaEntity> findAll(Pageable pageable);
    
    @Query(value = "SELECT * FROM  factura f, usuario u  WHERE  f.id_usuario = u.id and ((f.iva = ?1 OR f.fecha LIKE %?2%) OR (u.nombre LIKE  %?3% OR u.apellido1 LIKE  %?3% OR u.apellido2 LIKE  %?3%))", nativeQuery = true)
    Page<FacturaEntity> findByIvaContainingOrFechaContainingOrUsernameContaining(String iva, String fecha, String username, Pageable oPageable);

    @Query(value = "SELECT * FROM factura f, usuario u WHERE f.id_usuario = u.id and f.id_usuario = ?1 AND ((u.nombre LIKE  %?4% OR u.apellido1 LIKE  %?4% OR u.apellido2 LIKE  %?4%) OR (f.iva = ?2 OR f.fecha LIKE %?3%))", nativeQuery = true)
    Page<FacturaEntity> findByUsuarioIdAndIvaContainingOrFechaContainingOrUsernameContaining(long id_usuario, String iva, String fecha, String username, Pageable oPageable);

    @Query(value = "SELECT SUM(c.cantidad * c.precio) FROM factura f, compra c WHERE f.id_usuario = ?1 AND c.id_factura = f.id", nativeQuery = true)
    Double getTotalFacturasUsuario(long id_usuario);

    @Query(value = "SELECT SUM(c.cantidad * c.precio) FROM factura f, compra c WHERE f.id = ?1 AND c.id_factura = f.id", nativeQuery = true)
    Double getTotalFactura(long id_factura);

    Long countByUsuarioId(Long userID);
}
