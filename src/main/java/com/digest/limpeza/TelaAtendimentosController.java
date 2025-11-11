package com.digest.limpeza;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TelaAtendimentosController implements Initializable {
	@FXML
    private ComboBox<String> cbAtendimentos;

    private static final String ATENDIMENTOS_FOLDER = "dados/atendimentos";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarOpcoesAtendimento();
    }

    private void carregarOpcoesAtendimento() {
        // Lista de nomes de arquivos JSON que estão na pasta (você precisa listá-los)
        // Como o ClassLoader tem dificuldade em listar arquivos dentro do JAR, 
        // vamos listar os arquivos conhecidos ou usar um conjunto de nomes fixo por simplicidade:
        
    	Set<String> jsonFiles = new HashSet<String>(Arrays.asList(
    		    "cumulacaoReponsabilidadeSECIJU.json",
    		    "outra_opção.json"
    		));

        for (String fileName : jsonFiles) {
            String fullPath = "/" + ATENDIMENTOS_FOLDER + "/" + fileName;
            
            try (InputStream is = getClass().getResourceAsStream(fullPath)) {
                
                if (is != null) {
                    // 1. Lê o InputStream e converte para um objeto JSON (JsonNode)
                    JsonNode rootNode = mapper.readTree(is);
                    
                    // 2. Extrai o valor da chave "option"
                    String optionValue = rootNode.get("option").asText();
                    
                    // 3. Adiciona ao ComboBox
                    if (optionValue != null && !optionValue.isEmpty()) {
                        cbAtendimentos.getItems().add(optionValue);
                    }
                    
                } else {
                    System.err.println("Arquivo não encontrado no classpath: " + fullPath);
                }

            } catch (Exception e) {
                System.err.println("Erro ao ler ou parsear o arquivo JSON: " + fileName);
                e.printStackTrace();
            }
        }
    }
    
    // Você pode adicionar um manipulador aqui para quando uma opção for selecionada
    @FXML
    private void handleSelection() {
        String selecionado = cbAtendimentos.getSelectionModel().getSelectedItem();
        System.out.println("Opção selecionada: " + selecionado);
        // Lógica para carregar o arquivo JSON ou iniciar o atendimento
    }
}