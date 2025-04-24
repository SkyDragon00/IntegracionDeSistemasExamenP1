package com.example.apache_camel;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogCambiosResultadosTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName,
                     String tableName, boolean before, int type) throws SQLException {
        // No es necesario implementar nada aquí
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        String pacienteId = (String) newRow[2];
        String tipoExamen = (String) newRow[3];
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());

        String sql = "INSERT INTO log_cambios_resultados (operacion, paciente_id, tipo_examen, fecha) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "INSERT");
            stmt.setString(2, pacienteId);
            stmt.setString(3, tipoExamen);
            stmt.setTimestamp(4, fechaActual);
            stmt.executeUpdate();
        }

        System.out.println("📝 LOG - Se registró INSERT para paciente: " + pacienteId + ", examen: " + tipoExamen);
    }

    @Override
    public void close() {}

    @Override
    public void remove() {}
}
