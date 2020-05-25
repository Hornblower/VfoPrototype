/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

/**
 * Abstract class representing a two-state state machine.
 * 
 * @author Coz
 */
public abstract class VfoState {
    protected String VFO_A = "vfoA";
    protected String VFO_B = "vfoB";
    protected long vfoFrequency;
    protected String stateName;
    ArrayList<VfoState> states;
    ReentrantReadWriteLock lock;
    JTextField field;
    JRadioButtonMenuItem button;
    
    VfoState() {    
    }
    protected  void addStatesList(ArrayList<VfoState> statesList) {
        states = statesList;
    }
    protected  void addLock(ReentrantReadWriteLock aLock) {
        lock = aLock;        
    }
    protected  void setTextField(JTextField textField) {
        field = textField;
    }
     
    
    public abstract void setFrequency(long freq);
    public abstract long getFrequency();
    public abstract VfoState getNextState();
    public abstract String getName();
    
    
}
