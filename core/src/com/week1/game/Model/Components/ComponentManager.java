package com.week1.game.Model.Components;

public abstract class ComponentManager<TComponentType extends AComponent> {

    /* Return a new component that can be used by the entity with entID.*/
    abstract TComponentType newComponent(int entID);

    /* Remove the component from use, allowing it to be reused for a different entity.*/
    abstract boolean removeComponent(int entID);

}
