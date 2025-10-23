package com.digest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    // O método main agora só chama o launch()
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o FXML (o layout da sua interface)
        Parent root = FXMLLoader.load(getClass().getResource("limpeza/MainScene.fxml"));

        // Cria a cena com o tamanho desejado (300x300)
        Scene scene = new Scene(root, 1054, 650);

        primaryStage.setTitle("Ferramenta de Limpeza de Arquivos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}