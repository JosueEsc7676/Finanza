package Com.Finanzas.FinanzeApp.repositorios;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByUsuarioId(Long id);

    List<Movimiento> findByUsuarioIdAndCategoriaTipo(Long usuarioId, String tipo);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Movimiento m WHERE m.usuario.id = :usuarioId AND m.categoria.tipo = 'Ingreso'")
    Double totalIngresos(@Param("usuarioId") Long usuarioId);

    List<Movimiento> findTop4ByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Movimiento m WHERE m.usuario.id = :usuarioId AND m.categoria.tipo = 'Egreso'")
    Double totalEgresos(@Param("usuarioId") Long usuarioId);

    @Query("SELECT m.categoria.nombre, SUM(m.monto) FROM Movimiento m WHERE m.categoria.tipo = 'Egreso' AND m.usuario.id = :usuarioId GROUP BY m.categoria.nombre")
    List<Object[]> obtenerGastosPorCategoria(@Param("usuarioId") Long usuarioId);

    @Query("SELECT m.categoria.nombre, SUM(m.monto) FROM Movimiento m WHERE m.categoria.tipo = 'Egreso' AND m.usuario.id = :usuarioId AND m.fecha BETWEEN :inicio AND :fin GROUP BY m.categoria.nombre ORDER BY SUM(m.monto) DESC")
    List<Object[]> obtenerGastosPorCategoria(@Param("usuarioId") Long usuarioId,
                                             @Param("inicio") LocalDate inicio,
                                             @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Movimiento m WHERE m.categoria.tipo = 'Egreso' AND m.usuario.id = :usuarioId AND m.fecha BETWEEN :inicio AND :fin")
    Double totalGastos(@Param("usuarioId") Long usuarioId,
                       @Param("inicio") LocalDate inicio,
                       @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Movimiento m WHERE m.categoria.tipo = 'Ingreso' AND m.usuario.id = :usuarioId AND m.fecha BETWEEN :inicio AND :fin")
    Double totalIngresos(@Param("usuarioId") Long usuarioId,
                         @Param("inicio") LocalDate inicio,
                         @Param("fin") LocalDate fin);

    @Query("SELECT m.descripcion, m.categoria.nombre, m.fecha, m.monto FROM Movimiento m WHERE m.categoria.tipo = 'Egreso' AND m.usuario.id = :usuarioId AND m.fecha BETWEEN :inicio AND :fin ORDER BY m.monto DESC")
    List<Object[]> topGastos(@Param("usuarioId") Long usuarioId,
                             @Param("inicio") LocalDate inicio,
                             @Param("fin") LocalDate fin,
                             Pageable pageable);

    // ✅ CORREGIDO: usamos DATE_FORMAT para evitar conflicto con ONLY_FULL_GROUP_BY
    @Query(value = """
        SELECT DATE_FORMAT(m.fecha, '%Y-%m') AS periodo, SUM(m.monto)
        FROM movimientos m
        JOIN categoria c ON c.id = m.categoria_id
        WHERE c.tipo = 'Egreso' AND m.usuario_id = :usuarioId AND m.fecha BETWEEN :inicio AND :fin
        GROUP BY periodo
        ORDER BY periodo
        """, nativeQuery = true)
    List<Object[]> gastoMensual(@Param("usuarioId") Long usuarioId,
                                @Param("inicio") LocalDate inicio,
                                @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Movimiento m WHERE m.categoria.tipo = 'Egreso' AND m.usuario.id = :usuarioId AND m.fecha BETWEEN :inicio AND :fin")
    Double totalGastosPeriodo(@Param("usuarioId") Long usuarioId,
                              @Param("inicio") LocalDate inicio,
                              @Param("fin") LocalDate fin);
}
