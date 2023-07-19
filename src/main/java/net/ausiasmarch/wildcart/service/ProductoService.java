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
import javax.transaction.Transactional;
import net.ausiasmarch.wildcart.exception.ResourceNotFoundException;
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import net.ausiasmarch.wildcart.helper.RandomHelper;
import net.ausiasmarch.wildcart.helper.ValidationHelper;
import net.ausiasmarch.wildcart.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    @Autowired
    TipoProductoService oTipoproductoService;

    @Autowired
    ProductoRepository oProductoRepository;

    @Autowired
    AuthService oAuthService;

    private final String[] PRODUCTO = {"Silla", "Mesa", "Armario", "Cama", "Marco", "Mesita", "Aparador", "Lampara", "Sillón", "Sofá", "Vitrina"};
    private final String[] MATERIAL = {"madera", "metal", "plastico", "bambú", "cristal", "cartón", "mármol"};
    private final String[] COLOR = {"blanco", "negro", "verde", "azul", "rojo", "rosa", "amarillo", "marron", "morado", "naranja"};
    private final String[] TAMANYO = {"pequeño", "mediano", "grade", "extra grande"};
    private final String[] LETTERS_CODE = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public void validate(Long id) {
        if (!oProductoRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public void validate(ProductoEntity oProductoEntity) {
        ValidationHelper.isBetween(oProductoEntity.getCodigo(), 2, 50, "codigo en ProductoService (el campo debe tener longitud de 2 a 50 caracteres)");
        ValidationHelper.isBetween(oProductoEntity.getNombre(), 2, 255, "codigo en ProductoService (el campo debe tener longitud de 2 a 255 caracteres)");
        ValidationHelper.isBetween(oProductoEntity.getExistencias(), 0, 1000, "campo existencias (de 0 a 1000)");
        ValidationHelper.isBetween(oProductoEntity.getPrecio(), 0, 100000, "campo precio (de 0 a 100000)");
        ValidationHelper.isBetween(oProductoEntity.getDescuento(), 0, 100, "campo descuento (de 0 a 100)");
        oTipoproductoService.validate(oProductoEntity.getTipoproducto().getId());
    }

    public ProductoEntity get(Long id) {
        validate(id);
        return oProductoRepository.getById(id);
    }

    public Long count() {
        return oProductoRepository.count();
    }

    public Page<ProductoEntity> getPage(Pageable oPageable, String strFilter, Long lTipoProducto) {
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<ProductoEntity> oPage = null;
        if (lTipoProducto == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oProductoRepository.findAll(oPageable);
            } else {
                oPage = oProductoRepository.findByNombreIgnoreCaseContainingOrCodigoIgnoreCaseContaining(strFilter, strFilter, oPageable);
            }
        } else {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oProductoRepository.findByTipoproductoId(lTipoProducto, oPageable);
            } else {
                oPage = oProductoRepository.findByTipoproductoIdAndNombreOrCodigo(lTipoProducto, strFilter, strFilter, oPageable);
            }
        }
        return oPage;
    }

    @Transactional
    public Long create(ProductoEntity oProductoEntity) {
        oAuthService.OnlyAdmins();
        validate(oProductoEntity);
        oTipoproductoService.validate(oProductoEntity.getTipoproducto().getId());
        oProductoEntity.setTipoproducto(oTipoproductoService.get(oProductoEntity.getTipoproducto().getId()));
        oProductoEntity.setId(null);
        return ((ProductoEntity) oProductoRepository.save(oProductoEntity)).getId();
    }

    @Transactional
    public Long update(ProductoEntity oProductoEntity) {
        oAuthService.OnlyAdmins();
        validate(oProductoEntity.getId());
        validate(oProductoEntity);
        oTipoproductoService.validate(oProductoEntity.getTipoproducto().getId());
        oProductoEntity.setTipoproducto(oTipoproductoService.get(oProductoEntity.getTipoproducto().getId()));
        return oProductoRepository.save(oProductoEntity).getId();
    }

    public Long delete(Long id) {
        oAuthService.OnlyAdmins();
        validate(id);
        oProductoRepository.deleteById(id);
        return id;
    }

    public Long generateSome(int amount) {
        oAuthService.OnlyAdmins();
        for (int i = 0; i < amount; i++) {
            ProductoEntity oProductoEntity;
            oProductoEntity = generate();
            oProductoRepository.save(oProductoEntity);
        }
        return oProductoRepository.count();
    }

    public ProductoEntity generate() {
        ProductoEntity oProductoEntity = new ProductoEntity();
        oProductoEntity.setCodigo(Integer.toString(RandomHelper.getRandomInt(1, 100000)) + LETTERS_CODE[RandomHelper.getRandomInt(0, 25)]);
        oProductoEntity.setNombre(generateProduct());
        oProductoEntity.setExistencias(RandomHelper.getRandomInt(0, 100));
        oProductoEntity.setPrecio(RandomHelper.getRadomDouble(0, 100));
        //oProductoEntity.setImagen((long) RandomHelper.getRandomInt(0, 100));
        oProductoEntity.setImagen(1L);
        oProductoEntity.setDescuento(RandomHelper.getRandomInt(0, 51));
        //oProductoEntity.setTipoproducto(oTipoProductoRepository.getById((long) RandomHelper.getRandomInt(1, (int) oTipoProductoRepository.count())));
        oProductoEntity.setTipoproducto(oTipoproductoService.getOneRandom());
        return oProductoEntity;
    }

    private String generateProduct() {
        String name = PRODUCTO[RandomHelper.getRandomInt(0, PRODUCTO.length - 1)];
        String material = MATERIAL[RandomHelper.getRandomInt(0, MATERIAL.length - 1)].toLowerCase();
        String color = COLOR[RandomHelper.getRandomInt(0, COLOR.length - 1)].toLowerCase();
        String tamanyo = TAMANYO[RandomHelper.getRandomInt(0, TAMANYO.length - 1)].toLowerCase();
        return name + " de " + material + " de color " + color + " de tamaño " + tamanyo;
    }

    public ProductoEntity getOneRandom() {
        ProductoEntity oProductoEntity = null;
        int iPosicion = RandomHelper.getRandomInt(0, (int) oProductoRepository.count() - 1);
        Pageable oPageable = PageRequest.of(iPosicion, 1);
        Page<ProductoEntity> tipoProductoPage = oProductoRepository.findAll(oPageable);
        List<ProductoEntity> tipoProductoList = tipoProductoPage.getContent();
        oProductoEntity = oProductoRepository.getById(tipoProductoList.get(0).getId());
        return oProductoEntity;
    }

    public List<ProductoEntity> nByDescuentoDesc(Long quantity) {
        oAuthService.OnlyAdmins();
        if (quantity <= 10) {
            return oProductoRepository.findTop10ByOrderByDescuentoDesc();
        } else {
            if (quantity <= 50) {
                return oProductoRepository.findTop50ByOrderByDescuentoDesc();
            } else {
                if (quantity <= 100) {
                    return oProductoRepository.findTop100ByOrderByDescuentoDesc();
                } else {
                    throw new ResourceNotFoundException("quantity not affordable");
                }
            }
        }
    }

    public List<ProductoEntity> nByDescuentoAsc(Long quantity) {
        oAuthService.OnlyAdmins();
        if (quantity <= 10) {
            return oProductoRepository.findTop10ByOrderByDescuentoAsc();
        } else {
            if (quantity <= 50) {
                return oProductoRepository.findTop50ByOrderByDescuentoAsc();
            } else {
                if (quantity <= 100) {
                    return oProductoRepository.findTop100ByOrderByDescuentoAsc();
                } else {
                    throw new ResourceNotFoundException("quantity not affordable");
                }
            }
        }
    }    
    
}
