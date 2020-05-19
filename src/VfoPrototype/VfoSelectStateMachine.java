/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import static VfoPrototype.VfoPrototype2.singletonInstance;
import java.text.DecimalFormat;
import javax.swing.JRadioButton;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The idea here is to make a reentrant state machine that can give results to
 * any thread as to the selected VFO and its frequency.
 * 
 * @author Coz
 */
public class VfoSelectStateMachine {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();    
    JRadioButton vfoA;
    JRadioButton vfoB;
 
    public VfoSelectStateMachine(JRadioButton a, JRadioButton b) {   
        vfoA =  a;
        vfoB =  b;
        //Radio buttons must be in the same group to be exclusively selected.
        assert (vfoA.getModel().getGroup()==vfoB.getModel().getGroup());
    }
    
    public long getVfoAFrequency() {
        long frequencyHertz = 0;
        try {
            lock.readLock().lock();  //blocks until lock is available.            
            String valString;      
            //Simlate read freq from Radio VFO A.
            valString = singletonInstance.frequencyVfoA.getText();        
            double freqMhz = Double.valueOf(valString);
            frequencyHertz = (long) (freqMhz * 1.E06) ;
        }
        finally {
            lock.readLock().unlock(); 
        }
        return frequencyHertz;
    }
    
    public long getVfoBFrequency() {
        long frequencyHertz = 0;
        try {
            lock.readLock().lock();  //blocks until lock is available.               
            String valString;      
            //Simlate read freq from Radio VFO A.
            valString = singletonInstance.frequencyVfoB.getText();        
            double freqMhz = Double.valueOf(valString);
            frequencyHertz = (long) (freqMhz * 1.E06) ; 
        }
        finally {
            lock.readLock().unlock(); 
        }
        return frequencyHertz;
    }

    public long getSelectedVfoFrequency() {
        long frequencyHertz = 0;
        try {
            lock.readLock().lock();  //blocks until lock is available.
            String valString;
            //Simlate read freq from Radio.
            if (vfoA.isSelected()) {
                // Read frequency from Radio VFO A.
                valString = singletonInstance.frequencyVfoA.getText();
            } else {
                // Read frequency from Radio VFO B.
                valString = singletonInstance.frequencyVfoB.getText();
            }
            double freqMhz = Double.valueOf(valString);
            frequencyHertz = (long) (freqMhz * 1.E06) ;
        }
        finally {
            lock.readLock().unlock(); 
        }
        return frequencyHertz;
    }
    
    public boolean vfoA_IsSelected() {
        boolean isVfoA = true;
        lock.readLock().lock();  //blocks until lock is available.
        //Simlate read selection from Radio.       
        isVfoA = ( vfoA.isSelected() );
        lock.readLock().unlock();  
        return isVfoA;
    }    
 
    public boolean setVfoASelected(){
        boolean success = true;  // Simulation is always successful.
        boolean isSelectedA = vfoA_IsSelected();
        if (isSelectedA) return success; // VFO B is already selected.
        
        lock.writeLock().lock();  //blocks until lock is available.
        System.out.println("obtained lock. Vfo A is selected :" + isSelectedA);
        vfoA.setSelected(true);
        
        lock.writeLock().unlock();
        isSelectedA = vfoA_IsSelected();
        System.out.println("Released the lock. Vfo A is selected :"+ isSelectedA);
        return success;
    }

    public boolean setVfoBSelected(){
        boolean success = true;  // Simulation is always successful. 
        boolean isSelectedA = vfoA_IsSelected();
        if(!isSelectedA) return success; // VFO B is already selected.
        
        lock.writeLock().lock();  //blocks until lock is available.       
        System.out.println("obtained lock. Vfo A is selected :" + isSelectedA);
        vfoB.setSelected(true);
        
        lock.writeLock().unlock();
        isSelectedA = vfoA_IsSelected();
        System.out.println("Released the lock. Vfo A is selected :"+ isSelectedA);
        return success;
    }
    
    /**
     * Write the given frequency to the currently selected radio VFO.
     * @return true when frequency successfully communicated to radio.
     * @param frequencyHertz
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
     * @return true when frequency successfully communicated to radio.
     * @param frequencyHertz 
     * @param isVfoA
     * 
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

