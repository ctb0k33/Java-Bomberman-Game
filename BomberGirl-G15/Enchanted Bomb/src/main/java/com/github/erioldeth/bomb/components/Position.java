package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.github.erioldeth.bomb.Map;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;

import java.util.List;

public class Position
		extends Component {
	private final HitBox bound;
	private Point2D topLeft, viewDif;
	
	public Position(SpawnData data,
	                double boundX,
	                double boundY,
	                double boundWidth,
	                double boundHeight) {
		topLeft = new Point2D(data.getX(), data.getY());
		bound = new HitBox(new Point2D(boundX, boundY), BoundingShape.box(boundWidth, boundHeight));
	}
	
	public Position(SpawnData data,
	                double boundWidth,
	                double boundHeight) {
		this(data, 0, 0, boundWidth, boundHeight);
	}
	
	public Position(SpawnData data,
	                double boundX,
	                double boundY,
	                double boundSide) {
		this(data, boundX, boundY, boundSide, boundSide);
	}
	
	public Position(SpawnData data,
	                double boundSide) {
		this(data, boundSide, boundSide);
	}
	
	@Override
	public void onAdded() {
		entity.getBoundingBoxComponent().addHitBox(bound);
		entity.setScaleOrigin(bound.centerLocal());
		entity.setRotationOrigin(bound.centerLocal());
		viewDif = entity.getPosition().subtract(bound.getMinXWorld(), bound.getMinYWorld());
		moveToMidCell(topLeft, Map.cellSide, Map.cellSide);
	}
	
	public Point2D getPos() {
		return topLeft;
	}
	
	public Point2D getCellPos(Point2D curPoint) {
		var x = Math.floor((curPoint.getX() - Map.startX) / Map.cellSide) * Map.cellSide + Map.startX;
		var y = Math.floor((curPoint.getY() - Map.startY) / Map.cellSide) * Map.cellSide + Map.startY;
		return new Point2D(x, y);
	}
	
	public void moveTo(double x,
	                   double y) {
		moveTo(new Point2D(x, y));
	}
	
	public void moveTo(Point2D pos) {
		topLeft = pos;
		entity.setPosition(pos.add(viewDif));
	}
	
	public void moveToMidCell(double midX,
	                          double midY) {
		moveToMidCell(new Point2D(midX, midY));
	}
	
	public void moveToMidCell(Point2D cellMidPoint) {
		moveTo(topLeft.add(cellMidPoint.subtract(center())));
	}
	
	public void moveToMidCell(Point2D cellPos,
	                          double cellWidth,
	                          double cellHeight) {
		moveToMidCell(cellPos.add(cellWidth / 2, cellHeight / 2));
	}
	
	public BoundingBox bound() {
		return new BoundingBox(left(), top(), width(), height());
	}
	
	public List<Point2D> corner() {
		return List.of(topLeft, topLeft.add(width(), 0), topLeft.add(0, height()), topLeft.add(width(), height()));
	}
	
	public Point2D center() {
		return topLeft.add(width() / 2, height() / 2);
	}
	
	public Point2D localCenter() {
		return bound.centerLocal();
	}
	
	public double left() {
		return topLeft.getX();
	}
	
	public double right() {
		return left() + width();
	}
	
	public double top() {
		return topLeft.getY();
	}
	
	public double bottom() {
		return top() + height();
	}
	
	public double width() {
		return bound.getWidth();
	}
	
	public double height() {
		return bound.getHeight();
	}
	
}