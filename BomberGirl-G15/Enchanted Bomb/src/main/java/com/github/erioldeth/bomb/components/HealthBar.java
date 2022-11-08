package com.github.erioldeth.bomb.components;

import com.almasb.fxgl.dsl.components.view.GenericBarViewComponent;
import com.almasb.fxgl.entity.component.Component;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

public class HealthBar
		extends Component {
	private final DoubleProperty health = new SimpleDoubleProperty();
	private final Color barColor;
	
	public HealthBar(Color barColor) {
		this.barColor = barColor;
	}
	
	@Override
	public void onAdded() {
		double barWidth = 30, barHeight = 10;
		var ePosComp = entity.getComponent(Position.class);
		var eLocalCenter = ePosComp.localCenter();
		health.bindBidirectional(entity.getProperties().intProperty("hitPoint"));
		
		var barX = eLocalCenter.getX() - barWidth / 2;
		var barY = eLocalCenter.getY() - ePosComp.height() / 2 - barHeight - 2.5;
		
		var healthBar = new GenericBarViewComponent(barX, barY, barColor, health, barWidth, barHeight);
		healthBar.getBar().setTraceFill(Color.TRANSPARENT);
		entity.addComponent(healthBar);
	}
}