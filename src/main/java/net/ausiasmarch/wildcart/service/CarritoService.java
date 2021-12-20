package net.ausiasmarch.wildcart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import net.ausiasmarch.wildcart.Exception.CarritoVacioEnCompraException;
import net.ausiasmarch.wildcart.Exception.FaltaCantidadDeProductoEnCompraException;
import net.ausiasmarch.wildcart.Exception.UnauthorizedException;
import net.ausiasmarch.wildcart.entity.CarritoEntity;
import net.ausiasmarch.wildcart.entity.CompraEntity;
import net.ausiasmarch.wildcart.entity.FacturaEntity;
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;
import net.ausiasmarch.wildcart.helper.RandomHelper;
import net.ausiasmarch.wildcart.repository.CarritoRepository;
import net.ausiasmarch.wildcart.repository.CompraRepository;
import net.ausiasmarch.wildcart.repository.FacturaRepository;
import net.ausiasmarch.wildcart.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.ausiasmarch.wildcart.repository.UsuarioRepository;

@Service
public class CarritoService {

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    ProductoService oProductoService;

    @Autowired
    UsuarioService oUsuarioService;

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    CarritoRepository oCarritoRepository;

    @Autowired
    CompraRepository oCompraRepository;

    @Autowired
    ProductoRepository oProductoRepository;

    public ArrayList<CarritoEntity> generate(int rowsPerUser) {
        ArrayList<CarritoEntity> rows = new ArrayList<>();
        List users = oUsuarioRepository.findAll();
        int randomCantidad = 0;
        for (int i = 0; i < users.size(); i++) {
            for (int o = 0; o < rowsPerUser; o++) {
                randomCantidad = RandomHelper.getRandomInt(1, 10);
                CarritoEntity row = new CarritoEntity();
                row.setUsuario(oUsuarioService.getRandomUsuario());
                row.setProducto(oProductoService.getRandomProducto());
                row.setCantidad(randomCantidad);
                rows.add(row);
            }
        }
        return rows;
    }

    @Transactional
    public void compra() throws FaltaCantidadDeProductoEnCompraException, UnauthorizedException, CarritoVacioEnCompraException {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oSessionUsuarioEntity != null) {
            FacturaEntity oFacturaEntity = new FacturaEntity();
            oFacturaEntity.setIva(21);
            oFacturaEntity.setFecha(LocalDateTime.now());
            oFacturaEntity.setPagado(false);
            UsuarioEntity oUsuarioEntity = new UsuarioEntity();
            oUsuarioEntity.setId(oSessionUsuarioEntity.getId());
            oFacturaEntity.setUsuario(oUsuarioEntity);
            List<CarritoEntity> oCarritoList = oCarritoRepository.findByUsuarioId(oSessionUsuarioEntity.getId());
            if (oCarritoList.size() > 0) {
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
                        throw new FaltaCantidadDeProductoEnCompraException(oProductoEntity.getId());
                    }
                }
            } else {
                throw new CarritoVacioEnCompraException();
            }
            oFacturaRepository.save(oFacturaEntity);
            oCarritoRepository.deleteAllByUsuario(oSessionUsuarioEntity);

        } else {
            throw new UnauthorizedException();
        }
    }

}
