package com.github.fecrol.yasm.comon.entities.interfaces;

public interface UpdatableEntity<T> {

    void updateUsing(T updatedEntity);
}
