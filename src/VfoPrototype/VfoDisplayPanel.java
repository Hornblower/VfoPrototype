/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import VfoPrototype.VfoPrototype.*;
import javax.swing.SpinnerModel;
import VfoPrototype.CyclingSpinnerNumberModel;

/**
 *
 * @author Coz
 */
final public class VfoDisplayPanel extends JPanel 
        implements MouseWheelListener, MouseListener {

    protected ArrayList<FreqDigit> freqDigits = null;
    VfoPrototype aFrame;
    long sv_freq;
    long currentFrequency = 3563000L;
    double oldFrequency = 3557000.0;
    long freqVfoA = 3563000;
    long freqVfoB = 3557000;
    boolean inhibit = true;
    long modulatedValue = 0;
    

    public VfoDisplayPanel(VfoPrototype frame) {
        aFrame = frame;
    }

    public void initDigits() {
        DigitChangeListener digitChangeListener;

        freqDigits = new ArrayList<>();
         
        addlinkedDigits(aFrame.jSpinner1Hertz, aFrame.jSpinner10Hertz);
        addlinkedDigits(aFrame.jSpinner10Hertz, aFrame.jSpinner100Hertz);
        addlinkedDigits(aFrame.jSpinner100Hertz, aFrame.jSpinner1khz);
        addlinkedDigits(aFrame.jSpinner1khz, aFrame.jSpinner10khz); 
        addlinkedDigits(aFrame.jSpinner1khz, aFrame.jSpinner100khz);
        addlinkedDigits(aFrame.jSpinner100khz, aFrame.jSpinner1Mhz);
        addlinkedDigits(aFrame.jSpinner1Mhz, aFrame.jSpinner10Mhz);
        addlinkedDigits(aFrame.jSpinner10Mhz, aFrame.jSpinner100Mhz);
        addlinkedDigits(aFrame.jSpinner100Mhz, aFrame.jSpinner1000Mhz);
        // Highest decade spinner is not linked.
        FreqDigit digit;
        digit = (FreqDigit) aFrame.jSpinner1000Mhz;
        freqDigits.add(digit);
        inhibit = false;
        initFrequency();
    }

    private void addlinkedDigits(JSpinner low, JSpinner high) {
        FreqDigit digitLow = (FreqDigit) low;
        FreqDigit digitHigh = (FreqDigit) high;
        // Spinner model is set to incorrect class.
        // Replace model with cycling model.
        CyclingSpinnerNumberModel lowModel = new CyclingSpinnerNumberModel(0,0,9,1);
        digitLow.setModel(lowModel);
        CyclingSpinnerNumberModel highModel = new CyclingSpinnerNumberModel(0,0,9,1);
        digitHigh.setModel(highModel);
        // Link the models.        
        lowModel.setLinkedModel(highModel);
        // Add the low decade digit to the VFO panel array.
        freqDigits.add(digitLow);
    }
    
    protected boolean validSetup() {
        return (!inhibit);
    }

    public void initFrequency() {
        frequencyToDigits(currentFrequency);
    }

    public void frequencyToDigits(long v) {
        sv_freq = v;
        currentFrequency = sv_freq;
        modulatedValue = v;
        ListIterator<FreqDigit> rev = freqDigits.listIterator(freqDigits.size());
        // Expecting list ordered from LSD to MSD.      
        int size = freqDigits.size();
        for (int i = 0; i < size; i++) {
            FreqDigit fd = freqDigits.get(i);
            fd.setDigit(modulatedValue % 10);
            fd.setBright(modulatedValue != 0);
            //fd.setBright(v != 0)
            modulatedValue /= 10;
        }         
        setFrequency(sv_freq);
    }
    
    public long digitsToFrequency() {
        if (validSetup()) {
            currentFrequency = 0;
            for (FreqDigit fd : freqDigits) {
                SpinnerNumberModel  model = (SpinnerNumberModel) fd.getModel();
                int value = model.getNumber().intValue();
                currentFrequency = (currentFrequency * 10) + value;
                // @specification: Leading zeroes are dimmed.
                fd.setBright(currentFrequency != 0);
            }
        }
        sv_freq = currentFrequency;
        setFrequency(sv_freq);
        return currentFrequency;
    }

    public void chooseVfoA() {
        currentFrequency = freqVfoA;
    }
    
    public void chooseVfoB() {
        currentFrequency = freqVfoB;
    }
    

    /*
     *  @return true when there has been a change in VFO frequency.
    */
    public boolean setFrequency(long v) {
        boolean changed = false;
        try {
            if (validSetup()) {
                if (v < 0) {
                    v = sv_freq;
                }
                if (v < 0) {
                    throw (new Exception("frequency <= 0"));
                }
                if (oldFrequency != v) {
                    
                    sv_freq = v;
                    // @todo: Call sendRadioCom  upon change.
                    oldFrequency = v;
                    changed = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return changed;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
