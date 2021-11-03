package ru.gb.pool;

import ru.gb.base.SpritesPool;
import ru.gb.sprites.Bullet;

public class BulletPool extends SpritesPool<Bullet> {
    @Override
    protected Bullet newObject() {
        return new Bullet();
    }
}
