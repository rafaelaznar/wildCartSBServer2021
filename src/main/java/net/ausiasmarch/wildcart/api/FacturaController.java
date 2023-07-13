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

import net.ausiasmarch.wildcart.entity.FacturaEntity;
import net.ausiasmarch.wildcart.service.FacturaService;
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
@RequestMapping("/factura")

public class FacturaController {

    @Autowired
    FacturaService oFacturaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<FacturaEntity>(oFacturaService.get(id), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oFacturaService.count(), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Page<FacturaEntity>> getPage(
           @ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
           @RequestParam(name = "filter", required = false) String strFilter,
           @RequestParam(name = "usuario", required = false) Long lUsuario) {
        return new ResponseEntity<Page<FacturaEntity>>(oFacturaService.getPage(oPageable, strFilter, lUsuario), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Long> create(@RequestBody FacturaEntity oFacturaEntity) {
        return new ResponseEntity<Long>(oFacturaService.create(oFacturaEntity), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Long> update(@RequestBody FacturaEntity oFacturaEntity) {
        return new ResponseEntity<Long>(oFacturaService.update(oFacturaEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Long>(oFacturaService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<FacturaEntity> generate() {
        return new ResponseEntity<FacturaEntity>(oFacturaService.generate(), HttpStatus.OK);
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<Long> generateSome(@PathVariable(value = "amount") int amount) {
        return new ResponseEntity<Long>(oFacturaService.generateSome(amount), HttpStatus.OK);
    }

    @GetMapping("/total/usuario/{id}")
    public ResponseEntity<?> getTotalFacturasUsuario(@PathVariable(value = "id") Long id_usuario) {
        return new ResponseEntity<Double>(oFacturaService.getTotalFacturas4User(id_usuario), HttpStatus.OK);
    }

    @GetMapping("/total/{id}")
    public ResponseEntity<?> getTotalFactura(@PathVariable(value = "id") Long id_factura) {
        return new ResponseEntity<Double>(oFacturaService.getTotalFactura(id_factura), HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<Long> purgue() {
        return new ResponseEntity<Long>(oFacturaService.purge(), HttpStatus.OK);
    }
}
