package com.github.erioldeth.bomb;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.TimerAction;
import com.github.erioldeth.bomb.Menu.ChronMainMenu;
import com.github.erioldeth.bomb.Menu.MyPauseMenu;
import com.github.erioldeth.bomb.components.AI;
import com.github.erioldeth.bomb.components.Animation;
import com.github.erioldeth.bomb.components.HealthBar;
import com.github.erioldeth.bomb.components.Movement;
import com.github.erioldeth.bomb.components.Position;
import com.github.erioldeth.bomb.physics.EntityAndExplosion;
import com.github.erioldeth.bomb.physics.EntityAndObstacle;
import com.github.erioldeth.bomb.physics.ObstacleAndExplosion;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class Main
		extends GameApplication {
	private boolean playing = false;
	private Entity player;
	private Movement playerController;
	
	public static void main(String[] args) {
		launch(args);
	}
	//settings -> input -> vars -> game -> physics -> UI
	
	@Override
	protected void initSettings(GameSettings settings) {
		settings.setWidth(1200);
		settings.setHeight(800);
		
		settings.setTitle("Bomb");
		settings.setVersion("0.1");
		settings.setAppIcon("icon/bomb.png");
		
		settings.setTicksPerSecond(60);
		
		settings.setMainMenuEnabled(true);
		settings.setGameMenuEnabled(true);
		settings.setSceneFactory(new SceneFactory() {
			@NotNull
			@Override
			public FXGLMenu newGameMenu() {
				return new MyPauseMenu();
			}
			
			@NotNull
			@Override
			public FXGLMenu newMainMenu() {
				ChronMainMenu.start();
				return new ChronMainMenu();
			}
		});
		
		
		settings.setApplicationMode(ApplicationMode.RELEASE);
	}
	
	@Override
	protected void initGame() {
		ChronMainMenu.stop();
		Sound.Loop("Enchanted Bomb/src/main/resources/assets/sounds/GameMusic.wav");
		FXGL.getGameWorld().addEntityFactory(new Spawner());
	}
	
	@Override
	protected void initGameVars(java.util.Map<String, Object> vars) {
		vars.put("ammo", 1);
		vars.put("range", 1);
		vars.put("damage", 1);
		vars.put("chargingTime", 2.5);
		
		vars.put("level", 1);
		vars.put("portal", 0);
		vars.put("maxPortal", 5);
		vars.put("enemy", 0);
		vars.put("maxEnemy", 10);
	}
	
	@Override
	protected void initInput() {
		FXGL.onKeyBuilder(KeyCode.LEFT, "move left")
		    .onActionBegin(() -> playerController.moveLeft())
		    .onActionEnd(() -> playerController.moveRight());
		
		FXGL.onKeyBuilder(KeyCode.RIGHT, "move right")
		    .onActionBegin(() -> playerController.moveRight())
		    .onActionEnd(() -> playerController.moveLeft());
		
		FXGL.onKeyBuilder(KeyCode.UP, "move up")
		    .onActionBegin(() -> playerController.moveUp())
		    .onActionEnd(() -> playerController.moveDown());
		
		FXGL.onKeyBuilder(KeyCode.DOWN, "move down")
		    .onActionBegin(() -> playerController.moveDown())
		    .onActionEnd(() -> playerController.moveUp());
		
		FXGL.onKey(KeyCode.SPACE, "plant bomb", () -> {
			if(FXGL.geti("ammo") == 0) return;
			var posComp = player.getComponent(Position.class);
			var center = posComp.center();
			if(Map.empty(center.getX(), center.getY())) {
				Map.setCell(center.getX(), center.getY(), 'b');
				FXGL.spawn("bomb", posComp.getCellPos(center));
				FXGL.inc("ammo", -1);
			}
		});
	}
	
	@Override
	protected void initUI() {
		var gameScene = FXGL.getGameScene();
		
		gameScene.setBackgroundColor(Color.SILVER);
		
		var ammoText = TextMaker.createText("ammo");
		var rangeText = TextMaker.createText("range");
		var damageText = TextMaker.createText("damage");
		var enemyText = TextMaker.createText("enemy");
		var portalText = TextMaker.createText("portal");
		
		HBox box = new HBox(75, ammoText, rangeText, damageText, enemyText, portalText);
		box.setPrefWidth(FXGL.getAppWidth());
		box.setPrefHeight(Map.startY);
		box.setAlignment(Pos.CENTER);
		
		gameScene.addUINode(box);
		switchScene();
	}
	
	@Override
	protected void initPhysics() {
		var gamePhysics = FXGL.getPhysicsWorld();
		gamePhysics.addCollisionHandler(new EntityAndObstacle(Type.PLAYER, Type.HARD_WALL));
		gamePhysics.addCollisionHandler(new EntityAndObstacle(Type.PLAYER, Type.SOFT_WALL));
		gamePhysics.addCollisionHandler(new EntityAndExplosion(Type.PLAYER, Type.EXPLOSION));
		gamePhysics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
			private TimerAction damaging = null;
			
			@Override
			protected void onCollision(Entity player,
			                           Entity enemy) {
				if(damaging == null || damaging.isExpired()) {
					damaging = FXGL.runOnce(() -> player.setProperty("hitPoint", player.getInt("hitPoint") - enemy.getInt("damage")), Duration.seconds(0.5));
				}
			}
			
			@Override
			protected void onCollisionEnd(Entity player,
			                              Entity enemy) {
				damaging.expire();
			}
		});
		gamePhysics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.RADAR) {
			private AI aiComp;
			
			@Override
			protected void onCollision(Entity player,
			                           Entity radar) {
				aiComp = ((Entity)radar.getObject("owner")).getComponent(AI.class);
				aiComp.pursuit(player);
			}
			
			@Override
			protected void onCollisionEnd(Entity player,
			                              Entity radar) {
				aiComp.stop();
			}
		});
		FXGL.onCollision(Type.PLAYER, Type.ITEM, (player, item) -> {
			Sound.play("Enchanted Bomb/src/main/resources/assets/sounds/TakeItem.wav");
			var type = item.getString("type");
			var lastIndex = type.length() - 1;
			switch(type.substring(0, lastIndex)) {
				case "ammo", "range", "damage" -> {
					var amountInc = type.charAt(lastIndex) == '+' ? 1 : -1;
					type = type.substring(0, lastIndex);
					FXGL.inc(type, amountInc);
					if(FXGL.geti(type) < 1) FXGL.set(type, 1);
					switch(type) {
						case "ammo" -> {
							if(FXGL.geti(type) > 5) FXGL.set(type, 5);
						}
						case "range" -> {
							if(FXGL.geti(type) > 4) FXGL.set(type, 4);
						}
						case "damage" -> {
							if(FXGL.geti(type) > 3) FXGL.set(type, 3);
						}
					}
				}
				case "hp" -> {
					var newHp = player.getInt("hitPoint") + (type.charAt(lastIndex) == '+' ? 1 : -1);
					if(newHp > 5) newHp = 5;
					else if(newHp < 1) newHp = 1;
					player.setProperty("hitPoint", newHp);
				}
				case "speed" -> {
					var newSpeed = player.getDouble("speed") + (type.charAt(lastIndex) == '+' ? .2 : -.2);
					if(newSpeed > 2.4) newSpeed = 2.4;
					else if(newSpeed < .6) newSpeed = .6;
					player.setProperty("speed", newSpeed);
				}
			}
			item.removeFromWorld();
		});
		
		gamePhysics.addCollisionHandler(new EntityAndObstacle(Type.ENEMY, Type.HARD_WALL) {
			@Override
			protected void onCollisionEnd(Entity entity,
			                              Entity obstacle) {
				entity.getComponent(AI.class).stop();
			}
		});
		gamePhysics.addCollisionHandler(new EntityAndObstacle(Type.ENEMY, Type.SOFT_WALL) {
			@Override
			protected void onCollisionEnd(Entity entity,
			                              Entity obstacle) {
				entity.getComponent(AI.class).stop();
			}
		});
		gamePhysics.addCollisionHandler(new EntityAndExplosion(Type.ENEMY, Type.EXPLOSION));
		
		gamePhysics.addCollisionHandler(new ObstacleAndExplosion(Type.SOFT_WALL, Type.EXPLOSION) {
			@Override
			protected void onCollisionEnd(Entity softWall,
			                              Entity explosion) {
				Sound.play("Enchanted Bomb/src/main/resources/assets/sounds/BrickDebris.wav");
				var wPosComp = softWall.getComponent(Position.class);
				if(FXGLMath.randomBoolean(0.2)) Map.spawnItem(wPosComp.getCellPos(wPosComp.getPos()));
			}
		});
		gamePhysics.addCollisionHandler(new ObstacleAndExplosion(Type.DECOR, Type.EXPLOSION) {
			@Override
			protected void onCollisionEnd(Entity decor,
			                              Entity explosion) {
				var dPosComp = decor.getComponent(Position.class);
				Map.spawnItem(dPosComp.getCellPos(dPosComp.getPos()));
			}
		});
		gamePhysics.addCollisionHandler(new ObstacleAndExplosion(Type.PORTAL, Type.EXPLOSION));
		FXGL.onCollision(Type.ITEM, Type.EXPLOSION, (item, explosion) -> item.removeFromWorld());
	}
	
	@Override
	protected void onUpdate(double tpf) {
		if(!playing || player == null || player.getPropertyOptional("hitPoint").isEmpty()) return;
		if(player.getInt("hitPoint") <= 0) {
			playing = false;
			FXGL.runOnce(this::gameOver, Duration.seconds(1));
		}
		else if(FXGL.geti("enemy") == 0 && FXGL.geti("portal") == 0) {
			playing = false;
			FXGL.runOnce(this::newLevel, Duration.seconds(0.5));
		}
	}
	
	private void newLevel() {
		FXGL.getInput().setProcessInput(false);
		FXGL.getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
		resetPlayerStat();
		FXGL.inc("level", 1);
		FXGL.inc("maxPortal", 1);
		FXGL.inc("maxEnemy", 2);
		switchScene();
	}
	
	private void gameOver() {
		FXGL.getInput().setProcessInput(false);
		FXGL.getDialogService().showConfirmationBox("Game over\nPlay again ? ", yes -> {
			var controller = FXGL.getGameController();
			if(yes) controller.startNewGame();
			else {
				controller.gotoMainMenu();
				Sound.loop.stop();
				ChronMainMenu.start();
			}
		});
	}
	
	private void switchScene() {
		FXGL.getInput().setProcessInput(false);
		var levelText = TextMaker.createText("level");
		levelText.textProperty().unbind();
		levelText.setText("Level " + FXGL.geti("level"));
		levelText.setFill(Color.SEAGREEN);
		levelText.setScaleX(5);
		levelText.setScaleY(5);
		
		var scene = FXGL.getGameScene();
		var bg = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.SILVER);
		
		HBox box = new HBox(levelText);
		box.setPrefWidth(FXGL.getAppWidth());
		box.setPrefHeight(FXGL.getAppHeight());
		box.setAlignment(Pos.CENTER);
		
		scene.addUINodes(bg, box);
		
		FXGL.runOnce(() -> {
			scene.removeUINode(box);
			scene.removeUINode(bg);
			Map.makeNewMap();
			createPlayer();
			FXGL.getInput().setProcessInput(true);
		}, Duration.seconds(2));
	}
	
	private void createPlayer() {
		SpawnData playerSpawnData = new SpawnData(Map.cellSide + Map.startX, Map.cellSide + Map.startY);
		player = FXGL.entityBuilder()
		             .type(Type.PLAYER)
		             .scale(1.5, 1.5)
		             .with("hitPoint", 5)
		             .with("speed", 1.)
		             .with(new Animation(Resource.bomber, false))
		             .with(new Position(playerSpawnData, 2, 0, 18, 24))
		             .with(new HealthBar(Color.LIMEGREEN))
		             .with(new Movement())
		             .collidable()
		             .buildAndAttach();
		playerController = player.getComponent(Movement.class);
		playing = true;
	}
	
	private void resetPlayerStat() {
		FXGL.set("ammo", 1);
		FXGL.set("range", 1);
		FXGL.set("damage", 1);
	}
}

class TextMaker {
	private static final Font font = Font.loadFont(TextMaker.class.getClassLoader()
	                                                              .getResourceAsStream("assets/fonts/ZakirahsHandB.ttf"), 32);
	private static final Effect effect = new Glow();
	private static final Color color = Color.MAROON;
	
	static Text createText(String statName) {
		Text stat = new Text();
		stat.setFont(font);
		stat.textProperty().bind(new SimpleStringProperty(statName + ": ").concat(FXGL.getip(statName).asString()));
		stat.setFill(color);
		stat.setEffect(effect);
		return stat;
	}
}