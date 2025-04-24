package com.example.apache_camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PedidoFileRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:input?noop=true&include=.*\\.csv")
                .process(exchange -> {
                    String fileName = (String) exchange.getIn().getHeader("CamelFileName");
                    System.out.println("NUEVO ARCHIVO DETECTADO: " + fileName);
                })
                .to("file:output");
    }
}
