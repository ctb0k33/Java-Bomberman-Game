package com.github.erioldeth.bomb;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Resource {
	public static Map<String, AnimationChannel> bomber = getAnim("entity/bomber.png", "bomber.txt");
	public static Map<String, AnimationChannel> bomb = getAnim("entity/bomb.png", "bomb.txt");
	public static Map<String, AnimationChannel> explosionCore = getAnim("entity/bomb.png", "explosion_core.txt");
	public static Map<String, AnimationChannel> explosionSide = getAnim("entity/bomb.png", "explosion_side.txt");
	public static Map<String, AnimationChannel> explosionTail = getAnim("entity/bomb.png", "explosion_tail.txt");
	public static Map<String, AnimationChannel> baromu = getAnim("entity/baromu.png", "baromu.txt");
	public static Map<String, AnimationChannel> onil = getAnim("entity/onil.png", "onil.txt");
	public static Map<String, AnimationChannel> ammoBuff = getAnim("entity/item.png", "ammo_buff.txt");
	public static Map<String, AnimationChannel> ammoDebuff = getAnim("entity/item.png", "ammo_debuff.txt");
	public static Map<String, AnimationChannel> rangeBuff = getAnim("entity/item.png", "range_buff.txt");
	public static Map<String, AnimationChannel> rangeDebuff = getAnim("entity/item.png", "range_debuff.txt");
	public static Map<String, AnimationChannel> damageBuff = getAnim("entity/item.png", "damage_buff.txt");
	public static Map<String, AnimationChannel> damageDebuff = getAnim("entity/item.png", "damage_debuff.txt");
	public static Map<String, AnimationChannel> speedBuff = getAnim("entity/item.png", "speed_buff.txt");
	public static Map<String, AnimationChannel> speedDebuff = getAnim("entity/item.png", "speed_debuff.txt");
	public static Map<String, AnimationChannel> hpBuff = getAnim("entity/item.png", "hitPoint_buff.txt");
	public static Map<String, AnimationChannel> hpDebuff = getAnim("entity/item.png", "hitPoint_debuff.txt");
	public static Map<String, AnimationChannel> softWall, decor;
	public static Map<String, AnimationChannel> fixedPortal = getAnim("map/fixed_portal.png", "portal.txt");
	public static Map<String, AnimationChannel> randPortal = getAnim("map/random_portal.png", "portal.txt");
	
	public static Map<String, AnimationChannel> getAnim(String spriteSheetFile,
	                                                    String frameDataFile) {
		//		System.out.println(spriteSheetFile + " " + frameDataFile);
		Map<String, AnimationChannel> animResource = new HashMap<>();
		var spriteImage = FXGL.image(spriteSheetFile);
		try(var frameDataStream = Resource.class.getClassLoader()
		                                        .getResourceAsStream("assets/textures/frameData/" + frameDataFile)) {
			if(frameDataStream != null) {
				var read = new Scanner(frameDataStream);
				var framesPerRow = read.nextInt();
				var frameWidth = read.nextInt();
				var frameHeight = read.nextInt();
				while(read.hasNext()) {
					var animName = read.next();
					var frameDuration = Duration.seconds(read.nextDouble());
					var startFrame = read.nextInt();
					var endFrame = read.nextInt();
					animResource.put(animName, new AnimationChannel(spriteImage, framesPerRow, frameWidth, frameHeight,
					                                                frameDuration, startFrame, endFrame));
				}
				//System.out.println(animResource);
			}
			else System.out.println("Data stream of " + frameDataFile + " is null");
		}
		catch(IOException exception) {
			System.out.println(exception.getMessage());
		}
		return animResource;
	}
}