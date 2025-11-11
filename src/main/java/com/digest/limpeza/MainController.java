package com.digest.limpeza;

import com.digest.service.LimpezaTxtECsv;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.nio.file.Path;

import javafx.stage.FileChooser;
import javafx.stage.Stage; 
import javafx.event.ActionEvent; 
import java.io.File;

public class MainController {

    // 1. Injetar o BorderPane (usando o fx:id="mainPane")
    @FXML
    private BorderPane mainPane;

    // --- Métodos de Manipulação de Menu (onAction) ---

    @FXML
    private void handleMenuInicio() {
        // Carrega e exibe a tela de Início
        loadScreen("TelaInicio.fxml");
    }

    @FXML
    private void handleMenuAsistente() {
        // Carrega e exibe a tela de Asistente de Carga
        loadScreen("TelaAsistente.fxml");
    }

    @FXML
    private void handleMenuLimpeza() {
        // Carrega e exibe a tela de Módulo de Limpeza
        loadScreen("TelaLimpeza.fxml");
    }
    
    @FXML
    private void handleTelaAtendimentos() {
    	// Carrega e exibe a tela de Atendimentos
    	loadScreen("TelaAtendimentos.fxml");
    }
    
    @FXML
    private void handleUploadButtonAction(ActionEvent event) {
        
        // 1. Cria o FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o Arquivo para Limpeza (.txt ou .csv)");
        
        // Opcional: Filtros para tipos de arquivo (Txt e Csv)
        FileChooser.ExtensionFilter extFilterCsv = 
        		new FileChooser.ExtensionFilter("Arquivos CSV (*.csv)", "*.csv");
        FileChooser.ExtensionFilter extFilterTxt = 
            new FileChooser.ExtensionFilter("Arquivos de Texto (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().addAll(extFilterTxt, extFilterCsv);
        
        // 2. Obtém a janela (Stage) principal para que o FileChooser seja modal
        // Isso garante que o FileChooser apareça acima da janela principal.
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        // 3. Mostra o diálogo e espera pela seleção do arquivo
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
        	Path filePath = selectedFile.toPath();
            
            // 4. Chama o Serviço de Limpeza
            try {
            	
            	
                LimpezaTxtECsv.filtraPorCodificacao(filePath);             
                
            } catch (IOException e) {
                // Opcional: Adicionar lógica de feedback de erro (ex: Alerta ou Label)
                System.err.println("Erro ao processar o arquivo: ");
                e.printStackTrace();
            }
            
        } else {
            System.out.println("Nenhuma arquivo selecionado.");
        }
    }

    // --- Lógica Comum para Carregar Telas ---
    
    /**
     * Carrega um arquivo FXML e define o nó raiz (Parent) como o centro do mainPane.
     * @param fxmlName O nome do arquivo FXML a ser carregado (ex: "TelaInicio.fxml").
     */
    private void loadScreen(String fxmlName) {
        try {
            // Usa o classloader para encontrar o recurso (certifique-se de que os FXMLs estão no classpath)
        	String fullPath = "/com/digest/limpeza/" + fxmlName;
        	Parent screen = FXMLLoader.load(getClass().getResource(fullPath));
            
            // Define o conteúdo carregado como o novo centro do BorderPane
            mainPane.setCenter(screen);
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela: " + fxmlName);
            e.printStackTrace();
            // Opcional: Mostrar uma mensagem de erro ao usuário
        }
    }
}
