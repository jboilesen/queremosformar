package utils;

import java.awt.Point;

public class Banco {
	private Point posicao = null;

	public Point getPosicao(){
		return this.posicao;
	}
	public void setPosicao(Point posicao){
		this.posicao = posicao;
	}
	public void setX(int x){
		this.posicao.x = x;
	}
	public void setY(int y){
		this.posicao.y = y;
	}
	public int getX(){
		return this.posicao.x;
	}
	public int getY(){
		return this.posicao.y;
	}
}
