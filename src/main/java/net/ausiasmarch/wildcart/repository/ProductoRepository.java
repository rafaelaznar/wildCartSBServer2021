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
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    Page<ProductoEntity> findByNombreIgnoreCaseContainingOrCodigoIgnoreCaseContaining(String nombre, String codigo, Pageable oPageable);

    Page<ProductoEntity> findByTipoproductoId(Long id_tipoproducto, Pageable oPageable);

    @Query(value = "SELECT * FROM producto WHERE id_tipoproducto = ?1 AND (nombre LIKE  %?2% OR codigo LIKE %?3%)", nativeQuery = true)
    Page<ProductoEntity> findByTipoproductoIdAndNombreOrCodigo(long id_tipoproducto, String nombre, String codigo, Pageable oPageable);

    //Reports
    public List<ProductoEntity> findTop10ByOrderByDescuentoDesc();

    public List<ProductoEntity> findTop50ByOrderByDescuentoDesc();

    public List<ProductoEntity> findTop100ByOrderByDescuentoDesc();

    public List<ProductoEntity> findTop10ByOrderByDescuentoAsc();

    public List<ProductoEntity> findTop50ByOrderByDescuentoAsc();

    public List<ProductoEntity> findTop100ByOrderByDescuentoAsc();

    public List<ProductoEntity> findTop10ByOrderByExistenciasDesc();

    public List<ProductoEntity> findTop50ByOrderByExistenciasDesc();

    public List<ProductoEntity> findTop100ByOrderByExistenciasDesc();

    public List<ProductoEntity> findTop10ByOrderByExistenciasAsc();

    public List<ProductoEntity> findTop50ByOrderByExistenciasAsc();

    public List<ProductoEntity> findTop100ByOrderByExistenciasAsc();
}
