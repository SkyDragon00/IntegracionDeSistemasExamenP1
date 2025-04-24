package com.example.apache_camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Component
public class GuardarEnBDProcessor implements Processor {

    private final String URL = "jdbc:h2:mem:bionet"; // O usa tu URL MySQL
    private final String USER = "sa";
    private final String PASSWORD = "";

    @Override
    public void process(Exchange exchange) throws Exception {
        String filePath = exchange.getIn().getHeader("CamelFilePath", String.class);
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length != 5) continue;

                String laboratorioId = parts[0];
                String pacienteId = parts[1];
                String tipoExamen = parts[2];
                String resultado = parts[3];
                LocalDate fechaExamen = LocalDate.parse(parts[4]);

                String sql = "INSERT INTO resultados_examenes (laboratorio_id, paciente_id, tipo_examen, resultado, fecha_examen) " +
                             "VALUES (?, ?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, laboratorioId);
                stmt.setString(2, pacienteId);
                stmt.setString(3, tipoExamen);
                stmt.setString(4, resultado);
                stmt.setDate(5, java.sql.Date.valueOf(fechaExamen));
                stmt.executeUpdate();
            }
        }
    }
}
