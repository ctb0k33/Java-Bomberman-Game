package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import java.util.Map;

public class Animation
		extends Component {
	private final AnimatedTexture currentAnim;
	private final AnimationChannel idleFront, idleBack, idleSide;
	private final AnimationChannel moveFront, moveBack, moveSide;
	private final AnimationChannel die;
	private AnimationChannel idle;
	
	public Animation(Map<String, AnimationChannel> animSources,
	                 boolean playOnce) {
		idleFront = animSources.get("idleFront");
		idleBack = animSources.get("idleBack");
		idleSide = animSources.get("idleSide");
		
		moveFront = animSources.get("moveFront");
		moveBack = animSources.get("moveBack");
		moveSide = animSources.get("moveSide");
		
		die = animSources.get("die");
		
		currentAnim = new AnimatedTexture(idle = idleFront);
		
		if(playOnce) {
			currentAnim.play().setOnCycleFinished(this::die);
		}
		else currentAnim.loop();
	}
	
	@Override
	public void onAdded() {
		entity.getViewComponent().addChild(currentAnim);
	}
	
	public void stayStill() {
		currentAnim.loopNoOverride(idle);
	}
	
	public void moveHorizontal(double vecX) {
		idle = idleSide;
		currentAnim.loopNoOverride(moveSide);
		entity.setScaleX(-vecX * FXGLMath.abs(entity.getScaleX()));
	}
	
	public void moveVertical(double vecY) {
		idle = vecY == -1 ? idleBack : idleFront;
		currentAnim.loopNoOverride(vecY == -1 ? moveBack : moveFront);
	}
	
	public void die() {
		currentAnim.playAnimationChannel(die);
		currentAnim.setOnCycleFinished(() -> entity.removeFromWorld());
	}
}