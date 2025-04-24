package com.example.apache_camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class ArchivoValidadorProcessor implements Processor {

    @Override
public void process(Exchange exchange) throws Exception {
    String filePath = exchange.getIn().getHeader("CamelFilePath", String.class);
    List<String> lines = Files.readAllLines(Paths.get(filePath));

    // Elimina líneas vacías
    lines.removeIf(String::isBlank);

    if (lines.size() < 2) {
        throw new Exception("Archivo inválido: no tiene suficientes líneas de datos.");
    }

    // Validar solo las líneas de datos (ignorando encabezado)
    for (int i = 1; i < lines.size(); i++) {
        String line = lines.get(i).trim();
        if (line.isEmpty()) continue;

        String[] parts = line.split(",");
        if (parts.length != 5) {
            throw new Exception("Archivo inválido: línea mal estructurada -> " + line);
        }
    }

    exchange.getIn().setHeader("archivo_valido", true);
    System.out.println("✔️ VALIDACIÓN COMPLETA: " + filePath);
}

}
