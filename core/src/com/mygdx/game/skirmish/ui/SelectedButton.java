package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameplay.Commandable;

import java.util.Collections;

/**
 * Created by paddlefish on 09-Oct-16.
 */
public class SelectedButton extends Button {

    private Commandable selectedItem;

    //---------- Getters and Setters------------
    public Commandable getSelectedItem() {
        return selectedItem;
    }
    public void setSelectedItem(Commandable selectedItem) {
        this.selectedItem = selectedItem;
        if (selectedItem == null) {
            setStyle(new ButtonStyle());
        } else {
            SpriteDrawable portrait = new SpriteDrawable(selectedItem.getPortrait());
            setStyle(new Button.ButtonStyle(portrait, portrait, portrait));
        }
    }
    //------------------------------------

    public SelectedButton(SkirmishScreen screen) {
        super(new ButtonStyle());

        addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedItem != null) {
                    screen.getSelectionManager().setSelection(Collections.singleton(selectedItem));
                }
            }
        });
    }
}
