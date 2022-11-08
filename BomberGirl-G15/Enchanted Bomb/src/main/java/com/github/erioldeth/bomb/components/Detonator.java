package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import com.github.erioldeth.bomb.Map;
import javafx.util.Duration;

public class Detonator
		extends Component {
	TimerAction detonate;
	
	@Override
	public void onAdded() {
		detonate = FXGL.runOnce(() -> {
			var posComp = entity.getComponent(Position.class);
			Map.setCell(posComp.left(), posComp.top(), ' ');
			
			var corePos = posComp.getCellPos(posComp.getPos());
			FXGL.spawn("explosion_core", corePos);
			var range = FXGL.geti("range");
			
			var leftPos = corePos.getX() - Map.cellSide;
			var rightPos = corePos.getX() + Map.cellSide;
			var upPos = corePos.getY() - Map.cellSide;
			var downPos = corePos.getY() + Map.cellSide;
			
			boolean l = true, r = true, u = true, d = true;
			
			String part = "_side";
			
			while(range-- > 0 && (l || r || u || d)) {
				if(range == 0) part = "_tail";
				if(l) {
					FXGL.spawn("explosion" + part, new SpawnData(leftPos, corePos.getY()).put("rotation", 180.));
					l = Map.walkable(leftPos, corePos.getY());
					leftPos -= Map.cellSide;
				}
				if(r) {
					FXGL.spawn("explosion" + part, new SpawnData(rightPos, corePos.getY()).put("rotation", 0.));
					r = Map.walkable(rightPos, corePos.getY());
					rightPos += Map.cellSide;
				}
				if(u) {
					FXGL.spawn("explosion" + part, new SpawnData(corePos.getX(), upPos).put("rotation", -90.));
					u = Map.walkable(corePos.getX(), upPos);
					upPos -= Map.cellSide;
				}
				if(d) {
					FXGL.spawn("explosion" + part, new SpawnData(corePos.getX(), downPos).put("rotation", 90.));
					d = Map.walkable(corePos.getX(), downPos);
					downPos += Map.cellSide;
				}
			}
			
			FXGL.inc("ammo", 1);
			entity.removeFromWorld();
		}, Duration.seconds(FXGL.getd("chargingTime")));
	}
	
	@Override
	public void onRemoved() {
		detonate.expire();
	}
}