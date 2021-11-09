package ru.gb.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public abstract class SpritesPool<T extends Sprite> {

    protected final List<T> activeObjects = new ArrayList<>();
    protected final List<T> freeObjects = new ArrayList<>();

    protected abstract T newObject();

    public T obtain() {
        T object;
        if (freeObjects.isEmpty()) {
            object = newObject();
        } else {
            object = freeObjects.remove(freeObjects.size() -1);
        }
        activeObjects.add(object);
        System.out.println(getClass().getSimpleName() + " active/free: " + activeObjects.size() + " / " + freeObjects.size());
        return object;
    }

    public void freeAllActiveObjects() {
        freeObjects.addAll(activeObjects);
        activeObjects.clear();
    }

    public void updateActiveObjects(float delta) {
        for (T object: activeObjects) {
            if (!object.isDestroyed()) {
                object.update(delta);
            }
        }
    }

    public void drawActiveObjects(SpriteBatch batch) {
        for (T object: activeObjects) {
            if (!object.isDestroyed()) {
                object.draw(batch);
            }
        }
    }

    public void freeAllDestroyed() {
        for (int i = 0; i < activeObjects.size(); i++) {
            T object = activeObjects.get(i);
            if (object.isDestroyed()) {
                free(object);
                i--;
            }
        }
    }

    public List<T> getActiveObjects() {
        return activeObjects;
    }

    public void dispose() {
        activeObjects.clear();
        freeObjects.clear();
    }

    private void free(T object) {
        object.flushDestroyed();
        if (activeObjects.remove(object)) {
            freeObjects.add(object);
            System.out.println(getClass().getSimpleName() + " active/free: " + activeObjects.size() + " / " + freeObjects.size());
        }
    }
}
