package com.digest.service;

import org.mozilla.universalchardet.UniversalDetector;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class DetectorCodificacao {
	/**
     * Tenta detectar a codificação de um arquivo usando JUniversalCharDet.
     * Retorna a codificação detectada (ex: "UTF-8", "WINDOWS-1252") ou null se a confiança for baixa.
     * @param filePath O caminho do arquivo a ser verificado.
     * @return O nome da codificação (String) ou null.
     * @throws IOException Se houver erro ao ler o arquivo.
     */
    public static String detectarCodificacao(Path filePath) throws IOException {
        
        // Define o tamanho do buffer de leitura
        // Ler os primeiros 4KB (4096 bytes) geralmente é suficiente para a detecção
        byte[] buf = new byte[4096];
        
        // Cria o detector
        UniversalDetector detector = new UniversalDetector(null);
        
        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            int nread;
            
            // Lê o arquivo em blocos e alimenta o detector
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
        }
        
        // Finaliza a detecção
        detector.dataEnd();

        // Obtém o resultado da detecção
        String encoding = detector.getDetectedCharset();
        
        // Limpa o estado do detector para o próximo uso (boa prática)
        detector.reset();

        return encoding;
    }
}
