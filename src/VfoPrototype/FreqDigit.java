// ***************************************************************************
// *   Copyright (C) 2012 by Paul Lutus                                      *
// *   lutusp@arachnoid.com                                                  *
// *                                                                         *
// *   This program is free software; you can redistribute it and/or modify  *
// *   it under the terms of the GNU General Public License as published by  *
// *   the Free Software Foundation; either version 2 of the License, or     *
// *   (at your option) any later version.                                   *
// *                                                                         *
// *   This program is distributed in the hope that it will be useful,       *
// *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
// *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
// *   GNU General Public License for more details.                          *
// *                                                                         *
// *   You should have received a copy of the GNU General Public License     *
// *   along with this program; if not, write to the                         *
// *   Free Software Foundation, Inc.,                                       *
// *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
// ***************************************************************************

package VfoPrototype;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.EventListener;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class FreqDigit
 * 
 * A one digit spinner representing a decade of a multi-digit display where 
 * increment and decrement can affect the values of the entire display.  
 * 
 * When a digit reaches 9, an increment will wrap around the digit to 0
 * and the next higher decade is incremented recursively.
 * When a digit reaches 0, a decrement will wrap around the digit to 9 and the
 * next higher decade is decremented recursively.
 * Recursion ends when a digit has no link to a higher decade.
 * 
 * The displayed sum/frequency can be quickly scrolled to a desired quantity
 * with the arrow keys when focus is on a digit.
 * 
 * 
 * @Usage: Use the CyclingSpinnerNumberModel with this spinner.
 * 
 * @author coz
 */
final public class FreqDigit extends JSpinner 
        implements MouseWheelListener, ChangeListener {

    VfoDisplayPanel display;
    float fontScale;
    FreqDigit carry = null;
    Color nonZeroColor = new Color(0,192,0);
    Color zeroColor = new Color(0,64,0);
    int maxDigits = 10;
    int decade = 0;
    
   
           

    public FreqDigit(VfoPrototype proto, int decadePowerOfTen) throws IOException {
        super();
        decade = decadePowerOfTen;
        if (decadePowerOfTen >= 0 && decadePowerOfTen <= maxDigits) {
            DigitChangeListener changeListener = new DigitChangeListener();
            addMouseWheelListener(this);
            addChangeListener(changeListener);
            display = (VfoDisplayPanel) proto.vfoDisplayPanel;
            // WARNING: By default, SpinnerNumberModel is set by GUI designer automated code.
            setForeground(zeroColor);
        }
        
        String decadeStr = String.valueOf(decade);
        String name = String.valueOf("VFO "+ decadeStr +" hertz decade spinner");
        super.getAccessibleContext().setAccessibleName(name);
        String desc = String.valueOf(
            "Use up down arrows to change value. Use left right arrows to change decade");
        super.getAccessibleContext().setAccessibleDescription(desc);
        Integer qtyActions = super.getAccessibleContext().getAccessibleAction().getAccessibleActionCount();
        if ( qtyActions != null){
            for(int i=0; i<qtyActions; i++) {
                String description;
                description = super.getAccessibleContext().getAccessibleAction().getAccessibleActionDescription(i);
                System.out.println("FreqDigit "+String.valueOf(i)+" "+ description);
            }
        } else {
            // Add accessible increment and decrement actions.
        }
      

    }
 
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int v = e.getWheelRotation();
        CyclingSpinnerNumberModel model = (CyclingSpinnerNumberModel) this.getModel();
        
        if (v < 0)  model.getNextValue();
        else        model.getPreviousValue();
  
    }
    
    // This method not used.  Compiler needs it.  Why?
    @Override
    public void stateChanged(ChangeEvent e) {
        FreqDigit source = (FreqDigit)e.getSource();
    }
}
/*
 * Class DigitChangeListener
 *   Handle a ChangeEvent.
 *      Attempt to add announce change in voiceover.
*/
class DigitChangeListener implements ChangeListener {
    @Override
    public void stateChanged(ChangeEvent e) {
        FreqDigit source = (FreqDigit)e.getSource();
        Object obj = source.getModel().getValue();
        String digitStr = obj.toString();
        String decadeString = Integer.toString(source.decade);
        System.out.println(digitStr+" for decade: "+decadeString+" in DigitChangeListener");
        //source.setValue(obj);
        if( ! source.display.inhibit ) {
            source.display.currentFrequency = source.display.digitsToFrequency();
            source.display.aFrame.sendFreqToRadio(source.display.sv_freq);
 
        }
    }
}

