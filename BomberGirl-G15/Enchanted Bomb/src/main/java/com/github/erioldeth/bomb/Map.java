package com.github.erioldeth.bomb;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Map {
	public static final int cellSide = 48, startX = 0, startY = 80;
	private static final List<List<Character>> grid = new ArrayList<>();
	private static final List<Point2D> randomPortalPos = new ArrayList<>();
	private static final List<String> designSource = List.of("map1","map2", "map3", "map4");
	private static final List<String> textureList = List.of("valley", "primal", "edo", "modern", "loot", "candy");
	private static final List<String> enemy = List.of("baromu", "onil");//, "golem", "devastator");
	private static final List<String> spawnableEnemy = new ArrayList<>(enemy.size());
	private static final List<String> item = List.of("ammo_buff", "ammo_debuff", "range_buff", "range_debuff", "damage_buff", "damage_debuff", "speed_buff", "speed_debuff", "hitPoint_buff", "hitPoint_debuff");
	private static int textureId;
	private static TimerAction increaseDifficulty, randomSpawning;
	
	public static void makeNewMap() {
		grid.clear();
		
		randomPortalPos.clear();
		
		spawnableEnemy.clear();
		spawnableEnemy.add(enemy.get(0));
		
		if(increaseDifficulty != null) increaseDifficulty.expire();
		increaseDifficulty = FXGL.run(() -> {
			if(spawnableEnemy.size() < enemy.size()) spawnableEnemy.add(enemy.get(spawnableEnemy.size()));
		}, Duration.seconds(FXGL.random(30, 45)));
		
		if(randomSpawning != null) randomSpawning.expire();
		randomSpawning = FXGL.run(() -> FXGL.spawn("p", randomPortalPos.get(FXGL.random(0, randomPortalPos.size() - 1))), Duration.seconds(FXGL.random(20, 40)));
		
		textureId = FXGL.random(0, textureList.size() - 1);
		
		Resource.softWall = Resource.getAnim(textureSource() + "soft_wall.png", "soft_wall.txt");
		Resource.decor = Resource.getAnim(textureSource() + "decor.png", "decor.txt");
		
		try(var mapDataStream = Map.class.getClassLoader()
		                                 .getResourceAsStream("assets/levels/map" + FXGL.random(1, designSource.size()) + ".txt")) {
			if(mapDataStream != null) {
				var reader = new BufferedReader(new InputStreamReader(mapDataStream));
				String line;
				int row = startY, col = startX;
				while((line = reader.readLine()) != null) {
					List<Character> rowCell = new ArrayList<>(line.length());
					for(var cell : line.toCharArray()) {
						if(cell == 'P') {
							if(FXGL.geti("portal") < FXGL.geti("maxPortal"))
								cell = FXGLMath.randomBoolean(.7) ? 'P' : ' ';
							else cell = FXGLMath.randomBoolean(.3) ? 'P' : ' ';
						}
						else if(cell == 'p') {
							cell = ' ';
							randomPortalPos.add(new Point2D(col, row));
						}
						rowCell.add(cell);
						FXGL.spawn("g", col, row);
						if(cell != ' ') FXGL.spawn(String.valueOf(cell), col, row);
						col += cellSide;
					}
					grid.add(rowCell);
					row += cellSide;
					col = 0;
				}
			}
			else System.out.println("Data stream of " + designSource.get(0) + ".txt is null");
		}
		catch(IOException exception) {
			System.out.println(exception.getMessage());
		}
	}
	
	public static String textureSource() {
		return "map/" + textureList.get(textureId) + "/";
	}
	
	public static boolean walkable(double x,
	                               double y) {
		boolean result = false;
		try {
			x = (x - startX) / cellSide;
			y = (y - startY) / cellSide;
			var cell = grid.get((int)y).get((int)x);
			result = cell != 'w' && cell != 'W';
		}
		catch(IndexOutOfBoundsException ignored) {}
		return result;
	}
	
	public static boolean empty(double x,
	                            double y) {
		boolean result = false;
		try {
			x = (x - startX) / cellSide;
			y = (y - startY) / cellSide;
			result = grid.get((int)y).get((int)x) == ' ';
		}
		catch(IndexOutOfBoundsException ignored) {}
		return result;
	}
	
	public static void setCell(double x,
	                           double y,
	                           char entity) {
		try {
			x = (x - startX) / cellSide;
			y = (y - startY) / cellSide;
			grid.get((int)y).set((int)x, entity);
		}
		catch(IndexOutOfBoundsException ignored) {}
	}
	
	public static void spawnEnemy(Point2D pos) {
		if(FXGL.geti("enemy") == FXGL.geti("maxEnemy")) return;
		var pickedEnemy = spawnableEnemy.get(FXGL.random(0, spawnableEnemy.size() - 1));
		FXGL.spawn(pickedEnemy, pos);
		FXGL.inc("enemy", 1);
	}
	
	public static void spawnItem(Point2D pos) {
		var pickedItem = item.get(FXGL.random(0, item.size() - 1));
		FXGL.spawn(pickedItem, pos);
	}
}