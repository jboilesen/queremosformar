package utils;

public class Ambiente {

	private static int numeroPoupadores = 0;
	private int contaMovimentosRodada = 0;
	private int contaTicsFaltantes = 1000;
	private int idPoupador = 0;
	
	public int poupadorBanco = Constantes.semId;
	public int poupadorDespista = Constantes.semId;
	
	public int getPoupadorId(){
		this.idPoupador++;
		return this.idPoupador;
	}
	//metodo para contarmos quantos poupadores temos
	public void contaPoupador(){
		numeroPoupadores++;
	}
	//metodo para mantermos um controle de quantas rodadas ainda faltam
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
	public int getTicsFaltantes(){
		return this.contaTicsFaltantes;
	}
	
}
