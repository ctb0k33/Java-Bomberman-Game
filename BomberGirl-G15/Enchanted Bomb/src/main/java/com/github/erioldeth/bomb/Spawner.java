package com.github.erioldeth.bomb;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.github.erioldeth.bomb.components.AI;
import com.github.erioldeth.bomb.components.Animation;
import com.github.erioldeth.bomb.components.Detonator;
import com.github.erioldeth.bomb.components.HealthBar;
import com.github.erioldeth.bomb.components.Movement;
import com.github.erioldeth.bomb.components.Position;
import com.github.erioldeth.bomb.components.Radar;
import com.github.erioldeth.bomb.components.WarpGate;
import javafx.scene.paint.Color;

public class Spawner
		implements EntityFactory {
	@Spawns("baromu")
	public Entity baromu(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ENEMY)
		           .scale(1.75, 1.75)
		           .with("hitPoint", 1)
		           .with("speed", .5)
		           .with("damage", 1)
		           .with(new Animation(Resource.baromu, false))
		           .with(new Position(data, 20, 22))
		           .with(new HealthBar(Color.GOLDENROD))
		           .with(new Movement())
		           .with(new AI())
		           .collidable()
		           .build();
	}
	
	@Spawns("onil")
	public Entity onil(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ENEMY)
		           .scale(1.75, 1.75)
		           .with("hitPoint", 2)
		           .with("speed", .5)
		           .with("damage", 1)
		           .with(new Animation(Resource.onil, false))
		           .with(new Position(data, 1, 0, 16, 17))
		           .with(new HealthBar(Color.CORNFLOWERBLUE))
		           .with(new Movement())
		           .with(new AI())
		           .with(new Radar(5 * Map.cellSide))
		           .collidable()
		           .build();
	}
	
	@Spawns("bomb")
	public Entity bomb(SpawnData data) {
		Sound.play("BomberGirl-G15/Enchanted Bomb/src/main/resources/assets/sounds/BombPlant.wav");
		return FXGL.entityBuilder()
		           .type(Type.BOMB)
		           .zIndex(-1)
		           .scale(1.75, 1.75)
		           .with(new Animation(Resource.bomb, false))
		           .with(new Position(data, 16))
		           .with(new Detonator())
		           .collidable()
		           .build();
	}
	
	@Spawns("explosion_core")
	public Entity eCore(SpawnData data) {
		Sound.play("BomberGirl-G15/Enchanted Bomb/src/main/resources/assets/sounds/Explosion.wav");
		return FXGL.entityBuilder()
		           .type(Type.EXPLOSION)
		           .zIndex(-1)
		           .scale(Map.cellSide / 16., Map.cellSide / 16.)
		           .with(new Animation(Resource.explosionCore, true))
		           .with(new Position(data, 1, 1, 14))
		           .collidable()
		           .build();
	}
	
	@Spawns("explosion_side")
	public Entity eSide(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.EXPLOSION)
		           .zIndex(-1)
		           .scale(Map.cellSide / 16., Map.cellSide / 16.)
		           .with(new Animation(Resource.explosionSide, true))
		           .with(new Position(data, 1, 1, 14))
		           .rotate(data.get("rotation"))
		           .collidable()
		           .build();
	}
	
	@Spawns("explosion_tail")
	public Entity eTail(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.EXPLOSION)
		           .zIndex(-1)
		           .scale(Map.cellSide / 16., Map.cellSide / 16.)
		           .with(new Animation(Resource.explosionTail, true))
		           .with(new Position(data, 2, 2, 12))
		           .rotate(data.get("rotation"))
		           .collidable()
		           .build();
	}
	
	@Spawns("ammo_buff")
	public Entity ammoBuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "ammo+")
		           .with(new Animation(Resource.ammoBuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("ammo_debuff")
	public Entity ammoDebuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "ammo-")
		           .with(new Animation(Resource.ammoDebuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("range_buff")
	public Entity rangeBuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "range+")
		           .with(new Animation(Resource.rangeBuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("range_debuff")
	public Entity rangeDebuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "range-")
		           .with(new Animation(Resource.rangeDebuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("damage_buff")
	public Entity damageBuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "damage+")
		           .with(new Animation(Resource.damageBuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("damage_debuff")
	public Entity damageDebuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "damage-")
		           .with(new Animation(Resource.damageDebuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("speed_buff")
	public Entity speedBuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "speed+")
		           .with(new Animation(Resource.speedBuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("speed_debuff")
	public Entity speedDebuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "speed-")
		           .with(new Animation(Resource.speedDebuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("hitPoint_buff")
	public Entity hpBuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "hp+")
		           .with(new Animation(Resource.hpBuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("hitPoint_debuff")
	public Entity hpDebuff(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.ITEM)
		           .scale(2, 2)
		           .zIndex(-2)
		           .with("type", "hp-")
		           .with(new Animation(Resource.hpDebuff, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("g")
	public Entity ground(SpawnData data) {
		return FXGL.entityBuilder(data)
		           .zIndex(-5)
		           .scale(Map.cellSide / 16., Map.cellSide / 16.)
		           .view(Map.textureSource() + "ground.png")
		           .build();
	}
	
	@Spawns("W")
	public Entity hardWall(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.HARD_WALL)
		           .scale(Map.cellSide / 16., Map.cellSide / 16.)
		           .view(Map.textureSource() + "hard_wall.png")
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("w")
	public Entity softWall(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.SOFT_WALL)
		           .scale(Map.cellSide / 16., Map.cellSide / 16.)
		           .with(new Animation(Resource.softWall, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("d")
	public Entity decor(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.DECOR)
		           .scale(2, 2)
		           .with(new Animation(Resource.decor, false))
		           .with(new Position(data, 16))
		           .collidable()
		           .build();
	}
	
	@Spawns("P")
	public Entity fPortal(SpawnData data) {
		return FXGL.entityBuilder()
		           .type(Type.PORTAL)
		           .scale(2.5, 2.5)
		           .with(new Animation(Resource.fixedPortal, false))
		           .with(new Position(data, 16))
		           .with(new WarpGate(FXGL.random(30., 45.)))
		           .collidable()
		           .build();
	}
	
	@Spawns("p")
	public Entity rPortal(SpawnData data) {
		return FXGL.entityBuilder()
		           .scale(2.5, 2.5)
		           .with(new Animation(Resource.randPortal, false))
		           .with(new Position(data, 16))
		           .with(new WarpGate())
		           .build();
	}
}