package utils;

import java.awt.Point;
import java.util.ArrayList;

public class Ambiente {

	private static int numeroPoupadores = 0;
	private int contaMovimentosRodada = 0;
	private int contaTicsFaltantes = 1000;
	private ArrayList<Point> Moedas = new ArrayList<Point>();
	private ArrayList<Point> Poupadores = new ArrayList<Point>();
	private ArrayList<Point> Ladroes = new ArrayList<Point>();
	private ArrayList<Point> CasasVisitadas = new ArrayList<Point>();
	private ArrayList<Point> ultimasCasas = new ArrayList<Point>();
	
	private int moedasAdquiridas = 0;
	private Point Banco = new Point();
	private boolean bancoEncontrado = false;
	
	
	/*
	 *  I N T E L I G E N C I A
	 */
	
	/*se ja encontramos o banco*/
	public boolean bancoEncontrado(){
		return this.bancoEncontrado;
	}
	

	
	/*calculo de Heuristica a partir da distancia do banco*/
	public int heuristicaDistanciaBanco(Point ponto){
		int heuristica,distancia;
		heuristica = 0;
		distancia = calculaDistancia(this.getBanco(),ponto);
		if (distancia<=Constantes.muitoProximo){
			heuristica+=3;
		}else if (distancia<=Constantes.proximo){
			heuristica+=2;
		}else if (distancia<=Constantes.longe){
			heuristica+=1;
		}else{
			heuristica+=0;
		}
		return heuristica;
	}
	/*calculo de Heuristica a partir da distancia dos ladroes*/
	public int heuristicaDistanciaLadroes(Point ponto){
		int heuristica,distancia;
		Point poupador = new Point(ponto.x, ponto.y);
		heuristica = 0;
		for (Point ladrao:this.Ladroes){
			distancia = calculaDistancia(ladrao,poupador);
			if (distancia<=Constantes.muitoProximo){
				heuristica+=5;
			}else if (distancia<=Constantes.proximo){
				heuristica+=3;
			}else if (distancia<=Constantes.longe){
				heuristica+=1;
			}else{
				heuristica+=0;
			}
		}
		return heuristica;
	}
	public Point getMoedaMaisProxima(int id){
		Point poupador = this.Poupadores.get(id);
		Point moedaMaisProxima = new Point(-1,-1);
		int distancia = Constantes.Distancia_Desconhecida;
		
		for (Point moeda:this.Moedas){
			if (distancia==Constantes.Distancia_Desconhecida || calculaDistancia(poupador,moeda)<distancia){
				moedaMaisProxima.x = moeda.x;
				moedaMaisProxima.y = moeda.y;
				distancia = calculaDistancia(poupador,moeda);
			}
		}
		return moedaMaisProxima;
	}
	public Point getMoedaMaisDistante(int id){
		Point poupador = this.Poupadores.get(id);
		Point moedaMaisDistante = Constantes.pontoInvalido;
		int distancia = Constantes.Distancia_Desconhecida;
		
		for (Point moeda:this.Moedas){
			if (distancia==Constantes.Distancia_Desconhecida || calculaDistancia(poupador,moeda)>distancia){
				moedaMaisDistante = new Point();
				moedaMaisDistante.x = moeda.x;
				moedaMaisDistante.y = moeda.y;
				distancia = calculaDistancia(poupador,moeda);
			}
		}
		return moedaMaisDistante;
	}
	/*
	 *  F I M   I N T E L I G E N C I A
	 */
	
	/*
	 * 
	 *  M A P E A M E N T O
	 * 
	 * 
	 */
	public void atualizarMapa(Point vazio){
		int idLadrao = Constantes.semId;
		int idMoeda = Constantes.semId;
		
		for (Point ladrao:this.Ladroes){
			if (vazio.x==ladrao.x && vazio.y==ladrao.y){
				idLadrao = this.Ladroes.indexOf(ladrao);
				break;
			}
		}
		if (idLadrao!=Constantes.semId)
			this.Ladroes.remove(idLadrao);
		for (Point moeda:this.Moedas){
			if (vazio.x==moeda.x && vazio.y==moeda.y){
				idLadrao = this.Moedas.indexOf(moeda);
				break;
			}
		}
		if (idMoeda!=Constantes.semId)
			this.Moedas.remove(idMoeda);		
	}
	/*seta posicao do banco*/
	public void setBanco(Point banco){
		this.Banco.x = banco.x;
		this.Banco.y = banco.y;
		this.bancoEncontrado = true;
	}
	/*retorna a posicao do banco*/
	public Point getBanco(){
		return this.Banco;
	}
	/*adiciona uma moeda*/
	public void addMoeda(Point posicao){
		if (!verificaMoeda(posicao)){
			this.Moedas.add(new Point(posicao.x,posicao.y));
		}
	}
	/*verifica se uma moeda ja esta na lista*/
	public boolean verificaMoeda(Point posicao){
		for (Point moeda:this.Moedas){
			if (moeda.x==posicao.x && moeda.y==posicao.y){
				return true;
			}
		}
		return false;
	}
	/*remove uma moeda da lista de moedas e ja contabiliza*/
	public void pegaMoeda(Point posicao){
		int id = Constantes.semId;
		for (Point moeda:this.Moedas){
			if (moeda.x==posicao.x && moeda.y==posicao.y){
				id = this.Moedas.indexOf(moeda);
				this.moedasAdquiridas++;
			}
		}
		if (id!=Constantes.semId)
			this.Moedas.remove(id);
	}
	
	/*atualiza ou adiciona um ladrao*/
	public void atualizaLadrao(int id,Point posicao){
		if (!verificaLadrao(posicao))
			this.Ladroes.add(new Point (posicao.x,posicao.y));	
	}
	/*verifica se um ladrao ja esta na lista*/
	public boolean verificaLadrao(Point posicao){
		for (Point ladrao:this.Ladroes){
			if (ladrao.x==posicao.x && ladrao.y==posicao.y){
				return true;
			}
		}
		return false;
	}
	/*adiciona o registro de uma casa ja visitada*/
	public void addCasaVisitada(Point posicao){
		if (!CasaVisitada(posicao))
			this.CasasVisitadas.add(new Point(posicao.x,posicao.y));
	}
	/*verifica se uma casa ja foi visitada*/
	public boolean CasaVisitada(Point posicao){
		for (Point casa:this.CasasVisitadas){
			if (posicao.x == casa.x && posicao.y==casa.y){
				return true;
			}
		}
		return false;
	}

	/*atualiza a ultima casa de um poupador pelo id do poupador*/
	public void atualizaUltimaCasa(int id,Point posicao){
		this.ultimasCasas.get(id).x = posicao.x;
		this.ultimasCasas.get(id).y = posicao.y;
	}
	/*retorna a ultima casa de um poupador pelo id do poupador*/
	public Point ultimaCasa(int id){
		return this.ultimasCasas.get(id);
	}
	
	
	/*
	 *
	 * Poupadores
	 * 
	 */
	
	/*metodo para dar um id ao poupador e ja conta-lo*/
	public int getPoupadorId(Point posicao){
		this.Poupadores.add(new Point (posicao.x,posicao.y));
		numeroPoupadores++;
		for (Point poupador:this.Poupadores){
			if (poupador.x == posicao.x && poupador.y==posicao.y){
				this.ultimasCasas.add(this.Poupadores.indexOf(poupador),new Point(posicao.x,posicao.y));
				return this.Poupadores.indexOf(poupador);
			}
		}
		return Constantes.semId;
	}
	/*retorna a posicao do poupador pelo id*/
	public Point getPosicaoPoupador(int id){
		return this.Poupadores.get(id);
	}	
	/*guarda a posicao de um poupador*/
	public void atualizaPoupador(int id,Point posicao){
		this.Poupadores.get(id).x = posicao.x;
		this.Poupadores.get(id).y = posicao.y;
	}
	
	
	
	/*
	 * U T I L I D A D E S
	 * 
	 */
	/*metodo para calculo de distancia entre dois pontos*/
	public int calculaDistancia(Point pontoInicial, Point pontoFinal){
		int distancia;
		distancia = Math.abs(pontoFinal.x - pontoInicial.x);
		distancia+= Math.abs(pontoFinal.y - pontoInicial.y);
		return distancia;
	}
	/*metodo para mantermos um controle de quantas rodadas ainda faltam*/
	public void contaMovimento(){
		this.contaMovimentosRodada++;
		if (this.contaMovimentosRodada==numeroPoupadores){
			contaTicsFaltantes--;
			if (contaTicsFaltantes<0){
				contaTicsFaltantes = 1000;
			}
			this.contaMovimentosRodada = 0;
		}
	}
	/*retorno numero de tics faltantes*/
	public int getTicsFaltantes(){
		return this.contaTicsFaltantes;
	}
	
}
