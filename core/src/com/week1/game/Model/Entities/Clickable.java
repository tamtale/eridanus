package com.week1.game.Model.Entities;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

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
    public <T> T accept(ClickableVisitor<T> clickableVisitor) {
      return clickableVisitor.acceptNull();
    }
  };

  interface ClickableVisitor<T> {
    T acceptUnit(Unit unit);
    T acceptBlockLocation(Vector3 vector);
    T acceptCrystal(Crystal crystal);
    T acceptNull();
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

  <T> T accept(ClickableVisitor<T> clickableVisitor);
}

