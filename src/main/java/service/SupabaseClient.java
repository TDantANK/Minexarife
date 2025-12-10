package service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * SupabaseClient com logs em get/post/patch/delete e conveniências list*.
 */
public class SupabaseClient {

    private final String baseUrl;
    private final String anonKey;
    private final HttpClient http;
    private final ObjectMapper mapper;

    public SupabaseClient() {
        String envUrl = System.getenv("SUPABASE_URL");
        String envKey = System.getenv("SUPABASE_KEY");
        // fallback dev option (opcional) - descomente se quiser hardcode temporário
        // if (envUrl == null || envUrl.isBlank()) envUrl = "https://kuuambkymmaicdgknnkz.supabase.co";
        // if (envKey == null || envKey.isBlank()) envKey = "sb_publishable_AlQwpXOpkRfTmCKLMG9zig_2kJS-twJ";

        if (envUrl == null || envUrl.isBlank() || envKey == null || envKey.isBlank()) {
            throw new IllegalStateException("Defina SUPABASE_URL e SUPABASE_KEY nas variáveis de ambiente.");
        }
        this.baseUrl = envUrl.endsWith("/") ? envUrl.substring(0, envUrl.length()-1) : envUrl;
        this.anonKey = envKey;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private HttpRequest.Builder requestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/v1/" + path))
                .header("apikey", anonKey)
                .header("Authorization", "Bearer " + anonKey)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
    }

    public String get(String path) throws IOException, InterruptedException {
        System.out.println("[SupabaseClient] GET -> " + path);
        HttpRequest req = requestBuilder(path).GET().build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("[SupabaseClient] STATUS -> " + resp.statusCode());
        System.out.println("[SupabaseClient] RESPONSE -> " + resp.body());
        if (resp.statusCode() / 100 != 2) throw new IOException("GET failed: " + resp.statusCode() + " -> " + resp.body());
        return resp.body();
    }

    public String post(String path, String json) throws IOException, InterruptedException {
        System.out.println("[SupabaseClient] POST -> " + path);
        System.out.println("[SupabaseClient] BODY -> " + json);
        HttpRequest req = requestBuilder(path)
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("[SupabaseClient] STATUS -> " + resp.statusCode());
        System.out.println("[SupabaseClient] RESPONSE -> " + resp.body());
        if (resp.statusCode() / 100 != 2) throw new IOException("POST failed: " + resp.statusCode() + " -> " + resp.body());
        return resp.body();
    }

    public String patch(String path, String json) throws IOException, InterruptedException {
        System.out.println("[SupabaseClient] PATCH -> " + path);
        System.out.println("[SupabaseClient] BODY -> " + json);
        HttpRequest req = requestBuilder(path)
                .header("Prefer", "return=representation")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("[SupabaseClient] STATUS -> " + resp.statusCode());
        System.out.println("[SupabaseClient] RESPONSE -> " + resp.body());
        if (resp.statusCode() / 100 != 2) throw new IOException("PATCH failed: " + resp.statusCode() + " -> " + resp.body());
        return resp.body();
    }

    public String delete(String path) throws IOException, InterruptedException {
        System.out.println("[SupabaseClient] DELETE -> " + path);
        HttpRequest req = requestBuilder(path).DELETE().build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("[SupabaseClient] STATUS -> " + resp.statusCode());
        System.out.println("[SupabaseClient] RESPONSE -> " + resp.body());
        if (resp.statusCode() / 100 != 2) throw new IOException("DELETE failed: " + resp.statusCode() + " -> " + resp.body());
        return resp.body();
    }

    // -----------------------
    // DTO classes (públicos)
    // -----------------------
    public static class Filial {
        public String id;
        public String nome;
        public String local;
    }

    public static class Trabalhador {
        public String id;
        public String nome;
        public String matricula;
        public String filial_id;
        public String contato;
    }

    public static class TipoFerramenta {
        public String id;
        public String nome;
        public String descricao;
    }

    public static class Ferramenta {
        public String id;
        public String tipo_id;
        public String identificacao;
        public String filial_id;
        public String responsavel_id;
        public String status;
    }

    public static class Veiculo {
        public String id;
        public String identificacao;
        public String modelo;
        public String filial_id;
        public String responsavel_id;
        public String status;
    }

    // -----------------------
    // Conveniências que retornam listas tipadas
    // -----------------------
    public List<Filial> listFiliais() throws IOException, InterruptedException {
        String body = get("filiais?select=*");
        return mapper.readValue(body.getBytes(StandardCharsets.UTF_8),
                mapper.getTypeFactory().constructCollectionType(List.class, Filial.class));
    }

    public List<Trabalhador> listTrabalhadores() throws IOException, InterruptedException {
        String body = get("trabalhadores?select=*");
        return mapper.readValue(body.getBytes(StandardCharsets.UTF_8),
                mapper.getTypeFactory().constructCollectionType(List.class, Trabalhador.class));
    }

    public List<TipoFerramenta> listTiposFerramenta() throws IOException, InterruptedException {
        String body = get("tipos_ferramenta?select=*");
        return mapper.readValue(body.getBytes(StandardCharsets.UTF_8),
                mapper.getTypeFactory().constructCollectionType(List.class, TipoFerramenta.class));
    }

    public List<Ferramenta> listFerramentas() throws IOException, InterruptedException {
        String body = get("ferramentas?select=*");
        return mapper.readValue(body.getBytes(StandardCharsets.UTF_8),
                mapper.getTypeFactory().constructCollectionType(List.class, Ferramenta.class));
    }

    public List<Veiculo> listVeiculos() throws IOException, InterruptedException {
        String body = get("veiculos?select=*");
        return mapper.readValue(body.getBytes(StandardCharsets.UTF_8),
                mapper.getTypeFactory().constructCollectionType(List.class, Veiculo.class));
    }

    public Filial createFilial(String nome, String local) throws IOException, InterruptedException {
        String body = post("filiais", "{\"nome\":\"" + escapeJson(nome) + "\",\"local\":\"" + escapeJson(local) + "\"}");
        List<Filial> l = mapper.readValue(body.getBytes(StandardCharsets.UTF_8),
                mapper.getTypeFactory().constructCollectionType(List.class, Filial.class));
        return l.isEmpty() ? null : l.get(0);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
