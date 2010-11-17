package algoritmo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import utils.Ambiente;
import utils.Ponto;

public class Poupador extends ProgramaPoupador {

	public static final int Ve_Sem_visao_para_o_local = -2;
	public static final int Ve_Fora_do_ambiente = -1;
	public static final int Ve_Celula_vazia = 0;
	public static final int Ve_Parede = 1;
	public static final int Ve_Banco = 3;
	public static final int Ve_Moeda = 4;
	public static final int Ve_Pastilha_do_Poder = 5;
	public static final int Ve_Poupador0 = 100;
	public static final int Ve_Poupador1 = 110;
	public static final int Ve_Ladrao1 = 200;
	public static final int Ve_Ladrao2 = 210;
	public static final int Ve_Ladrao3 = 220;
	public static final int Ve_Ladrao4 = 230;

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

	private boolean contado = false;
	private int[] visao = null;
	public static Ambiente ambiente = new Ambiente();
	private ArrayList<Ponto> moedasAPegar = new ArrayList<Ponto>();

	private int direcaoDeMovimento = 0;

	public int acao() {

		// para sabermos quantos jogadores temos
		if (!this.contado) {
			ambiente.contaPoupador();
			this.contado = true;
		}

		// primeiro guardamos a visao
		visao = sensor.getVisaoIdentificacao();
		empilharMoedas();

		// se nao estamos na ultima rodada
		if (ambiente.getTicsFaltantes() > 1) {
			// se aproxima da moeda mais proxima
		} else {
			// se pegar a moeda for seguro, pega a moeda

		}
		// registra o movimento no ambiente
		ambiente.contaMovimento();

		if (estouNumlabirinto()) {
			direcaoDeMovimento = sairLabirinto();
		}

		if (!(moedasAPegar.isEmpty())) {
			Ponto pegar = moedasAPegar.get(0);

			if (pegar.getX() == sensor.getPosicao().x
					&& pegar.getY() == sensor.getPosicao().y) {
				moedasAPegar.remove(0);
			}
			direcaoDeMovimento = tentaPegarMoeda();
		}

		if (viLadrao()) {
			direcaoDeMovimento = fugirLadrao();
		}

		while (direcaoDeMovimento == 0) {
			direcaoDeMovimento = (int) (Math.random() * 5 );
		}
		return direcaoDeMovimento;
	}
	
	private boolean validarMovimento(){
		if (direcaoDeMovimento == Mov_Acima) {
			if(visao[7] == Ve_Moeda){ return true;}
			if(visao[7] == Ve_Celula_vazia){ return true;}
			if(visao[7] == Ve_Pastilha_do_Poder && sensor.getNumeroDeMoedas() >= 5 ){ return true;}
		}
		if (direcaoDeMovimento == Mov_Baixo) {
			if(visao[11] == Ve_Moeda){ return true;}
			if(visao[11] == Ve_Celula_vazia){ return true;}
			if(visao[11] == Ve_Pastilha_do_Poder && sensor.getNumeroDeMoedas() >= 5 ){ return true;}
		}
		if (direcaoDeMovimento == Mov_Esquerda) {
			if(visao[12] == Ve_Moeda){ return true;}
			if(visao[12] == Ve_Celula_vazia){ return true;}
			if(visao[12] == Ve_Pastilha_do_Poder && sensor.getNumeroDeMoedas() >= 5 ){ return true;}
		}
		if (direcaoDeMovimento == Mov_Direita) {
			if(visao[16] == Ve_Moeda){ return true;}
			if(visao[16] == Ve_Celula_vazia){ return true;}
			if(visao[16] == Ve_Pastilha_do_Poder && sensor.getNumeroDeMoedas() >= 5 ){ return true;}
		}
		return false;
	}

	private boolean ladrao(int ponto) {
		if (ponto == Ve_Ladrao1 || ponto == Ve_Ladrao2 || ponto == Ve_Ladrao3
				|| ponto == Ve_Ladrao4) {
			return true;
		}
		return false;
	}
	
	private boolean viLadrao(){
		for(int i =0 ; i<24 ;i++){
			if(ladrao(visao[i])){
				return true;
			}
		}
		return false;
	}

	private int fugirLadrao() {
		if (direcaoDeMovimento == Mov_Acima) {
			if (ladrao(visao[0]) || ladrao(visao[1]) || ladrao(visao[2])
					|| ladrao(visao[3]) || ladrao(visao[4]) || ladrao(visao[5])
					|| ladrao(visao[6]) || ladrao(visao[7]) || ladrao(visao[8])
					|| ladrao(visao[9])) {
				return Mov_Baixo;
			}
		}
		if (direcaoDeMovimento == Mov_Baixo) {
			if (ladrao(visao[14]) || ladrao(visao[15]) || ladrao(visao[16])
					|| ladrao(visao[17]) || ladrao(visao[18]) || ladrao(visao[19])
					|| ladrao(visao[20]) || ladrao(visao[21]) || ladrao(visao[22])
					|| ladrao(visao[23])) {
				return Mov_Acima;
			}
		}
		if (direcaoDeMovimento == Mov_Esquerda) {
			if (ladrao(visao[0]) || ladrao(visao[1]) || ladrao(visao[5])
					|| ladrao(visao[6]) || ladrao(visao[10]) || ladrao(visao[11])
					|| ladrao(visao[14]) || ladrao(visao[15]) || ladrao(visao[19])
					|| ladrao(visao[20])) {
				return Mov_Direita;
			}
		}
		if (direcaoDeMovimento == Mov_Direita) {
			if (ladrao(visao[3]) || ladrao(visao[4]) || ladrao(visao[8])
					|| ladrao(visao[9]) || ladrao(visao[12]) || ladrao(visao[13])
					|| ladrao(visao[17]) || ladrao(visao[18]) || ladrao(visao[22])
					|| ladrao(visao[23])) {
				return Mov_Esquerda;
			}
		}
		return 0;
	}

	private boolean evitarLadrao() {
		if (direcaoDeMovimento == Mov_Acima) {
			if (ladrao(visao[6]) || ladrao(visao[8]) || ladrao(visao[2])
					|| ladrao(visao[5]) || ladrao(visao[9]) || ladrao(visao[3])
					|| ladrao(visao[1])) {
				return false;
			}
		}
		if (direcaoDeMovimento == Mov_Baixo) {
			if (ladrao(visao[15]) || ladrao(visao[17]) || ladrao(visao[21])
					|| ladrao(visao[14]) || ladrao(visao[18])
					|| ladrao(visao[22]) || ladrao(visao[20])) {
				return false;
			}
		}
		if (direcaoDeMovimento == Mov_Esquerda) {
			if (ladrao(visao[6]) || ladrao(visao[15]) || ladrao(visao[10])
					|| ladrao(visao[14]) || ladrao(visao[5])
					|| ladrao(visao[1]) || ladrao(visao[20])) {
				return false;
			}
		}
		if (direcaoDeMovimento == Mov_Direita) {
			if (ladrao(visao[3]) || ladrao(visao[8]) || ladrao(visao[9])
					|| ladrao(visao[13]) || ladrao(visao[18])
					|| ladrao(visao[17]) || ladrao(visao[22])) {
				return false;
			}
		}
		return true;
	}

	private int irPonto(Ponto ponto) {
		Point atual = sensor.getPosicao();
		if (ponto.getX() > atual.getX()) {
			return Mov_Direita;
		}
		if (ponto.getX() < atual.getX()) {
			return Mov_Esquerda;
		}
		if (ponto.getY() > atual.getY()) {
			return Mov_Baixo;
		}
		if (ponto.getY() < atual.getY()) {
			return Mov_Acima;
		}
		return 0;
	}

	private void empilharMoeda(Ponto ponto) {
		Ponto temp = null;
		if (moedasAPegar.isEmpty()) {
			moedasAPegar.add(ponto);
			return;
		} else {
			Iterator<Ponto> i = moedasAPegar.listIterator();
			while (i.hasNext()) {
				temp = i.next();
				if (ponto.getX() == temp.getX() && ponto.getY() == temp.getY()) {
					return;
				}
			}
			moedasAPegar.add(ponto);
			return;
		}
	}

	private Ponto moeda(int xDif, int yDif) {
		Ponto atual = new Ponto(sensor.getPosicao().x + xDif,
				sensor.getPosicao().y + yDif);
		return atual;
	}

	private void empilharMoedas() {
		if (visao[0] == Ve_Moeda) {
			empilharMoeda(moeda(-2, -2));
		}
		if (visao[1] == Ve_Moeda) {
			empilharMoeda(moeda(-1, -2));
		}
		if (visao[2] == Ve_Moeda) {
			empilharMoeda(moeda(0, -2));
		}
		if (visao[3] == Ve_Moeda) {
			empilharMoeda(moeda(+1, -2));
		}
		if (visao[4] == Ve_Moeda) {
			empilharMoeda(moeda(+2, -2));
		}
		if (visao[5] == Ve_Moeda) {
			empilharMoeda(moeda(-2, -1));
		}
		if (visao[6] == Ve_Moeda) {
			empilharMoeda(moeda(-1, -1));
		}
		if (visao[7] == Ve_Moeda) {
			empilharMoeda(moeda(0, -1));
		}
		if (visao[8] == Ve_Moeda) {
			empilharMoeda(moeda(1, -1));
		}
		if (visao[9] == Ve_Moeda) {
			empilharMoeda(moeda(2, -1));
		}
		if (visao[10] == Ve_Moeda) {
			empilharMoeda(moeda(-2, 0));
		}
		if (visao[11] == Ve_Moeda) {
			empilharMoeda(moeda(-1, 0));
		}
		if (visao[12] == Ve_Moeda) {
			empilharMoeda(moeda(1, 0));
		}
		if (visao[13] == Ve_Moeda) {
			empilharMoeda(moeda(2, 0));
		}
		if (visao[14] == Ve_Moeda) {
			empilharMoeda(moeda(-2, 1));
		}
		if (visao[15] == Ve_Moeda) {
			empilharMoeda(moeda(-1, 1));
		}
		if (visao[16] == Ve_Moeda) {
			empilharMoeda(moeda(0, 1));
		}
		if (visao[17] == Ve_Moeda) {
			empilharMoeda(moeda(1, 1));
		}
		if (visao[18] == Ve_Moeda) {
			empilharMoeda(moeda(2, 1));
		}
		if (visao[19] == Ve_Moeda) {
			empilharMoeda(moeda(-2, 2));
		}
		if (visao[20] == Ve_Moeda) {
			empilharMoeda(moeda(-2, 2));
		}
		if (visao[21] == Ve_Moeda) {
			empilharMoeda(moeda(0, 2));
		}
		if (visao[22] == Ve_Moeda) {
			empilharMoeda(moeda(1, 2));
		}
		if (visao[23] == Ve_Moeda) {
			empilharMoeda(moeda(2, 2));
		}
	}

	private boolean estouNumlabirinto() {
		if (visao[7] == Ve_Parede && visao[16] == Ve_Parede) {
			return true;
		}
		if (visao[11] == Ve_Parede && visao[12] == Ve_Parede) {
			return true;
		}
		if (visao[6] == Ve_Parede && visao[8] == Ve_Parede
				&& visao[15] == Ve_Parede && visao[17] == Ve_Parede) {
			return true;
		}
		return false;
	}

	private int sairLabirinto() {
		if (direcaoDeMovimento == 0) {
			return decidirDirecaoNoLabirinto();
		} else {
			if (curva() != 0) {
				return curva();
			} else {
				if (possoProsseguirDirecao()) {
					return direcaoDeMovimento;
				} else {
					if (becoSemSaida() != 0) {
						return becoSemSaida();
					} else {
						return decidirDirecaoNoLabirinto();
					}
				}
			}
		}
	}

	private int curva() {
		if (direcaoDeMovimento == Mov_Acima) {
			if (visao[12] != Ve_Parede) {
				return Mov_Direita;
			}
		}
		if (direcaoDeMovimento == Mov_Baixo) {
			if (visao[11] != Ve_Parede) {
				return Mov_Esquerda;
			}
		}
		if (direcaoDeMovimento == Mov_Esquerda) {
			if (visao[7] != Ve_Parede) {
				return Mov_Acima;
			}
		}
		if (direcaoDeMovimento == Mov_Direita) {
			if (visao[16] != Ve_Parede) {
				return Mov_Baixo;
			}
		}
		return 0;
	}

	private boolean possoProsseguirDirecao() {
		if (direcaoDeMovimento == Mov_Acima && visao[7] == Ve_Parede) {
			return false;
		}
		if (direcaoDeMovimento == Mov_Baixo && visao[16] == Ve_Parede) {
			return false;
		}
		if (direcaoDeMovimento == Mov_Esquerda && visao[11] == Ve_Parede) {
			return false;
		}
		if (direcaoDeMovimento == Mov_Direita && visao[12] == Ve_Parede) {
			return false;
		}
		return true;
	}

	private int becoSemSaida() {
		if (visao[7] == Ve_Parede && visao[16] == Ve_Parede
				&& visao[12] == Ve_Parede) {
			return Mov_Esquerda;
		}
		if (visao[7] == Ve_Parede && visao[16] == Ve_Parede
				&& visao[11] == Ve_Parede) {
			return Mov_Direita;
		}
		if (visao[11] == Ve_Parede && visao[16] == Ve_Parede
				&& visao[12] == Ve_Parede) {
			return Mov_Acima;
		}
		if (visao[7] == Ve_Parede && visao[12] == Ve_Parede
				&& visao[11] == Ve_Parede) {
			return Mov_Baixo;
		}
		return 0;
	}

	private int decidirDirecaoNoLabirinto() {
		if (direcaoDeMovimento == 0) {
			if (visao[11] != Ve_Parede) {
				return Mov_Esquerda;
			}
			if (visao[7] != Ve_Parede) {
				return Mov_Acima;
			}
			if (visao[16] != Ve_Parede) {
				return Mov_Baixo;
			}
			if (visao[12] != Ve_Parede) {
				return Mov_Direita;
			}
		} else {
			if (direcaoDeMovimento == Mov_Acima) {
				if (visao[12] == Ve_Parede) {
					return Mov_Esquerda;
				}
				return Mov_Direita;
			}
			if (direcaoDeMovimento == Mov_Baixo) {
				if (visao[11] == Ve_Parede) {
					return Mov_Direita;
				}
				return Mov_Esquerda;
			}
			if (direcaoDeMovimento == Mov_Esquerda) {
				if (visao[16] == Ve_Parede) {
					return Mov_Acima;
				}
				return Mov_Baixo;
			}
			if (direcaoDeMovimento == Mov_Direita) {
				if (visao[7] == Ve_Parede) {
					return Mov_Baixo;
				}
				return Mov_Acima;
			}
		}
		return 0;

	}

	private int tentaPegarMoeda() {
		if (!moedasAPegar.isEmpty()) {
			Ponto pegar = moedasAPegar.get(0);
			return irPonto(pegar);
		}
		return 0;
	}
}