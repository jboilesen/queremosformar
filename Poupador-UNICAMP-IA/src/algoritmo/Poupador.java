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

	private int[] visao = null;
	public static Ambiente ambiente = new Ambiente();
	private ArrayList<Ponto> moedasAPegar = new ArrayList<Ponto>();
	private ArrayList<Integer> movimentosFazer = new ArrayList<Integer>();
	private int direcaoDeMovimento = 0;

	public int acao() {

		// primeiro guardamos a visao
		visao = sensor.getVisaoIdentificacao();

		if (visao[11] == Ve_Pastilha_do_Poder
				&& visao[7] == Ve_Pastilha_do_Poder
				&& visao[16] == Ve_Pastilha_do_Poder) {
			ambiente.contaMovimento();
			return 0;
		}

		empilharMoedas();

		if (viLadrao()) {
			direcaoDeMovimento = fugirLadrao();
		} else {
			if (estouNumlabirinto()) {
				direcaoDeMovimento = sairLabirinto();
			} else {

				if (!(moedasAPegar.isEmpty())) {
					Ponto pegar = moedasAPegar.get(0);

					if (pegar.getX() == sensor.getPosicao().x
							&& pegar.getY() == sensor.getPosicao().y) {
						moedasAPegar.remove(0);
					}
					direcaoDeMovimento = tentaPegarMoeda();
				} else {
					if (!movimentosFazer.isEmpty()) {
						direcaoDeMovimento = movimentosFazer.get(0);
						movimentosFazer.remove(0);
					}
				}
			}
		}

		if (direcaoDeMovimento == 0) {
			procurarMoedas();
			direcaoDeMovimento = irPonto(moedasAPegar.get(0));
		}

		if (!validarMovimentos(direcaoDeMovimento)) {
			printMovimentos(direcaoDeMovimento);
			if (movimentosFazer.isEmpty()) {
				redecidirCaminho();
			}
			direcaoDeMovimento = movimentosFazer.get(0);
			movimentosFazer.remove(0);
		}

		// registra o movimento no ambiente
		ambiente.contaMovimento();
		return direcaoDeMovimento;
	}

	private void procurarMoedas() {
		if (validarMovimento(visao[0])) {
			moedasAPegar.add(moeda(-2, -2));
		}
		if (validarMovimento(visao[4])) {
			moedasAPegar.add(moeda(2, -2));
		}
		if (validarMovimento(visao[19])) {
			moedasAPegar.add(moeda(-2, 2));
		}
		if (validarMovimento(visao[23])) {
			moedasAPegar.add(moeda(2, 2));
		}
	}

	private void printMovimentos(int movimento) {
		if (movimento == Mov_Acima) {
			System.out.println("Mov_Acima");
		}
		if (movimento == Mov_Baixo) {
			System.out.println("Mov_Baixo");
		}
		if (movimento == Mov_Esquerda) {
			System.out.println("Mov_Esquerda");
		}
		if (movimento == Mov_Direita) {
			System.out.println("Mov_Direita");
		}
		if (movimento == Mov_Parado) {
			System.out.println("Mov_Parado");
		}
	}

	private void redecidirCaminho() {
		System.out.println("Redecidir recebeu ");
		printMovimentos(direcaoDeMovimento);
		if (direcaoDeMovimento == Mov_Acima) {
			if (validarMovimentos(Mov_Esquerda, Mov_Acima)) {
				movimentosFazer.add(Mov_Esquerda);
			} else if (validarMovimentos(Mov_Direita, Mov_Acima)) {
				movimentosFazer.add(Mov_Direita);
			} else if (validarMovimentos(Mov_Direita, Mov_Direita)) {
				movimentosFazer.add(Mov_Direita);
				movimentosFazer.add(Mov_Direita);
			} else if (validarMovimentos(Mov_Esquerda, Mov_Esquerda)) {
				movimentosFazer.add(Mov_Esquerda);
				movimentosFazer.add(Mov_Esquerda);
			}
			movimentosFazer.add(Mov_Acima);
			return;

		}
		if (direcaoDeMovimento == Mov_Baixo) {
			if (validarMovimentos(Mov_Esquerda, Mov_Baixo)) {
				movimentosFazer.add(Mov_Esquerda);
			} else if (validarMovimentos(Mov_Direita, Mov_Baixo)) {
				movimentosFazer.add(Mov_Direita);
			} else if (validarMovimentos(Mov_Direita, Mov_Direita)) {
				movimentosFazer.add(Mov_Direita);
				movimentosFazer.add(Mov_Direita);
			} else if (validarMovimentos(Mov_Esquerda, Mov_Esquerda)) {
				movimentosFazer.add(Mov_Esquerda);
				movimentosFazer.add(Mov_Esquerda);
			}
			movimentosFazer.add(Mov_Baixo);
			return;
		}
		if (direcaoDeMovimento == Mov_Esquerda) {
			if (validarMovimentos(Mov_Acima, Mov_Esquerda)) {
				movimentosFazer.add(Mov_Acima);
			} else if (validarMovimentos(Mov_Baixo, Mov_Esquerda)) {
				movimentosFazer.add(Mov_Baixo);
			} else if (validarMovimentos(Mov_Baixo, Mov_Baixo)) {
				movimentosFazer.add(Mov_Baixo);
				movimentosFazer.add(Mov_Baixo);
			} else if (validarMovimentos(Mov_Acima, Mov_Acima)) {
				movimentosFazer.add(Mov_Acima);
				movimentosFazer.add(Mov_Acima);
			}
			movimentosFazer.add(Mov_Esquerda);
			return;
		}
		if (direcaoDeMovimento == Mov_Direita) {
			if (validarMovimentos(Mov_Acima, Mov_Direita)) {
				movimentosFazer.add(Mov_Acima);
			} else if (validarMovimentos(Mov_Baixo, Mov_Direita)) {
				movimentosFazer.add(Mov_Baixo);
			} else if (validarMovimentos(Mov_Baixo, Mov_Baixo)) {
				movimentosFazer.add(Mov_Baixo);
				movimentosFazer.add(Mov_Baixo);
			} else if (validarMovimentos(Mov_Acima, Mov_Acima)) {
				movimentosFazer.add(Mov_Acima);
				movimentosFazer.add(Mov_Acima);
			}
			movimentosFazer.add(Mov_Direita);
			return;
		}
		System.out.println("fiz nada");
	}

	private boolean validarMovimento(int posicao) {
		if (posicao == Ve_Moeda) {
			return true;
		}
		if (posicao == Ve_Celula_vazia) {
			return true;
		}
		if (posicao == Ve_Pastilha_do_Poder && sensor.getNumeroDeMoedas() >= 5) {
			return true;
		}
		return false;
	}

	private boolean validarMovimentos(int movimento) {
		if (movimento == Mov_Acima) {
			return validarMovimento(visao[7]);
		}
		if (movimento == Mov_Baixo) {
			return validarMovimento(visao[16]);
		}
		if (movimento == Mov_Esquerda) {
			return validarMovimento(visao[11]);
		}
		if (movimento == Mov_Direita) {
			return validarMovimento(visao[12]);
		}
		return false;
	}

	private int qualCaminho(int movimento1, int movimento2) {
		if (validarMovimentos(movimento1, movimento2)) {
			return 1;
		}
		if (validarMovimentos(movimento2, movimento1)) {
			return 2;
		}
		return 0;
	}

	private boolean validarMovimentos(int movimento1, int movimento2) {
		if (movimento1 == Mov_Acima) {
			if (movimento2 == Mov_Acima) {
				return (validarMovimento(visao[7]) && validarMovimento(visao[2]));
			}
			if (movimento2 == Mov_Esquerda) {
				return (validarMovimento(visao[7]) && validarMovimento(visao[6]));
			}
			if (movimento2 == Mov_Direita) {
				return (validarMovimento(visao[7]) && validarMovimento(visao[8]));
			}

		}
		if (movimento1 == Mov_Baixo) {
			if (movimento2 == Mov_Baixo) {
				return (validarMovimento(visao[16]) && validarMovimento(visao[21]));
			}
			if (movimento2 == Mov_Esquerda) {
				return (validarMovimento(visao[16]) && validarMovimento(visao[15]));
			}
			if (movimento2 == Mov_Direita) {
				return (validarMovimento(visao[16]) && validarMovimento(visao[17]));
			}

		}
		if (movimento1 == Mov_Esquerda) {
			if (movimento2 == Mov_Esquerda) {
				return (validarMovimento(visao[11]) && validarMovimento(visao[10]));
			}
			if (movimento2 == Mov_Acima) {
				return (validarMovimento(visao[11]) && validarMovimento(visao[6]));
			}
			if (movimento2 == Mov_Baixo) {
				return (validarMovimento(visao[11]) && validarMovimento(visao[15]));
			}

		}
		if (movimento1 == Mov_Direita) {
			if (movimento2 == Mov_Direita) {
				return (validarMovimento(visao[12]) && validarMovimento(visao[13]));
			}
			if (movimento2 == Mov_Acima) {
				return (validarMovimento(visao[12]) && validarMovimento(visao[8]));
			}
			if (movimento2 == Mov_Baixo) {
				return (validarMovimento(visao[12]) && validarMovimento(visao[17]));
			}
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

	private boolean viLadrao() {
		for (int i = 0; i < 24; i++) {
			if (ladrao(visao[i])) {
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
					|| ladrao(visao[17]) || ladrao(visao[18])
					|| ladrao(visao[19]) || ladrao(visao[20])
					|| ladrao(visao[21]) || ladrao(visao[22])
					|| ladrao(visao[23])) {
				return Mov_Acima;
			}
		}
		if (direcaoDeMovimento == Mov_Esquerda) {
			if (ladrao(visao[0]) || ladrao(visao[1]) || ladrao(visao[5])
					|| ladrao(visao[6]) || ladrao(visao[10])
					|| ladrao(visao[11]) || ladrao(visao[14])
					|| ladrao(visao[15]) || ladrao(visao[19])
					|| ladrao(visao[20])) {
				return Mov_Direita;
			}
		}
		if (direcaoDeMovimento == Mov_Direita) {
			if (ladrao(visao[3]) || ladrao(visao[4]) || ladrao(visao[8])
					|| ladrao(visao[9]) || ladrao(visao[12])
					|| ladrao(visao[13]) || ladrao(visao[17])
					|| ladrao(visao[18]) || ladrao(visao[22])
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