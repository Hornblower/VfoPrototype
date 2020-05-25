/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

/**
 * Concrete class represents the state of using radio VFO B.
 * 
 * @author Coz
 */
public class VfoBState extends VfoState {
    VfoBState(JTextField aField , JRadioButtonMenuItem aButton) {
        stateName = VFO_B;
        field = aField;
        button = aButton; 
    }   
    public JRadioButtonMenuItem getButton() {   
        return button;
    }
    public JTextField getField() {
        return field;
    }

    @Override
    public String getName() {
        return stateName;
    }        

    @Override
    public void setFrequency(long freq) {
        vfoFrequency = freq;
    }

    @Override
    public long getFrequency() {
        long frequencyHertz = 0;
        try {
            lock.readLock().lock();  //blocks until lock is available.               
            String valString;      
            //Simlate read freq from Radio VFO A.
            valString = field.getText();        
            double freqMhz = Double.valueOf(valString);
            frequencyHertz = (long) (freqMhz * 1.E06) ; 
        }
        finally {
            lock.readLock().unlock(); 
        }
        return frequencyHertz;
    }

    @Override
    public VfoState getNextState() {
        return(VfoState) states.get(0);
        
    }

    
   
}
