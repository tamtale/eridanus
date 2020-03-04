package com.week1.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Util3D {

    public static Util3D ONLY = new Util3D();

    private ModelBuilder builder = new ModelBuilder();

    public Model createBox(float width, float height, float depth, Color color) {
        return builder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(color)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }
}
