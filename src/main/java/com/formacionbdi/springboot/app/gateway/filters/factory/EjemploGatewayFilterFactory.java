package com.formacionbdi.springboot.app.gateway.filters.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class EjemploGatewayFilterFactory
		extends AbstractGatewayFilterFactory<EjemploGatewayFilterFactory.Configuracion> {

	private final Logger logger = LoggerFactory.getLogger(EjemploGatewayFilterFactory.class);
	
	public EjemploGatewayFilterFactory() {
		super(Configuracion.class);
	}

	// por detras e usa webflux (programacion reactiva)

	@Override
	public GatewayFilter apply(Configuracion config) {//estos son filtros globales, ya dentro del yml configuramos filtros personalizados por endpoint

		return (exchange, chain) -> {// chain es pre y exchange es el post //en caso de ordenar el filto usar OrderedGatewayFilter(no es necesario)

			logger.info("ejecutando pre gateway filter factory: " + config.mensaje);
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {

				Optional.ofNullable(config.cookieValor).ifPresent(cookie -> {
					exchange.getResponse().addCookie(ResponseCookie.from(config.cookieNombre, cookie).build());// build
																												// genera
																												// el
																												// objeto
				});

				logger.info("ejecutando post gateway filter factory: " + config.mensaje);
			}));
		};
	}
	
	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("mensaje","cookieNombre","cookieValor"); //aqui es donde se configura la clase y el metodo en el mismo order que se defini en el yml
	}
	
	


	@Override
	public String name() {
		return "EjemploCookie";//nombre de nuestro flintro personalizado y definir en el yml
	}




	public static class Configuracion { // clase anidada

		private String mensaje;
		private String cookieValor;
		private String cookieNombre;

		public String getMensaje() {
			return mensaje;
		}

		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}

		public String getCookieValor() {
			return cookieValor;
		}

		public void setCookieValor(String cookieValor) {
			this.cookieValor = cookieValor;
		}

		public String getCookieNombre() {
			return cookieNombre;
		}

		public void setCookieNombre(String cookieNombre) {
			this.cookieNombre = cookieNombre;
		}

	}

}
