package com.inventario1.controller;

import com.inventario1.model.Producto;
import com.inventario1.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:8081")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    public List<Producto> obtenerTodosProductos() {
        System.out.println("📦 Obteniendo todos los productos...");
        return productoService.obtenerTodosProductos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        System.out.println("🔍 Buscando producto con ID: " + id);
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        return producto.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> obtenerProductoPorCodigo(@PathVariable String codigo) {
        System.out.println("🔍 Buscando producto con código: " + codigo);
        Optional<Producto> producto = productoService.obtenerProductoPorCodigo(codigo);
        return producto.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar")
    public List<Producto> buscarProductos(@RequestParam String nombre) {
        System.out.println("🔎 Buscando productos con nombre: " + nombre);
        return productoService.buscarPorNombre(nombre);
    }
    
    @GetMapping("/categoria/{categoria}")
    public List<Producto> obtenerProductosPorCategoria(@PathVariable String categoria) {
        System.out.println("📂 Filtrando productos por categoría: " + categoria);
        return productoService.buscarPorCategoria(categoria);
    }
    
    @GetMapping("/stock-bajo")
    public List<Producto> obtenerProductosStockBajo() {
        System.out.println("⚠️ Obteniendo productos con stock bajo...");
        return productoService.obtenerProductosStockBajo();
    }
    
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        System.out.println("➕ Creando nuevo producto: " + producto.getNombre());
        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.ok(nuevoProducto);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        System.out.println("✏️ Actualizando producto ID: " + id);
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        System.out.println("📦 Actualizando stock del producto ID: " + id + " - Cantidad: " + cantidad);
        try {
            Producto producto = productoService.actualizarStock(id, cantidad);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        System.out.println("🗑️ Eliminando producto ID: " + id);
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/categorias")
    public List<String> obtenerCategorias() {
        System.out.println("📋 Obteniendo lista de categorías...");
        return productoService.obtenerCategorias();
    }
    
    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {
        System.out.println("📊 Generando estadísticas...");
        return productoService.obtenerEstadisticas();
    }
    
    @GetMapping("/reporte")
    public String generarReporte() {
        System.out.println("📄 Generando reporte...");
        Map<String, Object> stats = productoService.obtenerEstadisticas();
        return String.format(
            "=== REPORTE SISTEMA INVENTARIO 1 ===\n" +
            "📦 Total Productos: %d\n" +
            "📊 Stock Total: %d unidades\n" +
            "💰 Valor Total Inventario: $%.2f\n" +
            "⚡ Precio Promedio: $%.2f\n" +
            "⚠️ Productos con Stock Bajo: %d\n" +
            "================================",
            stats.get("totalProductos"),
            stats.get("totalStock"),
            stats.get("valorTotalInventario"),
            stats.get("precioPromedio"),
            stats.get("productosStockBajo")
        );
    }
}
