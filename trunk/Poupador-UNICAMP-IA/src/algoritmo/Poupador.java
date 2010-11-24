package algoritmo;

import java.awt.Point;
import java.util.ArrayList;

import utils.Ambiente;
import utils.Banco;
import utils.Constantes;


public class Poupador extends ProgramaPoupador {
	private int id = Constantes.semId;
	private boolean contado = false;
	private int[][] visualizacao = new int[5][5];
	private ArrayList<Point> Moedas = new ArrayList<Point>();
	private ArrayList<Point> Ladroes = new ArrayList<Point>();
	private Point objetivoMaisProximo = new Point();
	private int distanciaAteobjetivoMaisProximo = Constantes.Distancia_Desconhecida;
	private Banco banco = new Banco();
	private int numMoedas = 0;
	private int objetivo = Constantes.pegarMoedas;
	
	public static Ambiente ambiente = new Ambiente();
	
	public int acao() {
		int i,j,k,dist,movimento;
		//limpamos a lista de ladroes e moedas pois iremos ter uma nova visualizacao agora
		this.Ladroes.clear();
		this.Moedas.clear();
		//limpa a posicao do banco
		this.banco.setPosicao(null);
		//guardamos um id para nosso poupador
		if (this.id == Constantes.semId){
			this.id = ambiente.getPoupadorId();
		}
		//para sabermos quantos jogadores temos
		if (!this.contado){
			ambiente.contaPoupador();
			this.contado = true;
		}
		
		//apenas ajustando a visualizacao do poupador para melhor compreensao
		k=0;
		for (i=0;i<5;i++){
			for (j=0;j<5;j++){
				if (j==2 && i==2){
					visualizacao[i][j] = Constantes.Ve_Fora_do_ambiente;
				}else{
					visualizacao[i][j] = sensor.getVisaoIdentificacao()[k];
					k++;	
				}
			}
		}
		
		/*IMPLEMENTAR*/
		//Aqui eh apenas um aviso importante, talvez nao ha o que implementar.
		//Verificar se este meio de atribuicao de objetivos esta bom o suficiente
		for (i=0;i<5;i++){
			for (j=0;j<5;j++){
				//se esta posicao eh uma moeda
				if (visualizacao[i][j]==Constantes.Ve_Moeda){
					Point moeda = new Point();
					moeda.x = j;
					moeda.y = i;
					this.Moedas.add(moeda);
				}
				//verifica se esta posicao eh um ladrao (todas com valor maior que ou igual a
				//200 eh um ladrao
				if (visualizacao[i][j]>=Constantes.Ve_Ladrao){
					Point ladrao = new Point();
					ladrao.x = j;
					ladrao.y = i;
					this.Ladroes.add(ladrao);
					if (ambiente.poupadorDespista == Constantes.semId && this.objetivo == Constantes.pegarMoedas){
						ambiente.poupadorDespista = this.id;
						objetivo = Constantes.despistarLadroes;
					}
				}
				//se ve o banco, armazena no ambiente
				if (visualizacao[i][j]==Constantes.Ve_Banco){
					Point posicaoBanco = new Point();
					banco.setPosicao(posicaoBanco);
					//se ninguem esta indo depositar no banco e este poupador esta sem objetivo definido,
					//ele agora define que vai buscar depositar no banco
					if (ambiente.poupadorBanco == Constantes.semId && this.objetivo == Constantes.pegarMoedas){
						ambiente.poupadorBanco = this.id;
						objetivo = Constantes.depositarBanco;
					}
				}
			}
		}
		
		//agora decidimos a acao
		if (ambiente.getTicsFaltantes()>1){
			//se nao estamos na ultima rodada
			switch (this.objetivo){
				case Constantes.pegarMoedas:
					for (Point ponto: this.Moedas){
						//calcula a distancia que esta moeda esta do banco
						dist = calculaDistancia(Constantes.posicao,ponto);
						//seta esta moeda como objetivo
						if (dist<this.distanciaAteobjetivoMaisProximo || this.distanciaAteobjetivoMaisProximo == Constantes.Distancia_Desconhecida){
							this.objetivoMaisProximo = ponto;
							this.distanciaAteobjetivoMaisProximo = dist;
						}
					}
					movimento = seAproximadoObjetivo();
					if (movimento!=Constantes.Mov_Desconhecido){
						//registra o movimento no ambiente
						ambiente.contaMovimento();
						return movimento;
					}
				break;
				case Constantes.depositarBanco:
					//se nao pegou nenhuma moeda
					if (this.numMoedas == 0){
						//ainda ve o banco?
						if (this.banco.getPosicao()!=null){
							//se nao ve nenhum ladrao
							if (this.Ladroes.isEmpty()){
								//se ve moedas
								if (!this.Moedas.isEmpty()){
									this.distanciaAteobjetivoMaisProximo = Constantes.Distancia_Desconhecida;
									for (Point moeda: this.Moedas){
										//calcula a distancia que esta moeda esta do banco
										dist = calculaDistancia(moeda, this.banco.getPosicao());
										//seta esta moeda como objetivo
										if (dist<this.distanciaAteobjetivoMaisProximo || this.distanciaAteobjetivoMaisProximo == Constantes.Distancia_Desconhecida){
											this.objetivoMaisProximo = moeda;
											this.distanciaAteobjetivoMaisProximo = dist;
										}
									}
								}else{
									//se aproxima do banco
									this.objetivoMaisProximo = this.banco.getPosicao();
									this.distanciaAteobjetivoMaisProximo = calculaDistancia(Constantes.posicao, this.banco.getPosicao());
								}
								//sem ladroes por perto podemos pegar a moeda tranquilamente
								if (this.distanciaAteobjetivoMaisProximo>1){
									movimento = seAproximadoObjetivo();
								}else{
									movimento = chegaAoObjetivo();
								}
								if (movimento!=Constantes.Mov_Desconhecido){
									//registra o movimento no ambiente
									ambiente.contaMovimento();
									return movimento;
								}
							}else{
								/*IMPLEMENTAR*/
								//Aqui eh interessante fazer a troca de quem despista ladroes
								//Por exemplo: esse passa a despistar e outro tenta achar o banco
								//registra o movimento no ambiente
								ambiente.contaMovimento();
								return Constantes.Mov_Parado;							
							}
						}else{
							//se nao vemos o banco
							/*IMPLEMENTAR*/
						}
					}else{
						//se temos moedas
						/*IMPLEMENTAR*/
					}
				break;
				case Constantes.despistarLadroes:
					movimento = fugirLadrao();
					if (movimento!=Constantes.Mov_Desconhecido){
						//registra o movimento no ambiente
						ambiente.contaMovimento();
						return movimento;
					}
				break;
			}
		}else{
			//se estamos na ultima rodada
			switch (this.objetivo){
				case Constantes.pegarMoedas:
					movimento = chegaAoObjetivo();
					if (movimento!=Constantes.Mov_Desconhecido){
						//registra o movimento no ambiente
						ambiente.contaMovimento();
						return movimento;
					}
				break;
				case Constantes.depositarBanco:
					movimento = chegaAoObjetivo();
					if (movimento!=Constantes.Mov_Desconhecido){
						//registra o movimento no ambiente
						ambiente.contaMovimento();
						return movimento;
					}
				break;
				case Constantes.despistarLadroes:
					movimento = fugirLadrao();
					if (movimento!=Constantes.Mov_Desconhecido){
						//registra o movimento no ambiente
						ambiente.contaMovimento();
						return movimento;
					}
				break;
			}
		}
		//registra o movimento no ambiente
		ambiente.contaMovimento();
		return (int) (Math.random() * 5);
	}
	private int fugirLadrao(){
		boolean flag = true;
		Point ponto = new Point();
		if (visualizacao[1][2]==Constantes.Ve_Celula_vazia){
			ponto.x = 2;
			ponto.y = 1;
			for (Point ladrao: this.Ladroes){
				if (calculaDistancia(ponto, ladrao)<2){
					flag = false;
					break;
				}
			}
			if (flag){
				return Constantes.Mov_Acima;
			}
		}
		flag = true;
		if (visualizacao[2][1]==Constantes.Ve_Celula_vazia){
			ponto.x = 1;
			ponto.y = 2;
			for (Point ladrao: this.Ladroes){
				if (calculaDistancia(ponto, ladrao)<2){
					flag = false;
					break;
				}
			}
			if (flag){
				return Constantes.Mov_Esquerda;
			}
		}
		flag = true;
		if (visualizacao[2][3]==Constantes.Ve_Celula_vazia){
			ponto.x = 3;
			ponto.y = 2;
			for (Point ladrao: this.Ladroes){
				if (calculaDistancia(ponto, ladrao)<2){
					flag = false;
					break;
				}
			}
			if (flag){
				return Constantes.Mov_Direita;
			}
		}
		flag = true;
		if (visualizacao[3][2]==Constantes.Ve_Celula_vazia){
			ponto.x = 2;
			ponto.y = 3;
			for (Point ladrao: this.Ladroes){
				if (calculaDistancia(ponto, ladrao)<2){
					flag = false;
					break;
				}
			}
			if (flag){
				return Constantes.Mov_Baixo;
			}
		}
		return Constantes.Mov_Desconhecido;
	}
	//tenta pegar a moeda mais proxima
	private int chegaAoObjetivo(){
		Point ponto = new Point();
		if (this.distanciaAteobjetivoMaisProximo!=Constantes.Distancia_Desconhecida){
			if (visualizacao[1][2]==Constantes.Ve_Moeda){
				ponto.x = 2;
				ponto.y = 1;
				this.numMoedas++;
				return Constantes.Mov_Acima;
			}
			if (visualizacao[2][1]==Constantes.Ve_Moeda){
				ponto.x = 1;
				ponto.y = 2;
				this.numMoedas++;
				return Constantes.Mov_Esquerda;
			}
			if (visualizacao[2][3]==Constantes.Ve_Moeda){
				ponto.x = 3;
				ponto.y = 2;
				this.numMoedas++;
				return Constantes.Mov_Direita;
			}
			if (visualizacao[3][2]==Constantes.Ve_Moeda){
				ponto.x = 2;
				ponto.y = 3;
				this.numMoedas++;
				return Constantes.Mov_Baixo;
			}				
		}else{
			if (visualizacao[1][2]==Constantes.Ve_Celula_vazia){
				return Constantes.Mov_Acima;
			}
			if (visualizacao[2][1]==Constantes.Ve_Celula_vazia){
				return Constantes.Mov_Esquerda;
			}
			if (visualizacao[2][3]==Constantes.Ve_Celula_vazia){
				return Constantes.Mov_Direita;
			}
			if (visualizacao[3][2]==Constantes.Ve_Celula_vazia){
				return Constantes.Mov_Baixo;
			}				
		}
		return Constantes.Mov_Desconhecido;
	}
	//se aproxima da moeda mais proxima
	private int seAproximadoObjetivo(){
		Point ponto = new Point();
		//se esta a 1 de distancia da moeda mais proxima, fica parado
		if (this.distanciaAteobjetivoMaisProximo == 1){
			return Constantes.Mov_Parado;
		}else{
			//tenta se aproximar da moeda mais proxima se ela for conhecida
			if (this.distanciaAteobjetivoMaisProximo!=Constantes.Distancia_Desconhecida){
				if (visualizacao[1][2]==Constantes.Ve_Celula_vazia){
					ponto.x = 2;
					ponto.y = 1;
					//se neste ponto que podemos andar a distancia eh menor ate a moeda
					if (calculaDistancia(ponto,this.objetivoMaisProximo)<this.distanciaAteobjetivoMaisProximo){
						return Constantes.Mov_Acima;
					}
				}
				if (visualizacao[2][1]==Constantes.Ve_Celula_vazia){
					ponto.x = 1;
					ponto.y = 2;
					//se neste ponto que podemos andar a distancia eh menor ate a moeda
					if (calculaDistancia(ponto,this.objetivoMaisProximo)<this.distanciaAteobjetivoMaisProximo){
						return Constantes.Mov_Esquerda;
					}
				}
				if (visualizacao[2][3]==Constantes.Ve_Celula_vazia){
					ponto.x = 3;
					ponto.y = 2;
					//se neste ponto que podemos andar a distancia eh menor ate a moeda
					if (calculaDistancia(ponto,this.objetivoMaisProximo)<this.distanciaAteobjetivoMaisProximo){
						return Constantes.Mov_Direita;
					}
				}
				if (visualizacao[3][2]==Constantes.Ve_Celula_vazia){
					ponto.x = 2;
					ponto.y = 3;
					//se neste ponto que podemos andar a distancia eh menor ate a moeda
					if (calculaDistancia(ponto,this.objetivoMaisProximo)<this.distanciaAteobjetivoMaisProximo){
						return Constantes.Mov_Baixo;
					}
				}
			}else{
				if (visualizacao[3][2]==Constantes.Ve_Celula_vazia){
					return Constantes.Mov_Baixo;
				}
				if (visualizacao[2][3]==Constantes.Ve_Celula_vazia){
					return Constantes.Mov_Direita;
				}
				if (visualizacao[1][2]==Constantes.Ve_Celula_vazia){
					return Constantes.Mov_Acima;
				}
				if (visualizacao[2][1]==Constantes.Ve_Celula_vazia){
					return Constantes.Mov_Esquerda;
				}
			}
		}
		return Constantes.Mov_Desconhecido;
	}
	private int calculaDistancia(Point pontoPartida,Point pontoFinal){
		int distX, distY;
		distX = Math.abs(pontoFinal.x - pontoPartida.x);
		distY = Math.abs(pontoFinal.y - pontoPartida.y);
		return distX+distY;
	}

}