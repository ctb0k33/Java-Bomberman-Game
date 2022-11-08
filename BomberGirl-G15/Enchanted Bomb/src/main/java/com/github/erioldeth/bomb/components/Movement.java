package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.Stack;

public class Movement
		extends Component {
	private boolean disabled = false, following = false;
	private Point2D direction = new Point2D(0, 0);
	private Stack<Point2D> path = null;
	
	@Override
	public void onUpdate(double tpf) {
		if(disabled) return;
		var ePosComp = entity.getComponent(Position.class);
		if(following && path != null && !path.isEmpty()) {
			if(ePosComp.center().distance(path.peek()) <= 1) path.pop();
			if(path.isEmpty()) return;
			var dest = path.peek();
			var eCenter = ePosComp.center();
			
			if(Math.abs(eCenter.getX() - dest.getX()) > 1)
				direction = new Point2D(eCenter.getX() > dest.getX() ? -1 : 1, direction.getY());
			else direction = new Point2D(0, direction.getY());
			
			if(Math.abs(eCenter.getY() - dest.getY()) > 1)
				direction = new Point2D(direction.getX(), eCenter.getY() > dest.getY() ? -1 : 1);
			else direction = new Point2D(direction.getX(), 0);
			
			changeAnimation(true);
		}
		if(entity.getInt("hitPoint") <= 0) {
			disabled = true;
			entity.getComponent(Animation.class).die();
		}
		else if(direction.magnitude() != 0) {
			Point2D vec = direction.normalize().multiply(entity.getDouble("speed"));
			ePosComp.moveTo(ePosComp.getPos().add(vec));
		}
	}
	
	public void followPath(Stack<Point2D> path) {
		if(!following) {
			System.out.println("inc speed");
			entity.setProperty("speed", entity.getDouble("speed") * 2);
			following = true;
		}
		this.path = path;
	}
	
	public void enableMovement() {
		disabled = false;
	}
	
	public void disableMovement() {
		if(following) {
			following = false;
			entity.setProperty("speed", entity.getDouble("speed") / 2);
		}
		disabled = true;
		direction = new Point2D(0, 0);
		entity.getComponent(Animation.class).stayStill();
	}
	
	public Point2D getDirection() {
		return direction;
	}
	
	public void moveLeft() {
		changeDirection(-1, 0);
	}
	
	public void moveRight() {
		changeDirection(1, 0);
	}
	
	public void moveUp() {
		changeDirection(0, -1);
	}
	
	public void moveDown() {
		changeDirection(0, 1);
	}
	
	private void changeDirection(float changeX,
	                             float changeY) {
		if(disabled) return;
		var changeable = direction.magnitude() != 1;
		direction = direction.add(changeX, changeY);
		changeAnimation(changeable);
	}
	
	private void changeAnimation(boolean changeable) {
		var anim = entity.getComponent(Animation.class);
		if(direction.magnitude() == 0) anim.stayStill();
		else if(changeable) {
			if(direction.getX() != 0) anim.moveHorizontal(direction.getX());
			else anim.moveVertical(direction.getY());
		}
	}
}