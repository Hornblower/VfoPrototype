/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;


/**
 * Implements a decade digit within a multi-digit numeric display.
 * 
 * It is one digit wide and has the range of 0 to 9 which wraps around
 * when incremented or decremented past its range.  The model is
 * DecadeModel which takes care of the wrap around and passing on the
 * carry to adjacent decade digits.  It was originally developed/used for a 
 * JSpinner version of the display.
 * 
 * @author Coz
 */
public class DecadeDigit extends JLabel
        implements Accessible, FocusListener, MouseWheelListener, 
        MouseListener, PropertyChangeListener  {
    
    static protected boolean nastyShiftFocusFlag = false;
    DecadeModel model;
    VfoDisplayControl frameGroup;
    final Border WHITE_BORDER = BorderFactory.createLineBorder(Color.WHITE);
    final Border BLACK_BORDER = BorderFactory.createLineBorder(Color.BLACK);

    final static Font DIGITS_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 30);
    final static Color NON_ZERO_COLOR = new Color(0,192,0);
    final static Color ZERO_COLOR = new Color(0,64,0);
    protected float fontScale = 0.0f;
    final static String VALUE_CHANGE = "valueChange";
    private int value = 0;
    final private int digitDecade; // Used only for automatic testing.
   
    public DecadeDigit(VfoDisplayControl group, int decade, float scale) {
        super();
        fontScale = scale;
        frameGroup = group;
        digitDecade = decade;
        
        setModel(new DecadeModel(0,this));
        setToolTipText("Use arrows to change value and tranverse digits.");
        // Set foreground numeral color Green. Set background transparent.        
        setFocusable(true);
        setForeground(Color.GREEN);

        Color backTransparent = new Color(0,0,0,0);
        setBackground(backTransparent);   
        setOpaque(false);
        if (decade >= 0 && decade <= 9){
            setForeground(ZERO_COLOR);
            setText("0"); // Need a text digit to adjust font size.
        } else {
            // Original JRX scientific notation groups indicator.
            setText(".");
            setForeground(NON_ZERO_COLOR);
        }       
        addMouseWheelListener(this);
        addMouseListener(this);
        addFocusListener(this);
        addPropertyChangeListener(VALUE_CHANGE, this);
        addPropertyChangeListener(VALUE_CHANGE, frameGroup);
        setValue(this.getModel().getValue());
        requestFocus();       
    }
    
    
    public void addInputActions() {
        String incName = "Increment";
        InputMap inputMap = frameGroup.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), incName);
        ActionMap actionMap = frameGroup.getActionMap();
        actionMap.put(incName,  (new IncrementAction(incName, null, "Increment Value", KeyEvent.VK_UP)));
        
        String decName = "Decrement";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), decName);
        actionMap.put(decName,  (new DecrementAction(decName, null, "Deccrement Value", KeyEvent.VK_DOWN)));               
    }
    
    public float getFontScale() {
        return fontScale;
    }
    
    public void setFontScale( float scale ) {
        fontScale = scale;
    }
    private void setModel(DecadeModel digitModel) {
        model = digitModel;
    }

    public DecadeModel getModel() {
        return model;
    }
    
    public static String getName(int powerOf10){
        assert(powerOf10 < VfoDisplayControl.QUANTITY_DIGITS);
        String[] name = { "Ones","Tens","Hundreds","Thousands",
            "Ten thousands","hundred thousands","millions",
            "ten millions","Hundred millions","Billions"};
        String accName = name[powerOf10]+" digit ";
        return accName;
    }
    /**
     * Given the remainder of the frequency after mod 10 operation, dim the
     * digit when the remainder is zero.
     * 
     * @param v is remainder after mod 10 operation.
     */
    public void setBright(boolean v) {
        setForeground(v?NON_ZERO_COLOR:ZERO_COLOR);
    }

    // The only way for a user to set a decade is by linking DecadeDigits.
    private void setDecade(int powerOfTen) {
        DecadeModel myModel = (DecadeModel)getModel();
        myModel.setDecade(powerOfTen); 
    }
    
    /**
     * Method sets the accessible name, accessible description and the jcomponent
     * name based on the model's decade.  Decade zero is set by default upon
     * construction of the DecadeDigit. All other decades are set by linking
     * the models; @see method linkToNextHigherDecade().
     */
    public void setAccessibleInfo(){
//        DecadeModel myModel = (DecadeModel)getModel();
//        int powerOfTen = myModel.getDecade();
//        AccessibleContext ftfContext = this.getAccessibleContext();
//        String name = getName(powerOfTen);
//        ftfContext.setAccessibleName(name);
//        setName(name);  // Technically, this is not accessible info.
//        ftfContext.setAccessibleDescription(
//            "Up and down arrows change value; Left and right arrows traverse digits.");
    }
    
    // Start linking DecadeDigits with the lowest(rightMost) digit.
    private void linkToNextHigherDecade(DecadeDigit higherDecadeDigit) {
        higherDecadeDigit.setDecade(getDecade() + 1);
        DecadeModel low = (DecadeModel)this.getModel();
        DecadeModel high = (DecadeModel)higherDecadeDigit.getModel();
        low.setLinkedModel(high);
    }
    
    public int getDecade() {
        return ((DecadeModel) getModel()).getDecade();
    }
    
    public static void linkAllDigits(ArrayList<DecadeDigit> freqDigits, int arraySize) {
        int limit = arraySize - 1;
        for (int count = 0 ; count < limit; count ++)
            freqDigits.get(count).linkToNextHigherDecade(freqDigits.get(count+1));           
    }
    
    
    /**
     * There are two representations of the current value of the field.  One is
     * the textual representation in the JLabel which is displayed.
     *  The other is the currentValue stored in the DecadeModel.  The
     * idea is to keep them in sync or die a painful death.  In any case, the
     * currentValue of the model always wins.  This method sets them both to the
     * same value or makes sure that they are equal.  
     * 
     * @param obj Object representing the numeric value.
     */    
    public void setValue(Object obj) {
        int oldValue = (int)value;
        int newValue = (Integer) obj;
        value = newValue;
        setText(Integer.toString(value));
        this.getModel().setValue((int) value);
        this.firePropertyChange(VALUE_CHANGE, oldValue, value);
    }
    
    /**
     * This method retrieves the current digit value and makes sure the two values
     * for the field, displayed value and model value),  match.
     * 
     * @return object representing value.
     */
    
    public Object getValue() {
        assert (this.getModel() != null);
       DecadeModel digitModel = getModel();
        if ( digitModel == null) {
            //It's too early in the construction process.  Don't have a model yet.
            return value;
        }
        Object modelObj = (Integer) digitModel.getValue();
        int modelValue = (Integer)modelObj;
        assert(value == modelValue);
        return modelObj;
    }
    

    @Override
    public void focusGained(FocusEvent e) {
        
       if (nastyShiftFocusFlag)  {
            transferFocusBackward();
            System.out.println("transfer focus backward");
            nastyShiftFocusFlag = false;          
        } else {
            DecadeDigit digit = (DecadeDigit)e.getComponent();
            System.out.println("Focus gained by decade digit :"+digit.getDecade());
            AccessibleContext context = digit.getAccessibleContext();
            
            setText(String.valueOf(getValue()));
            context.setAccessibleDescription(getText());
            System.out.println("Decade digit new value : "+ getText());
            setBorder(WHITE_BORDER);
        }
        System.out.println("DecadeDigit :"+ getName() + " received focus.");
    }

    @Override
    public void focusLost(FocusEvent e) {
        setBorder(BLACK_BORDER);        
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Object oldValue = this.getValue();
        int oldInt = (Integer)oldValue;
        int v = e.getWheelRotation();
        int newInt = 0;
        if (v < 0) {
            newInt = model.getPreviousValue();            
        } else {            
            newInt = model.getNextValue();
        }
        this.setValue((Integer)newInt);
        firePropertyChange(VALUE_CHANGE, oldInt, newInt);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Object oldValue = this.getValue();
            int oldInt = (Integer)oldValue;
            int my = e.getY();
            int cy = getHeight() / 2;
            int newInt = 0;
            if (my < cy) {
                newInt = model.getNextValue();
            } else {
                newInt = model.getPreviousValue();
            }
            this.setValue((Integer)newInt);
            firePropertyChange(VALUE_CHANGE, oldInt, newInt);       
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }


    //@Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Object oldValue = this.getValue();
        int oldInt = (Integer)oldValue;
        if (key == KeyEvent.VK_UP) {
            int newInt = model.getNextValue();
            Object obj = (Integer)newInt;
            this.setValue(obj); // Method sets the label text.
            // The JLabel text has changed, but is not read aloud by voiceOver. BUG????
            setEnabled(false);
            setEnabled(true);
            setText(String.valueOf(newInt)); // Set the text AGAIN.!!!!
            nastyShiftFocusFlag = true;
            System.out.println("transfer focus forward");
            transferFocus();
            setText(String.valueOf(newInt));
//            int currentValue = newInt;
//            AccessibleContext context = getAccessibleContext();
//            int powerOfTen  = ((DecadeModel)getModel()).getDecade();           
//            String name = getName(powerOfTen)+" "+Integer.toString(currentValue);
//            context.setAccessibleDescription(name);
//            System.out.println("DecadeDigit accessibleName changed to :"+ name );
//            //setName(name);

            //firePropertyChange(VALUE_CHANGE, oldInt, newInt);    
        }
        if (key == KeyEvent.VK_DOWN) {
            int newInt = model.getPreviousValue();
            Object obj = (Integer)newInt;
            this.setValue(obj);  // Method sets the label text.
            // The JLabel text has changed, but is not read aloud by voiceOver. BUG????
                        setEnabled(false);
            setEnabled(true);
            nastyShiftFocusFlag = true;
            KeyboardFocusManager focusManager =
                        KeyboardFocusManager.getCurrentKeyboardFocusManager();
            focusManager.focusPreviousComponent();

            setText(String.valueOf(newInt));
//            int currentValue = newInt;
//            AccessibleContext context = getAccessibleContext();
//            int powerOfTen  = ((DecadeModel)getModel()).getDecade();           
//            String name = getName(powerOfTen)+" "+Integer.toString(currentValue);
//            context.setAccessibleDescription(name);
//            System.out.println("DecadeDigit accessibleName changed to :"+ name );
//            //setName(name);
 
            //firePropertyChange(VALUE_CHANGE, oldInt, newInt);    
        }
    }

    //@Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName == VALUE_CHANGE) {
            int currentValue = (Integer) getValue();
            AccessibleContext context = getAccessibleContext();
            int powerOfTen  = ((DecadeModel)getModel()).getDecade();           
            String name = getName(powerOfTen)+" "+Integer.toString(currentValue);
            //context.setAccessibleName(name);
            //setName(name);
            
            
            
        }       
    }
}


class IncrementAction extends AbstractAction  {
    public IncrementAction(String name, ImageIcon icon, String shortDescription, Integer mnemonic)
    {
        super(name, icon);
        putValue(SHORT_DESCRIPTION, shortDescription);
        putValue(MNEMONIC_KEY, mnemonic);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        VfoDisplayControl display = (VfoDisplayControl) obj;
        if (! display.isSelected() ) {
            try {
                display.setSelected(true);
            } catch (PropertyVetoException exc) {
                System.out.println(exc);
                return;
            }
        }
        if (display.isSelected()) {
            Component comp = display.getFocusOwner();
            //Component recentComp = display.getMostRecentFocusOwner();       
            DecadeDigit digit = (DecadeDigit)comp;
            DecadeModel model = digit.getModel();       
            int newInt = model.getNextValue();
            Object valueObj = (Integer)newInt;        
            digit.setValue(valueObj); // Method sets the label text.
        }
    }
}   


class DecrementAction extends AbstractAction  {
    public DecrementAction(String name, ImageIcon icon, String shortDescription, Integer mnemonic)
    {
        super(name, icon);
        putValue(SHORT_DESCRIPTION, shortDescription);
        putValue(MNEMONIC_KEY, mnemonic);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        VfoDisplayControl display = (VfoDisplayControl) obj;
        if (! display.isSelected() ) {
            try {
                display.setSelected(true);
            } catch (PropertyVetoException exc) {
                System.out.println(exc);
                return;
            }
        }
        if (display.isSelected()) {
            Component comp = display.getFocusOwner();
            //Component recentComp = display.getMostRecentFocusOwner();       
            DecadeDigit digit = (DecadeDigit)comp;
            DecadeModel model = digit.getModel();       
            int newInt = model.getPreviousValue();
            Object valueObj = (Integer)newInt;        
            digit.setValue(valueObj); // Method sets the label text.
        }
    }
}   
