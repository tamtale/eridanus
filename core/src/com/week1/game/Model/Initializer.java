package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/*
 * Static asset initializer to ensure that all the models
 * are ready for use by the time the user has loaded the game.
 */
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.week1.game.Model.Entities.Crystal;

import java.awt.*;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class Initializer {

    private static ModelBuilder BUILDER = new ModelBuilder();
    public static AssetManager assetManager = new AssetManager();
    
    public static Model waterBlock;
    public static Model earthBlock;
    public static Model fireBlock;
    public static Model moonStone;
    public static Model spaceObsidian;
    public static Model spaceGold;
    public static Model easterEgg;
    public static Model spawner;

    // Used for nametags
    public static BitmapFont.BitmapFontData bmfData;
    public static Pixmap fontPixmap;
    
    public static void init() {
        waterBlock = fileBasedModel("water2.png");
        earthBlock = fileBasedModel("earth3.png");
        fireBlock = fileBasedModel("fire2.png");
        moonStone = fileBasedModel("moonstone3.png");
        spaceObsidian = fileBasedModel("space_obsidian2.png");
        spaceGold = fileBasedModel("gold2.png");
        easterEgg = fileBasedModel("cat_boi.png");
        spawner = fileBasedModel("spawner2.png");

        bmfData = new BitmapFont().getData();
        fontPixmap = new Pixmap(Gdx.files.internal(bmfData.getImagePath(0)));
    }
    
    private static Model fileBasedModel(String fileName) {
        return BUILDER.createBox(1f, 1f, 1f,
                new Material(TextureAttribute.createDiffuse(new Texture(fileName))), 
                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
    }
}
