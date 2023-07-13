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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;
import net.ausiasmarch.wildcart.service.UsuarioService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService oUsuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> get(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<UsuarioEntity>(oUsuarioService.get(id), HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioEntity> getFromUsername(@PathVariable(value = "username") String username) {
        return new ResponseEntity<UsuarioEntity>(oUsuarioService.get(username), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oUsuarioService.count(), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Page<UsuarioEntity>> getPage(
           @ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
           @RequestParam(name = "filter", required = false) String strFilter,
           @RequestParam(name = "tipousuario", required = false) Long lTipoUsuario) {
        return new ResponseEntity<Page<UsuarioEntity>>(oUsuarioService.getPage(oPageable, strFilter, lTipoUsuario), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Long> create(@RequestBody UsuarioEntity oNewUsuarioEntity) {
        return new ResponseEntity<Long>(oUsuarioService.create(oNewUsuarioEntity), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Long> update(@RequestBody UsuarioEntity oUsuarioEntity) {
        return new ResponseEntity<Long>(oUsuarioService.update(oUsuarioEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Long>(oUsuarioService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<UsuarioEntity> generate() {
        return new ResponseEntity<UsuarioEntity>(oUsuarioService.generate(), HttpStatus.OK);
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<Long> generateSome(@PathVariable(value = "amount") Integer amount) {
        return new ResponseEntity<>(oUsuarioService.generateSome(amount), HttpStatus.OK);
    }

    @GetMapping("/flipactive/{id}")
    public ResponseEntity<UsuarioEntity> flipactive(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<UsuarioEntity>(oUsuarioService.flipActive(id), HttpStatus.OK);
    }

    @GetMapping("/flipvalid/{id}")
    public ResponseEntity<UsuarioEntity> flipvalid(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<UsuarioEntity>(oUsuarioService.flipValid(id), HttpStatus.OK);
    }
}
