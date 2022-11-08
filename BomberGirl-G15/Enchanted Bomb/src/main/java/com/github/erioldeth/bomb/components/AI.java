package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import com.github.erioldeth.bomb.Map;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

public class AI
		extends Component {
	private Position ePosComp;
	private Movement eMoveComp;
	private TimerAction wandering, resting;
	
	public AI() {}
	
	public void wander() {
		clearTimer();
		switch(FXGL.random(1, 4)) {
			case 1 -> eMoveComp.moveLeft();
			case 2 -> eMoveComp.moveRight();
			case 3 -> eMoveComp.moveUp();
			case 4 -> eMoveComp.moveDown();
		}
		wandering = FXGL.runOnce(this::stop, Duration.seconds(3));
	}
	
	public void pursuit(Entity other) {
		if(entity == null || other == null) return;
		
		var oPosComp = other.getComponent(Position.class);
		
		var eCellCenter = ePosComp.getCellPos(ePosComp.center()).add(Map.cellSide / 2., Map.cellSide / 2.);
		var oCellCenter = oPosComp.getCellPos(oPosComp.center()).add(Map.cellSide / 2., Map.cellSide / 2.);
		
		var path = getPath(eCellCenter, oCellCenter);
		if(path.isEmpty()) return;
		clearTimer();
		eMoveComp.followPath(getPath(eCellCenter, oCellCenter));
	}
	
	public void stop() {
		if(entity == null) return;
		clearTimer();
		rebootMovement();
		resting = FXGL.runOnce(this::wander, Duration.seconds(1));
	}
	
	private void rebootMovement() {
		eMoveComp.disableMovement();
		eMoveComp.enableMovement();
	}
	
	private void clearTimer() {
		if(wandering != null) wandering.expire();
		if(resting != null) resting.expire();
	}
	
	@Override
	public void onAdded() {
		ePosComp = entity.getComponent(Position.class);
		eMoveComp = entity.getComponent(Movement.class);
		wander();
	}
	
	@Override
	public void onRemoved() {
		clearTimer();
		FXGL.inc("enemy", -1);
	}
	
	private Stack<Point2D> getPath(Point2D start,
	                               Point2D end) {
		Node startNode = new Node(null, start), endNode = null;
		Stack<Point2D> path = new Stack<>();
		if(!start.equals(end)) {
			PriorityQueue<Node> nodes = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
			HashMap<Point2D, Double> opened = new HashMap<>(), closed = new HashMap<>();
			
			nodes.add(startNode);
			opened.put(start, 0.);
			
			while(!nodes.isEmpty()) {
				var pickedNode = nodes.poll();
				var curPoint = pickedNode.curPoint;
				
				if(Map.walkable(curPoint.getX() - Map.cellSide, curPoint.getY()))
					endNode = checkNode(pickedNode, curPoint.add(-Map.cellSide, 0), end, nodes, opened, closed);
				if(endNode == null && Map.walkable(curPoint.getX() + Map.cellSide, curPoint.getY()))
					endNode = checkNode(pickedNode, curPoint.add(Map.cellSide, 0), end, nodes, opened, closed);
				if(endNode == null && Map.walkable(curPoint.getX(), curPoint.getY() - Map.cellSide))
					endNode = checkNode(pickedNode, curPoint.add(0, -Map.cellSide), end, nodes, opened, closed);
				if(endNode == null && Map.walkable(curPoint.getX(), curPoint.getY() + Map.cellSide))
					endNode = checkNode(pickedNode, curPoint.add(0, Map.cellSide), end, nodes, opened, closed);
				
				if(endNode != null) {
					while(endNode.prev != null) {
						path.push(endNode.curPoint);
						endNode = endNode.prev;
					}
					break;
				}
				closed.put(curPoint, pickedNode.f);
			}
		}
		return path;
	}
	
	private Node checkNode(Node prev,
	                       Point2D nextPoint,
	                       Point2D endPoint,
	                       PriorityQueue<Node> nodes,
	                       HashMap<Point2D, Double> opened,
	                       HashMap<Point2D, Double> closed) {
		var nextNode = new Node(prev, nextPoint);
		if(nextPoint.equals(endPoint)) return nextNode;
		nextNode.g = prev.g + Map.cellSide;
		nextNode.h = FXGLMath.abs(endPoint.getX() - nextPoint.getX()) + FXGLMath.abs(
				endPoint.getY() - nextPoint.getY());
		nextNode.f = nextNode.g + nextNode.h;
		
		if(opened.getOrDefault(nextPoint, 1e5) > nextNode.f) {
			if(closed.getOrDefault(nextPoint, 1e5) > nextNode.f) {
				nodes.add(nextNode);
				opened.put(nextPoint, nextNode.f);
			}
		}
		return null;
	}
}

class Node {
	public Node prev;
	public Point2D curPoint;
	public double f = 0, g = 0, h = 0;
	
	public Node(Node prev,
	            Point2D curPoint) {
		this.prev = prev;
		this.curPoint = curPoint;
	}
}