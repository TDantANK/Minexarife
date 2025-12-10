package ui;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

import java.util.Stack;

/**
 * MainWindow - gerenciador central de telas com histórico (back stack).
 *
 * Uso:
 *  - Cada screen deve ter um método getRoot() que retorna um javafx.scene.Node
 *  - Para navegar mantendo histórico, MainWindow.navigateTo(node)
 *  - Para voltar: MainWindow.goBack()
 *
 * Padrão de telas existentes no projeto:
 *  - ui.screens.WelcomeScreen
 *  - ui.screens.FilialSelectScreen
 *  - ui.screens.MainDashboard
 *  - ui.screens.geral.GeralView
 *  - ui.screens.trabalhadores.TrabalhadoresView
 *  - ui.screens.ferramentas.FerramentasView
 *  - ui.screens.veiculos.VeiculosView
 */
public class MainWindow extends Application {

    private Stage stage;
    private BorderPane root;
    private final Stack<Node> history = new Stack<>();
    private String currentFilial = null;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.root = new BorderPane();

        // initial scene size; screens can override layout inside center
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("MineXarife");
        showWelcome(); // entry point
        stage.show();
    }

    // -----------------------
    // Navigation API
    // -----------------------

    /**
     * Navega para um Node preservando a tela atual no histórico (stack).
     * Use este método para que goBack() funcione.
     */
    public void navigateTo(Node node) {
        Node current = root.getCenter();
        if (current != null) {
            history.push(current);
        }
        root.setCenter(node);
    }

    /**
     * Volta para a tela anterior (se existir).
     * Se não houver tela anterior, não faz nada.
     */
    public void goBack() {
        if (!history.isEmpty()) {
            Node previous = history.pop();
            root.setCenter(previous);
        }
    }

    /**
     * Substitui a tela atual sem guardar histórico (limpa histórico).
     * Use quando iniciar um fluxo novo (ex: iniciar app -> welcome).
     */
    public void setCenterClearingHistory(Node node) {
        history.clear();
        root.setCenter(node);
    }

    // -----------------------
    // High-level screen methods (convenience)
    // -----------------------

    public void showWelcome() {
        // start fresh: clear history so back from welcome does nothing
        ui.screens.WelcomeScreen screen = new ui.screens.WelcomeScreen(this);
        setCenterClearingHistory(screen.getRoot());
    }

    public void showFilialSelect() {
        // navigate from welcome -> filial select (keeps welcome in history)
        ui.screens.FilialSelectScreen screen = new ui.screens.FilialSelectScreen(this);
        navigateTo(screen.getRoot());
    }

    public void showDashboard() {
        ui.screens.MainDashboard screen = new ui.screens.MainDashboard(this);
        navigateTo(screen.getRoot());
    }

    public void openGeral() {
        ui.screens.geral.GeralView screen = new ui.screens.geral.GeralView(this);
        navigateTo(screen.getRoot());
    }

    public void openTrabalhadores() {
        ui.screens.trabalhadores.TrabalhadoresView screen = new ui.screens.trabalhadores.TrabalhadoresView(this);
        navigateTo(screen.getRoot());
    }

    public void openFerramentas() {
        ui.screens.ferramentas.FerramentasView screen = new ui.screens.ferramentas.FerramentasView(this);
        navigateTo(screen.getRoot());
    }

    public void openVeiculos() {
        ui.screens.veiculos.VeiculosView screen = new ui.screens.veiculos.VeiculosView(this);
        navigateTo(screen.getRoot());
    }

    // -----------------------
    // Filial state
    // -----------------------

    public void setCurrentFilial(String filial) {
        this.currentFilial = filial;
    }

    public String getCurrentFilial() {
        return this.currentFilial;
    }

    // -----------------------
    // Utility: expose root if screens need direct access to top/bottom
    // -----------------------
    public BorderPane getRootPane() {
        return this.root;
    }

    public Stage getStage() {
        return this.stage;
    }

    // -----------------------
    // main
    // -----------------------
    public static void main(String[] args) {
        launch(args);
    }
}
