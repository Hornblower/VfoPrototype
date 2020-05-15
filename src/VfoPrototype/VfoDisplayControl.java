/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleText;
import static javax.accessibility.AccessibleText.WORD;
import javax.accessibility.AccessibleValue;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


/**
 * Implements a ten digit integer display/control where each digit can be 
 * manipulated, wraps around and carries to the next digit appropriately.
 * 
 * @author Coz
 */
final public class VfoDisplayControl extends JPanel {

    protected ArrayList<DecadeSpinner> freqDigits = null;
    public final static int QUANTITY_DIGITS = 10;
    VfoPrototype aFrame;
    long sv_freq;
    long currentFrequency = 3563000L;
    long oldFrequency = 0;
    boolean inhibit = true;  // Ignore user interaction with FreqDigits.
    Vector<Component> order;
    
    

    public VfoDisplayControl(VfoPrototype frame) {
        aFrame = frame;
    }

    public void initDigits() {

        freqDigits = new ArrayList<>();
        freqDigits.add((DecadeSpinner)(aFrame.jSpinner1Hertz));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner10Hertz);
        freqDigits.get(0).linkToNextHigherDecade(freqDigits.get(1));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner100Hertz);
        freqDigits.get(1).linkToNextHigherDecade(freqDigits.get(2));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner1khz);
        freqDigits.get(2).linkToNextHigherDecade(freqDigits.get(3));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner10khz);
        freqDigits.get(3).linkToNextHigherDecade(freqDigits.get(4));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner100khz);
        freqDigits.get(4).linkToNextHigherDecade(freqDigits.get(5));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner1Mhz);
        freqDigits.get(5).linkToNextHigherDecade(freqDigits.get(6));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner10Mhz);    
        freqDigits.get(6).linkToNextHigherDecade(freqDigits.get(7));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner100Mhz);
        freqDigits.get(7).linkToNextHigherDecade(freqDigits.get(8));
        
        freqDigits.add((DecadeSpinner)aFrame.jSpinner1000Mhz);
        freqDigits.get(8).linkToNextHigherDecade(freqDigits.get(9));
     
        assert(freqDigits.size() == QUANTITY_DIGITS);
        
        order = new Vector<Component>(QUANTITY_DIGITS);
        for (int iii=0; iii<QUANTITY_DIGITS; iii++) {
            // The order vector contains the spinner editor formated text fields.
            Component ftf = freqDigits.get(iii).getEditor().getComponent(0);
            order.add(ftf);
        }   
        inhibit = false;
    }
    
    public Vector<Component> getTraversalOrder() {
        return order;
    }
        
    
    public void debugSpinner(DecadeSpinner spinner) {
        
        DecadeSpinnerModel model = (DecadeSpinnerModel)spinner.getModel();
        // Useful accessible items:
        AccessibleContext context = spinner.getAccessibleContext();
        AccessibleAction action = context.getAccessibleAction();
        int qty = action.getAccessibleActionCount();
        if (qty >= 2) {
            // action[0] is increment
            String actstr = action.getAccessibleActionDescription(0);
            System.out.println("action 0 description: "+ actstr);
            // action[1] is decrement
            actstr = action.getAccessibleActionDescription(1);
            System.out.println("action 1 description: "+ actstr);

        }
        AccessibleValue accVal =  context.getAccessibleValue();
        Number currentVal = accVal.getCurrentAccessibleValue();
        System.out.println(" original spinner currentAccessibleValue: "+ currentVal.toString());
        Number wildNumber = 6;
        boolean success =  accVal.setCurrentAccessibleValue(wildNumber);
        if (success) {
            currentVal = accVal.getCurrentAccessibleValue();
            System.out.println(" modified currentAccessibleValue: "+ currentVal.toString());
        }
        success = action.doAccessibleAction(0);
        if (success) {
            currentVal = Integer.getInteger( spinner.getModel().getValue().toString());
            System.out.println(" after accessibleAction INCREMENT  model value: "+ currentVal.toString());
            currentVal = accVal.getCurrentAccessibleValue();
            System.out.println(" after accessibleAction INCREMENT  currentAccessibleValue: "+ currentVal.toString());    
        }
        AccessibleText  accText =  context.getAccessibleText();
        String sentenceStr = accText.getAtIndex(WORD, 0);
        System.out.println(" accessibleText WORD at index 0 :   "+ sentenceStr);

        //ftf.setBackground(Color.DARK_GRAY); // Does nothing.
    }        
    
    public void initFrequency(long v) {
        frequencyToDigits(v);
    }

    public void frequencyToDigits(long v) {
        if(inhibit) return;
        sv_freq = v;
        currentFrequency = sv_freq;
        long modulatedValue = v;
        // Expecting list ordered from LSD to MSD.      
        int size = freqDigits.size();
        for (int i = 0; i < size; i++) {
            DecadeSpinner fd = freqDigits.get(i);
            fd.setValue( (int) (modulatedValue % 10));
            modulatedValue /= 10;
        }         
        setRadioFrequency(sv_freq);
    }


    /*
    * @Method digitsToFrequency
    * @Return the frequency in hertz shown by the collection of JSpinner digits.
    * @TODO  use right ƒoption V key  to read out vfo frequency 
    */    
    
    public long digitsToFrequency() {
        sv_freq = 0;
        if (!inhibit) {
            inhibit = true;
            freqDigits.forEach((dig) -> {
                DecadeSpinnerModel  model = (DecadeSpinnerModel) dig.getModel();
                Object value = model.getValue();
                String digitString = value.toString();
                Integer digit = Integer.valueOf(digitString);
                int decade = model.getDecade();
                sv_freq =  (long)Math.pow(10, decade) * digit + sv_freq ;
            });
        }
        
        inhibit = false;
        setRadioFrequency(sv_freq);
        return sv_freq;
    }

    

    /*  When there is a change in the VFO frequency, and the value is valid,
     *  send the new frequency to the radio.
     *  @return true when there has been a change in VFO frequency.
    */
    public boolean setRadioFrequency(long v) {
        inhibit = true;
        boolean changed = false;
        try {
                // Validate v is not negative.
                if (v < 0) {
                    v = sv_freq;
                }
                if (v < 0) {
                    throw (new Exception("set frequency is negative."));
                }
                if (oldFrequency != v) {   
                    sv_freq = v;
                    aFrame.singletonInstance.sendFreqToRadio(v);
                    oldFrequency = v;
                    changed = true;
                }   
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        inhibit = false;
        return changed;
    }
}
// Not used..
class VfoAction extends AbstractAction {
    public VfoAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
    }
    public void actionPerformed(ActionEvent e) {
        System.out.println("Action for Vfo radio button " + e.getSource().toString());
       
    }
}
