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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.nio.CharBuffer;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author coz
 */
final public class FreqDigit extends JSpinner implements MouseWheelListener, MouseListener, Readable, ChangeListener {

    VfoDisplayPanel display;
    float fontScale;
    FreqDigit carry = null;
    private long value = 0;
    Color nonZeroColor = new Color(0,192,0);
    Color zeroColor = new Color(0,64,0);
    // SpinnerNumberModel is set by GUI designer.
   
           

    public FreqDigit(VfoPrototype proto, long dflt) throws IOException {
        super();
        if (dflt >= 0 && dflt <= 9) {
            DigitChangeListener changeListener = new DigitChangeListener();
            addMouseWheelListener(this);
            addMouseListener(this);
            addChangeListener(changeListener);
            display = (VfoDisplayPanel) proto.vfoDisplayPanel;
            value = dflt;
            setDigit(dflt);
            setForeground(zeroColor);
        }
        else {
            removeMouseWheelListener(this);
            removeMouseListener(this);
            throw new UnsupportedOperationException("too many digits, Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }     
    }

    public void setDigit(long v) {
        SpinnerNumberModel  model = (SpinnerNumberModel) this.getModel();
        model.setValue(v);
    }

    public void setCarry(FreqDigit fd) {
        carry = fd;
    }
    
    public void setBright(boolean v) {
        setForeground(v?nonZeroColor:zeroColor);
    }

    private void increm(int v) {
        boolean carried = false;
        if (value >= 0) {
            long nv = value + v;
            setDigit((nv + 10) % 10);
            if (carry != null) {
                if (nv > 9) {
                    carry.increm(1);
                    carried = true;
                }
                if (nv < 0) {
                    carry.increm(-1);
                    carried = true;
                }
            }
            // if this is the high digit of the number
            if (!carried) {
                display.digitsToFrequency();
            }
        }
    }

    
    @Override
    public Object getNextValue() {
        return super.getNextValue(); //To change body of generated methods, choose Tools | Templates.
    }

    // Do not need to override getValue as it is method of JSpinner.

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int v = e.getWheelRotation();
        int iv = (v < 0) ? 1 : -1;
        increm(iv);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // @todo This code is wrong.
            int my = e.getY();
            int cy = getHeight() / 2;
            int inc = (my < cy) ? 1 : -1;
            increm(inc);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void setModel(SpinnerModel model) {
        super.setModel(model); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param cb
     * @return
     * @throws IOException
     */
    @Override
    public int read(CharBuffer cb) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        if (source.hasFocus() ) {
            Object obj = source.getModel().getValue();
            String digitStr = obj.toString();
            System.out.println(digitStr+" DigitChangeListener");
            source.display.digitsToFrequency(); 
        }
    }
}

