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
import java.util.Optional;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    UsuarioEntity findByLoginAndPassword(String login, String password); //oAuth

    Optional<UsuarioEntity> findByLogin(String login); //oAuth

    Optional<UsuarioEntity> findByEmail(String login);

    Optional<UsuarioEntity> findByToken(String token);

    boolean existsByLogin(String login);

    @Query(value = "SELECT * FROM usuario WHERE id_tipousuario = ?1 AND (dni like %?2% OR nombre LIKE %?3% OR apellido1 LIKE %?4% OR apellido2 LIKE %?5%)", nativeQuery = true)
    Page<UsuarioEntity> findByTipousuarioIdAndDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(Long filtertype, String dni, String nombre, String apellido1, String apellido2, Pageable oPageable);

    Page<UsuarioEntity> findByDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(String dni, String nombre, String apellido1, String apellido2, Pageable oPageable);

    Page<UsuarioEntity> findByTipousuarioId(Long tipoproducto, Pageable oPageable);

    //Reports
    public List<UsuarioEntity> findTop10ByOrderByDescuentoDesc();

    public List<UsuarioEntity> findTop50ByOrderByDescuentoDesc();

    public List<UsuarioEntity> findTop100ByOrderByDescuentoDesc();

    public List<UsuarioEntity> findTop10ByOrderByDescuentoAsc();

    public List<UsuarioEntity> findTop50ByOrderByDescuentoAsc();

    public List<UsuarioEntity> findTop100ByOrderByDescuentoAsc();

}
