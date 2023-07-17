package com.formacionbdi.springboot.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class EjemploGlobalFilters implements GlobalFilter, Ordered{

	private final Logger logger = LoggerFactory.getLogger(EjemploGlobalFilters.class);
	//por detras e usa webflux (programacion reactiva)
	//aqui se tratan las peticiones que van entrando o saliendo o intermedio
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("ejecutando filtro pre");
		exchange.getRequest().mutate().headers(h -> h.add("token", "123456"));//mutate se usa para poder modificar el request y sea modificacble, en este caso se grega token al header
		
		
		//chain filter hace que continue la peticion al micro pero primero pasa por aqui
		//then() es cuando finaliza la petcion del mciro y se devuelve la respuesta
		
		//creamos el objeto reactivo que hace algo( una implementacion)
		return chain.filter(exchange).then(Mono.fromRunnable(()->{
			logger.info("Ejecutando filtro post");
			
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(valor ->{//desimos que si no nulo tokenn le agregamos el token
				exchange.getResponse().getHeaders().add("token", valor);
			}) ;
			
			exchange.getResponse().getCookies().add("color",ResponseCookie.from("color", "rojo").build());//squi decimos que vamos a devolver un objeto color rojo en la cookie del response de los header
			exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);//la rspeusta la vamos a devilver en texto plano
		}));
	}
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return -1;
	}

}
