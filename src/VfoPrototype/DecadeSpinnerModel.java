/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import javax.swing.SpinnerNumberModel;

/**
 * Implements a one digit wrap around spinner with recursive
 * carry to higher decades.  
 * Subclasses SpinnerNumberModel.
 * Overrides getNextValue() and getPreviousValue.
 * 
 * Use static class method linkModels() to link decades.
 * 
 * @author Coz
 */
final public class DecadeSpinnerModel extends SpinnerNumberModel {
    protected DecadeSpinnerModel linkedModel = null;
    private int decade;   
    
    public DecadeSpinnerModel() {
        super(0,0,9,1);
        decade = 0;  // Default to zero decade.
    }
    
    public DecadeSpinnerModel(int aPowerOfTen) {
        super(0,0,9,1);
        decade = aPowerOfTen;
    }

    public void setLinkedModel(DecadeSpinnerModel aLinkedModel) {
        linkedModel = aLinkedModel; 
    }
        
    protected void setDecade(int n) {
        decade = n;
    }
    protected int getDecade() {
        return decade;
    }    
    /*
     * Implement digit wrap around and decade recursive increment.
    */
    @Override
    public Object getNextValue() {
        Object obj = super.getNextValue();
        if (obj == null) {
            // The digit wants to go higher than maximum.
            // Wrap the digit around to minimum.  
            obj = super.getMinimum();
            //this.setValue(obj);
            if (linkedModel != null) {
                Object linkedModelValue = linkedModel.getNextValue();
                // @todo limit recursion
                linkedModel.setValue(linkedModelValue);
            } else {
                // The VfoDisplayPanel needs to know that the limit has been reached.
                fireStateChanged();
                // @TODO Coz , fix this.
                // Highest decade is maxed out.
                // End the recursion.  Set the max. freq.
                //VfoDisplayPanel panel = ( VfoDisplayPanel) VfoPrototype.singletonInstance.displayPanel;  
                //panel.frequencyToDigits(9999999999L);
            }
        }
        return obj;
    }

    @Override
    public Object getPreviousValue() {
        Object obj = super.getPreviousValue();
        if(obj == null) {
            // The digit wants to go lower than minimum.
            // Wrap the digit around to maximum.
            obj = super.getMaximum();
            //this.setValue(obj);
            if(linkedModel != null) {
                linkedModel.setValue(linkedModel.getPreviousValue());
            } else {
                 // The VfoDisplayPanel needs to know that the limit has been reached.
                fireStateChanged();
                // @todo Coz, fix this.  It does not work right.
                // we got to the highest decade, but the VFO frequency
                // should NOT wrap around. Set it to zero.
                //VfoDisplayPanel panel = ( VfoDisplayPanel) VfoPrototype.singletonInstance.displayPanel;  
                //panel.frequencyToDigits(0L);
            }
        }  
        return obj;       
    }    
}
