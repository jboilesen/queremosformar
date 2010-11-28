package algoritmo;

import java.awt.Point;
import java.util.ArrayList;

import utils.Ambiente;
import utils.Constantes;


public class Poupador extends ProgramaPoupador {
	private int id = Constantes.semId;
	private int[][] visualizacao = new int[5][5];
	private Point posicao = new Point();
	private int papel = Constantes.cacador;
	private int empreitadaAtual = Constantes.catalogarMoedas;
	private Point moedaMarcada = new Point();
	private Point ultimaCasa;
	private ArrayList<Point> caminho = new ArrayList<Point>();
	private int moedasCarregadas = 0;
	
	/*comunicacao simples sobre quem esta fazendo o que*/
	public static Point depositante = Constantes.pontoInvalido;
	public static int statusDepositante = Constantes.semId;
	
	public static Point despistador = Constantes.pontoInvalido;
	public static int statusDespistador = Constantes.semId;
	
	public static Ambiente ambiente = new Ambiente();
	
	public int acao() {
		int movimento;
		/*pegamos a posicao deste poupador*/
		this.posicao.x = sensor.getPosicao().x;
		this.posicao.y = sensor.getPosicao().y;
		
		/*guarda esta casa como visitada*/
		ambiente.addCasaVisitada(posicao);
		
		verificarDeadlock();
		
		this.caminho.add(new Point(this.posicao.x,this.posicao.y));

		/*se este poupador ainda nao esta contabilizado no ambiente, contabiliza ele*/
		if (this.id==Constantes.semId){
			this.id = ambiente.getPoupadorId(posicao);
		}
		
		this.ultimaCasa = new Point(ambiente.ultimaCasa(this.id));
		
		/*vamos interpretar o que vemos, e registrar no ambiente*/
		interpretaVisao();
		
		/*definindo os papéis de cada poupador*/
		definirPapeis();
		/*definimos qual eh o papel deste poupador*/
			
		movimento = Constantes.Mov_Parado;
		switch (this.papel){
			case Constantes.cacador:
				switch (this.empreitadaAtual){
					case Constantes.catalogarMoedas:
						//procura visitar casas nao visitadas OBS: Setar numero de rodadas maximo nesta empreitada
						movimento = catalogarMoedas();
					break;
					case Constantes.marcarMoeda:
						//procura guardar uma moeda ateh que se aproxime um ladrao
						movimento = marcaMoeda();
					break;
					case Constantes.fugirLadrao:
						//tenta chegar o mais proximo possivel da moeda mais distante possivel
						movimento = BuscaMoedaMaisDistante();
					break;
				}
			break;
			/*atuacao do depositante esta OK!*/
			case Constantes.depositante:
				switch (this.empreitadaAtual){
					case Constantes.buscarBanco:
						//se aproxima do banco
						movimento = buscarBanco();
					break;
					case Constantes.buscarMoeda:
						movimento = pegarMoedaBanco();
					break;
					case Constantes.depositarMoeda:
						//tenta depositar a moeda
						movimento = depositarMoeda();
					break;
				}
			break;
			case Constantes.despistador:
				switch (this.empreitadaAtual){
					case Constantes.buscarLadroes:
						//procura estar proximo aos ladroes
						movimento = catalogarMoedas();
					break;
					case Constantes.atrairLadroes:
						//procura levar os ladroes para algum lugar...se eles ficarem mtas rodadas proximos ao banco, mudar estrategia
						movimento = catalogarMoedas();
					break;
					case Constantes.isolarLadroes:
						movimento = catalogarMoedas();
					break;
				}
			break;
		}
		
		/*corrigindo posição para gravar no ambiente*/
		ambiente.atualizaUltimaCasa(this.id,posicao);
		switch(movimento){
			case Constantes.Mov_Acima:
				this.posicao.y = this.posicao.y-1;
			break;
			case Constantes.Mov_Baixo:
				this.posicao.y = this.posicao.y+1;
			break;
			case Constantes.Mov_Direita:
				this.posicao.x = this.posicao.x+1;
			break;
			case Constantes.Mov_Esquerda:
				this.posicao.x = this.posicao.x-1;
			break;
		}
		
		if (ambiente.verificaMoeda(this.posicao)){
			this.moedasCarregadas++;
		}
		ambiente.contaMovimento();
		ambiente.atualizaPoupador(this.id,posicao);
		return movimento;
	}
	
	/*
	 * 
	 *  A T U A C O E S
	 * 
	 */
	public void definirPapeis(){
		switch (this.papel){
			case Constantes.cacador:
				switch (this.empreitadaAtual){
					case Constantes.catalogarMoedas:
						if (ambiente.getTicsFaltantes()<700){
							//se o banco foi encontrado
							if (ambiente.bancoEncontrado()){
								//se ja existe um depositante
								if ((depositante.x == Constantes.pontoInvalido.x && depositante.y == Constantes.pontoInvalido.y)||(ambiente.heuristicaDistanciaLadroes(depositante)>Constantes.proximo)){
									depositante = new Point(this.posicao.x,this.posicao.y);
									this.papel = Constantes.depositante;
									this.empreitadaAtual = Constantes.buscarBanco;
								}else{
									//verifica se o depositante esta suave na nave
									//se nao estiver, substitui ele!
									if (ambiente.heuristicaDistanciaLadroes(depositante)>Constantes.muitoProximo){
										depositante = new Point(this.posicao.x,this.posicao.y);
										this.papel = Constantes.depositante;
										this.empreitadaAtual = Constantes.buscarBanco;
									}else{
										if (ambiente.heuristicaDistanciaLadroes(this.posicao)>Constantes.proximo){
											//se nao ha despistador ou se ele esta mais suave que eu
											if (ambiente.heuristicaDistanciaLadroes(despistador)<=Constantes.proximo || (despistador.x==Constantes.pontoInvalido.x && despistador.y==Constantes.pontoInvalido.y)){
												despistador = new Point(this.posicao.x,this.posicao.y);
												this.papel = Constantes.despistador;
												this.empreitadaAtual = Constantes.atrairLadroes;
											}else{
												this.empreitadaAtual = Constantes.marcarMoeda;
											}
										}else{
											this.empreitadaAtual = Constantes.marcarMoeda;
										}
									}
								}
							}else{
								if (ambiente.heuristicaDistanciaLadroes(this.posicao)>Constantes.muitoProximo){
									this.empreitadaAtual = Constantes.fugirLadrao;
								}else{
									this.empreitadaAtual = Constantes.marcarMoeda;
								}
							}
						}
					break;
					case Constantes.marcarMoeda:
						if (ambiente.heuristicaDistanciaLadroes(this.posicao)>=Constantes.muitoProximo)
							this.empreitadaAtual = Constantes.fugirLadrao;
					break;
					case Constantes.fugirLadrao:
						if (ambiente.heuristicaDistanciaLadroes(this.posicao)>Constantes.proximo)
							this.empreitadaAtual = Constantes.catalogarMoedas;
					break;
				}
			break;
			case Constantes.depositante:
				if (ambiente.getTicsFaltantes()<700){
					switch (this.empreitadaAtual){
						case Constantes.buscarBanco:
							if (ambiente.heuristicaDistanciaBanco(this.posicao)>Constantes.muitoProximo){
								this.empreitadaAtual = Constantes.buscarMoeda;
							}else{
								if (ambiente.heuristicaDistanciaLadroes(this.posicao)>Constantes.muitoProximo){
									depositante = Constantes.pontoInvalido;
									this.papel = Constantes.cacador;
									this.empreitadaAtual = Constantes.catalogarMoedas;
								}else{
									this.empreitadaAtual = Constantes.buscarMoeda;
								}
							}
						break;
						case Constantes.buscarMoeda:
						break;
						case Constantes.depositarMoeda:
						break;
					}
				}
			break;
			case Constantes.despistador:
			break;
		}
	}
	
	/*
	 * Controles de caminho
	 * 
	 */
	//controle de deadlocks
	public void verificarDeadlock(){
		int k = 0;
		for (Point passoControle:this.caminho){
			k = 0;
			for (Point passo:this.caminho){
				if (passoControle.x == passo.x && passoControle.y == passo.y){
					k++;
				}
			}
			if (k>=3){
				this.caminho.clear();
				break;
			}		
		}
	}
	public boolean verificaCaminho(Point posicao){
		for (Point passo:this.caminho){
			if (passo.x == posicao.x && passo.y == posicao.y){
				return true;
			}
		}
		return false;
	}
	/*
	 * D E P O S I T A N T E (OK!)
	 */
	public int depositarMoeda(){
		int movimento = Constantes.Mov_Parado;
		int movimentoValido = Constantes.Ve_Celula_vazia;
		
		//marca o banco como se fosse uma moeda
		Point moedaMaisProxima = ambiente.getBanco();
		this.moedaMarcada = new Point(moedaMaisProxima.x,moedaMaisProxima.y);
		
		//se a distancia for soh um pode depositar
		if (ambiente.calculaDistancia(this.posicao, this.moedaMarcada)==1){
			movimentoValido = Constantes.Ve_Banco;
		}
		
		//anda ateh o banco buscando fazer na menor distancia possivel
		if (ambiente.heuristicaDistanciaLadroes(this.posicao)<=Constantes.muitoProximo){
			Point acima,abaixo,esquerda,direita;
			int menorDist = Constantes.Distancia_Desconhecida;
			int distAcima = Constantes.Distancia_Desconhecida;
			int distAbaixo = Constantes.Distancia_Desconhecida;
			int distEsquerda = Constantes.Distancia_Desconhecida;
			int distDireita = Constantes.Distancia_Desconhecida;
			
			acima = new Point();
			acima.x = this.posicao.x;
			acima.y = this.posicao.y-1;
			distAcima = ambiente.calculaDistancia(acima, this.moedaMarcada);
			abaixo = new Point();
			abaixo.x = this.posicao.x;
			abaixo.y = this.posicao.y+1;
			distAbaixo = ambiente.calculaDistancia(abaixo, this.moedaMarcada);
			esquerda = new Point();
			esquerda.x = this.posicao.x-1;
			esquerda.y = this.posicao.y;
			distEsquerda = ambiente.calculaDistancia(esquerda, this.moedaMarcada);
			direita = new Point();
			direita.x = this.posicao.x+1;
			direita.y = this.posicao.y;
			distDireita = ambiente.calculaDistancia(direita, this.moedaMarcada);
			if (visualizacao[1][2]==movimentoValido && ambiente.heuristicaDistanciaLadroes(acima)<=Constantes.muitoProximo){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}
			if (visualizacao[3][2]==movimentoValido && ambiente.heuristicaDistanciaLadroes(abaixo)<=Constantes.muitoProximo)
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			if (visualizacao[2][1]==movimentoValido && ambiente.heuristicaDistanciaLadroes(esquerda)<=Constantes.muitoProximo)
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			if (visualizacao[2][3]==movimentoValido && ambiente.heuristicaDistanciaLadroes(direita)<=Constantes.muitoProximo)
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}
			
			if (visualizacao[1][2]==movimentoValido){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}
			if (visualizacao[3][2]==movimentoValido)
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			if (visualizacao[2][1]==movimentoValido)
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			if (visualizacao[2][3]==movimentoValido)
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}
		}
		
		return movimento;	
	}
	
	public int pegarMoedaBanco(){
		int movimento = Constantes.Mov_Parado;
		int movimentoValido = Constantes.Ve_Celula_vazia;
		
		//descobre qual moeda marcar
		Point moedaMaisProxima = ambiente.getMoedaMaisProxima(this.id);
		//acha a moeda mais proxima
		this.moedaMarcada = new Point(moedaMaisProxima.x,moedaMaisProxima.y);
		
		if (ambiente.calculaDistancia(this.posicao, this.moedaMarcada)==1){
			movimentoValido = Constantes.Ve_Moeda;
		}
		//anda ateh a moeda mais proxima
		if (ambiente.heuristicaDistanciaLadroes(this.posicao)>=Constantes.muitoProximo && ambiente.heuristicaDistanciaBanco(this.posicao)>=Constantes.muitoProximo){
			Point acima,abaixo,esquerda,direita;
			int menorDist = Constantes.Distancia_Desconhecida;
			int distAcima = Constantes.Distancia_Desconhecida;
			int distAbaixo = Constantes.Distancia_Desconhecida;
			int distEsquerda = Constantes.Distancia_Desconhecida;
			int distDireita = Constantes.Distancia_Desconhecida;
			
			acima = new Point();
			acima.x = this.posicao.x;
			acima.y = this.posicao.y-1;
			distAcima = ambiente.calculaDistancia(acima, this.moedaMarcada);
			abaixo = new Point();
			abaixo.x = this.posicao.x;
			abaixo.y = this.posicao.y+1;
			distAbaixo = ambiente.calculaDistancia(abaixo, this.moedaMarcada);
			esquerda = new Point();
			esquerda.x = this.posicao.x-1;
			esquerda.y = this.posicao.y;
			distEsquerda = ambiente.calculaDistancia(esquerda, this.moedaMarcada);
			direita = new Point();
			direita.x = this.posicao.x+1;
			direita.y = this.posicao.y;
			distDireita = ambiente.calculaDistancia(direita, this.moedaMarcada);
			
			if (visualizacao[1][2]==movimentoValido && ambiente.heuristicaDistanciaLadroes(acima)<=Constantes.proximo){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}
			if (visualizacao[3][2]==movimentoValido && ambiente.heuristicaDistanciaLadroes(abaixo)<=Constantes.proximo)
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			if (visualizacao[2][1]==movimentoValido && ambiente.heuristicaDistanciaLadroes(esquerda)<=Constantes.proximo)
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			if (visualizacao[2][3]==movimentoValido && ambiente.heuristicaDistanciaLadroes(direita)<=Constantes.proximo)
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}			
			if (visualizacao[1][2]==movimentoValido){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}
			if (visualizacao[3][2]==movimentoValido)
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			if (visualizacao[2][1]==movimentoValido)
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			if (visualizacao[2][3]==movimentoValido)
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}
		}
		
		return movimento;
	}
	public int buscarBanco(){
		int movimento = Constantes.Mov_Parado;
		int movimentoValido = Constantes.Ve_Celula_vazia;
		Point banco = ambiente.getBanco();
		
		//anda ateh a moeda mais proxima
		if (ambiente.calculaDistancia(this.posicao, banco)>1){
			Point acima,abaixo,esquerda,direita;
			int menorDist = Constantes.Distancia_Desconhecida;
			int distAcima = Constantes.Distancia_Desconhecida;
			int distAbaixo = Constantes.Distancia_Desconhecida;
			int distEsquerda = Constantes.Distancia_Desconhecida;
			int distDireita = Constantes.Distancia_Desconhecida;
			
			acima = new Point();
			acima.x = this.posicao.x;
			acima.y = this.posicao.y-1;
			distAcima = ambiente.calculaDistancia(acima, banco);		
			abaixo = new Point();
			abaixo.x = this.posicao.x;
			abaixo.y = this.posicao.y+1;
			distAbaixo = ambiente.calculaDistancia(abaixo, banco);
			esquerda = new Point();
			esquerda.x = this.posicao.x-1;
			esquerda.y = this.posicao.y;
			distEsquerda = ambiente.calculaDistancia(esquerda, banco);
			direita = new Point();
			direita.x = this.posicao.x+1;
			direita.y = this.posicao.y;
			distDireita = ambiente.calculaDistancia(direita, banco);
			
			if (visualizacao[1][2]==movimentoValido && ambiente.heuristicaDistanciaLadroes(acima)<=Constantes.proximo){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}
			if (visualizacao[3][2]==movimentoValido && ambiente.heuristicaDistanciaLadroes(abaixo)<=Constantes.proximo)
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			if (visualizacao[2][1]==movimentoValido && ambiente.heuristicaDistanciaLadroes(esquerda)<=Constantes.proximo)
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			if (visualizacao[2][3]==movimentoValido && ambiente.heuristicaDistanciaLadroes(direita)<=Constantes.proximo)
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}				
			if (visualizacao[1][2]==movimentoValido && (this.ultimaCasa.x!=acima.x || this.ultimaCasa.y!=acima.y)){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}

			if (visualizacao[3][2]==movimentoValido && (this.ultimaCasa.x!=abaixo.x || this.ultimaCasa.y!=abaixo.y))
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			if (visualizacao[2][1]==movimentoValido && (this.ultimaCasa.x!=esquerda.x || this.ultimaCasa.y!=esquerda.y))
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			if (visualizacao[2][3]==movimentoValido && (this.ultimaCasa.x!=direita.x || this.ultimaCasa.y!=direita.y))
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}	
		}
		return movimento;
	}
	/*
	 * F I M  D E P O S I T A N T E
	 * 
	 */
	
	/*
	 * 
	 * C A C A D O R
	 * 
	 */
	public int marcaMoeda(){
		int movimento = Constantes.Mov_Parado;
		int movimentoValido = Constantes.Ve_Celula_vazia;
		
		//descobre qual moeda marcar
		Point moedaMaisProxima = ambiente.getMoedaMaisProxima(this.id);
		//acha a moeda mais proxima
		this.moedaMarcada = new Point(moedaMaisProxima.x,moedaMaisProxima.y);
		if (ambiente.getTicsFaltantes()<5 && ambiente.heuristicaDistanciaLadroes(this.posicao)<Constantes.muitoProximo ){
			if (ambiente.calculaDistancia(this.posicao,moedaMaisProxima)==1)
				movimentoValido = Constantes.Ve_Moeda;
		}		
		//anda ateh a moeda mais proxima
		if (ambiente.calculaDistancia(this.posicao, this.moedaMarcada)>1 || (ambiente.getTicsFaltantes()<5 && ambiente.heuristicaDistanciaLadroes(posicao)<=Constantes.proximo )){
			Point acima,abaixo,esquerda,direita;
			int menorDist = Constantes.Distancia_Desconhecida;
			int distAcima = Constantes.Distancia_Desconhecida;
			int distAbaixo = Constantes.Distancia_Desconhecida;
			int distEsquerda = Constantes.Distancia_Desconhecida;
			int distDireita = Constantes.Distancia_Desconhecida;
			
			acima = new Point();
			acima.x = this.posicao.x;
			acima.y = this.posicao.y-1;
			distAcima = ambiente.calculaDistancia(acima, this.moedaMarcada);
			if (visualizacao[1][2]==movimentoValido){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}
			
			abaixo = new Point();
			abaixo.x = this.posicao.x;
			abaixo.y = this.posicao.y+1;
			distAbaixo = ambiente.calculaDistancia(abaixo, this.moedaMarcada);
			if (visualizacao[3][2]==movimentoValido)
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			
			esquerda = new Point();
			esquerda.x = this.posicao.x-1;
			esquerda.y = this.posicao.y;
			distEsquerda = ambiente.calculaDistancia(esquerda, this.moedaMarcada);
			if (visualizacao[2][1]==movimentoValido)
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			
			direita = new Point();
			direita.x = this.posicao.x+1;
			direita.y = this.posicao.y;
			distDireita = ambiente.calculaDistancia(direita, this.moedaMarcada);
			if (visualizacao[2][3]==movimentoValido)
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}
		}
		return movimento;
	}
	public int catalogarMoedas(){
		Point acima,abaixo,esquerda,direita;
		
		acima = new Point();
		acima.x = this.posicao.x;
		acima.y = this.posicao.y-1;
		
		abaixo = new Point();
		abaixo.x = this.posicao.x;
		abaixo.y = this.posicao.y+1;
		
		esquerda = new Point();
		esquerda.x = this.posicao.x-1;
		esquerda.y = this.posicao.y;
		
		direita = new Point();
		direita.x = this.posicao.x+1;
		direita.y = this.posicao.y;

		if (!verificaCaminho(acima))
			if (visualizacao[1][2]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Acima;
		if (!verificaCaminho(abaixo))
			if (visualizacao[3][2]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Baixo;
		if (!verificaCaminho(esquerda))
			if (visualizacao[2][1]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Esquerda;
		if (!verificaCaminho(direita))
			if (visualizacao[2][3]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Direita;
		
		
		if (!ambiente.CasaVisitada(acima))
			if (visualizacao[1][2]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Acima;
		if (!ambiente.CasaVisitada(abaixo))
			if (visualizacao[3][2]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Baixo;
		if (!ambiente.CasaVisitada(esquerda))
			if (visualizacao[2][1]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Esquerda;
		if (!ambiente.CasaVisitada(direita))
			if (visualizacao[2][3]==Constantes.Ve_Celula_vazia)
				return Constantes.Mov_Direita;
		/*todas as casas foram visitadas ou sao movimentos invalidos
		entao retorna algum movimento sem ser a ultima casa em que ele estava*/
		if (visualizacao[1][2]==Constantes.Ve_Celula_vazia)
			if (acima.x!=this.ultimaCasa.x || acima.y!=this.ultimaCasa.y)
				return Constantes.Mov_Acima;
		if (visualizacao[3][2]==Constantes.Ve_Celula_vazia)
			if (abaixo.x!=this.ultimaCasa.x || abaixo.y!=this.ultimaCasa.y)
				return Constantes.Mov_Baixo;
		if (visualizacao[2][1]==Constantes.Ve_Celula_vazia)
			if (esquerda.x!=this.ultimaCasa.x || esquerda.y!=this.ultimaCasa.y)
				return Constantes.Mov_Esquerda;
		if (visualizacao[2][3]==Constantes.Ve_Celula_vazia)
			if (direita.x!=this.ultimaCasa.x || direita.y!=this.ultimaCasa.y)
				return Constantes.Mov_Direita;
		
		return Constantes.Mov_Parado;
		
	}
	public int BuscaMoedaMaisDistante(){
		int movimento = Constantes.Mov_Parado;
		int movimentoValido = Constantes.Ve_Celula_vazia;
		
		//descobre qual moeda marcar
		Point moedaMaisProxima = ambiente.getMoedaMaisDistante(this.id);
		//acha a moeda mais proxima
		this.moedaMarcada = new Point(moedaMaisProxima.x,moedaMaisProxima.y);
		if (ambiente.getTicsFaltantes()<5 && ambiente.heuristicaDistanciaLadroes(this.posicao)<=Constantes.proximo ){
			if (ambiente.calculaDistancia(this.posicao,moedaMaisProxima)==1)
				movimentoValido = Constantes.Ve_Moeda;
		}
		
		//anda ateh a moeda mais proxima
		if (ambiente.calculaDistancia(this.posicao, this.moedaMarcada)>1 || (ambiente.getTicsFaltantes()<5 && ambiente.heuristicaDistanciaLadroes(posicao)<=Constantes.proximo )){
			Point acima,abaixo,esquerda,direita;
			int menorDist = Constantes.Distancia_Desconhecida;
			int distAcima = Constantes.Distancia_Desconhecida;
			int distAbaixo = Constantes.Distancia_Desconhecida;
			int distEsquerda = Constantes.Distancia_Desconhecida;
			int distDireita = Constantes.Distancia_Desconhecida;
			
			acima = new Point();
			acima.x = this.posicao.x;
			acima.y = this.posicao.y-1;
			distAcima = ambiente.calculaDistancia(acima, this.moedaMarcada);
			if (visualizacao[1][2]==movimentoValido){
				menorDist = distAcima;
				movimento = Constantes.Mov_Acima;
			}
			
			abaixo = new Point();
			abaixo.x = this.posicao.x;
			abaixo.y = this.posicao.y+1;
			distAbaixo = ambiente.calculaDistancia(abaixo, this.moedaMarcada);
			if (visualizacao[3][2]==movimentoValido)
				if (distAbaixo<distAcima || distAcima==Constantes.Distancia_Desconhecida){
					menorDist = distAbaixo;
					movimento = Constantes.Mov_Baixo;
				}
			
			esquerda = new Point();
			esquerda.x = this.posicao.x-1;
			esquerda.y = this.posicao.y;
			distEsquerda = ambiente.calculaDistancia(esquerda, this.moedaMarcada);
			if (visualizacao[2][1]==movimentoValido)
				if (distEsquerda<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distEsquerda;
					movimento = Constantes.Mov_Esquerda;
				}
			
			direita = new Point();
			direita.x = this.posicao.x+1;
			direita.y = this.posicao.y;
			distDireita = ambiente.calculaDistancia(direita, this.moedaMarcada);
			if (visualizacao[2][3]==movimentoValido)
				if (distDireita<menorDist || menorDist==Constantes.Distancia_Desconhecida){
					menorDist = distDireita;
					movimento = Constantes.Mov_Direita;
				}
		}
		return movimento;
	}
	/*
	 * 
	 *  F I M  C A C A D O R
	 * 
	 */
	
	/*
	 * 
	 * Interpretacoes
	 * 
	 */
	/*pega a visao do sensor e coloca ela na visualizacao*/
	public void interpretaVisao(){
		int i,j,k;
		
		/*vamos analisar a visualizacao como uma matriz*/
		k=0;
		for (i=0;i<5;i++){
			for(j=0;j<5;j++){
				if (i==2 && j==2){
					this.visualizacao[i][j] = Constantes.Ve_Sem_visao_para_o_local;
				}else{
					this.visualizacao[i][j] = sensor.getVisaoIdentificacao()[k];
					k++;
				}
			}
		}
		
		/*agora vamos analisar nossa visualizacao*/
		for (i=0;i<5;i++){
			for (j=0;j<5;j++){
				switch(visualizacao[i][j]){
					case Constantes.Ve_Banco:
						if (!ambiente.bancoEncontrado()){
							Point banco = new Point();
							switch(i){
								case 0:
									banco.y = this.posicao.y - 2;
								break;
								case 1:
									banco.y = this.posicao.y - 1;
								break;
								case 2:
									banco.y = this.posicao.y;
								break;
								case 3:
									banco.y = this.posicao.y + 1;
								break;
								case 4:
									banco.y = this.posicao.y + 2;
								break;
							}
							switch(j){
								case 0:
									banco.x = this.posicao.x - 2;
								break;
								case 1:
									banco.x = this.posicao.x - 1;
								break;
								case 2:
									banco.x = this.posicao.x;
								break;
								case 3:
									banco.x = this.posicao.x + 1;
								break;
								case 4:
									banco.x = this.posicao.x + 2;
								break;							
							}
							ambiente.setBanco(banco);
						}
					break;
					case Constantes.Ve_Moeda:
						Point moeda = new Point();
						switch(i){
							case 0:
								moeda.y = this.posicao.y - 2;
							break;
							case 1:
								moeda.y = this.posicao.y - 1;
							break;
							case 2:
								moeda.y = this.posicao.y;
							break;
							case 3:
								moeda.y = this.posicao.y + 1;
							break;
							case 4:
								moeda.y = this.posicao.y + 2;
							break;
						}
						switch(j){
							case 0:
								moeda.x = this.posicao.x - 2;
							break;
							case 1:
								moeda.x = this.posicao.x - 1;
							break;
							case 2:
								moeda.x = this.posicao.x;
							break;
							case 3:
								moeda.x = this.posicao.x + 1;
							break;
							case 4:
								moeda.x = this.posicao.x + 2;
							break;
						}					
						ambiente.addMoeda(moeda);
					break;
					case Constantes.Ve_Celula_vazia:
						//atualiza ladroes e moedas, caso estes estavam nesta celula e nao estao mais
						Point vazio = new Point();
						switch(i){
							case 0:
								vazio.y = this.posicao.y - 2;
							break;
							case 1:
								vazio.y = this.posicao.y - 1;
							break;
							case 2:
								vazio.y = this.posicao.y;
							break;
							case 3:
								vazio.y = this.posicao.y + 1;
							break;
							case 4:
								vazio.y = this.posicao.y + 2;
							break;
						}
						switch(j){
							case 0:
								vazio.x = this.posicao.x - 2;
							break;
							case 1:
								vazio.x = this.posicao.x - 1;
							break;
							case 2:
								vazio.x = this.posicao.x;
							break;
							case 3:
								vazio.x = this.posicao.x + 1;
							break;
							case 4:
								vazio.x = this.posicao.x + 2;
							break;
						}								
						ambiente.atualizarMapa(new Point(vazio.x,vazio.y));
					break;
					default:
						/*se for maior que ou igual a 200 eh um ladrao*/
						if (visualizacao[i][j]>=Constantes.Ve_Ladrao){
							Point ladrao = new Point();
							switch(i){
								case 0:
									ladrao.y = this.posicao.y - 2;
								break;
								case 1:
									ladrao.y = this.posicao.y - 1;
								break;
								case 2:
									ladrao.y = this.posicao.y;
								break;
								case 3:
									ladrao.y = this.posicao.y + 1;
								break;
								case 4:
									ladrao.y = this.posicao.y + 2;
								break;
							}
							switch(j){
								case 0:
									ladrao.x = this.posicao.x - 2;
								break;
								case 1:
									ladrao.x = this.posicao.x - 1;
								break;
								case 2:
									ladrao.x = this.posicao.x;
								break;
								case 3:
									ladrao.x = this.posicao.x + 1;
								break;
								case 4:
									ladrao.x = this.posicao.x + 2;
								break;							
							}
							ambiente.atualizaLadrao(visualizacao[i][j], ladrao);
						}
					break;
				}
			}
		}
		
	}
}