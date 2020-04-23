/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Implements digit wrap around with recursive
 * carry to higher decades.  
 * Subclasses SpinnerNumberModel.
 * Overrides getNextValue() and getPreviousValue.
 * 
 * Use static class method linkModels() to link decades.
 * 
 * @author Coz
 */
final public class CyclingSpinnerNumberModel extends SpinnerNumberModel implements ChangeListener {
    protected CyclingSpinnerNumberModel linkedModel = null;
    
    static public void linkModels(CyclingSpinnerNumberModel lowModel, CyclingSpinnerNumberModel highModel) {  
        lowModel.setLinkedModel(highModel);
    }
    
    public CyclingSpinnerNumberModel (int currentVal, int minVal, int maxVal, int stepVal) {
        super(currentVal, minVal, maxVal, stepVal);        
    }
    
    private void setLinkedModel(CyclingSpinnerNumberModel aLinkedModel) {
        linkedModel = aLinkedModel; 
    }
    
    
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
                // @todo limit recursion
                linkedModel.setValue(linkedModel.getPreviousValue());
            }
        }
        return obj;       
    }    

    @Override
    public void stateChanged(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
