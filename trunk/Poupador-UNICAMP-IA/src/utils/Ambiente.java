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
	private int poupadorDespistador = Constantes.semId;
	private int poupadorDepositante = Constantes.semId;
	
	
	/*
	 *  I N T E L I G E N C I A
	 */
	
	/*define os papeis de cada poupador*/
	public void definirPapeis(){
		int heuristica;
		int despistador,despistadorHeuristica;
		int depositante,depositanteHeuristica;
		int segundoDepositante;
		
		despistador = Constantes.semId;
		despistadorHeuristica = Constantes.Distancia_Desconhecida;
		
		depositante = Constantes.semId;
		depositanteHeuristica = Constantes.Distancia_Desconhecida;
		
		segundoDepositante = Constantes.semId;
		
		/*para cada poupador, ve sua distancia dos ladroes*/
		for (Point poupador:this.Poupadores){
			if (!this.Ladroes.isEmpty()){
				/*definindo o despistador*/
				heuristica = heuristicaDistanciaLadroes(this.Poupadores.indexOf(poupador));
				
				/*ordenando para saber quem tem a maior heuristica*/
				if (despistador==Constantes.semId){
					despistador=this.Poupadores.indexOf(poupador);
					despistadorHeuristica=heuristica;
				}else{
					if (despistadorHeuristica<heuristica){
						despistador = this.Poupadores.indexOf(poupador);
						despistadorHeuristica=heuristica;
					}
				}
			}
			/*define o depositante*/
			if (this.bancoEncontrado()){
				heuristica = heuristicaDistanciaBanco(this.Poupadores.indexOf(poupador));
				/*se nao temos depositante definido, ou se a heuristica deste eh maior que o do anterior*/
				if (depositante == Constantes.semId || depositanteHeuristica<heuristica){					
					/*guardamos o antigo depositante como segundo melhor depositante*/
					segundoDepositante = depositante;
					/*guardamos este depositante*/
					depositante = this.Poupadores.indexOf(poupador);
					depositanteHeuristica = heuristica;
				}
			}
		}
		
		/*nosso despistador nao pode ser depositante*/
		if (despistador==depositante){
			depositante = segundoDepositante;
		}
		
		/*definimos os papeis*/
		this.poupadorDepositante = depositante;
		this.poupadorDespistador = despistador;
	}
	
	/* retorna o id de quem eh despistador */
	public int getDespistador(){
		return this.poupadorDespistador;
	}
	
	/* retorna o id do depositante */
	public int getDepositante(){
		return this.poupadorDepositante;
	}
	
	/*se ja encontramos o banco*/
	public boolean bancoEncontrado(){
		return this.bancoEncontrado;
	}
	
	/*calculo de Heuristica a partir da distancia do banco*/
	public int heuristicaDistanciaBanco(int id){
		int heuristica,distancia;
		Point poupador = this.Poupadores.get(id);
		heuristica = 0;
		distancia = calculaDistancia(this.getBanco(),poupador);
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
	public int heuristicaDistanciaLadroes(int id){
		int heuristica,distancia;
		Point poupador = this.Poupadores.get(id);
		heuristica = 0;
		for (Point ladrao:this.Ladroes){
			distancia = calculaDistancia(ladrao,poupador);
			if (distancia<=Constantes.muitoProximo){
				heuristica+=3;
			}else if (distancia<=Constantes.proximo){
				heuristica+=2;
			}else if (distancia<=Constantes.longe){
				heuristica+=1;
			}else{
				heuristica+=0;
			}
		}
		return heuristica;
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
		for (Point moeda:this.Moedas){
			if (moeda.x==posicao.x && moeda.y==posicao.y){
				this.Moedas.remove(moeda);
				this.moedasAdquiridas++;
			}
		}		
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
