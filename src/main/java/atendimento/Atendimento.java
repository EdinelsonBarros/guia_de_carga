package atendimento;

import java.util.ArrayList;

public class Atendimento {
	String nome;
	String via;
	ArrayList<String> colunas_tabela;
	ArrayList<String> passos;
	
	
	public Atendimento() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 *  Essa classe será usada para criar os objetos de atendimento
	
	
	*/

	public Atendimento(String nome, String via, ArrayList<String> colunas_tabela, ArrayList<String> passos) {
		super();
		this.nome = nome;
		this.via = via;
		this.colunas_tabela = colunas_tabela;
		this.passos = passos;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getVia() {
		return via;
	}


	public void setVia(String via) {
		this.via = via;
	}


	public ArrayList<String> getColunas_tabela() {
		return colunas_tabela;
	}


	public void setColunas_tabela(ArrayList<String> colunas_tabela) {
		this.colunas_tabela = colunas_tabela;
	}


	public ArrayList<String> getPassos() {
		return passos;
	}


	public void setPassos(ArrayList<String> passos) {
		this.passos = passos;
	}
	
	
	
}
