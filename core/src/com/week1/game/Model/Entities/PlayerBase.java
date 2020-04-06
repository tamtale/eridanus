package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.utils.ScreenUtils;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.TowerBuilder.TowerDetails;

public class PlayerBase extends Tower {

    Decal nameDisplay;
//    Matrix4 textTransform = new Matrix4();
//    Matrix4 oldMatrix;
    String stringLabel = "default";

    Texture texture = null;
    Pixmap pm = null;// pixmap to write on
    static BitmapFont font;

//    private static Vector3 tmpVec3 = new Vector3();
//    private static Vector3 textPosition = new Vector3();

    public PlayerBase(float x, float y, float z, TowerDetails towerDetails, int playerID, int towerType) {
        super(x, y, z, towerDetails, playerID, towerType);
//        init();
    }

    public static void makeFonts() {
        font = new BitmapFont();
    }

    // https://stackoverflow.com/questions/17250051/libgdx-write-text-on-texture
    public void init() {
        if(texture == null) newTexture(Color.BLUE, Color.WHITE);
//        TextureAttribute ta_tex     = TextureAttribute.createDiffuse(texture);
//        final Material material_box = new Material(ta_tex, ColorAttribute.createSpecular(1, 1, 1, 1),
//                FloatAttribute.createShininess(8f));
//        final long attributes1      = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
//        final Model boxModel = modelBuilder.createBox(1f, 1f, 1f, material_box, attributes1);

    }
    //---------------------------
    public Texture newTexture(Color fg_color, Color bg_color)
    {
        Pixmap pm = renderTest( fg_color, bg_color );
        texture = new Texture(pm);//***here's your new dynamic texture***
//        disposables.add(texture);//store the texture
        return texture;
    }
    //---------------------------
    public Pixmap renderTest(Color fg_color, Color bg_color)
    {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        SpriteBatch spriteBatch = new SpriteBatch();

        float m_fboScaler = 1f;
        FrameBuffer m_fbo = new FrameBuffer(Pixmap.Format.RGB565, (int) (width * m_fboScaler), (int) (height * m_fboScaler), false);
        m_fbo.begin();
        Gdx.gl.glClearColor(bg_color.r, bg_color.g, bg_color.b, bg_color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Matrix4 normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
        spriteBatch.setProjectionMatrix(normalProjection);

        spriteBatch.begin();
        spriteBatch.setColor(fg_color);
        //do some drawing ***here's where you draw your dynamic texture***
        font.draw(spriteBatch, "THIS IS TEST",  width/4, height - 20);//multi-line draw

        spriteBatch.end();//finish write to buffer

        pm = ScreenUtils.getFrameBufferPixmap(0, 0, (int) width, (int) height);//write frame buffer to Pixmap

        m_fbo.end();
        //      pm.dispose();
        //      flipped.dispose();
        //      tx.dispose();
        m_fbo.dispose();
        m_fbo = null;
        spriteBatch.dispose();
        //      return texture;
        return pm;
    }

    public void drawBaseName(RenderConfig config) {
/*
        textTransform.idt().scl(4f).rotate(0.1f, 0.1f, 1, 45).translate(0, 2, 25f);
//Probably need to scale it down. Scale before moving or rotating.


//        textPosition.set(0, 0, 0);
//        textPosition.add(0,0,2.5f);
//        tmpVec3.set(config.getCam().position).sub(textPosition);

//        textTransform.setToTranslation(textPosition).rotate(Vector3.Z, tmpVec3);

        SpriteBatch batch = config.getBatch();
        oldMatrix = batch.getProjectionMatrix();
//        batch.setProjectionMatrix(textTransform.set(config.getCam().combined).mul(textTransform));
//        batch.setProjectionMatrix(textTransform);

        gameFont.draw(batch, stringLabel, 0, 0);
        batch.draw(stringTexture, 0, 0);

        batch.flush();
//        DecalBatch batch = config.getDecalBatch();

//        // Orient the decal
//        Plane p = config.getCam().frustum.planes[0];
//        Intersector.intersectLinePlane(
//                unitPosition.x, unitPosition.y, unitPosition.z,
//                x + p.normal.x,y + p.normal.y, z + p.normal.z,
//                p, lookAt);
//        nameDisplay.lookAt(lookAt, config.getCam().up);
//
//        // Set the position of the decal
//        this.getDisplayPos(unitPosition);
//        unitPosition.add(0,0,2.5f);
//        nameDisplay.setPosition(unitPosition);

        // Update decal texture (color and size)
//        nameDisplay.setTextureRegion(HealthBar.getHealthBarTexture(this.getCurrentHealth(), this.getMaxHealth()));
//        nameDisplay.setWidth(maxWidth * this.getCurrentHealth() / this.getMaxHealth());

        // Add the decal for drawing
//        batch.add(nameDisplay);
//        batch.flush();
*/

//        config.getBatch().draw(newTexture(Color.BLUE, Color.BROWN),400, 400);

        if (nameDisplay == null) {
            nameDisplay = Decal.newDecal(maxWidth, 0.1f, new TextureRegion(newTexture(Color.BLUE, Color.BROWN)));
            maxWidth = hpBarWidthFactor * (float)Math.log(this.getMaxHealth());
        }

        DecalBatch batch = config.getDecalBatch();

        // Orient the decal
        Plane p = config.getCam().frustum.planes[0];
        Intersector.intersectLinePlane(
                unitPosition.x, unitPosition.y, unitPosition.z,
                unitPosition.x + p.normal.x, unitPosition.y + p.normal.y, unitPosition.z + p.normal.z,
                p, lookAt);
        nameDisplay.lookAt(lookAt, config.getCam().up);

        // Set the position of the decal
        this.getDisplayPos(unitPosition);
        unitPosition.add(0,0,1.5f);
        nameDisplay.setPosition(unitPosition);

        // Update decal texture (color and size)
        nameDisplay.setTextureRegion(HealthBar.getHealthBarTexture(this.getCurrentHealth(), this.getMaxHealth()));
        nameDisplay.setWidth(maxWidth * this.getCurrentHealth() / this.getMaxHealth());

        // Add the decal for drawing
        batch.add(nameDisplay);
        batch.flush();




    }

    public void setStringLabel(String stringLabel) {
        this.stringLabel = stringLabel;
    }

    @Override
    public <T> T accept(DamageableVisitor<T> visitor) {
        return visitor.acceptBase(this);
    }

    @Override
    public String toString() {
        return "PlayerBase {" +
            "x=" + x +
            ", y=" + y +
            ", playerID=" + playerID +
            ", towerType=" + towerType +
            ", hp=" + hp +
            ", maxHp=" + maxHp +
            ", dmg=" + dmg +
            ", range=" + range +
            ", cost=" + cost +
            '}';
    }


}
