package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.github.erioldeth.bomb.Type;
import javafx.geometry.Point2D;

public class Radar
		extends Component {
	private final double range;
	private Entity radar;
	private Position posComp;
	
	public Radar(double range) {
		this.range = range;
	}
	
	@Override
	public void onAdded() {
		posComp = entity.getComponent(Position.class);
		radar = FXGL.entityBuilder()
		            .type(Type.RADAR)
		            .with("owner", entity)
		            .bbox(BoundingShape.circle(range))
		            .collidable()
		            .buildAndAttach();
		radar.setLocalAnchor(new Point2D(range, range));
	}
	
	@Override
	public void onUpdate(double tpf) {
		radar.setAnchoredPosition(posComp.center());
	}
	
	@Override
	public void onRemoved() {
		radar.removeFromWorld();
	}
}