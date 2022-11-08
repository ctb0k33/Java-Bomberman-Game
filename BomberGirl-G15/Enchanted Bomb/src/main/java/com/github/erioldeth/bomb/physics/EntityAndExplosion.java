package com.github.erioldeth.bomb.physics;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

public class EntityAndExplosion
		extends CollisionHandler {
	public EntityAndExplosion(Object entity,
	                          Object explosion) {
		super(entity, explosion);
	}
	
	@Override
	protected void onCollisionBegin(Entity entity,
	                                Entity explosion) {
		entity.setProperty("hitPoint", entity.getInt("hitPoint") - FXGL.geti("damage"));
	}
}