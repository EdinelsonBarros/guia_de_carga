package com.digest.limpeza;

import java.text.Normalizer;
import java.util.regex.Pattern;


public class Limpeza {

    // Regex para caracteres de controle/quebra
    private static final Pattern CARACTERES_CONTROLE_PATTERN = Pattern.compile("[\\n\\r\\p{C}]");
    
    // Regex para todos os caracteres que NÃO são letra, número ou espaço.
    
    private static final Pattern LIMPA_COLUNA_NUMERICA_PATTERN = Pattern.compile("[^0-9 ]");
    private static final Pattern CARACTERES_ESPECIAIS_PATTERN = Pattern.compile("[^a-zA-Z0-9 ]");
    private static final Pattern CARACTERES_ESPECIAIS_PATTERN_PARCIAL = Pattern.compile("[^a-zA-Z0-9 ,;]");

	// ----------------------------------------------------
	// FUNÇÕES DE LIMPEZA INDIVIDUAIS
	// ----------------------------------------------------

    /**
     * Aplica a lógica de Desacentuação (Acentos e caracteres como Ç).
     * @param textoOriginal A string a ser desacentuada.
     * @return A string sem acentos.
     */
    
    public static String limpaColunaNumerica(String textoOriginal) {
    	if(textoOriginal == null) {
    		return ""; // alterar para mostra que tem celula vazia no arquivo
    	}
    	return LIMPA_COLUNA_NUMERICA_PATTERN.matcher(textoOriginal).replaceAll("");
    }
    
    
    public static String limparAcentos(String textoOriginal) {
	    if (textoOriginal == null) {
	        return "";
	    }
        
        // Remove acentos e caracteres especiais (como ç) e depois o marcador
	    return Normalizer.normalize(textoOriginal, Normalizer.Form.NFD)
	                     .replaceAll("[\\p{Mn}]", "");
    }
    
	public Limpeza() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public static String limparCaracteresDeControle(String textoOriginal) {
	    if (textoOriginal == null) {
	        return "";
	    }
	    // Aplica a remoção real dos caracteres de controle/quebra
	    return CARACTERES_CONTROLE_PATTERN.matcher(textoOriginal).replaceAll("");
	}
    
    
    public static String limparEspeciaisEPontuacao(String textoOriginal) {
        if (textoOriginal == null) {
	        return "";
	    }
        // Substitui TUDO que NÃO for (^) letra (a-z, A-Z), número (0-9) ou espaço.
        return CARACTERES_ESPECIAIS_PATTERN.matcher(textoOriginal).replaceAll("");
    }
    
    public static String limparEspeciaisEPontuacaoParcial(String textoOriginal) {
    	if (textoOriginal == null) {
    		return "";
    	}
    	
    	return CARACTERES_ESPECIAIS_PATTERN_PARCIAL.matcher(textoOriginal).replaceAll("");
    }
    
//    public static String getCaracteresEspeciaisPatternParcial(String textoOriginal) {
//		if (textoOriginal == null) {
//	        return "";
//	    }
//		return CARACTERES_ESPECIAIS_PATTERN_PARCIAL.matcher(textoOriginal).replaceAll("");
//	}


	
	
	public static String limparTexto(String textoOriginal) {
	    if (textoOriginal == null) {
	        return "";
	    }
	    
	    String textoAtual = textoOriginal;

	    // PASSO 1: Desacentuação
	    textoAtual = limparAcentos(textoAtual);

	    // PASSO 2: Remoção de Quebras de Linha, Retorno de Carro e Caracteres Ocultos
	    textoAtual = limparCaracteresDeControle(textoAtual);
        
        // PASSO 3: REMOÇÃO DE TODOS OS CARACTERES ESPECIAIS/PONTUAÇÃO
        textoAtual = limparEspeciaisEPontuacao(textoAtual);
	    
	    // Retorna o texto final limpo (aplica o trim final)
	    return textoAtual.trim(); 
	}
	
	public static String limparTextoParcial(String textoOriginal) {
		if (textoOriginal == null) {
			return "";
		}
		
		String textoAtual = textoOriginal;
		
		// PASSO 1: Desacentuação
		textoAtual = limparAcentos(textoAtual);
		
		// PASSO 2: Remoção de Quebras de Linha, Retorno de Carro e Caracteres Ocultos
		textoAtual = limparCaracteresDeControle(textoAtual);
		
		// PASSO 3: REMOÇÃO DE TODOS OS CARACTERES ESPECIAIS/PONTUAÇÃO
		textoAtual = limparEspeciaisEPontuacaoParcial(textoAtual);
		
		// Retorna o texto final limpo (aplica o trim final)
		return textoAtual.trim(); 
	}

	
}
