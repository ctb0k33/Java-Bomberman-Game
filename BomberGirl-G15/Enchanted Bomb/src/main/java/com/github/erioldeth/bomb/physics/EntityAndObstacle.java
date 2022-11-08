package com.github.erioldeth.bomb.physics;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.github.erioldeth.bomb.Map;
import com.github.erioldeth.bomb.components.Movement;
import com.github.erioldeth.bomb.components.Position;
import javafx.geometry.Point2D;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class EntityAndObstacle
		extends CollisionHandler {
	private Position ePosComp, oPosComp;
	
	public EntityAndObstacle(Object entity,
	                         Object obstacle) {
		super(entity, obstacle);
	}
	
	@Override
	protected void onCollision(Entity entity,
	                           Entity obstacle) {
		var eDir = entity.getComponent(Movement.class).getDirection();
		if(eDir.magnitude() != 0) {
			ePosComp = entity.getComponent(Position.class);
			oPosComp = obstacle.getComponent(Position.class);
			var bound = oPosComp.bound();
			var moved = eDir.normalize().multiply(entity.getDouble("speed"));
			var impactPoint = ePosComp.corner()
			                          .stream()
			                          .filter(bound::contains)
			                          .min(Comparator.comparingDouble(oPosComp.center()::distance))
			                          .orElse(null);
			
			if(impactPoint == null) return;
			
			impactPoint = impactPoint.subtract(moved);
			
			double x0 = impactPoint.getX(), y0 = impactPoint.getY();
			double vx = eDir.getX(), vy = eDir.getY();
			List<Pair<Point2D, String>> intersect = new ArrayList<>(4);
			
			var left = vx == 0 ? -1 : (oPosComp.left() - x0) / vx;
			var right = vx == 0 ? -1 : (oPosComp.right() - x0) / vx;
			var top = vy == 0 ? -1 : (oPosComp.top() - y0) / vy;
			var bottom = vy == 0 ? -1 : (oPosComp.bottom() - y0) / vy;
			
			if(left >= 0) intersect.add(new Pair<>(new Point2D(oPosComp.left(), left * vy + y0), "left"));
			if(right >= 0) intersect.add(new Pair<>(new Point2D(oPosComp.right(), right * vy + y0), "right"));
			if(top >= 0) intersect.add(new Pair<>(new Point2D(top * vx + x0, oPosComp.top()), "top"));
			if(bottom >= 0) intersect.add(new Pair<>(new Point2D(bottom * vx + x0, oPosComp.bottom()), "bottom"));
			
			var finalImpactPoint = impactPoint;
			intersect.sort(Comparator.comparingDouble(p -> finalImpactPoint.distance(p.getFirst())));
			
			//			System.out.println(oPosComp.getPos());
			//			System.out.println(eDir);
			//			System.out.println(intersect);
			//			System.out.println();
			
			var newPos1 = newPos(intersect.get(0).getSecond());
			if(intersect.size() > 1) {
				var newPos2 = newPos(intersect.get(1).getSecond());
				var contain1 = bound.contains(intersect.get(0).getFirst());
				var walkable1 = checkCell(intersect.get(0).getSecond());
				var walkable2 = checkCell(intersect.get(1).getSecond());
				if(contain1) {
					if(walkable1) {
						if(walkable2) newPos1 = Stream.of(newPos1, newPos2)
						                              .min(Comparator.comparingDouble(
								                              ePosComp.getPos().subtract(moved)::distance))
						                              .get();
						
					}
					else newPos1 = newPos2;
				}
				else if(walkable2) newPos1 = newPos2;
			}
			ePosComp.moveTo(newPos1);
		}
	}
	
	private Point2D newPos(String dir) {
		double x = ePosComp.left(), y = ePosComp.top();
		switch(dir) {
			case "left" -> x = oPosComp.left() - 1.1 - ePosComp.width();
			case "right" -> x = oPosComp.right() + 1.1;
			case "top" -> y = oPosComp.top() - 1.1 - ePosComp.height();
			case "bottom" -> y = oPosComp.bottom() + 1.1;
		}
		return new Point2D(x, y);
	}
	
	private boolean checkCell(String dir) {
		double x = oPosComp.left(), y = oPosComp.top();
		switch(dir) {
			case "left" -> x -= Map.cellSide;
			case "right" -> x += Map.cellSide;
			case "top" -> y -= Map.cellSide;
			case "bottom" -> y += Map.cellSide;
		}
		return Map.walkable(x, y);
	}
}