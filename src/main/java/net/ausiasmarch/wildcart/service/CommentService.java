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

import java.util.List;
import net.ausiasmarch.wildcart.entity.CommentEntity;
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;
import net.ausiasmarch.wildcart.helper.RandomHelper;
import net.ausiasmarch.wildcart.repository.CommentRepository;
import net.ausiasmarch.wildcart.repository.ProductoRepository;
import net.ausiasmarch.wildcart.repository.UsuarioRepository;

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

    private final String[] WORDS = {"de", "a", "pero", "toma", "donde", "con", "un", "antes", "total", "mismo", "ahora", "sin", "hay", "en"};

    public Long generateSome() {

        List<ProductoEntity> oProductoList = oProductoRepository.findAll();
        for (int i = 0; i < oProductoList.size(); i++) {
            for (int j = 0; j <= 25; j++) {
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
        for (int i = 0; i < oUsuarioList.size(); i++) {
            for (int j = 0; j <= 25; j++) {
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
        return comment;
    }

    private String generateWord() {
        return WORDS[RandomHelper.getRandomInt(0, WORDS.length - 1)].toLowerCase();
    }

}
