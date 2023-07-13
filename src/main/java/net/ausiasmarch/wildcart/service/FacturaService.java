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

import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import net.ausiasmarch.wildcart.exception.ResourceNotFoundException;
import net.ausiasmarch.wildcart.entity.FacturaEntity;
import net.ausiasmarch.wildcart.exception.CannotPerformOperationException;
import net.ausiasmarch.wildcart.helper.RandomHelper;
import net.ausiasmarch.wildcart.helper.ValidationHelper;
import net.ausiasmarch.wildcart.repository.FacturaRepository;
import net.ausiasmarch.wildcart.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    UsuarioService oUsuarioService;

    @Autowired
    AuthService oAuthService;

    public void validate(Long id) {
        if (!oFacturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public void validate(FacturaEntity oFacturaEntity) {
        ValidationHelper.validateDate(oFacturaEntity.getFecha(), LocalDateTime.of(1990, 01, 01, 00, 00, 00), LocalDateTime.of(2025, 01, 01, 00, 00, 00), "campo fecha de factura");
        ValidationHelper.isBetween(oFacturaEntity.getIva(), 0, 30, "campo iva de factura");
        oUsuarioService.validate(oFacturaEntity.getUsuario().getId());
    }

    public FacturaEntity get(Long id) {
        validate(id);
        FacturaEntity oFactura = oFacturaRepository.getById(id);
        oAuthService.OnlyAdminsOrOwnUsersData(oFactura.getUsuario().getId());
        return oFacturaRepository.getById(id);
    }

    public Long count() {
        oAuthService.OnlyAdmins();
        return oFacturaRepository.count();
    }

    public Page<FacturaEntity> getPage(Pageable oPageable, String strFilter, Long lUsuario) {
        oAuthService.OnlyAdminsOrUsers();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        if (oAuthService.isAdmin()) {
            if (lUsuario != null) {
                if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                    return oFacturaRepository.findByUsuarioId(lUsuario, oPageable);
                } else {
                    return oFacturaRepository.findByUsuarioIdAndIvaContainingOrFechaContaining(lUsuario, strFilter, strFilter, oPageable);
                }
            } else {
                if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                    return oFacturaRepository.findAll(oPageable);
                } else {
                    return oFacturaRepository.findByIvaContainingOrFechaContaining(strFilter, strFilter, oPageable);
                }
            }
        } else {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                return oFacturaRepository.findByUsuarioId(oAuthService.getUserID(), oPageable);
            } else {
                return oFacturaRepository.findByUsuarioIdAndIvaContainingOrFechaContaining(oAuthService.getUserID(), strFilter, strFilter, oPageable);
            }
        }
    }

    @Transactional
    public Long create(FacturaEntity oFacturaEntity) {
        oAuthService.OnlyAdminsOrOwnUsersData(oFacturaEntity.getUsuario().getId());
        validate(oFacturaEntity);
        oFacturaEntity.setId(0L);
        oUsuarioService.validate(oFacturaEntity.getUsuario().getId());
        if (!oAuthService.isAdmin()) {
            oFacturaEntity.setFecha(LocalDateTime.now());
        }
        return oFacturaRepository.save(oFacturaEntity).getId();
    }

    @Transactional
    public Long update(FacturaEntity oFacturaEntity) {
        validate(oFacturaEntity.getId());
        oAuthService.OnlyAdminsOrOwnUsersData(get(oFacturaEntity.getId()).getUsuario().getId());
        validate(oFacturaEntity);
        oUsuarioService.validate(oFacturaEntity.getUsuario().getId());
        if (!oAuthService.isAdmin()) {
            oFacturaEntity.setFecha(LocalDateTime.now());
        }
        return oFacturaRepository.save(oFacturaEntity).getId();
    }

    public Long delete(Long id) {
        validate(id);
        oAuthService.OnlyAdminsOrOwnUsersData(get(id).getUsuario().getId());
        oFacturaRepository.deleteById(id);
        return id;
    }

    public Long generateSome(int amount) {
        if (oUsuarioService.count() > 0) {
            for (int i = 0; i < amount; i++) {
                FacturaEntity oFacturaEntity = generate();
                oFacturaRepository.save(oFacturaEntity);
            }
            return oFacturaRepository.count();
        } else {
            throw new CannotPerformOperationException("no hay usuarios en la base de datos");
        }
    }

    public FacturaEntity generate() {
        if (oUsuarioRepository.count() > 0) {
            int[] ivas = {4, 10, 21};
            int iva = ivas[(int) (Math.floor(Math.random() * ((ivas.length - 1) - 0 + 1) + 0))];
            FacturaEntity oFacturaEntity = new FacturaEntity();
            oFacturaEntity.setFecha(RandomHelper.getRadomDateTime());
            oFacturaEntity.setIva(iva);
            oFacturaEntity.setUsuario(oUsuarioService.getOneRandom());
            if (RandomHelper.getRandomInt(0, 1) == 0) {
                oFacturaEntity.setPagado(true);
            } else {
                oFacturaEntity.setPagado(false);
            }
            return oFacturaEntity;
        } else {
            return null;
        }
    }

    public FacturaEntity getOneRandom() {
        FacturaEntity oFacturaEntity = null;
        int iPosicion = RandomHelper.getRandomInt(0, (int) oFacturaRepository.count() - 1);
        Pageable oPageable = PageRequest.of(iPosicion, 1);
        Page<FacturaEntity> facturaPage = oFacturaRepository.findAll(oPageable);
        List<FacturaEntity> facturaList = facturaPage.getContent();
        oFacturaEntity = oFacturaRepository.getById(facturaList.get(0).getId());
        return oFacturaEntity;
    }

    public Double getTotalFacturas4User(Long id_usuario) {
        oUsuarioService.validate(id_usuario);
        oAuthService.OnlyAdminsOrOwnUsersData(id_usuario);
        return oFacturaRepository.getTotalFacturasUsuario(id_usuario);
    }

    public Double getTotalFactura(Long id_factura) {
        this.validate(id_factura);
        FacturaEntity oFactura = oFacturaRepository.getById(id_factura);
        oAuthService.OnlyAdminsOrOwnUsersData(oFactura.getUsuario().getId());
        return oFacturaRepository.getTotalFactura(id_factura);
    }

    public int purge() {
        //delete facturas without compras (maintenance)
        oAuthService.OnlyAdmins();
        return oFacturaRepository.purgeFacturas();
    }

}
