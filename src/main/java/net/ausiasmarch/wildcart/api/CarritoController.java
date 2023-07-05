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

import net.ausiasmarch.wildcart.entity.CarritoEntity;
import net.ausiasmarch.wildcart.service.CarritoService;
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
@RequestMapping("/carrito")
public class CarritoController {
    
    CarritoService oCarritoService;

    @Autowired
    public CarritoController(CarritoService oCarritoService) {
        this.oCarritoService = oCarritoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoEntity> get(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<CarritoEntity>(oCarritoService.get(id), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oCarritoService.count(), HttpStatus.OK);

    }

    @GetMapping("")
    public ResponseEntity<Page<CarritoEntity>> getPage(
            @ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
            @RequestParam(name = "id_producto", required = false) Long id_producto,
            @RequestParam(name = "id_usuario", required = false) Long id_usuario) {
        return new ResponseEntity<Page<CarritoEntity>>(oCarritoService.getPage(oPageable, id_usuario, id_producto),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CarritoEntity carritoEntity) {
        return new ResponseEntity<Long>(oCarritoService.create(carritoEntity), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Long> update(@RequestBody CarritoEntity carritoEntity) {
        return new ResponseEntity<Long>(oCarritoService.update(carritoEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Long>(oCarritoService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<Long> generate(@PathVariable(value = "amount") Long amount) {
        return new ResponseEntity<Long>(oCarritoService.generate(amount), HttpStatus.OK);
    }

    // API4USERS
    @PostMapping("/{id}/{amount}")
    public ResponseEntity<Long> add(@PathVariable(value = "id") long id, @PathVariable(value = "amount") int amount) {
        return new ResponseEntity<>(oCarritoService.add(id, amount), HttpStatus.OK);
    }

    @PutMapping("/buy")
    public ResponseEntity<Long> buy() {
        return new ResponseEntity<Long>(oCarritoService.purchase(), HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<Long> empty() {
        return new ResponseEntity<Long>(oCarritoService.empty(), HttpStatus.OK);
    }

    @DeleteMapping("/producto/{id_producto}")
    public ResponseEntity<Long> empty(
            @PathVariable(value = "id_producto") long id_producto) {
        return new ResponseEntity<Long>(oCarritoService.empty(id_producto), HttpStatus.OK);
    }

    @DeleteMapping("/producto/{id_producto}/{amount}")
    public ResponseEntity<Long> reduce(
            @PathVariable(value = "id_producto") long id_producto,
            @PathVariable(value = "amount") int amount) {
        return new ResponseEntity<Long>(oCarritoService.reduce(id_producto, amount), HttpStatus.OK);
    }

    @GetMapping("/total")
    public ResponseEntity<Double> total() {
        return new ResponseEntity<Double>(oCarritoService.getTotal(), HttpStatus.OK);
    }

}
