package algoritmo;

import java.awt.Point;
//import java.util.ArrayList;

import utils.Ambiente;
import utils.Constantes;


public class Poupador extends ProgramaPoupador {
	private int id = Constantes.semId;
	private int[][] visualizacao = new int[5][5];
	private Point posicao = new Point();
	private int papel = Constantes.cacador;
	private int empreitadaAtual = Constantes.catalogarMoedas;
	private Point ultimaCasa;
	public static Ambiente ambiente = new Ambiente();
	
	public int acao() {
		int movimento;
		/*pegamos a posicao deste poupador*/
		this.posicao.x = sensor.getPosicao().x;
		this.posicao.y = sensor.getPosicao().y;
		
		/*guarda esta casa como visitada*/
		ambiente.addCasaVisitada(posicao);
		
		/*se este poupador ainda nao esta contabilizado no ambiente, contabiliza ele*/
		if (this.id==Constantes.semId){
			this.id = ambiente.getPoupadorId(posicao);
		}
		
		this.ultimaCasa = new Point(ambiente.ultimaCasa(this.id));
		
		/*vamos interpretar o que vemos, e registrar no ambiente*/
		interpretaVisao();
		
		/*definindo os papéis de cada poupador*/
		ambiente.definirPapeis();

		/*definimos qual eh o papel deste poupador*/
		if (this.id==ambiente.getDepositante()){
			this.papel = Constantes.depositante;
		}else if (this.id==ambiente.getDespistador()){
			this.papel = Constantes.despistador;
		}
		
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
					break;
					case Constantes.fugirLadrao:
						//se um ladrao se aproxima, procura ir para outras moedas
						//usar a heuristica pra descobrir se um ladrao esta perto
					break;
				}
			break;
			case Constantes.depositante:
				switch (this.empreitadaAtual){
					case Constantes.buscarBanco:
						//se aproxima do banco
					break;
					case Constantes.buscarMoeda:
						//se aproxima da moeda mais proxima do banco
					break;
					case Constantes.depositarMoeda:
						//tenta depositar a moeda
					break;
				}
			break;
			case Constantes.despistador:
				switch (this.empreitadaAtual){
					case Constantes.buscarLadroes:
						//procura estar proximo aos ladroes
					break;
					case Constantes.atrairLadroes:
						//procura levar os ladroes para algum lugar...se eles ficarem mtas rodadas proximos ao banco, mudar estrategia
					break;
					case Constantes.isolarLadroes:
						//procura levar os ladroes o mais distante possivel do depositante
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
				this.posicao.x = posicao.x+1;
			break;
			case Constantes.Mov_Esquerda:
				this.posicao.x = posicao.x-1;
			break;
		}
		ambiente.contaMovimento();
		ambiente.atualizaPoupador(this.id,posicao);
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
							case 3:
								moeda.x = this.posicao.x + 1;
							break;
							case 4:
								moeda.x = this.posicao.x + 2;
							break;
						}					
						ambiente.addMoeda(moeda);
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
					/*Desenvolver rastreamento de casas vazias*/
					/*case Constantes.Ve_Celula_vazia:
						Point casa = new Point();
						switch(i){
							case 0:
								casa.y = this.posicao.y - 2;
							break;
							case 1:
								casa.y = this.posicao.y - 1;
							break;
							case 3:
								casa.y = this.posicao.y + 1;
							break;
							case 4:
								casa.y = this.posicao.y + 2;
							break;
						}
						switch(j){
							case 0:
								casa.x = this.posicao.x - 2;
							break;
							case 1:
								casa.x = this.posicao.x - 1;
							break;
							case 3:
								casa.x = this.posicao.x + 1;
							break;
							case 4:
								casa.x = this.posicao.x + 2;
							break;
						}					
						this.CasasVazias.add(casa);					
					break;*/
				}
			}
		}
		
	}

}