package com.blankmushroom.GEIS.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author admin
 */
public class TestLevelState extends AbstractAppState {
    private final Node mainRootNode;
    private final Node localrootNode = new Node("TestLevel");
    private final AssetManager assetManager;
    private Vector3f PMVector= new Vector3f(0,0,0);
    public TestLevelState(SimpleApplication app){
        mainRootNode = app.getRootNode();
        assetManager = app.getAssetManager();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        mainRootNode.attachChild(localrootNode);
        Geometry floor = new Geometry("Floor", new Quad(20, 20));
        floor.setLocalTranslation(-10,-2,10);
        floor.setLocalRotation(new Quaternion().fromAngles(-FastMath.PI*0.5f, 0,0));
        floor.setMaterial(assetManager.loadMaterial("Materials/placeholder.j3m"));
        floor.addControl(new RigidBodyControl(0));
        localrootNode.attachChild(floor);
        app.getStateManager().attach(new PlayerState((SimpleApplication) app, localrootNode,5));
    }


    @Override
    public void cleanup() {
        mainRootNode.detachChild(localrootNode);
        super.cleanup();
    }

    @Override
    public void update(float tpf) {
    }
}