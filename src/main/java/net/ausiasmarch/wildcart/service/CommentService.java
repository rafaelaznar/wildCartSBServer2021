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
import javax.servlet.http.HttpSession;
import net.ausiasmarch.wildcart.entity.CommentEntity;
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;
import net.ausiasmarch.wildcart.exception.ResourceNotFoundException;
import net.ausiasmarch.wildcart.exception.ResourceNotModifiedException;
import net.ausiasmarch.wildcart.exception.UnauthorizedException;
import net.ausiasmarch.wildcart.helper.RandomHelper;
import net.ausiasmarch.wildcart.helper.ValidationHelper;
import net.ausiasmarch.wildcart.repository.CommentRepository;
import net.ausiasmarch.wildcart.repository.ProductoRepository;
import net.ausiasmarch.wildcart.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class CommentService {

    @Autowired
    CommentRepository oCommentRepository;

    @Autowired
    ProductoRepository oProductoRepository;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    UsuarioService oUsuarioService;

    @Autowired
    ProductoService oProductoService;

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    AuthService oAuthService;

    private final String[] WORDS = {"de", "a", "pero", "toma", "donde", "con", "un", "antes", "total", "mismo", "ahora", "sin", "hay", "en"};

    public void validate(Long id) {
    }

    public void validate(CommentEntity oCommentEntity) {
        ValidationHelper.validateStringLength(oCommentEntity.getComment(), 10, 200, "error in comment length");
    }

    public CommentEntity get(Long id) {
        try {
            return oCommentRepository.findById(id).get();
        } catch (Exception ex) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public Page<CommentEntity> getPage(Pageable oPageable, String strFilter, Long id_usuario, Long id_producto) {
        //oAuthService.OnlyAdmins();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        if (strFilter == null || strFilter.length() == 0) {
            if (id_usuario == null) {
                if (id_producto == null) {
                    return oCommentRepository.findAll(oPageable);
                } else {
                    return oCommentRepository.findByProductoId(id_producto, oPageable);
                }
            } else {
                if (id_producto == null) {
                    return oCommentRepository.findByUsuarioId(id_usuario, oPageable);
                } else {
                    return oCommentRepository.findByUsuarioIdAndProductoId(id_usuario, id_producto, oPageable);
                }
            }
        } else {
            if (id_usuario == null) {
                if (id_producto == null) {
                    return oCommentRepository.findByCommentIgnoreCaseContaining(strFilter, oPageable);
                } else {
                    return oCommentRepository.findByCommentIgnoreCaseContainingAndProductoId(strFilter, id_producto, oPageable);
                }
            } else {
                if (id_producto == null) {
                    return oCommentRepository.findByCommentIgnoreCaseContainingAndUsuarioId(strFilter, id_usuario, oPageable);
                } else {
                    return oCommentRepository.findByCommentIgnoreCaseContainingAndUsuarioIdAndProductoId(strFilter, id_usuario, id_producto, oPageable);
                }
            }
        }

    }

    public Long count(String strFilter, Long id_usuario, Long id_producto) {
        //oAuthService.OnlyAdmins();

        if (strFilter == null || strFilter.length() == 0) {
            if (id_usuario == null) {
                if (id_producto == null) {
                    return oCommentRepository.count();
                } else {
                    return oCommentRepository.countByProductoId(id_producto);
                }
            } else {
                if (id_producto == null) {
                    return oCommentRepository.countByUsuarioId(id_usuario);
                } else {
                    return oCommentRepository.countByUsuarioIdAndProductoId(id_usuario, id_producto);
                }
            }
        } else {
            if (id_usuario == null) {
                if (id_producto == null) {
                    return oCommentRepository.countByCommentIgnoreCaseContaining(strFilter);
                } else {
                    return oCommentRepository.countByCommentIgnoreCaseContainingAndProductoId(strFilter, id_producto);
                }
            } else {
                if (id_producto == null) {
                    return oCommentRepository.countByCommentIgnoreCaseContainingAndUsuarioId(strFilter, id_usuario);
                } else {
                    return oCommentRepository.countByCommentIgnoreCaseContainingAndUsuarioIdAndProductoId(strFilter, id_usuario, id_producto);
                }
            }
        }
    }

    public Long create(CommentEntity oNewCommentEntity) {
        validate(oNewCommentEntity);
        if (oAuthService.isAdmin()) {            
            oNewCommentEntity.setId(0L);
            oNewCommentEntity.setProducto(oProductoService.get(oNewCommentEntity.getProducto().getId()));
            oNewCommentEntity.setUsuario(oUsuarioService.get(oNewCommentEntity.getUsuario().getId()));
            return oCommentRepository.save(oNewCommentEntity).getId();
        } else {
            if (oAuthService.isUser()) {
                oNewCommentEntity.setId(0L);
                oNewCommentEntity.setUsuario(oAuthService.getUser());
                //fechas
                oNewCommentEntity.setCreation(LocalDateTime.now());
                oNewCommentEntity.setLastedition(null);
                return oCommentRepository.save(oNewCommentEntity).getId();
            } else {
                throw new UnauthorizedException("no active session");
            }
        }
    }

    public Long update(CommentEntity oNewCommentEntity) {
        validate(oNewCommentEntity);
        if (oAuthService.isAdmin()) {                        
            oNewCommentEntity.setProducto(oProductoService.get(oNewCommentEntity.getProducto().getId()));
            oNewCommentEntity.setUsuario(oUsuarioService.get(oNewCommentEntity.getUsuario().getId()));
            return oCommentRepository.save(oNewCommentEntity).getId();
        } else {
            if (oAuthService.isUser()) {
                oNewCommentEntity.setUsuario(oAuthService.getUser());
                //fechas                                               
                oNewCommentEntity.setCreation(oCommentRepository.getById(oNewCommentEntity.getId()).getCreation());
                oNewCommentEntity.setLastedition(LocalDateTime.now());
                return oCommentRepository.save(oNewCommentEntity).getId();
            } else {
                throw new UnauthorizedException("no active session");
            }
        }
    }
           
    public Long delete(Long id) {
        oAuthService.OnlyAdminsOrOwnUsersData(oCommentRepository.getById(id).getUsuario().getId());
        if (oCommentRepository.existsById(id)) {
            oCommentRepository.deleteById(id);
            if (oCommentRepository.existsById(id)) {
                throw new ResourceNotModifiedException("can't remove register " + id);
            } else {
                return id;
            }
        } else {
            throw new ResourceNotModifiedException("id " + id + " not exist");
        }
    }

    public Long generateSome() {

        oHttpSession.setAttribute("usuario", oUsuarioRepository.getById(1L));

        List<ProductoEntity> oProductoList = oProductoRepository.findAll();
        for (int i = 0; i < oProductoList.size() - 1; i++) {
            for (int j = 0; j < 25; j++) {
                CommentEntity oCommentEntity = new CommentEntity();
                oCommentEntity.setProducto(oProductoList.get(i));
                oCommentEntity.setUsuario(oUsuarioService.getOneRandom());
                oCommentEntity.setCreation(RandomHelper.getRadomDateTime());
                oCommentEntity.setLastedition(null);
                oCommentEntity.setComment(this.generateComment());
                oCommentRepository.save(oCommentEntity);
            }
        }

        List<UsuarioEntity> oUsuarioList = oUsuarioRepository.findAll();
        for (int i = 0; i < oUsuarioList.size() - 1; i++) {
            for (int j = 0; j < 25; j++) {
                CommentEntity oCommentEntity = new CommentEntity();
                oCommentEntity.setProducto(oProductoService.getOneRandom());
                oCommentEntity.setUsuario(oUsuarioList.get(i));
                oCommentEntity.setCreation(RandomHelper.getRadomDateTime());
                oCommentEntity.setLastedition(null);
                oCommentEntity.setComment(this.generateComment());
                oCommentRepository.save(oCommentEntity);
            }
        }
        return oCommentRepository.count();
    }

    private String generateComment() {
        int repe = RandomHelper.getRandomInt(1, 10);
        String comment = "";
        for (int j = 1; j <= repe; j++) {
            comment += this.generateWord() + " ";
        }
        return comment.trim();
    }

    private String generateWord() {
        return WORDS[RandomHelper.getRandomInt(0, WORDS.length - 1)].toLowerCase();
    }

}
