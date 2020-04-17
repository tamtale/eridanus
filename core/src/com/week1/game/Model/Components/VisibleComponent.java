package com.week1.game.Model.Components;


import com.week1.game.Model.Systems.FogSystem;

public class VisibleComponent extends AComponent {
    private boolean visible;
    public VisibleComponent(boolean visible) {
        this.visible = visible;
    }
    
    public boolean visible() {
        return this.visible || !FogSystem.fogEnabled();
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
