package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.mygdx.game.GameData;
import com.mygdx.game.skirmish.gameplay.Commandable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 09-Oct-16.
 */
public class SelectionTable {
    public static final int MAX_SELECTED_RENDERED = 16;

    private final Table table;
    private final List<Button> selectedButtons;



    //----------- Getters and Setters -------------
    public Table getTable() {
        return table;
    }

    //---------------------------------------------

    public SelectionTable() {
        table = new Table();
        selectedButtons = new ArrayList<>();

        table.setBackground(new SpriteDrawable(GameData.getInstance().selectionBackGround));
        table.setPosition(Gdx.graphics.getWidth() / 4f, 0f);
        table.setWidth(Gdx.graphics.getWidth()  / 2f);
        table.setHeight(Gdx.graphics.getHeight() / 4f);
        table.pad(20f, 20f, 20f, 20f);

        Button button;
        for (int i = 0; i < MAX_SELECTED_RENDERED; i++) {
            if (i == MAX_SELECTED_RENDERED / 2) {
                table.row();
            }
            button = new Button(new Button.ButtonStyle());
            table.add(button)
                    .width((table.getWidth() - table.getPadX()) / (MAX_SELECTED_RENDERED / 2f))
                    .height((table.getHeight() - table.getPadY()) / 2f);
            selectedButtons.add(button);
        }


    }

    public void setSelection(List<Commandable> selection) {
        for (int i = 0; i < MAX_SELECTED_RENDERED; i++) {
            if (i >= selection.size()) {
                // If selection isn't large enough to fill all buttons, set button to have empty portrait
                selectedButtons.get(i).setStyle(new Button.ButtonStyle());
            } else {
                SpriteDrawable portrait = new SpriteDrawable(selection.get(i).getPortrait());
                selectedButtons.get(i).setStyle(new Button.ButtonStyle(portrait, portrait, portrait));
            }
        }
    }
}
