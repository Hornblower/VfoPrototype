/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * Implement carry to higher decade using SpinnerModel.
 * @author Coz
 */
final public class CyclingSpinnerNumberModel extends SpinnerNumberModel {
    protected int minValue, maxValue, step, currentValue;
    CyclingSpinnerNumberModel linkedModel=null;
    
    public CyclingSpinnerNumberModel (int currentVal, int minVal, int maxVal, int stepVal) {
        super(currentVal, minVal, maxVal, stepVal);
        minValue = minVal;
        maxValue = maxVal;
        step = stepVal;
        currentValue = currentVal;
        //SpinnerNumberModel parent = this;
    }
    
    public void setLinkedModel(CyclingSpinnerNumberModel aLinkedModel) {
        linkedModel = aLinkedModel; 
    }

    
    
    @Override
    public Object getNextValue() {
        Object obj = super.getNextValue();
        if(obj == null) {
            int val;
            //val = Integer.getInteger(obj.toString());
            val = minValue;
            if(linkedModel != null) {
                linkedModel.setValue(linkedModel.getNextValue());
            }
        }
        return obj;       
     }

    @Override
    public Object getPreviousValue() {
        Object obj = super.getNextValue();
        if(obj == null) {
            int val;
            //val = Integer.getInteger(obj.toString());
            val = maxValue;
            if(linkedModel != null) {
                if (linkedModel.getPreviousValue() == null){
                    // @bug why is this cluge necessary?
                    linkedModel.setValue(9);
                }
                linkedModel.setValue(linkedModel.getPreviousValue());
            }
        }
         return obj;       
    }    
}
