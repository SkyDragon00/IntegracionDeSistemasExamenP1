CREATE TABLE resultados_examenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    laboratorio_id VARCHAR(100) NOT NULL,
    paciente_id VARCHAR(100) NOT NULL,
    tipo_examen VARCHAR(100),
    resultado VARCHAR(255),
    fecha_examen DATE
);

CREATE UNIQUE INDEX idx_resultado_unico 
ON resultados_examenes (paciente_id, tipo_examen, fecha_examen);

CREATE TABLE log_cambios_resultados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    operacion VARCHAR(10),
    paciente_id VARCHAR(100),
    tipo_examen VARCHAR(100),
    fecha TIMESTAMP
);

CREATE TRIGGER trg_log_insert_resultados
AFTER INSERT ON resultados_examenes
FOR EACH ROW
CALL "com.example.apache_camel.LogCambiosResultadosTrigger";
