package com.week1.game.Model.Entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.week1.game.Model.World.GameWorld;

/*
 * Represents a clickable entity.
 */
public interface Clickable {

  Clickable NULL = new Clickable() {
    @Override
    public boolean intersects(Ray ray, Vector3 intersection) {
      return false;
    }

    @Override
    public void setSelected(boolean selected) {}

    @Override
    public void setHovered(boolean hovered) {

    }

    @Override
    public boolean visible() {
      return false;
    }

    @Override
    public <T> T accept(ClickableVisitor<T> clickableVisitor) {
      return clickableVisitor.acceptNull();
    }
  };

  interface ClickableVisitor<T> {
    T acceptUnit(Unit unit);
    T acceptBlock(ClickableBlock block);
    T acceptCrystal(Crystal crystal);
    T acceptTower(Tower t);
    T acceptNull();
    // A Visitor that does nothing.
    ClickableVisitor<Void> EMPTY = new ClickableVisitor() {
        @Override
        public Void acceptUnit(Unit unit) {
            return null;
        }

        @Override
        public Void acceptBlock(ClickableBlock block) {
            return null;
        }

        @Override
        public Void acceptCrystal(Crystal crystal) {
            return null;
        }

        @Override
        public Void acceptTower(Tower t) {
            return null;
        }

        @Override
        public Void acceptNull() {
            return null;
        }
    };
  }

  class ClickableBlock implements Clickable {

      Vector3 closestCoords;
      private BoundingBox boundingBox;
      public int x;
      public int y;
      public int z;
      GameWorld world; // This is dangerous, but probably fine :^)

      public ClickableBlock(BoundingBox box, Vector3 closestCoords, GameWorld world) {
          this.boundingBox = new BoundingBox(box);
          this.closestCoords = closestCoords;
          this.x = (int) closestCoords.x;
          this.y = (int) closestCoords.y;
          this.z = (int) closestCoords.z;
          this.world = world;
      }

      @Override
      public boolean intersects(Ray ray, Vector3 intersection) {
        return Intersector.intersectRayBounds(ray, boundingBox, intersection);
      }

      @Override
      public void setSelected(boolean selected) {
        world.setBlockSelected(x, y, z, selected);
      }

      @Override
      public void setHovered(boolean hovered) {
        world.setBlockHovered(x, y, z, hovered);
      }

      @Override
      public boolean visible() {
        return true; // For the purposes of the ClickOracle, all blocks will be "visible".
      }

      @Override
      public <T> T accept(ClickableVisitor<T> clickableVisitor) {
        return clickableVisitor.acceptBlock(this);
      }
    }

  /*
   * Whether or not the given ray intersects the entity in 3D space.
   * If it does, will update the provided Vector3 to reprsent the intersection point.
   */
  boolean intersects(Ray ray, Vector3 intersection);

  /*
   * Visually indicates that the clickable has been selected.
   */
  void setSelected(boolean selected);

  /*
   * Visually indicates that the clickable is hovered over.
   */
  void setHovered(boolean hovered);

  /* Whether or not the entity is visible through fog of war. */
  boolean visible();

  <T> T accept(ClickableVisitor<T> clickableVisitor);
}

