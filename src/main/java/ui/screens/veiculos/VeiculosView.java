package ui.screens.veiculos;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import ui.MainWindow;
import service.VeiculoService;
import service.SupabaseClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * VeiculosView - tabela de veículos com adicionar/remover (marca status) e busca.
 */
public class VeiculosView {

    private final BorderPane root = new BorderPane();
    private final TableView<SupabaseClient.Veiculo> table = new TableView<>();

    public VeiculosView(MainWindow main) {
        VBox top = new VBox(6);
        top.setPadding(new Insets(10));
        Text title = new Text("Veículos — Filial: " + (main.getCurrentFilial() == null ? "(nenhuma)" : main.getCurrentFilial()));
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        TextField search = new TextField();
        search.setPromptText("Pesquisar por identificação (placa)");

        HBox actions = new HBox(8);
        Button btnAdd = new Button("Adicionar Veículo");
        Button btnRemove = new Button("Remover Selecionado");
        Button btnTransfer = new Button("Transferir (manual)");
        Button back = new Button("⬅ Voltar");
        actions.getChildren().addAll(btnAdd, btnRemove, btnTransfer, back);
        actions.setAlignment(Pos.CENTER_LEFT);

        top.getChildren().addAll(title, search, actions);
        root.setTop(top);

        // Colunas da tabela
        TableColumn<SupabaseClient.Veiculo, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().id));
        TableColumn<SupabaseClient.Veiculo, String> identCol = new TableColumn<>("Identificação");
        identCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().identificacao));
        TableColumn<SupabaseClient.Veiculo, String> modelCol = new TableColumn<>("Modelo");
        modelCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().modelo == null ? "" : c.getValue().modelo));
        TableColumn<SupabaseClient.Veiculo, String> respCol = new TableColumn<>("Responsável");
        respCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().responsavel_id == null ? "(nenhum)" : c.getValue().responsavel_id));
        TableColumn<SupabaseClient.Veiculo, String> filialCol = new TableColumn<>("Filial");
        filialCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().filial_id == null ? "(nenhuma)" : c.getValue().filial_id));

        table.getColumns().addAll(idCol, identCol, modelCol, respCol, filialCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        root.setCenter(table);

        // ações
        back.setOnAction(e -> main.goBack());
        btnAdd.setOnAction(e -> showAddDialog(main));
        btnRemove.setOnAction(e -> removeSelected());

        // busca (filtra os items atualmente carregados)
        search.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.toLowerCase();
            List<SupabaseClient.Veiculo> filtered = table.getItems().stream()
                    .filter(v -> v.identificacao != null && v.identificacao.toLowerCase().contains(q))
                    .collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(filtered));
        });

        // carregamento inicial
        loadData(main);
    }

    private void loadData(MainWindow main) {
        String filial = main.getCurrentFilial();
        CompletableFuture.supplyAsync(() -> {
            try {
                VeiculoService svc = new VeiculoService();
                List<SupabaseClient.Veiculo> all = svc.listAllAsync().join();
                return all.stream()
                        .filter(v -> filial == null || filial.equals("(nenhuma)") || filial.equals(v.filial_id))
                        .collect(Collectors.toList());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(listData -> Platform.runLater(() -> table.setItems(FXCollections.observableArrayList(listData))))
          .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void showAddDialog(MainWindow main) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Adicionar Veículo");

        TextField ident = new TextField(); ident.setPromptText("Placa ou identificação");
        TextField modelo = new TextField(); modelo.setPromptText("Modelo");

        VBox box = new VBox(8, new Label("Identificação:"), ident, new Label("Modelo:"), modelo);
        box.setPadding(new Insets(8));
        d.getDialogPane().setContent(box);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) d.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDefaultButton(true);

        d.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                okButton.setDisable(true);
                String identificacao = ident.getText();
                String mod = modelo.getText();
                // chamada assíncrona e feedback
                VeiculoService svc = new VeiculoService();
                svc.createVeiculoAsync(identificacao, mod, main.getCurrentFilial())
                    .thenAccept(created -> {
                        Platform.runLater(() -> {
                            okButton.setDisable(false);
                            if (created != null) {
                                loadData(main);
                                new Alert(Alert.AlertType.INFORMATION, "Veículo criado: " + created.identificacao, ButtonType.OK).showAndWait();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Não foi possível criar o veículo.", ButtonType.OK).showAndWait();
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            okButton.setDisable(false);
                            new Alert(Alert.AlertType.ERROR, "Erro ao criar veículo: " + ex.getMessage(), ButtonType.OK).showAndWait();
                        });
                        return null;
                    });
            }
            return null;
        });

        d.showAndWait();
    }

    private void removeSelected() {
        SupabaseClient.Veiculo sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um veículo.", ButtonType.OK).showAndWait();
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Marcar veículo como removido?", ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                CompletableFuture.runAsync(() -> {
                    try {
                        VeiculoService svc = new VeiculoService();
                        svc.patchVeiculo(sel.id, java.util.Map.of("status", "removido"));
                        Platform.runLater(() -> table.getItems().remove(sel));
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            }
        });
    }

    public BorderPane getRoot() {
        return root;
    }
}
