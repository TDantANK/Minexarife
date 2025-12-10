package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FilialService {

    private final SupabaseClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public FilialService() {
        this.client = new SupabaseClient();
    }

    public CompletableFuture<List<SupabaseClient.Filial>> listAllAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String body = client.get("filiais?select=*");
                return mapper.readValue(body, new TypeReference<List<SupabaseClient.Filial>>() {});
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<SupabaseClient.Filial> listAll() { return listAllAsync().join(); }

    public CompletableFuture<SupabaseClient.Filial> createAsync(Map<String,Object> payload) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String bodyJson = mapper.writeValueAsString(payload);
                String resp = client.post("filiais", bodyJson);
                List<SupabaseClient.Filial> l = mapper.readValue(resp, new TypeReference<List<SupabaseClient.Filial>>() {});
                return l.isEmpty() ? null : l.get(0);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
