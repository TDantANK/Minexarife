package ui.screens;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ui.MainWindow;

import java.util.List;

/**
 * FilialSelectScreen
 * - ComboBox is populated with mock data (replace loadFiliais with Supabase client)
 */
public class FilialSelectScreen {

    private VBox root;

    public FilialSelectScreen(MainWindow main) {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Selecione sua filial");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ComboBox<String> combo = new ComboBox<>();
        // TODO: replace with real call to Supabase
        combo.getItems().addAll(loadFiliais());

        Button confirm = new Button("Confirmar filial");
        confirm.setOnAction(e -> {
            String sel = combo.getValue();
            if (sel != null && !sel.isEmpty()) {
                main.setCurrentFilial(sel);
                main.showDashboard();
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING, "Selecione uma filial antes de confirmar.", ButtonType.OK);
                a.showAndWait();
            }
        });

        Button refresh = new Button("Atualizar lista");
        refresh.setOnAction(e -> {
            combo.getItems().setAll(loadFiliais());
        });

        Button add = new Button("Adicionar nova filial");
        add.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setHeaderText("Nome da nova filial");
            d.showAndWait().ifPresent(name -> combo.getItems().add(name));
        });

        root.getChildren().addAll(title, combo, confirm, refresh, add);
    }

    private java.util.List<String> loadFiliais() {
        // Mock data. Replace with real Supabase fetch.
        return List.of("Filial A", "Filial B", "Filial C");
    }

    public VBox getRoot() {
        return root;
    }
}
