package com.example.apache_camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PedidoFileRoute extends RouteBuilder {

    @Autowired
    private ArchivoValidadorProcessor validador;

    @Autowired
    private GuardarEnBDProcessor guardarEnBD;

    @Override
    public void configure() {
        from("file:input-labs?noop=true&include=.*\\.csv&readLock=changed&readLockCheckInterval=1000&readLockTimeout=10000")
            .routeId("validar-archivos")
            .process(exchange -> {
                String fileName = (String) exchange.getIn().getHeader("CamelFileName");
                System.out.println("📁 NUEVO ARCHIVO DETECTADO: " + fileName);
            })
            .process(validador)
            .choice()
                .when(header("archivo_valido").isEqualTo(true))
                    .process(guardarEnBD)
                    .to("file:processed")
                .otherwise()
                    .to("file:error");
    }
}
