package service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * VeiculoService - operações de alto nível para veículos (com logs).
 */
public class VeiculoService {

    private final SupabaseClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public VeiculoService() {
        this.client = new SupabaseClient();
    }

    public CompletableFuture<List<SupabaseClient.Veiculo>> listAllAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return client.listVeiculos();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<SupabaseClient.Veiculo> listAll() {
        return listAllAsync().join();
    }

    public CompletableFuture<SupabaseClient.Veiculo> createVeiculoAsync(String identificacao, String modelo, String filialId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String body = "{"
                        + "\"identificacao\":\"" + escape(identificacao) + "\""
                        + (modelo != null ? ",\"modelo\":\"" + escape(modelo) + "\"" : "")
                        + (filialId != null ? ",\"filial_id\":\"" + escape(filialId) + "\"" : "")
                        + "}";
                System.out.println("[VeiculoService] create body: " + body);
                String resp = client.post("veiculos", body);
                System.out.println("[VeiculoService] create response: " + resp);
                List<SupabaseClient.Veiculo> created = mapper.readValue(
                        resp.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        mapper.getTypeFactory().constructCollectionType(List.class, SupabaseClient.Veiculo.class)
                );
                if (created.isEmpty()) throw new RuntimeException("Supabase returned empty list on create");
                return created.get(0);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public SupabaseClient.Veiculo createVeiculo(String identificacao, String modelo, String filialId) {
        return createVeiculoAsync(identificacao, modelo, filialId).join();
    }

    public CompletableFuture<Void> patchVeiculoAsync(String veiculoId, Map<String, Object> patch) {
        return CompletableFuture.runAsync(() -> {
            try {
                String bodyJson = mapper.writeValueAsString(patch);
                System.out.println("[VeiculoService] patch body: " + bodyJson);
                client.patch("veiculos?id=eq." + veiculoId, bodyJson);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void patchVeiculo(String veiculoId, Map<String, Object> patch) {
        patchVeiculoAsync(veiculoId, patch).join();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
