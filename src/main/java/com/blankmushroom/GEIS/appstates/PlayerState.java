package com.blankmushroom.GEIS.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
/**
 *
 * @author admin
 */
public class PlayerState extends AbstractAppState {
    private boolean isJumping=false;
    private float speed;
    private BulletAppState bulletAppState = new BulletAppState();
    private BetterCharacterControl characterControl;
    private final Application app;
    private final Node mainRootNode;
    private final Node localRootNode = new Node("Player");
    private final AssetManager assetManager;
    private final InputManager inputManager;
    private Vector3f PMVector= new Vector3f(0,0,0);
    public PlayerState(SimpleApplication app, Node mainRootNode,float speed){
        this.app=app;
        this.mainRootNode = mainRootNode;
        mainRootNode.attachChild(localRootNode);
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        this.speed=speed;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        stateManager.attach(bulletAppState);
        characterControl = new BetterCharacterControl(1,2,1);
        inputManager.setCursorVisible(true);
        Node plgeoNode = new Node("PlayerGeometry");
        //Geometry plgeom = new Geometry("PlayerBody",new Box(1,2,1));
        Spatial plgeom = assetManager.loadModel("Models/Person/person.j3o");
        plgeom.setLocalRotation(new Quaternion().fromAngles(0,FastMath.PI,0));
        plgeom.setLocalTranslation(0,-2,0);
        Geometry handrgeom = new Geometry("PlayerRightHand", new Box(0.25f,0.25f,0.25f));
        Geometry handlgeom = new Geometry("PlayerLeftHand", new Box(0.25f,0.25f,0.25f));
        Material handmat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Material plmat = assetManager.loadMaterial("Materials/person.j3m");
        handmat.setColor("Color",ColorRGBA.Blue);
        handrgeom.setLocalTranslation(1.5f,-0.5f,0);
        handlgeom.setLocalTranslation(-1.5f,-0.5f,0);
        plgeom.setMaterial(plmat);
        handrgeom.setMaterial(handmat);
        handlgeom.setMaterial(handmat);
        //plgeoNode.attachChild(handlgeom);
        //plgeoNode.attachChild(handrgeom);
        plgeoNode.attachChild(plgeom);
        localRootNode.addControl(characterControl);
        characterControl.setJumpForce(new Vector3f(0,1,0));
        characterControl.warp(new Vector3f(0,1f,0));
        bulletAppState.getPhysicsSpace().add(characterControl);
        bulletAppState.getPhysicsSpace().addAll(localRootNode);
        for (Spatial sp:mainRootNode.getChildren()) {
            bulletAppState.getPhysicsSpace().addAll(sp);
        }
        localRootNode.attachChild(plgeoNode);
        plgeoNode.setLocalTranslation(0,2,0);
        CameraNode camNode = new CameraNode("CamNode", app.getCamera());
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        localRootNode.attachChild(camNode);
        camNode.setLocalTranslation(new Vector3f(0, 10, 7.5f));
        camNode.lookAt(localRootNode.getLocalTranslation(), Vector3f.UNIT_Y);
        camNode.rotate(-FastMath.PI*0.025f,0,0);
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("LPunch", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("RPunch", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "Jump");
        inputManager.addListener(actionListener, "Forward");
        inputManager.addListener(actionListener, "Backward");
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener,"LPunch");
        inputManager.addListener(actionListener,"RPunch");
    }
    public float punchCharge = 0;
    private Boolean LisCharging=false;
    private Boolean RisCharging=false;
    public Boolean isBlocking=false;
    public Boolean isStriking=false;
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name=="Forward"&&keyPressed){
                PMVector=PMVector.add(new Vector3f(0,0,-1));
            }
            if(name=="Forward"&&!keyPressed){
                PMVector=PMVector.add(new Vector3f(0,0,1));
            }
            if(name=="Backward"&&keyPressed){
                PMVector=PMVector.add(new Vector3f(0,0,1));
            }
            if(name=="Backward"&&!keyPressed){
                PMVector=PMVector.add(new Vector3f(0,0,-1));
            }
            if(name=="Left"&&keyPressed){
                PMVector=PMVector.add(new Vector3f(1,0,0));
            }
            if(name=="Left"&&!keyPressed){
                PMVector=PMVector.add(new Vector3f(-1,0,0));
            }
            if(name=="Right"&&keyPressed){
                PMVector=PMVector.add(new Vector3f(-1,0,0));
            }
            if(name=="Right"&&!keyPressed){
                PMVector=PMVector.add(new Vector3f(1,0,0));
            }
            if(name=="Jump"){
                isJumping=!isJumping;
            }
            if((name=="LPunch"&&RisCharging)||(name=="RPunch"&&LisCharging)&&keyPressed){
                RisCharging=false;
                LisCharging=false;
                isBlocking=true;
                punchCharge=0;
            } else if(name=="LPunch"&&keyPressed){
                LisCharging=true;
                punchCharge=0;
            } else if(name=="RPunch"&&keyPressed){
                RisCharging=true;
                punchCharge=0;
            } else if(isBlocking&&(name=="LPunch"||name=="RPunch")&&!keyPressed){
                isBlocking=false;
            } else if((name=="LPunch"||name=="RPunch")&&!keyPressed){
                LisCharging = false;
                RisCharging = false;
                isStriking = true;
            }
        }
    };

    @Override
    public void cleanup() {
        mainRootNode.detachChild(localRootNode);
        super.cleanup();
    }
    @Override
    public void update(float tpf) {
        Node player =(Node)mainRootNode.getChild("Player");
        Vector3f lookvector =new Vector3f(inputManager.getCursorPosition().x-(app.getCamera().getWidth()*0.5f),player.getLocalTranslation().y,(app.getCamera().getHeight()*0.5f)-inputManager.getCursorPosition().y+120);
        Node plgeo =(Node) player.getChild("PlayerGeometry");
        plgeo.lookAt(lookvector, Vector3f.UNIT_Y);
        if(isJumping){
            characterControl.jump();
        }
        if(isBlocking){
        }
        if(!characterControl.isOnGround()){
            float jsp=speed*2f;
            characterControl.setWalkDirection(PMVector.normalize().mult(jsp));
        } else {
            characterControl.setWalkDirection(PMVector.normalize().mult(speed));
        }
    }
}