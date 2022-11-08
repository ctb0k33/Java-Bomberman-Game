package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import com.github.erioldeth.bomb.Map;
import javafx.util.Duration;

public class WarpGate
		extends Component {
	private boolean warpOnce = false;
	private double warpTime;
	private TimerAction warping = null;
	
	public WarpGate(double warpTime) {
		this.warpTime = warpTime;
	}
	
	public WarpGate() {
		warpOnce = true;
	}
	
	@Override
	public void onAdded() {
		if(!warpOnce) FXGL.inc("portal", 1);
		FXGL.runOnce(() -> {
			if(entity == null) return;
			var posComp = entity.getComponent(Position.class);
			var pos = posComp.getCellPos(posComp.getPos());
			Map.spawnEnemy(pos);
			if(warpOnce) entity.removeFromWorld();
			else warping = FXGL.run(() -> Map.spawnEnemy(pos), Duration.seconds(warpTime));
		}, Duration.seconds(FXGL.random(2.5, 5)));
	}
	
	@Override
	public void onRemoved() {
		if(!warpOnce) FXGL.inc("portal", -1);
		if(warping != null) warping.expire();
	}
}