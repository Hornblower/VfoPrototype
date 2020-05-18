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
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;


/**
 * Implements a ten digit integer display/control where each digit can be 
 * manipulated, wraps around and carries to the next digit appropriately.
 * 
 * @author Coz
 */
final public class VfoDisplayControl extends JInternalFrame implements 
        PropertyChangeListener {
    protected ArrayList<DecadeDigit> freqDigits = null;
    public final static int QUANTITY_DIGITS = 10;
    VfoPrototype2 aFrame = VfoPrototype2.singletonInstance;
    long sv_freq;
    long currentFrequency = 3563000L;
    long oldFrequency = 0;
    boolean inhibit = true;  // @todo implement a Lock if necessary.
    Vector<Component> order;
    
    

    public VfoDisplayControl(VfoPrototype2 frame) {
        super("VFO Display Control");
        aFrame = frame;
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
        
        order = new Vector<Component>(QUANTITY_DIGITS);
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
        TitledBorder border = (TitledBorder)pane.getBorder();
        border.setTitleColor(Color.GREEN);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(9));
        pane.add(freqDigits.get(8));
        pane.add(freqDigits.get(7));
        pane.add(freqDigits.get(6));
        
        pane = aFrame.jLayeredPaneKilohertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        border = (TitledBorder)pane.getBorder();
        border.setTitleColor(Color.GREEN);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(5));
        pane.add(freqDigits.get(4));
        pane.add(freqDigits.get(3));
        
        pane = aFrame.jLayeredPaneHertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        border = (TitledBorder)pane.getBorder();
        border.setTitleColor(Color.GREEN);
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
    
    
    /**
     * Given a long representation of a frequency in hertz, set the decade
     * digits to display that frequency.
     * 
     * @param v 
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
    * base ten representation and return it as a long.
    * 
    * @Method digitsToFrequency
    *
    * @Return the frequency in hertz shown by the collection of JSpinner digits.
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
            });
        }
        inhibit = false;
        return sv_freq;
    }
    
    public void adjustSize(JLayeredPane resizedComponent) {
        int fontSize = (int) (this.getWidth() / 11.0);
        for (Component comp : resizedComponent.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER)) {
            DecadeDigit digit = (DecadeDigit) comp;
            double fs = digit.fontScale;
            Font font = new Font("Monospace", Font.PLAIN, (int) (fontSize * fs));
            comp.setFont(font);
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {        
        Object source = evt.getSource();
        DecadeDigit digit = (DecadeDigit)source;
        String name = digit.getName();
        int value = (Integer) digit.getValue();
        System.out.print("PropertyChangeEvent :");
        System.out.print(" source : " + name);
        System.out.println(" ; getValue() returns :"+ value);
        
        
        
        
        
        // Coz need to do the things here that were done in 
        // VfoPrototype2.handleChangeEvent(javax.swing.event.ChangeEvent evt).
        // It makes much more sense to do them at this level.  It hides these
        // VfoDisplayControl operations that are necessary for voiceOver to
        // announce frequency appropriately.
        
        
        
        
        
        
        
        
        
        
        
        
    }       

}
