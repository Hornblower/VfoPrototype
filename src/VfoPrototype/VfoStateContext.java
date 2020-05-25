/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import static VfoPrototype.VfoSelectionInterface.SELECTED_COLOR;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Coz
 */
public class VfoStateContext {
    
    static Color SELECTED_COLOR = Color.WHITE; 
    static Color UNSELECTED_COLOR = Color.LIGHT_GRAY;

    private ArrayList<VfoState> states;
    private  VfoState currentState;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();  
    
    VfoStateContext(ArrayList<VfoState> stateList) {
        states = stateList;
        for (int ii = 0 ; ii < states.size(); ii++ ) {
            states.get(ii).addStatesList(stateList);
            states.get(ii).addLock(lock);
        }
        currentState = (VfoState) states.get(0);        
    }
    public String getCurrentState(){
        isVfoASelected();
        return currentState.getName();   
    }
    public long getFrequency() {
        return currentState.getFrequency();
    }
    public void setCurrentVfoFrequency(long freq) {
        currentState.setFrequency(freq);
    }
    
    public boolean isVfoASelected() {
        boolean isVfoA = true;
        lock.readLock().lock();  //blocks until lock is available.
        //Simlate read selection from Radio.       
        isVfoA = ( states.get(0).button.isSelected() );       
        lock.readLock().unlock(); 
        // This state machine must represent the radio button state.
        if (isVfoA) {
            currentState = (VfoState) states.get(0);
        } else {
            currentState = (VfoState) states.get(1);
        }
        return isVfoA;
    }    

    
    private boolean setVfoASelected(){
        boolean success = true;  // Simulation is always successful.
        boolean isSelectedA = isVfoASelected();
        if (isSelectedA) {
            currentState = (VfoState) states.get(0);
            return success; // VFO A is already selected.
        }
        // Change states from VFO B to VFO A
        
        lock.writeLock().lock();  //blocks until lock is available.
        System.out.println("obtained lock. Vfo A is selected :" + isSelectedA);
        ((VfoAState) states.get(0)).getButton().setSelected(true);
        ((VfoAState) states.get(0)).getField().setBackground(SELECTED_COLOR);     
        ((VfoBState) states.get(1)).getField().setBackground(UNSELECTED_COLOR);
        currentState = (VfoState) states.get(0);
        lock.writeLock().unlock();            
        
        isSelectedA = isVfoASelected();
        assert(isSelectedA);
        System.out.println("Released the lock. Vfo A is selected .");
        return success;
    }

    public boolean setVfoBSelected(){
        boolean success = true;  // Simulation is always successful. 
        boolean isSelectedA = isVfoASelected();
        if (!isSelectedA) {
            currentState = (VfoState) states.get(1);
            return success; // VFO B is already selected.
        }
        // Change states from VFO A to VFO B.       
        lock.writeLock().lock();  //blocks until lock is available.       
        System.out.println("obtained lock. Vfo A is selected :" + isSelectedA);
        ((VfoAState) states.get(1)).getButton().setSelected(true);
        ((VfoAState) states.get(1)).getField().setBackground(SELECTED_COLOR);     
        ((VfoBState) states.get(0)).getField().setBackground(UNSELECTED_COLOR);
        currentState = (VfoState) states.get(1);   
        lock.writeLock().unlock();            
        
        isSelectedA = isVfoASelected();
        assert(!isSelectedA);
        System.out.println("Released the lock. Vfo B is selected .");
        return success;
    }


}
