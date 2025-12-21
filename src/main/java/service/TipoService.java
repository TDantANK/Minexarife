package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TipoService {
    private final SupabaseClient client = new SupabaseClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<String> listTipos(String tabela) { // tabela pode ser "tipos_ferramenta" ou "tipos_veiculo"
        try {
            String json = client.get(tabela + "?select=nome&order=nome");
            List<Map<String, Object>> data = mapper.readValue(json, List.class);
            return data.stream().map(m -> m.get("nome").toString()).collect(Collectors.toList());
        } catch (Exception e) { return List.of(); }
    }

    public void saveTipo(String tabela, String nome) {
        try {
            client.post(tabela, "{\"nome\":\"" + nome + "\"}");
        } catch (Exception e) { e.printStackTrace(); }
    }
}