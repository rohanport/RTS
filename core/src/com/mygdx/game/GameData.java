package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by paddlefish on 17-Sep-16.
 *
 * Utility class containing data for all game resources, eg. sprites, audio, etc.
 */
public class GameData {

    //--------- Menu -----------------------
    public Sprite title = new Sprite(new Texture(Gdx.files.internal("sprites/menu/title.png")));


    //--------- UI -----------------------
    public Sprite healthBar = new Sprite(new Texture(Gdx.files.internal("sprites/skirmish/ui/healthBar.png")));
    public Sprite selectionBackGround = new Sprite(new Texture(Gdx.files.internal("sprites/skirmish/ui/selectionTableBackground.png")));


    //--------- Map ------------------------
    public Sprite bgGrass01 = new Sprite(new Texture(Gdx.files.internal("sprites/skirmish/terrain/grass01.jpg")));


    //--------- Units ----------------------
    public Sprite soldier1 = new Sprite(new Texture(Gdx.files.internal("sprites/skirmish/units/soldier1_portrait2.png")));
    public Sprite soldier1Portrait = new Sprite(new Texture(Gdx.files.internal("sprites/skirmish/units/soldier1_portrait2.png")));

    //--------- Buildings ----------------------
    public Sprite building1Portrait = new Sprite(new Texture(Gdx.files.internal("sprites/skirmish/buildings/building1_portrait.png")));

    private static GameData instance;

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }
}
