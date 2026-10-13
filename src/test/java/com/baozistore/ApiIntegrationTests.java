package com.baozistore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class ApiIntegrationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	void fluxoCompleto() throws Exception {
		mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Fulano123456\",\"clienteDesde\":\"2026-03-01\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.clienteDesde").value("2026-03-01"));

		mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Baozi de Porco\",\"preco\":7.50,\"estoque\":true}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1));

		mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
						.content("{\"clienteId\":1,\"produtoId\":1,\"quantidade\":12}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.cliente.nome").value("Fulano123456"))
				.andExpect(jsonPath("$.produto.nome").value("Baozi de Porco"))
				.andExpect(jsonPath("$.quantidade").value(12));

		mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
						.content("{\"clienteId\":99,\"produtoId\":1,\"quantidade\":1}"))
				.andExpect(status().isBadRequest());

		mvc.perform(get("/pedidos")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
		mvc.perform(get("/clientes/1")).andExpect(status().isOk());
		mvc.perform(get("/produtos/42")).andExpect(status().isNotFound());

		// cliente com pedido nao pode ser apagado
		mvc.perform(delete("/clientes/1")).andExpect(status().isConflict());

		mvc.perform(delete("/pedidos/1")).andExpect(status().isNoContent());
		mvc.perform(delete("/clientes/1")).andExpect(status().isNoContent());
		mvc.perform(get("/clientes/1")).andExpect(status().isNotFound());
	}
}
