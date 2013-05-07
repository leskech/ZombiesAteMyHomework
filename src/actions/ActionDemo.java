/*
 * Copyright (c) 2008-2012, Matthias Mann
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package actions;

import de.matthiasmann.twl.ActionMap.Action;
import de.matthiasmann.twl.FPSCounter;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import test.TestUtils;

public class ActionDemo extends Widget {

    private final FPSCounter fpsCounter;
    private final Label instructions;
    private final Label result;
    public boolean quit;
    public boolean start = false;

    @SuppressWarnings("LeakingThisInConstructor")
    public ActionDemo() {
        fpsCounter = new FPSCounter();
        add(fpsCounter);

        instructions = new Label();
        instructions.setTheme("instructions");
        instructions.setText("ZOMBIES ATE MY HOMEWORK -- PRESS SPACE TO DIE");
        add(instructions);
        
        result = new Label();
        result.setTheme("result");
        add(result);
        
        getOrCreateActionMap().addMapping(this);
        
        setCanAcceptKeyboardFocus(true);
    }
    
    @Action
    public void start() {
        result.setText("Starting game...");
        start=true;
    }
    
    @Override
    protected void layout() {
        // instructions are near the top 
        instructions.setSize(getInnerWidth(), instructions.getPreferredHeight());
        instructions.setPosition(getInnerX(), getInnerY() + 20);
        
        // result is in the screen center
        result.setSize(getInnerWidth(), result.getPreferredHeight());
        result.setPosition(getInnerX(), getInnerY() + (getInnerHeight() - result.getHeight())/2);
        
        // fpsCounter is bottom right
        fpsCounter.adjustSize();
        fpsCounter.setPosition(
                getInnerWidth() - fpsCounter.getWidth(),
                getInnerHeight() - fpsCounter.getHeight());
    }

}
