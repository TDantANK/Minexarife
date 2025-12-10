package ui.services;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * MockDataService
 * - Provides in-memory mock data for UI development.
 * - Replace methods with Supabase calls later.
 */
public class MockDataService {

    public static class Trabalhador {
        public String id;
        public String nome;
        public String matricula;
        public String filial;

        public Trabalhador(String id, String nome, String matricula, String filial) {
            this.id = id; this.nome = nome; this.matricula = matricula; this.filial = filial;
        }
        @Override public String toString(){ return nome + " (" + matricula + ")"; }
    }

    public static class Ferramenta {
        public String id;
        public String tipo;
        public String identificacao;
        public String responsavelId; // trabalhador id or null
        public String filial;
        public Ferramenta(String id, String tipo, String identificacao, String respId, String filial) {
            this.id=id; this.tipo=tipo; this.identificacao=identificacao; this.responsavelId=respId; this.filial=filial;
        }
    }

    public static class Veiculo {
        public String id;
        public String placa;
        public String responsavelId;
        public String filial;
        public Veiculo(String id, String placa, String respId, String filial) {
            this.id=id; this.placa=placa; this.responsavelId=respId; this.filial=filial;
        }
    }

    // sample data
    public static ObservableList<Trabalhador> loadTrabalhadores() {
        List<Trabalhador> list = new ArrayList<>();
        list.add(new Trabalhador("t1","Jerson","M001","Filial A"));
        list.add(new Trabalhador("t2","Wellington","M002","Filial A"));
        list.add(new Trabalhador("t3","Marcos","M003","Filial B"));
        return FXCollections.observableArrayList(list);
    }

    public static ObservableList<Ferramenta> loadFerramentas() {
        List<Ferramenta> list = new ArrayList<>();
        list.add(new Ferramenta("f1","Picareta","IIAB","t1","Filial A"));
        list.add(new Ferramenta("f2","Lanterna","XZ44","t2","Filial A"));
        list.add(new Ferramenta("f3","Picareta","CWE2",null,"Filial A"));
        list.add(new Ferramenta("f4","Martelo","BQ44","t1","Filial B"));
        return FXCollections.observableArrayList(list);
    }

    public static ObservableList<Veiculo> loadVeiculos() {
        List<Veiculo> list = new ArrayList<>();
        list.add(new Veiculo("v1","ABC-1234","t1","Filial A"));
        list.add(new Veiculo("v2","XYZ-9999",null,"Filial B"));
        return FXCollections.observableArrayList(list);
    }
}
