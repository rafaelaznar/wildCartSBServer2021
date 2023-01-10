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
package net.ausiasmarch.wildcart.service;

import java.util.ArrayList;
import java.util.List;
import net.ausiasmarch.wildcart.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import net.ausiasmarch.wildcart.entity.TipousuarioEntity;
import net.ausiasmarch.wildcart.helper.ValidationHelper;
import net.ausiasmarch.wildcart.repository.TipousuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TipousuarioService {

    @Autowired
    TipousuarioRepository oTipousuarioRepository;

    @Autowired
    AuthService oAuthService;

    public void validate(Long id) {
        if (!oTipousuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public void validate(TipousuarioEntity oTipousuarioEntity) {
        ValidationHelper.validateStringLength(oTipousuarioEntity.getNombre(), 2, 100, "campo nombre de Tipousuario (el campo debe tener longitud de 2 a 100 caracteres)");
    }

    public List<TipousuarioEntity> generateUsersType() {
        List<TipousuarioEntity> usersTypeList = new ArrayList<>();
        usersTypeList.add(new TipousuarioEntity(1L, "administrador"));
        usersTypeList.add(new TipousuarioEntity(2L, "usuario"));
        return usersTypeList;
    }

    public TipousuarioEntity get(Long id) {
        validate(id);
        return oTipousuarioRepository.getById(id);
    }

    public List<TipousuarioEntity> all() {
        return oTipousuarioRepository.findAll();
    }

    public Long count() {
        return oTipousuarioRepository.count();
    }

    public Page<TipousuarioEntity> getPage(Pageable oPageable, String strFilter) {
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<TipousuarioEntity> oPage = null;
        if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
            oPage = oTipousuarioRepository.findAll(oPageable);
        } else {
            oPage = oTipousuarioRepository.findByNombreIgnoreCaseContaining(strFilter, oPageable);
        }
        return oPage;
    }

    public Long update(TipousuarioEntity oTipoUsuarioEntity) {
        oAuthService.OnlyAdmins();
        validate(oTipoUsuarioEntity.getId());
        validate(oTipoUsuarioEntity);
        return oTipousuarioRepository.save(oTipoUsuarioEntity).getId();
    }

    public Long generate() {
        oAuthService.OnlyAdmins();
        List<TipousuarioEntity> usersTypeList = generateUsersType();

        for (int i = usersTypeList.size() - 1; i >= 0; i--) {
            oTipousuarioRepository.save(usersTypeList.get(i));
        }
        return oTipousuarioRepository.count();
    }
}
