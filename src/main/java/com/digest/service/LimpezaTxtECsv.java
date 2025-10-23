package com.digest.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.digest.limpeza.Limpeza;

public class LimpezaTxtECsv {

public static void limparTxtECsv(String caminhoArquivo) throws IOException {
		
		
	    Path path = Paths.get(caminhoArquivo);
	    
	    System.out.println("Lendo arquivo: " + caminhoArquivo);
	   
	    List<String> linhas = Files.readAllLines(path, StandardCharsets.UTF_8);
	    StringBuilder conteudoLimpo = new StringBuilder();
	    int linhasProcessadas = 0;
	    
	    System.out.println("\n--- Log de Remoção de Caracteres ---");

	    for (String linha : linhas) {
	    	linhasProcessadas++; // Incrementa o contador da linha do arquivo original

	        // 1. Exclui a linha se for vazia/só espaços.
	        if (linha.trim().isEmpty()) {
	            System.out.println("Linha " + linhasProcessadas + ": IGNORADA. (Linha vazia ou com apenas espaços)");
	            continue; // Pula para a próxima iteração
	        }
	        
	        // Chama a função de limpeza com o número da linha
	        
	        String linhaLimpa = Limpeza.limparTextoParcial(linha);
	        //String linhaLimpa = limparTexto(linha, linhasProcessadas);
	        
	        // Adiciona a linha ao conteúdo final SOMENTE se ela não tiver ficado vazia após a limpeza.
	        if (!linhaLimpa.isEmpty()) {
	            conteudoLimpo.append(linhaLimpa);
	            conteudoLimpo.append(System.lineSeparator()); 
	        }
	    }

	    System.out.println("-----------------------------------\n");

	    // --- Remove a última quebra de linha ---
	    if (conteudoLimpo.length() > 0) {
	        int sepLen = System.lineSeparator().length(); 
	        conteudoLimpo.setLength(conteudoLimpo.length() - sepLen);
	    }

	    // Sobrescreve o arquivo original
	    Files.write(path, conteudoLimpo.toString().getBytes(StandardCharsets.UTF_8));
	    
	    System.out.println("Arquivo TXT/CSV limpo e salvo em: " + caminhoArquivo);
	}
}
