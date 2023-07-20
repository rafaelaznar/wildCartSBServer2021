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
package net.ausiasmarch.wildcart.api;

import java.util.List;
import net.ausiasmarch.wildcart.entity.ProductoCarritoViewEntity;
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import net.ausiasmarch.wildcart.service.AuthService;
import net.ausiasmarch.wildcart.service.ProductoCarritoViewService;
import net.ausiasmarch.wildcart.service.ProductoService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    ProductoService oProductoService;

    @Autowired
    ProductoCarritoViewService oProductoCarritoViewService;

    @Autowired
    AuthService oAuthService;

    // /producto/3
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        //System.out.println("ProductoController petición: get: producto:" + id + " usuario: " + oAuthService.getUserID() + " is uaser: " + oAuthService.isUser());
        if (oAuthService.isUser()) {
            return new ResponseEntity<ProductoCarritoViewEntity>(oProductoCarritoViewService.get(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<ProductoEntity>(oProductoService.get(id), HttpStatus.OK);
        }
    }

    // producto/count
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oProductoService.count(), HttpStatus.OK);
    }

    // /producto?page=0&size=10&sort=precio,desc&filter=verde&tipoproducto=2
    @GetMapping("")
    public ResponseEntity<Page<ProductoEntity>> getPage(
           @ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
           @RequestParam(name = "filter", required = false) String strFilter,
           @RequestParam(name = "tipoproducto", required = false) Long lTipoProducto) {
        return new ResponseEntity<Page<ProductoEntity>>(oProductoService.getPage(oPageable, strFilter, lTipoProducto), HttpStatus.OK);
    }

    // producto/
    @PostMapping("/")
    public ResponseEntity<Long> create(@RequestBody ProductoEntity oProductoEntity) {
        return new ResponseEntity<Long>(oProductoService.create(oProductoEntity), HttpStatus.OK);
    }

    //producto/
    @PutMapping("/")
    public ResponseEntity<Long> update(@RequestBody ProductoEntity oProductoEntity) {
        return new ResponseEntity<Long>(oProductoService.update(oProductoEntity), HttpStatus.OK);
    }

    // producto/id
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Long>(oProductoService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<ProductoEntity> generate() {
        return new ResponseEntity<ProductoEntity>(oProductoService.generate(), HttpStatus.OK);
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<Long> generateSome(@PathVariable(value = "amount") int amount) {
        return new ResponseEntity<>(oProductoService.generateSome(amount), HttpStatus.OK);
    }

    @GetMapping("/nByDescuentoDesc/{quantity}")
    public ResponseEntity<List<ProductoEntity>> nByDescuentoDesc(@PathVariable(value = "quantity") Long quantity) {
        return new ResponseEntity<List<ProductoEntity>>(oProductoService.nByDescuentoDesc(quantity), HttpStatus.OK);
    }

    @GetMapping("/nByDescuentoAsc/{quantity}")
    public ResponseEntity<List<ProductoEntity>> nByDescuentoAsc(@PathVariable(value = "quantity") Long quantity) {
        return new ResponseEntity<List<ProductoEntity>>(oProductoService.nByDescuentoAsc(quantity), HttpStatus.OK);
    }

    @GetMapping("/nByExistenciasDesc/{quantity}")
    public ResponseEntity<List<ProductoEntity>> nByExistenciasDesc(@PathVariable(value = "quantity") Long quantity) {
        return new ResponseEntity<List<ProductoEntity>>(oProductoService.nByExistenciasDesc(quantity), HttpStatus.OK);
    }

    @GetMapping("/nByExistenciasAsc/{quantity}")
    public ResponseEntity<List<ProductoEntity>> nByExistenciasAsc(@PathVariable(value = "quantity") Long quantity) {
        return new ResponseEntity<List<ProductoEntity>>(oProductoService.nByExistenciasAsc(quantity), HttpStatus.OK);
    }

}
