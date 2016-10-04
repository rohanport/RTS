package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingBase;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameobjects.GameObjectsObserver;
import com.mygdx.game.skirmish.gameobjects.units.UnitBase;
import com.mygdx.game.skirmish.util.MapUtils;
import com.mygdx.game.skirmish.util.Settings;

import java.util.ArrayList;
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

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Settings.HotKeys.ATK:
                state = SelectionInputState.ATK;
                break;
            default:
                boolean keyPressProccssed = false;
                Iterator<Commandable> selectionIterator = selection.iterator();
                while (!keyPressProccssed && selectionIterator.hasNext()) {
                    Commandable commandable = selectionIterator.next();
                    keyPressProccssed = commandable.processKeyStroke(keycode);
                }
                break;
        }

        return false;
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
        newSelection.addAll(screen.getUnitManager().getIntersectingUnits(MapUtils.screenCoords2MapCoords(
                screen.getCam(),
                screenX,
                screenY
        )));
        newSelection.addAll(screen.getBuildingManager().getIntersectingBuildings(MapUtils.screenCoords2MapCoords(
                screen.getCam(),
                screenX,
                screenY
        )));
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

            Vector2 mapA = MapUtils.screenCoords2MapCoords(screen.getCam(), selector.getX(), selector.getY());
            Vector2 mapB = MapUtils.screenCoords2MapCoords(screen.getCam(), selector.getX() + selector.getWidth(), selector.getY());
            Vector2 mapC = MapUtils.screenCoords2MapCoords(screen.getCam(), selector.getX() + selector.getWidth(), selector.getY() + selector.getHeight());
            Vector2 mapD = MapUtils.screenCoords2MapCoords(screen.getCam(), selector.getX(), selector.getY() + selector.getHeight());

            Polygon selectionPolygon = new Polygon(new float[]{mapA.x, mapA.y,
                    mapB.x, mapB.y,
                    mapC.x, mapC.y,
                    mapD.x, mapD.y
            });

            List<UnitBase> intersectingUnits = screen.getUnitManager().getIntersectingUnits(selectionPolygon);
            List<BuildingBase> intersectingBuildings = screen.getBuildingManager().getIntersectingBuildings(selectionPolygon);

            newSelection.clear();
            newSelection.addAll(intersectingUnits);
            newSelection.addAll(intersectingBuildings);
        }
    }

    private void handleRightClick(float screenX, float screenY) {
        Vector2 mapCords = MapUtils.screenCoords2NodeCoords(screen.getCam(), screenX, screenY);
        int mapX = Math.round(mapCords.x);
        int mapY = Math.round(mapCords.y);

        List<Commandable> moveables = selection.stream().filter(Commandable::isMoveable).collect(Collectors.toList());

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Commandable moveable : moveables) {
            if (moveable.getMapCenterX() < minX) {
                minX = moveable.getMapCenterX();
            }

            if (moveable.getMapCenterX() > maxX) {
                maxX = moveable.getMapCenterX();
            }

            if (moveable.getMapCenterY() < minY) {
                minY = moveable.getMapCenterY();
            }

            if (moveable.getMapCenterY() > maxY) {
                maxY = moveable.getMapCenterY();
            }
        }

        if (minX <= mapX && mapX <= maxX &&
                minY <= mapY && mapY <= maxY) {
            for (Commandable moveable : moveables) {
                moveable.processRightClick(mapX, mapY);
            }
        } else {
            int centerX = (minX + maxX) / 2;
            int centerY = (minY + maxY) / 2;
            int diffX = mapX - centerX;
            int diffY = mapY - centerY;

            for (Commandable moveable : moveables) {
                moveable.processRightClick(moveable.getMapCenterX() + diffX, moveable.getMapCenterY() + diffY);
            }
        }
    }

    private void handleAtkCommand(int screenX, int screenY) {
        List<BuildingBase> targetBuildings = screen.getBuildingManager().getIntersectingBuildings(MapUtils.screenCoords2MapCoords(
                screen.getCam(),
                screenX,
                screenY
        ));

        if (targetBuildings.size() > 0) {
            int targetID =  targetBuildings.get(0).getID();
            selection.forEach(commandable -> commandable.processAtkCommand(targetID));
            return;
        }

        List<UnitBase> targetedUnits = screen.getUnitManager().getIntersectingUnits(MapUtils.screenCoords2MapCoords(
                screen.getCam(),
                screenX,
                screenY
        ));

        if (targetedUnits.size() > 0) {
            int targetID =  targetedUnits.get(0).getID();
            selection.forEach(commandable -> commandable.processAtkCommand(targetID));
            return;
        }
    }

    private void handleBuildCommand(int screenX, int screenY) {
        Vector2 buildDestCoords = MapUtils.screenCoords2NodeCoords(screen.getCam(), screenX, screenY);

        boolean commandProccessed = false;
        int i = 0;
        while (!commandProccessed && i < selection.size()) {
            commandProccessed = selection.get(i).processBuildCommand(Math.round(buildDestCoords.x), Math.round(buildDestCoords.y));
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
}
