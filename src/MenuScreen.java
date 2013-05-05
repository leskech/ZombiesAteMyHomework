import java.awt.BorderLayout;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import com.sun.opengl.util.FPSAnimator;

/**
 * A minimal JOGL demo.
 * 
 * @author <a href="mailto:kain@land-of-kain.de">Kai Ruhl</a>
 * @since 26 Feb 2009
 */
public class MenuScreen extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;
    private int fps = 60;
    private FPSAnimator animator;

    public MenuScreen(int width, int height) {
        addGLEventListener(this);
        JFrame frame = new JFrame("Game Demo");
	    frame.getContentPane().add(this, BorderLayout.CENTER);
	    frame.setSize(800, 500);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	    this.requestFocus();
    }
    
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL(drawable.getGL()));
        final GL gl = drawable.getGL();

        // Enable z- (depth) buffer for hidden surface removal. 
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        // Enable smooth shading.
        gl.glShadeModel(GL.GL_SMOOTH);

        // Define "clear" color.
        gl.glClearColor(0f, 0f, 0f, 0f);

        // We want a nice perspective.
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        // Start animator.
        animator = new FPSAnimator(this, fps);
        animator.start();
    }
    
    public void display(GLAutoDrawable drawable) {
        if (!animator.isAnimating()) {
            return;
        }
        final GL gl = drawable.getGL();

        // Clear screen.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        throw new UnsupportedOperationException("Changing display is not supported.");
    }

}
