package com.digest.limpeza;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ColumnCardController {
    @FXML
    private Label nomeLabel;

    @FXML
    private Label tipoLabel;

    public void setNome(String nome) {
        if (nomeLabel != null) nomeLabel.setText(nome);
    }

    public void setTipo(String tipo) {
        if (tipoLabel != null) tipoLabel.setText(tipo);
    }
}
