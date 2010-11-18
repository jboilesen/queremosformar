package algoritmo;

import controle.Constantes;

public class Ladrao extends ProgramaLadrao {
	
	public static final int Ve_Poupador1 = 100;
	public static final int Ve_Poupador2 = 110;
	
	public static final int Mov_Parado = 0;
	public static final int Mov_Baixo = 2;
	public static final int Mov_Acima = 1;
	public static final int Mov_Direita = 3;
	public static final int Mov_Esquerda = 4;

	public static final int Sente_nenhum_cheiro = 0;
	public static final int Sente_cheiro_muito_fraco = 1;
	public static final int Sente_cheiro_fraco = 2;
	public static final int Sente_cheiro_medio = 3;
	public static final int Sente_cheiro_forte = 4;
	public static final int Sente_cheiro_muito_forte = 5;
	
	private int[] visao;
	private int[] cheiro;
	private int direcaoMovimento = 0;
	
	public int acao() {
		
		visao = sensor.getVisaoIdentificacao();
		cheiro = sensor.getAmbienteOlfatoPoupador();
		
		if(viPoupador()){
			//Começa a perseguir
			direcaoMovimento = perseguePoupador();
			return direcaoMovimento;
		}
		
		if(sentiCheiro()){
			//Tenta encontrar o poupador
			direcaoMovimento = procuraPoupador();
			return direcaoMovimento;
		}
		
		return (int) (Math.random() * 5);
	}
	
	private boolean poupador(int ponto){
		if (ponto == Ve_Poupador1 || ponto == Ve_Poupador2) {
			return true;
		}
		return false;
	}
	
	private boolean viPoupador(){
		for (int i = 0; i < 24; i++) {
			if (poupador(visao[i])) {
				return true;
			}
		}
		return false;
	}

	private int perseguePoupador(){
		if(poupador(visao[0]) || poupador(visao[1]) || poupador(visao[2])
				|| poupador(visao[3]) || poupador(visao[4]) || poupador(visao[6])
					|| poupador(visao[7]) || poupador(visao[8])){
			return Mov_Acima;
		}else if(poupador(visao[15]) || poupador(visao[16]) || poupador(visao[17])
					|| poupador(visao[19]) || poupador(visao[20]) || poupador(visao[21])
						|| poupador(visao[22]) || poupador(visao[23])){
			return Mov_Baixo;
		}else if(poupador(visao[5]) || poupador(visao[10]) || poupador(visao[14])
				|| poupador(visao[11])){
			return Mov_Esquerda;
		}else if(poupador(visao[9]) || poupador(visao[13]) || poupador(visao[18])
				|| poupador(visao[12])){
			return Mov_Direita;
		}
		return 0;
	}
	
	private boolean sentiCheiro(){
		for (int i = 0; i < 8; i++) {
			if (cheiro[i] != 0) {
				return true;
			}
		}
		return false;
	}
	
	private int procuraPoupador(){
		if(cheiro[0] != 0 || cheiro[1] != 0 || cheiro[2] != 0){
			return Mov_Acima;
		}else if(cheiro[5] != 0 || cheiro[6] != 0 || cheiro[7] != 0){
			return Mov_Baixo;
		}else if(cheiro[3] != 0){
			return Mov_Esquerda;
		}else if(cheiro[4] != 0){
			return Mov_Direita;
		}
		return 0;
	}
}