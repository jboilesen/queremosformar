package algoritmo;

import utils.Ambiente;

public class Poupador extends ProgramaPoupador {
	private boolean contado = false;
	private int[] visao = null;
	public static Ambiente ambiente = new Ambiente();
	
	public int acao() {
		//para sabermos quantos jogadores temos
		if (!this.contado){
			ambiente.contaPoupador();
			this.contado = true;
		}
		
		//primeiro guardamos a visao
		visao = sensor.getVisaoIdentificacao();

		//se nao estamos na ultima rodada
		if (ambiente.getTicsFaltantes()>1){
			//se aproxima da moeda mais proxima
		}else{
			//se pegar a moeda for seguro, pega a moeda
			
		}
		//registra o movimento no ambiente
		ambiente.contaMovimento();
		return (int) (Math.random() * 5);
	}
}