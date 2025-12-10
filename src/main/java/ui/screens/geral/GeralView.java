package ui.screens.geral;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ui.MainWindow;
import service.FerramentaService;
import service.TrabalhadorService;
import service.VeiculoService;
import service.SupabaseClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * GeralView - mostra visão consolidada por trabalhador
 */
public class GeralView {

    private final BorderPane root = new BorderPane();

    public GeralView(MainWindow main) {
        VBox top = new VBox(8);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER_LEFT);
        Text title = new Text("Visão Geral - Filial: " + (main.getCurrentFilial() == null ? "(nenhuma)" : main.getCurrentFilial()));
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        top.getChildren().add(title);

        // build consolidated table
        TableView<Row> table = new TableView<>();
        TableColumn<Row, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(c -> c.getValue().nomeProperty());
        TableColumn<Row, Integer> qtdFerr = new TableColumn<>("Ferramentas");
        qtdFerr.setCellValueFactory(c -> c.getValue().qtdFerrProperty().asObject());
        TableColumn<Row, String> tiposCol = new TableColumn<>("Tipos");
        tiposCol.setCellValueFactory(c -> c.getValue().tiposProperty());
        TableColumn<Row, Integer> qtdVeic = new TableColumn<>("Veículos");
        qtdVeic.setCellValueFactory(c -> c.getValue().qtdVeicProperty().asObject());
        TableColumn<Row, String> filialCol = new TableColumn<>("Filial");
        filialCol.setCellValueFactory(c -> c.getValue().filialProperty());

        table.getColumns().addAll(nomeCol, qtdFerr, tiposCol, qtdVeic, filialCol);

        Button btnRefresh = new Button("Atualizar");
        btnRefresh.setOnAction(e -> loadData(main, table));

        Button back = new Button("⬅ Voltar");
        back.setOnAction(e -> main.goBack());

        HBox actions = new HBox(8, btnRefresh, back);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(8));

        root.setTop(top);
        root.setCenter(table);
        root.setBottom(actions);

        // initial load
        loadData(main, table);
    }

    private void loadData(MainWindow main, TableView<Row> table) {
        String filial = main.getCurrentFilial();
        CompletableFuture.supplyAsync(() -> {
            try {
                TrabalhadorService tsvc = new TrabalhadorService();
                FerramentaService fsvc = new FerramentaService();
                VeiculoService vsvc = new VeiculoService();

                List<SupabaseClient.Trabalhador> workers = tsvc.listAllAsync().join();
                List<SupabaseClient.Ferramenta> tools = fsvc.listAllAsync().join();
                List<SupabaseClient.Veiculo> veis = vsvc.listAllAsync().join();

                // filter by filial if selected
                List<SupabaseClient.Trabalhador> filWorkers = workers.stream()
                        .filter(w -> filial == null || filial.equals("(nenhuma)") || filial.equals(w.filial_id))
                        .collect(Collectors.toList());

                // build rows
                List<Row> rows = filWorkers.stream().map(w -> {
                    List<SupabaseClient.Ferramenta> wf = tools.stream().filter(t -> w.id.equals(t.responsavel_id)).collect(Collectors.toList());
                    List<String> tipos = wf.stream().map(t -> t.tipo_id).distinct().collect(Collectors.toList());
                    List<SupabaseClient.Veiculo> wv = veis.stream().filter(v -> w.id.equals(v.responsavel_id)).collect(Collectors.toList());
                    return new Row(w.nome, wf.size(), String.join(", ", tipos), wv.size(), w.filial_id);
                }).collect(Collectors.toList());

                return rows;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(rows -> {
            Platform.runLater(() -> table.setItems(FXCollections.observableArrayList(rows)));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public BorderPane getRoot() {
        return root;
    }

    // ------------------ Row model (lightweight) ------------------
    public static class Row {
        private final StringProperty nome = new SimpleStringProperty();
        private final IntegerProperty qtdFerr = new SimpleIntegerProperty();
        private final StringProperty tipos = new SimpleStringProperty();
        private final IntegerProperty qtdVeic = new SimpleIntegerProperty();
        private final StringProperty filial = new SimpleStringProperty();

        public Row(String nome, int qtdFerr, String tipos, int qtdVeic, String filial) {
            this.nome.set(nome);
            this.qtdFerr.set(qtdFerr);
            this.tipos.set(tipos);
            this.qtdVeic.set(qtdVeic);
            this.filial.set(filial);
        }

        public StringProperty nomeProperty() { return nome; }
        public IntegerProperty qtdFerrProperty() { return qtdFerr; }
        public StringProperty tiposProperty() { return tipos; }
        public IntegerProperty qtdVeicProperty() { return qtdVeic; }
        public StringProperty filialProperty() { return filial; }
    }
}
