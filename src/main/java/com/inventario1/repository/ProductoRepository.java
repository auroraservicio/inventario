package com.inventario1.repository;

import com.inventario1.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar producto por código
    Optional<Producto> findByCodigo(String codigo);
    
    // Buscar productos por nombre (contiene, ignore case)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar productos por categoría
    List<Producto> findByCategoria(String categoria);
    
    // Buscar productos con stock menor a un valor
    List<Producto> findByStockLessThan(Integer stock);
    
    // Buscar productos con stock bajo (menor al mínimo)
    @Query("SELECT p FROM Producto p WHERE p.stock < p.stockMinimo")
    List<Producto> findProductosStockBajo();
    
    // Buscar productos por rango de precio
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
    
    // Obtener todas las categorías únicas
    @Query("SELECT DISTINCT p.categoria FROM Producto p ORDER BY p.categoria")
    List<String> findAllCategorias();
    
    // Estadísticas de inventario
    @Query("SELECT COUNT(p), SUM(p.stock), AVG(p.precio) FROM Producto p")
    Object[] getEstadisticasInventario();
    
    // Valor total del inventario
    @Query("SELECT SUM(p.precio * p.stock) FROM Producto p")
    Double getValorTotalInventario();
}


