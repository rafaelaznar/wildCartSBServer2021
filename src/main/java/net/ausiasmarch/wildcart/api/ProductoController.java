package net.ausiasmarch.wildcart.api;

import java.util.List;
import javax.servlet.http.HttpSession;
import net.ausiasmarch.wildcart.entity.ProductoEntity;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;
import net.ausiasmarch.wildcart.repository.ProductoRepository;
import net.ausiasmarch.wildcart.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
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
    ProductoRepository oProductoRepository;

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    ProductoService oProductoService;

    // /producto/3
    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> get(@PathVariable(value = "id") Long id) {

        if (oProductoRepository.existsById(id)) {
            return new ResponseEntity<ProductoEntity>(oProductoRepository.getById(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // producto/all
    @GetMapping("/all")
    public ResponseEntity<List> getall() {
        return new ResponseEntity<List>(oProductoRepository.findAll(), HttpStatus.OK);
    }

    // producto/count
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oProductoRepository.count(), HttpStatus.OK);
    }

//    @GetMapping("/filter/{filtro}")
//    public ResponseEntity<Page<ProductoEntity>> getFilteredPage(@PathVariable(value = "filtro") String sfiltro, @PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable oPageable) {
//        Page<ProductoEntity> oPage = null;
//        oPage = oProductoRepository.findByNombreIgnoreCaseContainingOrCodigoIgnoreCaseContaining(sfiltro, sfiltro, oPageable);
//        return new ResponseEntity<Page<ProductoEntity>>(oPage, HttpStatus.OK);
//    }
//
//    @GetMapping("/filtertp/{filtro}")
//    public ResponseEntity<Page<ProductoEntity>> getFilteredTP(@PathVariable(value = "filtro") long sfiltro, @PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable oPageable) {
//        Page<ProductoEntity> oPage = null;
//        oPage = oProductoRepository.findAllBytipoproductoId(sfiltro, oPageable);
//        return new ResponseEntity<Page<ProductoEntity>>(oPage, HttpStatus.OK);
//    }

    // /producto?page=0&size=10&sort=precio,desc&filter=verde&filtertype=2
    @GetMapping("")
    public ResponseEntity<Page<ProductoEntity>> getPage(@PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
             @RequestParam(name = "filter") String filter, @RequestParam(name = "filtertype") Long filtertype) {
        Page<ProductoEntity> oPage = null;
        if (filtertype!=0) {
            oPage = oProductoRepository.findByTipoproductoIdAndNombreOrCodigo(filtertype,filter,filter,oPageable);
        } else {
            oPage = oProductoRepository.findByNombreIgnoreCaseContainingOrCodigoIgnoreCaseContaining(filter, filter, oPageable);
        }

       
        return new ResponseEntity<Page<ProductoEntity>>(oPage, HttpStatus.OK);
    }

    //CREAR
    // producto/
    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody ProductoEntity oProductoEntity) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {
            if (oUsuarioEntity == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                oProductoEntity.setId(null);

                return new ResponseEntity<ProductoEntity>(oProductoRepository.save(oProductoEntity), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/genera/{num}")
    public ResponseEntity<?> genera(@PathVariable(value = "num") int num) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {
            if (oUsuarioEntity == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                for (int i = 0; i < num; i++) {
                    ProductoEntity oProductoEntity;
                    oProductoEntity = oProductoService.generateRandomProduct();
                    oProductoRepository.save(oProductoEntity);
                }

                return new ResponseEntity<List>(oProductoRepository.findAll(), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

    }

    //UPDATE
    //producto/
    @PutMapping("/")
    public ResponseEntity<?> update(@RequestBody ProductoEntity oProductoEntity) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {
            if (oUsuarioEntity == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                if (oProductoRepository.existsById(oProductoEntity.getId())) {
                    return new ResponseEntity<ProductoEntity>(oProductoRepository.save(oProductoEntity), HttpStatus.OK);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }
            }
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);

        }
    }

    // producto/id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {

            if (oUsuarioEntity == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                if (oProductoRepository.existsById(id)) {
                    oProductoRepository.deleteById(id);
                    if (oProductoRepository.existsById(id)) {
                        return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                    } else {
                        return new ResponseEntity<Long>(id, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }
            }
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);

        }
    }

}
