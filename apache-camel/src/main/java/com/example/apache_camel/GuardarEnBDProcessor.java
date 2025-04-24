package com.example.apache_camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Component
public class GuardarEnBDProcessor implements Processor {

    private final String URL = "jdbc:h2:mem:bionet";
    private final String USER = "sa";
    private final String PASSWORD = "";

    @Override
    public void process(Exchange exchange) throws Exception {
        String filePath = exchange.getIn().getHeader("CamelFilePath", String.class);
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        String fileName = exchange.getIn().getHeader("CamelFileName", String.class);

        int insertados = 0;
        int duplicados = 0;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length != 5) continue;

                String laboratorioId = parts[0];
                String pacienteId = parts[1];
                String tipoExamen = parts[2];
                String resultado = parts[3];
                LocalDate fechaExamen = LocalDate.parse(parts[4]);

                // Verificar duplicado
                String checkSql = "SELECT COUNT(*) FROM resultados_examenes WHERE paciente_id = ? AND tipo_examen = ? AND fecha_examen = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, pacienteId);
                checkStmt.setString(2, tipoExamen);
                checkStmt.setDate(3, Date.valueOf(fechaExamen));
                ResultSet rs = checkStmt.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    System.out.println("⚠️ DUPLICADO - Ya existe resultado para paciente " + pacienteId + " examen " + tipoExamen + " en " + fechaExamen);
                    duplicados++;
                    continue;
                }

                String insertSql = "INSERT INTO resultados_examenes (laboratorio_id, paciente_id, tipo_examen, resultado, fecha_examen) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertSql);
                stmt.setString(1, laboratorioId);
                stmt.setString(2, pacienteId);
                stmt.setString(3, tipoExamen);
                stmt.setString(4, resultado);
                stmt.setDate(5, Date.valueOf(fechaExamen));
                stmt.executeUpdate();

                insertados++;
                System.out.println("✅ INSERTADO - Paciente: " + pacienteId + ", Examen: " + tipoExamen + ", Fecha: " + fechaExamen);
            }

            System.out.println("📄 ARCHIVO '" + fileName + "' PROCESADO: " + insertados + " insertados, " + duplicados + " duplicados.");
        }
    }
}
