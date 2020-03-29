package com.week1.game.Model.Components;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.OutputPath;

public class PathComponent implements IComponent {
    public Vector3 goal = new Vector3();
    public OutputPath path;
}
