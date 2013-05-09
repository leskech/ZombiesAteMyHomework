import de.matthiasmann.twl.ActionMap.Action;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Widget;

//uses menu.xml for params to create themed widget
public class Menu extends Widget {

    private final Label instructions;
    public boolean quit;
    public boolean start = false;

    
    //creates bg image in menu
    public Menu(GameEngine engine) {
        instructions = new Label();
        instructions.setTheme("instructions");
        instructions.setText(engine.getEnemiesKilled());
        add(instructions);
        
        getOrCreateActionMap().addMapping(this);
        
        setCanAcceptKeyboardFocus(true);
    }
    
    //handles menu keyboard interaction
    @Action
    public void start() {
        //result.setText("Starting game...");
        start=true;
    }
    
    @Override
    protected void layout() {
        instructions.setSize(getInnerWidth(), instructions.getPreferredHeight());
        instructions.setPosition(getInnerX(), getInnerY() + 20);
    }

}
