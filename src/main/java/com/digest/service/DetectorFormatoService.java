package com.digest.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DetectorFormatoService {

	/**
     * Tenta determinar o MIME Type de um arquivo.
     * * @param filePath O caminho (Path) para o arquivo a ser verificado.
     * @return O MIME Type (Ex: "image/png", "text/plain") ou null se não puder ser determinado.
     * @throws IOException Se ocorrer um erro de I/O ao acessar o arquivo.
     */
    public static String identifyMimeType(Path filePath) throws IOException {
        // Files.probeContentType(Path) é o método recomendado para esta tarefa.
        // Ele usa o sistema operacional para determinar o tipo, ou inspeciona o arquivo.
        String mimeType = Files.probeContentType(filePath);
        
        if (mimeType != null) {
            return mimeType;
        }
        
        // Se o probeContentType falhar, tentamos uma adivinhação básica baseada na extensão,
        // embora geralmente seja menos confiável.
        return guessTypeFromExtension(filePath);
    }
    
    /**
     * Tenta adivinhar o tipo a partir da extensão, como um fallback.
     */
    private static String guessTypeFromExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".zip")) {
            return "application/zip";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        }
        // Retorna um tipo genérico para binário desconhecido
        return "application/octet-stream"; 
    }

}

