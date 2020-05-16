/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import static VfoPrototype.VfoPrototype.singletonInstance;
import java.text.DecimalFormat;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
/**
 * The idea here is to make a reentrant state machine that can give results to
 * any thread as to the selected VFO and its frequency.
 * 
 * @author Coz
 */
public class VfoSelectStateMachine {
    ButtonGroup radioButtonGroup;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    AbstractButton vfoA;
    AbstractButton vfoB;
 
    public VfoSelectStateMachine(ButtonGroup group) {
        radioButtonGroup = group;
        group.getSelection();
        int count = group.getButtonCount();
        assert(count == 2);
        vfoA =  group.getElements().nextElement();
        vfoB =  group.getElements().nextElement();
        
    }
    
    public long getVfoAFrequency() {
        lock.readLock().lock();  //blocks until lock is available.    
        long frequencyHertz = 0;
        String valString;      
        //Simlate read freq from Radio VFO A.
        valString = singletonInstance.frequencyVfoA.getText();        
        double freqMhz = Double.valueOf(valString);
        frequencyHertz = (long) (freqMhz * 1.E06) ;           
        lock.readLock().unlock();        
        return frequencyHertz;
    }
    
    public long getVfoBFrequency() {
        lock.readLock().lock();  //blocks until lock is available.    
        long frequencyHertz = 0;
        String valString;      
        //Simlate read freq from Radio VFO A.
        valString = singletonInstance.frequencyVfoB.getText();        
        double freqMhz = Double.valueOf(valString);
        frequencyHertz = (long) (freqMhz * 1.E06) ;            
        lock.readLock().unlock();        
        return frequencyHertz;
    }



    public long getSelectedVfoFrequency() {
        lock.readLock().lock();  //blocks until lock is available.
        
        long frequencyHertz = 0;
        ButtonModel selected = radioButtonGroup.getSelection();
        String valString;
        //Simlate read freq from Radio.
        if (selected == vfoA.getModel()) {
            // Read frequency from Radio VFO A.
            valString = singletonInstance.frequencyVfoA.getText();
        } else {
            // Read frequency from Radio VFO B.
            valString = singletonInstance.frequencyVfoB.getText();
        }
        double freqMhz = Double.valueOf(valString);
        frequencyHertz = (long) (freqMhz * 1.E06) ;
                
        lock.readLock().unlock();        
        return frequencyHertz;
    }
    
    public boolean vfoA_IsSelected() {
        boolean isVfoA = true;
        lock.readLock().lock();  //blocks until lock is available.
        //Simlate read selection from Radio.
        ButtonModel selected = radioButtonGroup.getSelection();
        isVfoA = (selected == vfoA.getModel());
        lock.readLock().unlock();  
        return isVfoA;
    }    
 
    public boolean setVfoASelected(){
        boolean success = true;  // Simulation is always successful.   
        lock.writeLock().lock();  //blocks until lock is available.
        
        radioButtonGroup.setSelected(vfoA.getModel(), true);
        
        lock.writeLock().unlock();
        return success;
    }

    public boolean setVfoBSelected(){
        boolean success = true;  // Simulation is always successful.   
        lock.writeLock().lock();  //blocks until lock is available.
        
        radioButtonGroup.setSelected(vfoB.getModel(), true);
        
        lock.writeLock().unlock();
        return success;
    }

    
    
    
/**
     * Write the given frequency to the currently selected radio VFO.
     * @Return true when frequency successfully communicated to radio.
    */
    public boolean writeFrequencyToRadioSelectedVfo(long frequencyHertz) {
        boolean success = true;
        boolean isVfoA = vfoA_IsSelected(); // under readLock        
        lock.writeLock().lock();  //blocks until lock is available.
    
        // Simulate sending freq value to Radio for testing.
        // Update the radio frequency text field.
        double mhz = (double) frequencyHertz;
        mhz = mhz / 1000000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.000000");
        String numberAsString = decimalFormat.format(mhz);
        if (isVfoA)
            singletonInstance.frequencyVfoA.setText(numberAsString);
        else
            singletonInstance.frequencyVfoB.setText(numberAsString);

        lock.writeLock().unlock();
        return success;
    }
    





    
    /**
     * Given which Vfo to access, write the given frequency to the radio.
     * @Return true when frequency successfully communicated to radio.
    */
    public boolean writeFrequencyToRadio(long frequencyHertz, boolean isVfoA) {
        boolean success = true;
        lock.writeLock().lock();  //blocks until lock is available.
    
        // Simulate sending freq value to Radio for testing.
        // Update the radio frequency text field.
        double mhz = (double) frequencyHertz;
        mhz = mhz / 1000000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.000000");
        String numberAsString = decimalFormat.format(mhz);
        if (isVfoA)
            singletonInstance.frequencyVfoA.setText(numberAsString);
        else
            singletonInstance.frequencyVfoB.setText(numberAsString);

        lock.writeLock().unlock();
        return success;
    }


    public boolean writeFrequencyToRadioVfoA(long frequencyHertz) {
        return writeFrequencyToRadio(frequencyHertz, true);
    }

    public boolean writeFrequencyToRadioVfoB(long frequencyHertz) {
        return writeFrequencyToRadio(frequencyHertz, false);
    }
}

