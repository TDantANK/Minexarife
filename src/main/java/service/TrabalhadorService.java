package service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * TrabalhadorService - wrapper para trabalhadores.
 */
public class TrabalhadorService {

    private final SupabaseClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public TrabalhadorService() {
        this.client = new SupabaseClient();
    }

    public CompletableFuture<List<SupabaseClient.Trabalhador>> listAllAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return client.listTrabalhadores();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<SupabaseClient.Trabalhador> listAll() { return listAllAsync().join(); }

    public CompletableFuture<SupabaseClient.Trabalhador> createAsync(Map<String,Object> payload) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String bodyJson = mapper.writeValueAsString(payload);
                String resp = client.post("trabalhadores", bodyJson);
                List<SupabaseClient.Trabalhador> created = mapper.readValue(
                        resp.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        mapper.getTypeFactory().constructCollectionType(List.class, SupabaseClient.Trabalhador.class)
                );
                return created.isEmpty() ? null : created.get(0);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Patch para trabalhador (async). Ex: patchTrabalhador(id, Map.of("filial_id", null))
     */
    public CompletableFuture<Void> patchTrabalhadorAsync(String trabalhadorId, Map<String,Object> patch) {
        return CompletableFuture.runAsync(() -> {
            try {
                String bodyJson = mapper.writeValueAsString(patch);
                client.patch("trabalhadores?id=eq." + trabalhadorId, bodyJson);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void patchTrabalhador(String trabalhadorId, Map<String,Object> patch) {
        patchTrabalhadorAsync(trabalhadorId, patch).join();
    }
}
