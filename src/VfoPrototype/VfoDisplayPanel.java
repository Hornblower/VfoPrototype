/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSpinner;


/**
 *
 * @author Coz
 */
final public class VfoDisplayPanel extends JPanel {

    protected ArrayList<FreqDigit> freqDigits = null;
    VfoPrototype aFrame;
    long sv_freq;
    long currentFrequency = 3563000L;
    long oldFrequency = 0;
    boolean inhibit = true;  // Ignore user interaction with FreqDigits.
    
    

    public VfoDisplayPanel(VfoPrototype frame) {
        aFrame = frame;
    }

    public void initDigits() {
        //DigitChangeListener digitChangeListener; //reomve this line

        freqDigits = new ArrayList<>();
        freqDigits.add((FreqDigit) aFrame.jSpinner1Hertz);
        freqDigits.add((FreqDigit) aFrame.jSpinner10Hertz);
        freqDigits.add((FreqDigit) aFrame.jSpinner100Hertz);
        freqDigits.add((FreqDigit) aFrame.jSpinner1khz);
        freqDigits.add((FreqDigit) aFrame.jSpinner10khz);
        freqDigits.add((FreqDigit) aFrame.jSpinner100khz);
        freqDigits.add((FreqDigit) aFrame.jSpinner1Mhz);
        freqDigits.add((FreqDigit) aFrame.jSpinner10Mhz);
        freqDigits.add((FreqDigit) aFrame.jSpinner100Mhz);
        freqDigits.add((FreqDigit) aFrame.jSpinner1000Mhz);
        int index, last;
        last = freqDigits.size() -1;
        
        FreqDigit lowDigit, highDigit;
         // Highest decade spinner is not linked to another decade.
        for(index = 0; index < last ; index++) {
            lowDigit = (freqDigits.get(index));
            highDigit = freqDigits.get(index+1);
            linkDigits(lowDigit, highDigit);
        }
        inhibit = false;
        
        
    }

    private void linkDigits(JSpinner low, JSpinner high) {
        FreqDigit lowDigit;
        lowDigit = (FreqDigit) low;
        FreqDigit highDigit;
        lowDigit = (FreqDigit) high;
        CyclingSpinnerNumberModel lowModel = (CyclingSpinnerNumberModel) low.getModel();
        CyclingSpinnerNumberModel highModel = (CyclingSpinnerNumberModel) high.getModel(); 
        CyclingSpinnerNumberModel.linkModels(lowModel, highModel);
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
            FreqDigit fd = freqDigits.get(i);
            assert(fd.decade == i);
            fd.setValue(Integer.valueOf( (int) (modulatedValue % 10)));
            modulatedValue /= 10;
        }         
        setRadioFrequency(sv_freq);
    }


    /*
    * @Method digitsToFrequency
    * @Return the frequency in hertz shown by the collection of FreqDigits.
    *
    */    
    public long digitsToFrequency() {
        sv_freq = 0;
        if (!inhibit) {
            inhibit = true;
            freqDigits.forEach((dig) -> {
                CyclingSpinnerNumberModel  model = (CyclingSpinnerNumberModel) dig.getModel();
                Object value = model.getValue();
                String digitString = value.toString();
                Integer digit = Integer.valueOf(digitString);
                Integer decade = dig.decade;
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