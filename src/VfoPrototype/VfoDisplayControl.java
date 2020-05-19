/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleValue;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;


/**
 * Implements a ten digit integer display/control where each digit can be 
 * manipulated, wraps around and carries to the next digit appropriately.
 * 
 * The VfoDisplayControl digits can be incremented and decremented by the
 * up and down arrows. The left and right arrows traverse the digit. A digit
 * if given a new description upon focus which includes the decade of the 
 * digit and the current VFO frequency in Mhz.  VoiceOver announces that new
 * description.  There are shortcut keys ALT-A and ALT-B to select either
 * VFO A or VFO B.  These "radio buttons" choose which radio VFO is controlled
 * by manipulating the VFO Display Control digits.
 * 
 * Sighted users can click on the upper half of a digit to increment or lower
 * half to decrement.  The mouseWheel is a faster way to increment/decrement.
 * 
 * It is not possible to just type digits to enter a frequency in this app.  Each
 * digit is a formatted text field, but editing is disabled.  Editing introduces
 * many complications which include the question of when to commit.  That is why
 * editing is disabled.
 * 
 * To scan the band, pick a decade digit and hold down the up/down arrow.
 * 
 * @author Coz
 */
final public class VfoDisplayControl extends JInternalFrame 
        implements PropertyChangeListener {
    
    protected ArrayList<DecadeDigit> freqDigits = null;
    public final static int QUANTITY_DIGITS = 10;
    VfoPrototype2 aFrame = VfoPrototype2.singletonInstance;
    long sv_freq;
    long currentFrequency = 3563000L;
    long oldFrequency = 0;
    boolean inhibit = true;  // Inhibit interaction during construction.
    Vector<Component> order;
    boolean silent = false;

    public VfoDisplayControl(VfoPrototype2 frame) {
        super("VFO Display Control");
        aFrame = frame;
        super.setFrameIcon(null); // does not remove icon
        super.setClosable(false);
        super.setFocusCycleRoot(true);
        super.setFocusable(true);
        super.setJMenuBar(null); // does not remove menu bar
        setBackground(Color.red);
        pack();
        setVisible(false);
        
    }

    public void initDigits() {
        freqDigits = new ArrayList<>();
        freqDigits.add(new DecadeDigit(this, 0.7));    
        freqDigits.add(new DecadeDigit(this, 0.7));
        freqDigits.get(0).linkToNextHigherDecade(freqDigits.get(1));       
        freqDigits.add(new DecadeDigit(this, 0.7));
        freqDigits.get(1).linkToNextHigherDecade(freqDigits.get(2));   
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.get(2).linkToNextHigherDecade(freqDigits.get(3));       
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.get(3).linkToNextHigherDecade(freqDigits.get(4));       
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.get(4).linkToNextHigherDecade(freqDigits.get(5));       
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.get(5).linkToNextHigherDecade(freqDigits.get(6));       
        freqDigits.add(new DecadeDigit(this, 1.0));  
        freqDigits.get(6).linkToNextHigherDecade(freqDigits.get(7));       
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.get(7).linkToNextHigherDecade(freqDigits.get(8));       
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.get(8).linkToNextHigherDecade(freqDigits.get(9));     
        assert(freqDigits.size() == QUANTITY_DIGITS);       
        order = new Vector<>(QUANTITY_DIGITS);
        for (int iii=0; iii<QUANTITY_DIGITS; iii++) {
            // The order vector contains the formated text fields.
            Component ftf = freqDigits.get(iii);
            // Every ftf has unique accessible info based on decade. 
            ((DecadeDigit)ftf).setAccessibleInfo();
            order.add(ftf);
        } 
        insertDigitsIntoPanels();
        inhibit = false;
    }
    
    private void insertDigitsIntoPanels() {
        aFrame.digitsParent.setBackground(Color.black);
        JLayeredPane pane = aFrame.jLayeredPaneMegahertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        //TitledBorder border = (TitledBorder)pane.getBorder();
        //border.setTitleColor(Color.GREEN);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(9));
        pane.add(freqDigits.get(8));
        pane.add(freqDigits.get(7));
        pane.add(freqDigits.get(6));
        
        pane = aFrame.jLayeredPaneKilohertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        //border = (TitledBorder)pane.getBorder();
        //border.setTitleColor(Color.GREEN);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(5));
        pane.add(freqDigits.get(4));
        pane.add(freqDigits.get(3));
        
        pane = aFrame.jLayeredPaneHertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        //border = (TitledBorder)pane.getBorder();
        //border.setTitleColor(Color.GREEN);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(2));
        pane.add(freqDigits.get(1));
        pane.add(freqDigits.get(0));
    }
    
    public Vector<Component> getTraversalOrder() {
        return order;
    }
        
    public void initFrequency(long v) {
        frequencyToDigits(v);
    }
    
    public void setInhibit(boolean mode) {
        inhibit = mode;
    }
    public void setSilent(boolean mode) {
        silent = mode;
    }
    
    /**
     * Given a long representation of a frequency in hertz, set the decade
     * digits to display that frequency.
     * 
     * @param v A long representing frequency in Hertz
     */
    public void frequencyToDigits(long v) {
        if(inhibit) return;
        sv_freq = v;
        currentFrequency = sv_freq;
        long modulatedValue = v;
        // Expecting list ordered from LSD to MSD.      
        int size = freqDigits.size();
        for (int i = 0; i < size; i++) {
            DecadeDigit fd = freqDigits.get(i);
            fd.setValue( (int) (modulatedValue % 10));
            fd.setBright(modulatedValue != 0);
            modulatedValue /= 10;
        }         
    }

    /**
    * From the currently displayed digits in the VfoDisplayControl construct a
    * base ten representation and return it as a long.  Dim the leading zeroes.
    * 
    * @return the frequency in hertz shown by the collection of JSpinner digits.
    */    
    public long digitsToFrequency() {
        sv_freq = 0;
        if (!inhibit) {
            inhibit = true;
            freqDigits.forEach((dig) -> {
                DecadeModel  model = (DecadeModel) dig.getModel();
                Object value = model.getValue();
                String digitString = value.toString();
                Integer digit = Integer.valueOf(digitString);
                int decade = model.getDecade();
                sv_freq =  (long)Math.pow(10, decade) * digit + sv_freq ;
                dig.setBright(true); 
            });
            // Dim leading zeroes.
            for ( int ii = QUANTITY_DIGITS; ii>0; ii--) {
                DecadeDigit dig = freqDigits.get(ii-1);
                if ((Integer)dig.getValue() == 0) {
                    dig.setBright(false);
                } else {
                    break;
                }    
            }
        }
        inhibit = false;
        return sv_freq;
    }
    
    public void adjustSize(JLayeredPane resizedComponent) {
        int fontSize = (int) (this.getWidth() / 9.0);
        for (Component comp : resizedComponent.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER)) {
            DecadeDigit digit = (DecadeDigit) comp;
            double fs = digit.fontScale;
            Font font = new Font("Monospace", Font.PLAIN, (int) (fontSize * fs));
            comp.setFont(font);
        }
    }

    /**
     * This listener could possibly called very early on so there is a "silent"
     * variable that is used to prevent handling events before components are
     * created. Also, when the VFO selection is changed, that variable is set
     * true to mute the barrage of changes.
     * 
     * @param evt 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (silent) return;
        Object source = evt.getSource();
        DecadeDigit digit = (DecadeDigit)source;
        String name = digit.getName();
        // getValue() synchronizes the model and ftf values.
        int value = (Integer) digit.getValue();
        //System.out.print("PropertyChangeEvent :");
        //System.out.print(" source : " + name);
        //System.out.println(" ; getValue() returns :"+ value);        
        Component ftf = (Component)source;         
        // Update this ftf description with new frequency and decade name.
        // Update the debug panel frequency.           
        DecadeModel model = digit.getModel();
        DecadeModel decadeModel = (DecadeModel)model;
        int decade = decadeModel.getDecade();
        if( aFrame.vfoState == null) {
            // We are in the contruction process. Too early.
            return;
        }
        // Change the field description so voiceOver will announce it.
        long freq = digitsToFrequency();
        aFrame.vfoState.writeFrequencyToRadioSelectedVfo(freq);
        System.out.println("handleChangeEvent - model value: "+ String.valueOf(value)); 
        for ( int iii=0; iii<QUANTITY_DIGITS; iii++) {
            StringBuilder freqString = new StringBuilder("");
            freqString.append("VFO Frequency "+Double.toString(((double)freq)/1000000.)+" Mhz; ");
            Component comp = order.get(iii);
            JFormattedTextField ftfield = (JFormattedTextField)comp;
            freqString.append(ftfield.getAccessibleContext().getAccessibleName());
            ftfield.getAccessibleContext().setAccessibleDescription(freqString.toString());
        }
        // Print out just this field's name and description.
        //String ftfName = ftf.getAccessibleContext().getAccessibleName();
        //System.out.println("ftf accessible name :"+ftfName);
        String ftfDesc = ftf.getAccessibleContext().getAccessibleDescription();
        System.out.println("ftf accessible description :"+ftfDesc);               
    }       
}
