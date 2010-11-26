package utils;

import java.awt.Point;

public class Constantes {

	// Constantes de desenvolvimento
	public static final Point posicaoInicial = new Point(2,2);
	public static final int Distancia_Desconhecida = -1;
	public static final int semId = -1;
	
	// Constantes de proximidade
	public static final int muitoProximo = 5;
	public static final int proximo = 10;
	public static final int longe = 15;
	
	//Constantes de papeis

	/***************/
	/* Depositante */
	/***************/
	public static final int depositante = 1;
		//objetivo atual
		public static final int buscarBanco = 1;
		public static final int buscarMoeda = 2;
		public static final int depositarMoeda = 3;
	/***************/
	/* Despistador */
	/***************/	
	public static final int despistador = 2;
		public static final int buscarLadroes = 1;
		public static final int atrairLadroes = 2;
		public static final int isolarLadroes = 3;
		
	/***************/
	/*   Cacador   */
	/***************/			
	public static final int cacador = 3;
		public static final int catalogarMoedas=1;
		public static final int marcarMoeda=2;
		public static final int fugirLadrao=3;
	
	// Constantes de Visao
	public static final int Ve_Sem_visao_para_o_local = -2;
	public static final int Ve_Fora_do_ambiente = -1;
	public static final int Ve_Celula_vazia = 0;
	public static final int Ve_Parede = 1;
	public static final int Ve_Banco = 3;
	public static final int Ve_Moeda = 4;
	public static final int Ve_Pastilha_do_Poder = 5;
	public static final int Ve_Poupador = 100;
	public static final int Ve_Ladrao = 200;
	
	//Constantes de Movimentacao
	public static final int Mov_Desconhecido = -1;
	public static final int Mov_Parado = 0;
	public static final int Mov_Baixo = 2;
	public static final int Mov_Acima = 1;
	public static final int Mov_Direita = 3;
	public static final int Mov_Esquerda = 4;
	
	//Percepcao olfativa
	public static final int Sente_nenhum_cheiro = 0;
	public static final int Sente_cheiro_muito_fraco = 5;
	public static final int Sente_cheiro_fraco = 4;
	public static final int Sente_cheiro_medio = 3;
	public static final int Sente_cheiro_forte = 2;
	public static final int Sente_cheiro_muito_forte = 1;

	
}
