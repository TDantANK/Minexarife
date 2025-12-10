package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import ui.MainWindow;

public class MainDashboard {

    private final MainWindow main;
    private final VBox root;

    public MainDashboard(MainWindow main) {
        this.main = main;

        root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Menu Principal");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        // Botões de navegação
        Button geralBtn = new Button("Geral");
        Button trabBtn = new Button("Trabalhadores");
        Button ferrBtn = new Button("Ferramentas");
        Button veiBtn = new Button("Veículos");

        geralBtn.setPrefWidth(200);
        trabBtn.setPrefWidth(200);
        ferrBtn.setPrefWidth(200);
        veiBtn.setPrefWidth(200);

        geralBtn.setOnAction(e -> main.openGeral());
        trabBtn.setOnAction(e -> main.openTrabalhadores());
        ferrBtn.setOnAction(e -> main.openFerramentas());
        veiBtn.setOnAction(e -> main.openVeiculos());

        // Botões adicionais
        Button btnVoltar = new Button("⬅ Voltar");
        btnVoltar.setOnAction(e -> main.goBack());

        Button trocarFilial = new Button("Trocar Filial");
        trocarFilial.setOnAction(e -> main.showFilialSelect());

        HBox navBox = new HBox(20, btnVoltar, trocarFilial);
        navBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, geralBtn, trabBtn, ferrBtn, veiBtn, navBox);
    }

    public Node getRoot() {
        return root;
    }
}
