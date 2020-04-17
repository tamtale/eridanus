package com.week1.game.Model.Components;


import static com.week1.game.Model.StatsConfig.ENABLE_FOG;

public class VisibleComponent extends AComponent {
    private boolean visible;
    public VisibleComponent(boolean visible) {
        this.visible = visible;
    }
    
    public boolean visible() {
        return this.visible || !ENABLE_FOG;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
