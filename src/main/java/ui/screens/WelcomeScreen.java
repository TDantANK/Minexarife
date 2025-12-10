package ui.screens;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ui.MainWindow;

public class WelcomeScreen {

    private VBox root;

    public WelcomeScreen(MainWindow main) {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Text title = new Text("Bem-vindo ao MineXarife");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Button ok = new Button("OK");
        ok.setStyle("-fx-font-size: 20px;");
        ok.setOnAction(e -> main.showFilialSelect());

        root.getChildren().addAll(title, ok);
    }

    public VBox getRoot() {
        return root;
    }
}
