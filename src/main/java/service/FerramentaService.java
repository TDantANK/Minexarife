package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * FerramentaService - wrapper de alto nível para operações de ferramentas.
 * - métodos async retornam CompletableFuture
 * - métodos sync lançam RuntimeException em erro (use com cautela na UI thread)
 */
public class FerramentaService {

    private final SupabaseClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public FerramentaService() {
        this.client = new SupabaseClient();
    }

    // Async: obter todas as ferramentas
    public CompletableFuture<List<SupabaseClient.Ferramenta>> listAllAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String body = client.get("ferramentas?select=*");
                return mapper.readValue(body, new TypeReference<List<SupabaseClient.Ferramenta>>() {});
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Sync convenience (não chame da UI thread)
    public List<SupabaseClient.Ferramenta> listAll() {
        return listAllAsync().join();
    }

    // Patch exemplo: transferir responsavel ou filial
    public CompletableFuture<Void> patchFerramentaAsync(String ferramentaId, Map<String,Object> patch) {
        return CompletableFuture.runAsync(() -> {
            try {
                String bodyJson = mapper.writeValueAsString(patch);
                client.patch("ferramentas?id=eq." + ferramentaId, bodyJson);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void patchFerramenta(String ferramentaId, Map<String,Object> patch) {
        patchFerramentaAsync(ferramentaId, patch).join();
    }

    // Create exemplo
    public CompletableFuture<SupabaseClient.Ferramenta> createFerramentaAsync(Map<String,Object> payload) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String bodyJson = mapper.writeValueAsString(payload);
                String resp = client.post("ferramentas", bodyJson);
                List<SupabaseClient.Ferramenta> created = mapper.readValue(resp, new TypeReference<List<SupabaseClient.Ferramenta>>() {});
                return created.isEmpty() ? null : created.get(0);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
