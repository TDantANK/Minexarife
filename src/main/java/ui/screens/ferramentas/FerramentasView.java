package ui.screens.ferramentas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import ui.MainWindow;
import service.FerramentaService;
import service.SupabaseClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Ferramentas: left tipos, right instâncias, adicionar/remover/transferir (placeholder).
 */
public class FerramentasView {

    private final BorderPane root = new BorderPane();
    private final ListView<String> typesList = new ListView<>();
    private final TableView<SupabaseClient.Ferramenta> table = new TableView<>();

    public FerramentasView(MainWindow main) {
        VBox top = new VBox(6);
        top.setPadding(new Insets(10));
        Text title = new Text("Ferramentas — Filial: " + (main.getCurrentFilial()==null?"(nenhuma)":main.getCurrentFilial()));
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        TextField search = new TextField();
        search.setPromptText("Pesquisar identificação...");

        HBox actions = new HBox(8);
        Button btnAdd = new Button("Adicionar");
        Button btnRemove = new Button("Remover selecionada");
        Button btnTransfer = new Button("Transferir (manual)");
        Button back = new Button("⬅ Voltar");
        actions.getChildren().addAll(btnAdd, btnRemove, btnTransfer, back);
        actions.setAlignment(Pos.CENTER_LEFT);

        top.getChildren().addAll(title, search, actions);
        root.setTop(top);

        // types left
        typesList.setPrefWidth(200);

        // table columns
        TableColumn<SupabaseClient.Ferramenta, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().id));
        TableColumn<SupabaseClient.Ferramenta, String> identCol = new TableColumn<>("Identificação");
        identCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().identificacao));
        TableColumn<SupabaseClient.Ferramenta, String> respCol = new TableColumn<>("Responsável");
        respCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().responsavel_id == null ? "(não atribuída)" : c.getValue().responsavel_id));
        TableColumn<SupabaseClient.Ferramenta, String> filialCol = new TableColumn<>("Filial");
        filialCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().filial_id));

        table.getColumns().addAll(idCol, identCol, respCol, filialCol);

        HBox center = new HBox(10, typesList, table);
        center.setPadding(new Insets(10));
        root.setCenter(center);

        back.setOnAction(e -> main.goBack());
        btnAdd.setOnAction(e -> showAddDialog(main));
        btnRemove.setOnAction(e -> removeSelected());
        btnTransfer.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Transferência implementável: selecione destino e execute patch.", ButtonType.OK);
            a.showAndWait();
        });

        search.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV==null? "": newV.toLowerCase();
            table.setItems(FXCollections.observableArrayList(table.getItems().stream()
                    .filter(f -> f.identificacao.toLowerCase().contains(q)).collect(Collectors.toList())));
        });

        // load initial data
        loadData(main);
        typesList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) filterByType(newV);
        });
    }

    private void loadData(MainWindow main) {
        String filial = main.getCurrentFilial();
        CompletableFuture.supplyAsync(() -> {
            try {
                FerramentaService svc = new FerramentaService();
                List<SupabaseClient.Ferramenta> all = svc.listAllAsync().join();
                // derive distinct types from tipo_id; in full app you'd join tipos_ferramenta
                List<String> types = all.stream().map(t -> t.tipo_id).distinct().collect(Collectors.toList());
                // filter by filial
                List<SupabaseClient.Ferramenta> filtered = all.stream()
                        .filter(t -> filial==null || filial.equals("(nenhuma)") || filial.equals(t.filial_id))
                        .collect(Collectors.toList());
                return new Object[]{types, filtered};
            } catch (Exception ex) { throw new RuntimeException(ex); }
        }).thenAccept(arr -> {
            @SuppressWarnings("unchecked")
            List<String> types = (List<String>) arr[0];
            @SuppressWarnings("unchecked")
            List<SupabaseClient.Ferramenta> filtered = (List<SupabaseClient.Ferramenta>) arr[1];
            Platform.runLater(() -> {
                typesList.setItems(FXCollections.observableArrayList(types));
                table.setItems(FXCollections.observableArrayList(filtered));
            });
        }).exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void filterByType(String tipoId) {
        table.setItems(FXCollections.observableArrayList(
                table.getItems().stream().filter(f -> tipoId.equals(f.tipo_id)).collect(Collectors.toList())
        ));
    }

    private void showAddDialog(MainWindow main) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Adicionar Ferramenta");
        TextField tipo = new TextField(); tipo.setPromptText("tipo_id (uuid)");
        TextField ident = new TextField(); ident.setPromptText("identificação (ex: IIAB)");

        VBox box = new VBox(8, new Label("Tipo ID:"), tipo, new Label("Identificação:"), ident);
        d.getDialogPane().setContent(box);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                CompletableFuture.runAsync(() -> {
                    try {
                        FerramentaService svc = new FerramentaService();
                        svc.createFerramentaAsync(Map.of(
                                "tipo_id", tipo.getText(),
                                "identificacao", ident.getText(),
                                "filial_id", main.getCurrentFilial()
                        )).join();
                        loadData(main);
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            }
            return null;
        });

        d.showAndWait();
    }

    private void removeSelected() {
        SupabaseClient client = new SupabaseClient();
        SupabaseClient.Ferramenta sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma ferramenta.", ButtonType.OK).showAndWait();
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Marcar ferramenta como removida?", ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                CompletableFuture.runAsync(() -> {
                    try {
                        String patch = "{\"status\":\"removida\"}";
                        client.patch("ferramentas?id=eq." + sel.id, patch);
                        Platform.runLater(() -> table.getItems().remove(sel));
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            }
        });
    }

    public BorderPane getRoot() { return root; }
}
