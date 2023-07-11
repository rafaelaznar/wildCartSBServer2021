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
import net.ausiasmarch.wildcart.exception.UnauthorizedException;
import net.ausiasmarch.wildcart.entity.CarritoEntity;
import net.ausiasmarch.wildcart.entity.CompraEntity;
import net.ausiasmarch.wildcart.entity.FacturaEntity;
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;
import net.ausiasmarch.wildcart.exception.CannotPerformOperationException;
import net.ausiasmarch.wildcart.exception.ResourceNotFoundException;
import net.ausiasmarch.wildcart.helper.RandomHelper;
import net.ausiasmarch.wildcart.helper.ValidationHelper;
import net.ausiasmarch.wildcart.repository.CarritoRepository;
import net.ausiasmarch.wildcart.repository.CompraRepository;
import net.ausiasmarch.wildcart.repository.FacturaRepository;
import net.ausiasmarch.wildcart.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class CarritoService {

    @Autowired
    ProductoService oProductoService;

    @Autowired
    UsuarioService oUsuarioService;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    CarritoRepository oCarritoRepository;

    @Autowired
    CompraRepository oCompraRepository;

    @Autowired
    ProductoRepository oProductoRepository;

    @Autowired
    AuthService oAuthService;

    public void validate(Long id) {
        if (!oCarritoRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public void validate(CarritoEntity oCarritoEntity) {
        ValidationHelper.isBetween(oCarritoEntity.getCantidad(), 1, 1000, "campo cantidad de la entidad carrito (debe ser un entero entre 1 y 1000)");
        //el precio sale de la bd: se copia del precio del producto, se supone que está validado. Este campo se borrará porque el precio se fija en la compra!!!
        ValidationHelper.isBetween(oCarritoEntity.getPrecio(), 1, 10000, "campo precio de la entidad carrito (debe ser un entero entre 0 y 10000)");
        oUsuarioService.validate(oCarritoEntity.getUsuario().getId());
        oProductoService.validate(oCarritoEntity.getProducto().getId());
    }

    public Double getTotal() {
        oAuthService.OnlyUsers();
        return oCarritoRepository.getTotalByUser(oAuthService.getUserID());
    }    
    
// admins services
    public CarritoEntity get(Long id) {
        validate(id);
        return oCarritoRepository.getById(id);
    }

    public Long count() {
        oAuthService.OnlyAdminsOrUsers();
        if (oAuthService.isAdmin()) {
            return oCarritoRepository.count();
        } else {
            return oCarritoRepository.countByUsuarioId(oAuthService.getUserID());
        }
    }

    public Page<CarritoEntity> getPage(Pageable oPageable, Long lUsuario, Long lProducto) {
        oAuthService.OnlyAdminsOrUsers();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<CarritoEntity> oPage = null;
        if (oAuthService.isAdmin()) {
            if (lUsuario != null) {
                oPage = oCarritoRepository.findByUsuarioId(lUsuario, oPageable);
            } else if (lProducto != null) {
                oPage = oCarritoRepository.findByProductoId(lProducto, oPageable);
            } else {
                oPage = oCarritoRepository.findAll(oPageable);
            }
        } else {
            oPage = oCarritoRepository.findByUsuarioId(oAuthService.getUserID(), oPageable);
        }
        return oPage;
    }

    @Transactional
    public Long create(CarritoEntity oCarritoEntity) {
        oAuthService.OnlyAdmins(); //users must use add option
        validate(oCarritoEntity);
        oCarritoEntity.setId(null);
        oProductoService.validate(oCarritoEntity.getProducto().getId());
        oCarritoEntity.setProducto(oProductoService.get(oCarritoEntity.getProducto().getId()));
        oUsuarioService.validate(oCarritoEntity.getUsuario().getId());
        oCarritoEntity.setUsuario(oUsuarioService.get(oCarritoEntity.getUsuario().getId()));
        return oCarritoRepository.save(oCarritoEntity).getId();
    }

    @Transactional
    public Long update(CarritoEntity oCarritoEntity) {
        oAuthService.OnlyAdmins();
        validate(oCarritoEntity.getId());
        validate(oCarritoEntity);
        oProductoService.validate(oCarritoEntity.getProducto().getId());
        oCarritoEntity.setProducto(oProductoService.get(oCarritoEntity.getProducto().getId()));
        oUsuarioService.validate(oCarritoEntity.getUsuario().getId());
        oCarritoEntity.setUsuario(oUsuarioService.get(oCarritoEntity.getUsuario().getId()));
        return oCarritoRepository.save(oCarritoEntity).getId();
    }

    public Long delete(Long id) {
        validate(id);
        oAuthService.OnlyAdminsOrOwnUsersData(oCarritoRepository.getById(id).getUsuario().getId());
        oCarritoRepository.deleteById(id);
        return id;
    }

    public Long generate(Long amount) {
        oAuthService.OnlyAdmins();
        for (int i = 0; i < amount; i++) {
            CarritoEntity oCarrito = new CarritoEntity();
            oCarrito.setUsuario(oUsuarioService.getOneRandom());
            oCarrito.setProducto(oProductoService.getOneRandom());
            oCarrito.setCantidad(RandomHelper.getRandomInt(1, 10));
            oCarritoRepository.save(oCarrito);
        }
        return count();
    }

// users services
    @Transactional
    public Long add(Long id_producto, int amount) {
        oAuthService.OnlyUsers();
        ProductoEntity oProducto = oProductoService.get(id_producto);
        oProductoService.validate(oProducto.getId());
        if (amount > 0 && amount <= 1000) {
            if (oCarritoRepository.countByUsuarioIdAndProductoId(oAuthService.getUserID(), oProducto.getId()) == 0) {
                CarritoEntity oCarritoEntity = new CarritoEntity();
                oCarritoEntity.setId(null);
                oCarritoEntity.setProducto(oProductoService.get(oProducto.getId()));
                oCarritoEntity.setUsuario(oUsuarioService.get(oAuthService.getUserID()));
                oCarritoEntity.setCantidad(amount);
                oCarritoRepository.save(oCarritoEntity);
            } else {
                List<CarritoEntity> oCarritoEntityList = oCarritoRepository.findByUsuarioIdAndProductoId(oAuthService.getUserID(), oProducto.getId());
                if (oCarritoEntityList.size() == 1) {
                    CarritoEntity oCarritoEntity = oCarritoEntityList.get(0);
                    oCarritoEntity.setCantidad(oCarritoEntity.getCantidad() + amount);
                    oCarritoRepository.save(oCarritoEntity);
                } else {
                    Long sum = 0L;
                    for (int i = 0; i < oCarritoEntityList.size(); i++) {
                        sum = sum + oCarritoEntityList.get(i).getCantidad();
                    }
                    oCarritoRepository.deleteByUsuarioIdAndProductoId(oAuthService.getUserID(), oProducto.getId());
                    CarritoEntity oCarritoEntity = new CarritoEntity();
                    oCarritoEntity.setId(null);
                    oCarritoEntity.setProducto(oProductoService.get(oProducto.getId()));
                    oCarritoEntity.setUsuario(oUsuarioService.get(oAuthService.getUserID()));
                    oCarritoEntity.setCantidad((int) (sum + amount));
                    oCarritoRepository.save(oCarritoEntity);
                }
            }
        } else {
            throw new CannotPerformOperationException("amount must be between 1 and 1000");
        }
        return oCarritoRepository.countByUsuarioId(oAuthService.getUserID());
    }

    @Transactional
    public Long reduce(Long id_producto, int amount) {
        oAuthService.OnlyUsers();
        ProductoEntity oProducto = oProductoService.get(id_producto);
        oProductoService.validate(oProducto.getId());
        if (amount > 0 && amount <= 1000) {
            if (oCarritoRepository.countByUsuarioIdAndProductoId(oAuthService.getUserID(), oProducto.getId()) == 0) {
                throw new CannotPerformOperationException("no exist that product in users cart");
            } else {
                List<CarritoEntity> oCarritoEntityList = oCarritoRepository.findByUsuarioIdAndProductoId(oAuthService.getUserID(), oProducto.getId());
                if (oCarritoEntityList.size() == 1) {
                    CarritoEntity oCarritoEntity = oCarritoEntityList.get(0);
                    if (oCarritoEntity.getCantidad() - amount <= 0) {
                        oCarritoRepository.deleteByUsuarioIdAndProductoId(oAuthService.getUserID(), oProducto.getId());
                    } else {
                        oCarritoEntity.setCantidad(oCarritoEntity.getCantidad() - amount);
                        oCarritoRepository.save(oCarritoEntity);
                    }                    
                } else {
                    Long sum = 0L;
                    for (int i = 0; i < oCarritoEntityList.size(); i++) {
                        sum = sum + oCarritoEntityList.get(i).getCantidad();
                    }
                    oCarritoRepository.deleteByUsuarioIdAndProductoId(oAuthService.getUserID(), oProducto.getId());
                    CarritoEntity oCarritoEntity = new CarritoEntity();
                    oCarritoEntity.setId(null);
                    oCarritoEntity.setProducto(oProductoService.get(oProducto.getId()));
                    oCarritoEntity.setUsuario(oUsuarioService.get(oAuthService.getUserID()));
                    if (oCarritoEntity.getCantidad() - amount < 0) {
                        oCarritoEntity.setCantidad(0);
                    } else {
                        oCarritoEntity.setCantidad(oCarritoEntity.getCantidad() - amount);
                    }
                    oCarritoRepository.save(oCarritoEntity);
                }
            }
        } else {
            throw new CannotPerformOperationException("amount must be between 1 and 1000");
        }
        return oCarritoRepository.countByUsuarioId(oAuthService.getUserID());
    }

    @Transactional
    public Long empty() {
        oAuthService.OnlyUsers();
        if (oCarritoRepository.countByUsuarioId(oAuthService.getUserID()) > 0) {
            oCarritoRepository.deleteByUsuarioId(oAuthService.getUserID());
        }
        return oCarritoRepository.countByUsuarioId(oAuthService.getUserID());
    }

    @Transactional
    public Long empty(long id_producto) {
        oAuthService.OnlyUsers();
        if (oCarritoRepository.countByUsuarioId(oAuthService.getUserID()) > 0) {
            oCarritoRepository.deleteByUsuarioIdAndProductoId(oAuthService.getUserID(), id_producto);
        }
        return oCarritoRepository.countByUsuarioId(oAuthService.getUserID());
    }

    @Transactional
    public Long purchase() throws CannotPerformOperationException, UnauthorizedException {
        oAuthService.OnlyUsers();
        List<CarritoEntity> oCarritoList = oCarritoRepository.findByUsuarioId(oAuthService.getUserID());
        if (oCarritoList.isEmpty()) {
            throw new CannotPerformOperationException("Empty cart");
        } else {
            FacturaEntity oFacturaEntity = new FacturaEntity();
            oFacturaEntity.setIva(21);
            oFacturaEntity.setFecha(LocalDateTime.now());
            oFacturaEntity.setPagado(false);
            UsuarioEntity oUsuarioEntity = new UsuarioEntity(oAuthService.getUserID());
            oFacturaEntity.setUsuario(oUsuarioEntity);
            CarritoEntity oCarritoEntity = null;
            for (int i = 0; i < oCarritoList.size(); i++) {
                oCarritoEntity = oCarritoList.get(i);
                ProductoEntity oProductoEntity = oCarritoEntity.getProducto();
                if (oProductoEntity.getExistencias() >= oCarritoEntity.getCantidad()) {
                    CompraEntity oCompraEntity = new CompraEntity();
                    oCompraEntity.setCantidad(oCarritoEntity.getCantidad());
                    oCompraEntity.setDescuento_producto(oCarritoEntity.getProducto().getDescuento());
                    oCompraEntity.setDescuento_usuario(oCarritoEntity.getUsuario().getDescuento());
                    oCompraEntity.setFactura(oFacturaEntity);
                    oCompraEntity.setFecha(oFacturaEntity.getFecha());
                    oCompraEntity.setPrecio(oCarritoEntity.getProducto().getPrecio());
                    oCompraEntity.setProducto(oCarritoEntity.getProducto());
                    oCompraRepository.save(oCompraEntity);
                    oProductoEntity.setExistencias(oProductoEntity.getExistencias() - oCompraEntity.getCantidad());
                    oProductoRepository.save(oProductoEntity);
                } else {
                    throw new CannotPerformOperationException("No hay sufientes existencias del producto "
                            + oProductoEntity.getId() + "-" + oProductoEntity.getCodigo() + "-" + oProductoEntity.getNombre());
                }
            }
            oFacturaEntity = oFacturaRepository.save(oFacturaEntity);
            oFacturaRepository.flush();
            oCarritoRepository.deleteByUsuarioId(oUsuarioEntity.getId());
            //return ((Integer) oCarritoList.size()).longValue();
            return oFacturaEntity.getId();
        }
    }

}
