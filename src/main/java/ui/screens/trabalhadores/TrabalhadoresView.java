package ui.screens.trabalhadores;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import ui.MainWindow;
import service.TrabalhadorService;
import service.SupabaseClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * TrabalhadoresView - lista, detalhes, adicionar e remover (desvincular filial).
 */
public class TrabalhadoresView {

    private final BorderPane root = new BorderPane();
    private final ListView<SupabaseClient.Trabalhador> list = new ListView<>();
    private final Label lblNome = new Label("Nome: -");
    private final Label lblMat = new Label("Matrícula: -");
    private final Label lblFilial = new Label("Filial: -");

    public TrabalhadoresView(MainWindow main) {
        VBox top = new VBox(6);
        top.setPadding(new Insets(10));
        Text title = new Text("Trabalhadores — Filial: " + (main.getCurrentFilial() == null ? "(nenhuma)" : main.getCurrentFilial()));
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        TextField search = new TextField();
        search.setPromptText("Pesquisar por nome ou matrícula");

        HBox actions = new HBox(8);
        Button btnAdd = new Button("Adicionar");
        Button btnRemove = new Button("Remover selecionado");
        Button back = new Button("⬅ Voltar");
        actions.getChildren().addAll(btnAdd, btnRemove, back);
        actions.setAlignment(Pos.CENTER_RIGHT);

        top.getChildren().addAll(title, search, actions);

        // center: list + details
        VBox details = new VBox(8, lblNome, lblMat, lblFilial);
        details.setPadding(new Insets(10));
        details.setPrefWidth(320);

        HBox center = new HBox(10, list, details);
        center.setPadding(new Insets(10));

        root.setTop(top);
        root.setCenter(center);

        back.setOnAction(e -> main.goBack());
        btnAdd.setOnAction(e -> showAddDialog(main));
        btnRemove.setOnAction(e -> removeSelected());

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                lblNome.setText("Nome: " + newV.nome);
                lblMat.setText("Matrícula: " + (newV.matricula == null ? "-" : newV.matricula));
                lblFilial.setText("Filial: " + (newV.filial_id == null ? "(nenhuma)" : newV.filial_id));
            } else {
                lblNome.setText("Nome: -");
                lblMat.setText("Matrícula: -");
                lblFilial.setText("Filial: -");
            }
        });

        // search listener filtra itens já carregados
        search.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.toLowerCase();
            List<SupabaseClient.Trabalhador> filtered = list.getItems().stream()
                    .filter(t -> (t.nome != null && t.nome.toLowerCase().contains(q)) ||
                                 (t.matricula != null && t.matricula.toLowerCase().contains(q)))
                    .collect(Collectors.toList());
            list.setItems(FXCollections.observableArrayList(filtered));
        });

        loadData(main);
    }

    private void loadData(MainWindow main) {
        String filial = main.getCurrentFilial();
        CompletableFuture.supplyAsync(() -> {
            try {
                TrabalhadorService svc = new TrabalhadorService();
                List<SupabaseClient.Trabalhador> all = svc.listAllAsync().join();
                return all.stream()
                        .filter(t -> filial == null || filial.equals("(nenhuma)") || filial.equals(t.filial_id))
                        .collect(Collectors.toList());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(listData -> Platform.runLater(() -> list.setItems(FXCollections.observableArrayList(listData))))
          .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void showAddDialog(MainWindow main) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Adicionar Trabalhador");
        TextField nome = new TextField(); nome.setPromptText("Nome");
        TextField mat = new TextField(); mat.setPromptText("Matrícula");

        VBox box = new VBox(8, new Label("Nome:"), nome, new Label("Matrícula:"), mat);
        box.setPadding(new Insets(8));
        d.getDialogPane().setContent(box);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String n = nome.getText();
                String m = mat.getText();
                CompletableFuture.runAsync(() -> {
                    try {
                        TrabalhadorService svc = new TrabalhadorService();
                        svc.createAsync(Map.of("nome", n, "matricula", m, "filial_id", main.getCurrentFilial())).join();
                        loadData(main);
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            }
            return null;
        });

        d.showAndWait();
    }

    private void removeSelected() {
        SupabaseClient.Trabalhador sel = list.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um trabalhador primeiro.", ButtonType.OK).showAndWait();
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Remover trabalhador (desvincular da filial)?", ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                CompletableFuture.runAsync(() -> {
                    try {
                        TrabalhadorService svc = new TrabalhadorService();
                        svc.patchTrabalhador(sel.id, Map.of("filial_id", null));
                        Platform.runLater(() -> {
                            list.getItems().remove(sel);
                            lblNome.setText("Nome: -");
                            lblMat.setText("Matrícula: -");
                            lblFilial.setText("Filial: -");
                        });
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            }
        });
    }

    public BorderPane getRoot() {
        return root;
    }
}
