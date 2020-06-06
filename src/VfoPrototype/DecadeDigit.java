/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.BorderFactory;
import javax.swing.JLabel;


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
        MouseListener, KeyListener  {
    
    DecadeModel model;
    VfoDisplayControl frameGroup;
    final static Font DIGITS_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 30);
    final static Color NON_ZERO_COLOR = new Color(0,192,0);
    final static Color ZERO_COLOR = new Color(0,64,0);
    protected float fontScale = 0.0f;
    final static String VALUE_CHANGE = "valueChange";
    private long value = 0;
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
        setBorder(BorderFactory.createLineBorder(Color.red));

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
        addKeyListener(this);
        addFocusListener(this);
        addPropertyChangeListener(VALUE_CHANGE, frameGroup);                                 
        setValue(this.getModel().getValue());
        requestFocus();
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
        DecadeModel myModel = (DecadeModel)getModel();
        int powerOfTen = myModel.getDecade();
        AccessibleContext ftfContext = this.getAccessibleContext();
        String name = getName(powerOfTen)+" Decade digit";
        ftfContext.setAccessibleName(name);
        this.setName(name);
        ftfContext.setAccessibleDescription(
            "Up and down arrows change value; Left and right arrows traverse digits.");
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
        int oldValue = (Integer)getValue();
        int newValue = (Integer) obj;
        if (oldValue == newValue) return;
        value = newValue;
        setText(""+value);
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
        System.out.println("DecadeDigit :"+ getName() + " received focus.");
    }

    @Override
    public void focusLost(FocusEvent e) {
        
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
        //firePropertyChange(VALUE_CHANGE, oldInt, newInt);
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
            //firePropertyChange(VALUE_CHANGE, oldInt, newInt);       
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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Object oldValue = this.getValue();
        int oldInt = (Integer)oldValue;
        if (key == KeyEvent.VK_UP) {
            int newInt = model.getNextValue();
            Object obj = (Integer)newInt;
            this.setValue(obj);
            //firePropertyChange(VALUE_CHANGE, oldInt, newInt);
        }
        if (key == KeyEvent.VK_DOWN) {
            int newInt = model.getPreviousValue();
            Object obj = (Integer)newInt;
            this.setValue(obj);
            //firePropertyChange(VALUE_CHANGE, oldInt, newInt);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}

    

