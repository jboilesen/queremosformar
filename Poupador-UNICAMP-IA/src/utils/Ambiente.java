package utils;

public class Ambiente {
	private Banco banco = new Banco();
	private static int numeroPoupadores = 0;
	private int contaMovimentosRodada = 0;
	private int contaTicsFaltantes = 1000;
	
	//metodo para recuperarmos informacoes do banco
	public Banco getBanco(){
		return this.banco;
	}
	//metodo para guardarmos informacoes do banco
	public void setBanco(Banco banco){
		this.banco = banco;
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
			this.contaMovimentosRodada = 0;
		}
	}
	public int getTicsFaltantes(){
		return this.contaTicsFaltantes;
	}
	
}
