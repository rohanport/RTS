package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.GameObjectManager;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameobjects.GameObjectsObserver;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingBase;
import com.mygdx.game.skirmish.gameobjects.units.UnitBase;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.util.MapUtils;
import com.mygdx.game.skirmish.util.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 19-Sep-16.
 */
public class SelectionManager implements InputProcessor, GameObjectsObserver {
    private final SkirmishScreen screen;

    private List<Commandable> selection;
    private List<Commandable> newSelection;
    private Rectangle selector;
    private ShapeRenderer selectionRenderer;

    private boolean isSelecting;
    private SelectionInputState state;

    //----------- Getters and Setters -------------
    public List<Commandable> getSelection() {
        return selection;
    }
    public void setState(SelectionInputState state) {
        this.state = state;
    }
    //------------------------------------------

    public SelectionManager(SkirmishScreen screen) {
        this.screen = screen;

        selection = new ArrayList<>();
        newSelection = new ArrayList<>();
        selector = new Rectangle();
        selectionRenderer = new ShapeRenderer();
        state = SelectionInputState.NONE;
    }

    public void addToSelection(Commandable commandable) {
        selection.add(commandable);
    }
    public void setSelection(Collection<Commandable> commandables) {
        selection.clear();
        selection.addAll(commandables);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Settings.HotKeys.ATK:
                state = SelectionInputState.ATK;
                break;
            default:
                passKeyPressToSelection(keycode);
                break;
        }

        return false;
    }

    private void passKeyPressToSelection(int keycode) {
        boolean keyPressProccssed = false;
        Iterator<Commandable> selectionIterator = selection.iterator();
        while (!keyPressProccssed && selectionIterator.hasNext()) {
            Commandable commandable = selectionIterator.next();
            keyPressProccssed = commandable.processKeyStroke(isChaining(), keycode);
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            switch (state) {
                case ATK:
                    handleAtkCommand(screenX, screenY);
                    state = SelectionInputState.NONE;
                    break;
                case BUILD:
                    handleBuildCommand(screenX, screenY);
                    state = SelectionInputState.NONE;
                    break;
                default:
                    handleSelectionStart(screenX, screenY);
                    break;
            }
        } else if (button == 1) {
            handleRightClick(screenX, screenY);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            handleSelectionEnd();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        handleSelectionDrag(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private void handleSelectionStart(float screenX, float screenY) {
        selector.setX(screenX);
        selector.setY(screenY);
        isSelecting = true;

        newSelection.clear();
        screen.getGameObjectManagers().forEach(gameObjectManager ->
                newSelection.addAll(gameObjectManager.getIntersectingCommandables(
                        MapUtils.screenCoords2PxCoords(
                                screen.getCam(),
                                screenX,
                                screenY
                        ))
                )
        );
    }

    private void handleSelectionEnd() {
        if (newSelection.size() > 0) {
            selection.clear();
            selection.addAll(newSelection);
            newSelection.clear();
        }

        isSelecting = false;
    }

    private void handleSelectionDrag(float screenX, float screenY) {
        if (isSelecting) {
            selector.setWidth(screenX - selector.getX());
            selector.setHeight(screenY - selector.getY());

            Polygon selectionPolygon = SelectionUtils.getSelectingPolygon(screen.getCam(), selector);

            newSelection.clear();
            screen.getGameObjectManagers().forEach(gameObjectManager ->
                    newSelection.addAll(gameObjectManager.getIntersectingCommandables(selectionPolygon)));
        }
    }

    private void handleRightClick(float screenX, float screenY) {
        // Checking if a GameObject has been clicked on
        for (GameObjectManager gameObjectManager : screen.getGameObjectManagers()) {
            List<GameObject> intersectingObjects = gameObjectManager.getIntersecting(MapUtils.screenCoords2PxCoords(
                    screen.getCam(),
                    screenX,
                    screenY
            ));
            if (intersectingObjects.size() > 0) {
                //If a game object is clicked on, pass to each commandable for them handle
                selection.forEach( commandable -> commandable.processRightClickOn(isChaining(), intersectingObjects.get(0)));
                return;
            }
        }

        List<Commandable> moveables = selection.stream().filter(Commandable::isMoveable).collect(Collectors.toList());
        List<Commandable> nonMoveables = selection.stream().filter(commandable -> !commandable.isMoveable()).collect(Collectors.toList());

        Vector2 mapCords = MapUtils.screenCoords2NodeCoords(screen.getCam(), screenX, screenY);
        int mapX = Math.round(mapCords.x);
        int mapY = Math.round(mapCords.y);

        // diffCords are used to determine whether the units should move as a pack and keep formation or try and converge on one location
        Vector2 diffCords = SelectionUtils.getDiffFromSelectionCenterAndClick(mapX, mapY, moveables);

        if (diffCords == null) {
            //Diff cords are null if the click is inside the contaning rectangle of all the movables
            //This means all moveables should try and converge on this one location
            moveables.forEach(moveable -> moveable.processMoveCommand(isChaining(), mapX, mapY));
        } else {
            //Click is outside of the containing rectangle, so movables should try and move in formation towards that point
            moveables.forEach(moveable -> moveable.processMoveCommand(
                    isChaining(),
                    moveable.getMapCenterX() + (int) diffCords.x,
                    moveable.getMapCenterY() + (int) diffCords.y)
            );
        }

        //For non moveables, handle the right click by themselves
        nonMoveables.forEach(nonMoveable -> nonMoveable.processRightClick(isChaining(), mapX, mapY));
    }

    private void handleAtkCommand(int screenX, int screenY) {
        List<BuildingBase> targetBuildings = screen.getBuildingManager().getIntersecting(MapUtils.screenCoords2PxCoords(
                screen.getCam(),
                screenX,
                screenY
        ));

        if (targetBuildings.size() > 0) {
            int targetID =  targetBuildings.get(0).getID();
            selection.forEach(commandable -> commandable.processAtkCommand(isChaining(), targetID));
            return;
        }

        List<UnitBase> targetedUnits = screen.getUnitManager().getIntersecting(MapUtils.screenCoords2PxCoords(
                screen.getCam(),
                screenX,
                screenY
        ));

        if (targetedUnits.size() > 0) {
            int targetID =  targetedUnits.get(0).getID();
            selection.forEach(commandable -> commandable.processAtkCommand(isChaining(), targetID));
            return;
        }

        handleAtkMoveCommand(screenX, screenY);
    }

    private void handleAtkMoveCommand(int screenX, int screenY) {
        List<Commandable> moveables = selection.stream().filter(Commandable::isMoveable).collect(Collectors.toList());
        Vector2 mapCords = MapUtils.screenCoords2NodeCoords(screen.getCam(), screenX, screenY);
        int mapX = Math.round(mapCords.x);
        int mapY = Math.round(mapCords.y);

        Vector2 diffCords = SelectionUtils.getDiffFromSelectionCenterAndClick(mapX, mapY, moveables);

        if (diffCords == null) {
            moveables.forEach(moveable -> moveable.processAtkMoveCommand(isChaining(), mapX, mapY));
        } else {
            moveables.forEach(moveable -> moveable.processAtkMoveCommand(
                    isChaining(),
                    moveable.getMapCenterX() + (int) diffCords.x,
                    moveable.getMapCenterY() + (int) diffCords.y)
            );
        }
    }

    private void handleBuildCommand(int screenX, int screenY) {
        Vector2 buildDestCoords = MapUtils.screenCoords2NodeCoords(screen.getCam(), screenX, screenY);

        boolean commandProccessed = false;
        int i = 0;
        while (!commandProccessed && i < selection.size()) {
            commandProccessed = selection.get(i).processBuildCommand(isChaining(), Math.round(buildDestCoords.x), Math.round(buildDestCoords.y));
        }
    }

    public void renderSelectionMarkers(Camera cam) {
        selectionRenderer.setProjectionMatrix(cam.combined);
        selectionRenderer.begin(ShapeRenderer.ShapeType.Line);
        selectionRenderer.setColor(Color.WHITE);
        for (Commandable unit : selection) {
            unit.renderSelectionMarker(selectionRenderer);
        }
        selectionRenderer.end();
    }

    @Override
    public void notify(GameObject gameObject, Notification notification) {
        if (!(gameObject.getGameObjectType() == GameObjectType.UNIT ||
                gameObject.getGameObjectType() == GameObjectType.BUILDING)) {
            return;
        }

        switch (notification) {
            case CREATE:
                //Do nothing
                break;
            case DESTROY:
                selection.remove(gameObject);
                newSelection.remove(gameObject);
                break;
            default:
                throw new RuntimeException("Unknown notification " + notification);
        }
    }

    private boolean isChaining() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }
}
