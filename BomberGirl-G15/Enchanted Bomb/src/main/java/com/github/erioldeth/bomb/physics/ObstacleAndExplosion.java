package com.github.erioldeth.bomb.physics;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.github.erioldeth.bomb.Map;
import com.github.erioldeth.bomb.components.Animation;
import com.github.erioldeth.bomb.components.Position;

public class ObstacleAndExplosion
		extends CollisionHandler {
	public ObstacleAndExplosion(Object obstacle,
	                            Object explosion) {
		super(obstacle, explosion);
	}
	
	@Override
	protected void onCollisionBegin(Entity obstacle,
	                                Entity explosion) {
		var oPosComp = obstacle.getComponent(Position.class);
		Map.setCell(oPosComp.left(), oPosComp.top(), ' ');
		obstacle.getComponent(Animation.class)
		        .die();
	}
}