package com.inventario1.service;

import com.inventario1.model.Producto;
import com.inventario1.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    // Método para cargar datos iniciales
    @PostConstruct
    public void init() {
        // Verificar si ya hay datos para no duplicar
        if (productoRepository.count() == 0) {
            cargarDatosIniciales();
        }
    }
    
    private void cargarDatosIniciales() {
        // Datos de ejemplo que se guardarán en PostgreSQL
        Producto[] productosEjemplo = {
            new Producto("LAP-001", "Laptop Dell XPS 13", "Laptop gaming i7, 16GB RAM", "Tecnología", 1299.99, 15, 5),
            new Producto("MON-001", "Monitor Samsung 24\"", "Monitor LED Full HD", "Tecnología", 199.99, 8, 3),
            new Producto("TEC-001", "Teclado Mecánico RGB", "Teclado mecánico switches azules", "Accesorios", 89.99, 25, 10),
            new Producto("MOU-001", "Mouse Inalámbrico", "Mouse ergonómico 2400 DPI", "Accesorios", 49.99, 2, 5),
            new Producto("AUD-001", "Audífonos Sony", "Audífonos noise cancelling", "Audio", 349.99, 12, 8),
            new Producto("TEL-001", "Smartphone Samsung", "Teléfono Android 128GB", "Tecnología", 899.99, 7, 4)
        };
        
        for (Producto producto : productosEjemplo) {
            productoRepository.save(producto);
        }
        
        System.out.println("✅ Datos iniciales cargados en PostgreSQL");
    }
    
    public List<Producto> obtenerTodosProductos() {
        return productoRepository.findAll();
    }
    
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }
    
    public Optional<Producto> obtenerProductoPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }
    
    public Producto crearProducto(Producto producto) {
        // Validar que el código no exista
        if (productoRepository.findByCodigo(producto.getCodigo()).isPresent()) {
            throw new RuntimeException("El código del producto ya existe: " + producto.getCodigo());
        }
        
        return productoRepository.save(producto);
    }
    
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        return productoRepository.findById(id)
            .map(producto -> {
                producto.setNombre(productoActualizado.getNombre());
                producto.setDescripcion(productoActualizado.getDescripcion());
                producto.setCategoria(productoActualizado.getCategoria());
                producto.setPrecio(productoActualizado.getPrecio());
                producto.setStock(productoActualizado.getStock());
                producto.setStockMinimo(productoActualizado.getStockMinimo());
                return productoRepository.save(producto);
            })
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }
    
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }
    
    public Producto actualizarStock(Long id, Integer cantidad) {
        return productoRepository.findById(id)
            .map(producto -> {
                int nuevoStock = producto.getStock() + cantidad;
                if (nuevoStock < 0) {
                    throw new RuntimeException("Stock no puede ser negativo. Stock actual: " + producto.getStock());
                }
                producto.setStock(nuevoStock);
                return productoRepository.save(producto);
            })
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }
    
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    
    public List<Producto> obtenerProductosStockBajo() {
        return productoRepository.findProductosStockBajo();
    }
    
    public List<String> obtenerCategorias() {
        return productoRepository.findAllCategorias();
    }
    
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        Object[] stats = productoRepository.getEstadisticasInventario();
        Double valorTotal = productoRepository.getValorTotalInventario();
        List<Producto> stockBajo = productoRepository.findProductosStockBajo();
        
        estadisticas.put("totalProductos", stats[0] != null ? stats[0] : 0);
        estadisticas.put("totalStock", stats[1] != null ? stats[1] : 0);
        estadisticas.put("precioPromedio", stats[2] != null ? stats[2] : 0);
        estadisticas.put("valorTotalInventario", valorTotal != null ? valorTotal : 0.0);
        estadisticas.put("productosStockBajo", stockBajo.size());
        
        return estadisticas;
    }
}