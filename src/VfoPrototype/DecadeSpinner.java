/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;


import static VfoPrototype.VfoPrototype.singletonInstance;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.plaf.synth.SynthSpinnerUI;

/**
 * Implements a decade digit in a multi-digit numeric display.
 * 
 * It is one digit wide and has the range of 0 to 9 which wraps around
 * when incremented or decremented past its range.  The model defaults to
 * DecadeSpinnerModel which takes care of the wrap around and passing on the
 * carry to adjacent decade spinners.
 * 
 * @author Coz
 */
public class DecadeSpinner extends JSpinner implements Accessible, FocusListener {
   
    public DecadeSpinner() {
        super();
        // setModel() method overwrites the editor.  JSpinner model is private.
        setModel( new DecadeSpinnerModel(0));
        // Coz, this means that the entire spinner cannot gain focus.
        // Hopefully, the ftf will now be the only focus in the JSpinner.
        //this.setFocusable(false);
        // Coz, is this needed too?
        // this.transferFocusDownCycle();
        // JSpinner editor is private.
        AccessibleEditor ed = new AccessibleEditor(this);
        setEditor((JComponent)ed);
        setToolTipText("Lou, this is the new spinner tool tip text.  Use the force!");
        // Set foreground numeral color Green.
        // Note: without doing lnf things, you cannot change background color.
        Component ftf = getEditor().getComponent(0);
        ftf.setFocusable(true);
        //ftf.setForeground(Color.GREEN);
        // Hopefully, only the formated text field will get focus.
        ftf.requestFocus();
    }
    

    public static String getSpinnerName(int powerOf10){
        assert(powerOf10 < VfoDisplayControl.QUANTITY_DIGITS);
        String[] name = { "Ones","Tens","Hundreds","Thousands","Ten thousands","hundred thousands","millions","ten millions","Hundred millions","Billions"};
        String accName = name[powerOf10]+" digit ";
        return accName;
    }
    

    // The only way for a user to set a decade is by linking DecaddeSpinners.
    private void setDecade(int powerOfTen) {
        DecadeSpinnerModel myModel = (DecadeSpinnerModel)getModel();
        myModel.setDecade(powerOfTen);
        
        AccessibleContext ftfContext = getEditor().getComponent(0).getAccessibleContext();
        ftfContext.setAccessibleName(getSpinnerName(powerOfTen)+"textField");
        ftfContext.setAccessibleDescription("up and down arrows change value; left and right arrows traverse digits.");
    }
    
    // Start linking DecadeSpinners with the lowest(rightMost) digit.
    public void linkToNextHigherDecade(DecadeSpinner higherDecadeSpinner) {
        higherDecadeSpinner.setDecade(getDecade() + 1);
        DecadeSpinnerModel low = (DecadeSpinnerModel)this.getModel();
        DecadeSpinnerModel high = (DecadeSpinnerModel)higherDecadeSpinner.getModel();
        low.setLinkedModel(high);
    }
    
    public int getDecade() {
        return ((DecadeSpinnerModel) getModel()).getDecade();
    }
    
    public void dumpStuff() {  // looking for tooltip with malice
        JSpinner.AccessibleJSpinner accSpinn;
        AccessibleContext context = this.getAccessibleContext();
        accSpinn = (AccessibleJSpinner) context.getAccessibleComponent();
        if ( accSpinn != null) {
            String accSpinnToolTip = accSpinn.getToolTipText();
            System.out.println("AccessibleJSpinner toolTipText :"+accSpinnToolTip);
        }
        
        // getUI() returns SynthSpinnerUI.
        // SynthSpinnerUI uses handler in BasicSpinnerUI.
        BasicSpinnerUI spinnUI = (BasicSpinnerUI)getUI();
        int qty = spinnUI.getAccessibleChildrenCount(this);
        
        SynthSpinnerUI synthUI = (SynthSpinnerUI)getUI();
        qty = synthUI.getAccessibleChildrenCount(this);
    
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Set focus to the editor's formated text field.
        //this.transferFocus(); // Wierd result
        //this.transferFocusDownCycle();
        //Component ftf = getEditor().getComponent(0);
        //ftf.transferFocus();
     }

    @Override
    public void focusLost(FocusEvent e) {
        // Do nothing.
    }
}
