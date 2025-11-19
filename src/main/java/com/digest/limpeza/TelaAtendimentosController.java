package com.digest.limpeza;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Importação adicionada para carregar FXMLs dinamicamente
import javafx.fxml.Initializable;
import javafx.scene.Parent; // Importação adicionada para o nó raiz do FXML do Card
import javafx.scene.control.ComboBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane; // Importação adicionada para o FlowPane
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TelaAtendimentosController implements Initializable {
	@FXML
    private ComboBox<String> cbAtendimentos;

    // Nova UI para exibir título e descrição
    @FXML
    private Label tituloLabel;

    @FXML
    private TextArea descricaoArea;

    // Novos componentes: escolha de via e passos
    @FXML
    private ChoiceBox<String> cbVias;

    @FXML
    private ListView<String> passosList;

    // NOVO COMPONENTE: FlowPane para exibir os cards das colunas
    @FXML
    private FlowPane colunasFlow; // Referencia o fx:id="colunasFlow" do TelaAtendimentos.fxml 

    // REMOVIDOS os elementos da TableView:
    /*
    @FXML
    private TableView<ColumnSpec> colunasTable; 
    @FXML
    private TableColumn<ColumnSpec, String> colNome;
    @FXML
    private TableColumn<ColumnSpec, String> colTipo;
    */

    // Map para localizar o arquivo JSON correspondente a cada opção do combo
    private final Map<String, String> optionToFile = new HashMap<>();

    // Guarda o último JSON carregado (para trocar via sem reler o arquivo)
    private JsonNode currentRoot = null;

    private static final String ATENDIMENTOS_FOLDER = "dados/atendimentos";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // REMOVIDA a configuração das TableColumns

        carregarOpcoesAtendimento();

        // listener para quando o usuário mudar a via selecionada
        cbVias.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && currentRoot != null) {
                JsonNode viasNode = currentRoot.path("vias");
                JsonNode viaNode = viasNode.path(newV);
                if (viaNode.isMissingNode()) {
                    // às vezes a chave pode ter outro nome (ex.: 'tela'/'banco') - tentar ignorar
                    populateViaDetails(newV, null);
                } else {
                    populateViaDetails(newV, viaNode);
                }
            }
        });
    }

    private void carregarOpcoesAtendimento() {
        // Lista dinâmica de arquivos JSON na pasta de resources
        Set<String> jsonFiles = listJsonFilesInFolder(ATENDIMENTOS_FOLDER);

        if (jsonFiles.isEmpty()) {
            // fallback para lista conhecida (compatibilidade)
            jsonFiles = new HashSet<String>(Arrays.asList(
                    "cumulacaoReponsabilidadeSECIJU.json",
                    "outra_opção.json"
            ));
        }

        for (String fileName : jsonFiles) {
            String fullPath = "/" + ATENDIMENTOS_FOLDER + "/" + fileName;
            try (InputStream is = getClass().getResourceAsStream(fullPath)) {
                if (is != null) {
                    // Lê o InputStream e converte para um objeto JSON (JsonNode)
                    JsonNode rootNode = mapper.readTree(is);

                    // Extrai o valor da chave "option" e adiciona ao ComboBox
                    JsonNode optNode = rootNode.get("option");
                    String optionValue = (optNode != null && !optNode.isNull()) ? optNode.asText() : null;

                    if (optionValue != null && !optionValue.isEmpty()) {
                        cbAtendimentos.getItems().add(optionValue);
                        // Armazena o mapeamento para buscar o arquivo quando selecionado
                        optionToFile.put(optionValue, fileName);
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

    /**
     * Lista arquivos .json dentro de um diretório de resources, funcionando tanto
     * quando executado na IDE (protocolo file) quanto quando empacotado em JAR.
     * Retorna nomes de arquivo (somente o nome, sem caminho).
     */
    private Set<String> listJsonFilesInFolder(String folderPath) {
        Set<String> result = new HashSet<>();
        try {
            // Tenta localizar o recurso pelo classloader
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = cl.getResources(folderPath);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    // Quando em ambiente de desenvolvimento: caminho de arquivos
                    try {
                        Path folder = Paths.get(url.toURI());
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.json")) {
                            for (Path entry : stream) {
                                result.add(entry.getFileName().toString());
                            }
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if ("jar".equals(protocol)) {
                    // Quando empacotado em jar: abrir o JAR e listar entradas
                    String path = url.getPath(); // ex: file:/C:/.../app.jar!/dados/atendimentos
                    String jarPath = path.substring(5, path.indexOf("!")); // remove "file:"
                    try (JarFile jar = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jar.entries();
                        String prefix = folderPath + "/";
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(prefix) && !entry.isDirectory() && name.endsWith(".json")) {
                                // adiciona somente o nome do arquivo
                                String filename = name.substring(prefix.length());
                                // se estiver dentro de subpastas, pega apenas o último segmento
                                if (filename.contains("/")) {
                                    filename = filename.substring(filename.lastIndexOf("/") + 1);
                                }
                                result.add(filename);
                            }
                        }
                    }
                } else {
                    // Outros protocolos (jar:file, etc) - tentar tratar via URL.toString
                    String urlStr = url.toString();
                    if (urlStr.contains("!") && urlStr.contains(".jar")) {
                        // formato: jar:file:/path/to/jar.jar!/dados/atendimentos
                        int jarStart = urlStr.indexOf("file:");
                        int bang = urlStr.indexOf("!");
                        if (jarStart >= 0 && bang >= 0) {
                            String jarFilePath = urlStr.substring(jarStart + 5, bang);
                            try (JarFile jar = new JarFile(jarFilePath)) {
                                String prefix = folderPath + "/";
                                Enumeration<JarEntry> entries = jar.entries();
                                while (entries.hasMoreElements()) {
                                    JarEntry entry = entries.nextElement();
                                    String name = entry.getName();
                                    if (name.startsWith(prefix) && !entry.isDirectory() && name.endsWith(".json")) {
                                        String filename = name.substring(prefix.length());
                                        if (filename.contains("/")) {
                                            filename = filename.substring(filename.lastIndexOf("/") + 1);
                                        }
                                        result.add(filename);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    
    // Manipulador quando uma opção for selecionada
    @FXML
    private void handleSelection() {
        String selecionado = cbAtendimentos.getSelectionModel().getSelectedItem();
        System.out.println("Opção selecionada: " + selecionado);

        if (selecionado == null) {
            tituloLabel.setText("");
            descricaoArea.setText("");
            cbVias.getItems().clear();
            passosList.getItems().clear();
            colunasFlow.getChildren().clear(); // Limpa o FlowPane
            currentRoot = null;
            return;
        }

        String fileName = optionToFile.get(selecionado);
        if (fileName == null) {
            // Caso não encontremos o arquivo mapeado, limpa a visualização
            tituloLabel.setText(selecionado);
            descricaoArea.setText("Descrição não disponível.");
            return;
        }

        String fullPath = "/" + ATENDIMENTOS_FOLDER + "/" + fileName;
        try (InputStream is = getClass().getResourceAsStream(fullPath)) {
            if (is != null) {
                JsonNode root = mapper.readTree(is);
                currentRoot = root;

                JsonNode nomeItNode = root.get("nome_it");
                JsonNode descricaoItNode = root.get("descricao_it");

                String titulo = (nomeItNode != null && !nomeItNode.isNull()) ? nomeItNode.asText() : selecionado;
                String descricao = (descricaoItNode != null && !descricaoItNode.isNull()) ? descricaoItNode.asText() : "";

                tituloLabel.setText(titulo);
                descricaoArea.setText(descricao);

                // popula as vias (tela, banco, ...)
                cbVias.getItems().clear();
                JsonNode viasNode = root.path("vias");
                if (viasNode.isObject()) {
                    Iterator<String> it = viasNode.fieldNames();
                    while (it.hasNext()) {
                        String viaKey = it.next();
                        cbVias.getItems().add(viaKey);
                    }
                }

                // seleciona a primeira via automaticamente e popula detalhes
                if (!cbVias.getItems().isEmpty()) {
                    String first = cbVias.getItems().get(0);
                    cbVias.getSelectionModel().select(first);
                    JsonNode viaNode = viasNode.path(first);
                    populateViaDetails(first, viaNode);
                } else {
                    passosList.getItems().clear();
                    colunasFlow.getChildren().clear(); // Limpa o FlowPane
                }

            } else {
                tituloLabel.setText(selecionado);
                descricaoArea.setText("Arquivo de dados não encontrado: " + fileName);
            }
        } catch (IOException e) {
            tituloLabel.setText(selecionado);
            descricaoArea.setText("Erro ao ler o arquivo de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Popula passosList e colunasFlow com base na via selecionada.
     * viaNode pode ser null — nesse caso limpa as listas.
     */
    private void populateViaDetails(String viaKey, JsonNode viaNode) {
        passosList.getItems().clear();
        colunasFlow.getChildren().clear(); // NOVO: Limpa o FlowPane para os cards

        if (viaNode == null || viaNode.isMissingNode()) {
            return;
        }

        // descrição específica da via (ex: descricao_it_tela ou descricao_it_banco) — procura por qualquer campo que contenha 'descricao'
        String viaDescricao = "";
        Iterator<String> fieldNames = viaNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fn = fieldNames.next();
            if (fn.toLowerCase().contains("descricao")) {
                JsonNode d = viaNode.get(fn);
                if (d != null && !d.isNull()) {
                    viaDescricao = d.asText();
                    break;
                }
            }
        }
        if (!viaDescricao.isEmpty()) {
            descricaoArea.setText(viaDescricao);
        }

        // passos: array de objetos ou strings
        JsonNode passosNode = viaNode.path("passos");
        if (passosNode.isArray()) {
            for (JsonNode passo : passosNode) {
                if (passo.isObject()) {
                    // pega o primeiro valor do objeto (por exemplo {"passo01": "Abra..."})
                    Iterator<String> keys = passo.fieldNames();
                    if (keys.hasNext()) {
                        String k = keys.next();
                        JsonNode v = passo.get(k);
                        if (v != null && !v.isNull()) {
                            passosList.getItems().add(v.asText());
                        }
                    }
                } else if (passo.isTextual()) {
                    passosList.getItems().add(passo.asText());
                }
            }
        }

        // colunas: array de objetos com 'nome' e 'tipo'
        JsonNode colunasNode = viaNode.path("colunas");
        if (colunasNode.isArray()) {
            for (JsonNode col : colunasNode) {
                String nome = col.path("nome").asText("");
                String tipo = col.path("tipo").asText("");
                
                try {
                    // 1. Carrega o FXML do Card
                    // Certifique-se de que "ColumnCard.fxml" está no mesmo diretório/classpath
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ColumnCard.fxml"));
                    Parent columnCard = loader.load(); 

                    // 2. Obtém o Controller do Card
                    ColumnCardController controller = loader.getController();
                    
                    // 3. Define os dados no Controller do Card
                    controller.setNome(nome);
                    controller.setTipo(tipo);
                    
                    // 4. Adiciona o card (HBox) ao FlowPane
                    colunasFlow.getChildren().add(columnCard);

                } catch (IOException e) {
                    System.err.println("Erro ao carregar o ColumnCard.fxml: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // A classe ColumnSpec não é mais usada pela TableView, mas a mantive 
    // caso ela seja usada em outro lugar ou você precise dela como modelo de dados.
    public static class ColumnSpec {
        private final StringProperty nome = new SimpleStringProperty("");
        private final StringProperty tipo = new SimpleStringProperty("");

        public ColumnSpec(String nome, String tipo) {
            this.nome.set(nome);
            this.tipo.set(tipo);
        }

        public String getNome() { return nome.get(); }
        public StringProperty nomeProperty() { return nome; }
        public void setNome(String n) { nome.set(n); }

        public String getTipo() { return tipo.get(); }
        public StringProperty tipoProperty() { return tipo; }
        public void setTipo(String t) { tipo.set(t); }
    }
}