package com.week1.game.Model.Components;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.OutputPath;

/*
 * Component for entities with pathing toward a goal.
 */
public class PathComponent extends AComponent {
    public Vector3 goal = new Vector3();
    public OutputPath path;
}
