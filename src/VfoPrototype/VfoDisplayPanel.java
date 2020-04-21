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
import javax.swing.SpinnerNumberModel;
import VfoPrototype.VfoPrototype.*;

/**
 *
 * @author Coz
 */
final public class VfoDisplayPanel extends JPanel 
        implements MouseWheelListener, MouseListener {

    ArrayList<FreqDigit> freqDigits = null;
    VfoPrototype aFrame;
    long sv_freq;
    long digitFrequency = 3563000L;
    double oldFrequency = 3557000.0;
    boolean inhibit = true;
    long modulatedValue = 0;

    public VfoDisplayPanel(VfoPrototype frame) {
        aFrame = frame;
    }

    public void initDigits() {
        DigitChangeListener digitChangeListener;

        freqDigits = new ArrayList<>();
        FreqDigit digit = (FreqDigit) aFrame.jSpinner1Hertz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner10Hertz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner100Hertz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner1khz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner10khz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner100khz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner1Mhz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner10Mhz;
        freqDigits.add(digit);
        digit = (FreqDigit) aFrame.jSpinner100Mhz;
        freqDigits.add(digit);
        // @todo need one more digit.

        
        setupDigits(freqDigits);
        inhibit = false;
    }

    private void setupDigits(ArrayList<FreqDigit> digits) {
        FreqDigit ofd = null;
        int size = freqDigits.size();
        for (int i = 0; i < size; i++) {
            FreqDigit fd = digits.get(i);
            if (ofd != null) {
                fd.setCarry(ofd);
            }
            ofd = fd;
        }
    }

    protected boolean validSetup() {
        return (!inhibit);
    }

    public void initFrequency() {
        frequencyToDigits(digitFrequency);
    }

    public void frequencyToDigits(long v) {
        sv_freq = v;
        digitFrequency = sv_freq;
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
    
    protected long digitsToFrequency() {
        if (validSetup()) {
            digitFrequency = 0;
            for (FreqDigit fd : freqDigits) {
                SpinnerNumberModel  model = (SpinnerNumberModel) fd.getModel();
                int value = model.getNumber().intValue();
                digitFrequency = (digitFrequency * 10) + value;
                // @specification: Leading zeroes are dimmed.
                fd.setBright(digitFrequency != 0);
            }
        }
        sv_freq = digitFrequency;
        setFrequency(sv_freq);
        return digitFrequency;
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
