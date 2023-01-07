package com.blankmushroom.GEIS;

import com.blankmushroom.GEIS.appstates.TestLevelState;
import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.blankmushroom.GEIS.appstates.*;
/**
 * This is the Main Class of your Game. It should boot up your game and do initial initialisation
 * Move your Logic into AppStates or Controls or other java classes
 */
public class GEIS extends SimpleApplication {

    public static void main(String[] args) {
        GEIS app = new GEIS();

        //app.setShowSettings(false); //Settings dialog not supported on mac
        app.start();
    }

    @Override
    public void simpleInitApp() {

        this.getStateManager().attach(new TestLevelState(this));
        //this.flyCam.setEnabled(false);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //this method will be called every game tick and can be used to make updates
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //add render code here (if any)
    }
}
