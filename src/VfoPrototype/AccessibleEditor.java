package VfoPrototype;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.MaskFormatter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Coz
 * 
 * Copied this code from JSpinner.
 * 
 * Was public static class inside of JSpinner.  Now there is no protection for
 * multiple editors in one JSpinner....fix this.
 */

public class AccessibleEditor extends JPanel
    implements ChangeListener, PropertyChangeListener, LayoutManager
{
     private final Action INCREMENT_ACTION = new IncrementAction();
     private final Action DECREMENT_ACTION = new DecrementAction();
     private static final String INCREMENT = "increment";
     private static final String DECREMENT = "decrement";
     protected JSpinner mySpinner;
     
     /**
     * Constructs an editor component for the specified <code>JSpinner</code>.
     * This <code>AccessibleEditor</code> is it's own layout manager and
     * it is added to the spinner's <code>ChangeListener</code> list.
     * The constructor creates a single <code>JFormattedTextField</code> child,
     * initializes it's value to be the spinner model's current value
     * and adds it to <code>this</code> <code>AccessibleEditor</code>.
     *
     * @param spinner the spinner whose model <code>this</code> editor will monitor
     * @see #getTextField
     * @see JSpinner#addChangeListener
     */
    public AccessibleEditor(JSpinner spinner) {
        super(null);
        mySpinner = spinner;

        System.out.println("JSpinner ToolTipText :" + mySpinner.getToolTipText()) ;
        // mySpinner.setToolTipText("Lou, this is the new spinner tool tip text.  Use the force!");
        JFormattedTextField ftf = new JFormattedTextField(createFormatter("#"));
        ftf.setName("Spinner.formattedTextField");
        Font f = new Font(spinner.getFont().getName(), spinner.getFont().getStyle(), 
                spinner.getFont().getSize() + 4);
        
        ftf.setFont(f);
        ftf.setForeground(Color.GREEN);
        NumberEditor numEd = (NumberEditor)mySpinner.getEditor();
        numEd.getTextField().setBackground(Color.BLACK); 

        
        System.out.println("ftf background color is " + ftf.getBackground().toString());
        ftf.setValue(spinner.getValue());
        ftf.addPropertyChangeListener(this);
        ftf.setEditable(true); // VoiceOver expects that editor is editable.
        ftf.setInheritsPopupMenu(false);
        // When just the ftf has focus, you CAN use up and down arrows etc as below.
        String toolTipText = "Lou, you can use up and down arrows to change value. Use left and right to traverse digits. ";
        if (toolTipText != null) {
            ftf.setToolTipText(toolTipText);
        }

        add(ftf);

        setLayout(this);
        
        System.out.println("ftf after setLayout background color is " + ftf.getBackground().toString());
        spinner.addChangeListener(this);

        // We want the spinner's increment/decrement actions to be
        // active and also those of the JFormattedTextField. 
        ActionMap ftfMap = ftf.getActionMap();

        if (ftfMap != null) {
            ftfMap.put(INCREMENT, INCREMENT_ACTION);
            ftfMap.put(DECREMENT, DECREMENT_ACTION);

        }
    }


    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("new MaskFormatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    /**
     * Disconnect <code>this</code> editor from the specified
     * <code>JSpinner</code>.  By default, this method removes
     * itself from the spinners <code>ChangeListener</code> list.
     *
     * @param spinner the <code>JSpinner</code> to disconnect this
     *    editor from; the same spinner as was passed to the constructor.
     */
    public void dismiss(JSpinner spinner) {
        spinner.removeChangeListener(this);
    }


    /**
     * Returns the <code>JSpinner</code> ancestor of this editor or
     * <code>null</code> if none of the ancestors are a
     * <code>JSpinner</code>.
     * Typically the editor's parent is a <code>JSpinner</code> however
     * subclasses of <code>JSpinner</code> may override the
     * the <code>createEditor</code> method and insert one or more containers
     * between the <code>JSpinner</code> and it's editor.
     *
     * @return <code>JSpinner</code> ancestor; <code>null</code>
     *         if none of the ancestors are a <code>JSpinner</code>
     *
     * @see JSpinner#createEditor
     */
    public JSpinner getSpinner() {
        for (Component c = this; c != null; c = c.getParent()) {
            if (c instanceof JSpinner) {
                return (JSpinner)c;
            }
        }
        return null;
    }


    /**
     * Returns the <code>JFormattedTextField</code> child of this
     * editor.  By default the text field is the first and only
     * child of editor.
     *
     * @return the <code>JFormattedTextField</code> that gives the user
     *     access to the <code>SpinnerDateModel's</code> value.
     * @see #getSpinner
     * @see #getModel
     */
    public JFormattedTextField getTextField() {
        return (JFormattedTextField)getComponent(0);
    }


    /**
     * This method is called when the spinner's model's state changes.
     * It sets the <code>value</code> of the text field to the current
     * value of the spinners model.
     *
     * @param e the <code>ChangeEvent</code> whose source is the
     * <code>JSpinner</code> whose model has changed.
     * @see #getTextField
     * @see JSpinner#getValue
     */
    public void stateChanged(ChangeEvent e) {
        JSpinner spinner = (JSpinner)(e.getSource());
        getTextField().setValue(spinner.getValue());
    }


    /**
     * Called by the <code>JFormattedTextField</code>
     * <code>PropertyChangeListener</code>.  When the <code>"value"</code>
     * property changes, which implies that the user has typed a new
     * number, we set the value of the spinners model.
     * <p>
     * This class ignores <code>PropertyChangeEvents</code> whose
     * source is not the <code>JFormattedTextField</code>, so subclasses
     * may safely make <code>this</code> <code>DefaultEditor</code> a
     * <code>PropertyChangeListener</code> on other objects.
     *
     * @param e the <code>PropertyChangeEvent</code> whose source is
     *    the <code>JFormattedTextField</code> created by this class.
     * @see #getTextField
     */
    public void propertyChange(PropertyChangeEvent e)
    {
        JSpinner spinner = getSpinner();

        if (spinner == null) {
            // Indicates we aren't installed anywhere.
            return;
        }

        Object source = e.getSource();
        String name = e.getPropertyName();
        if (source instanceof JFormattedTextField) {
            if ("value".equals(name)) {
                Object lastValue = spinner.getValue();

                // Try to set the new value
                try {
                    spinner.setValue(getTextField().getValue());
                } catch (IllegalArgumentException iae) {
                    // SpinnerModel didn't like new value, reset
                    try {
                        ((JFormattedTextField)source).setValue(lastValue);
                    } catch (IllegalArgumentException iae2) {
                        // Still bogus, nothing else we can do, the
                        // SpinnerModel and JFormattedTextField are now out
                        // of sync.
                    }
                }
            } else if ("font".equals(name)) {
                Object newfont = e.getNewValue();
                if (newfont instanceof UIResource) {
                    // The text field font must match the JSpinner font if
                    // the text field font was not set by the user
                    Font font = spinner.getFont();
                    if (!newfont.equals(font)) {
                        getTextField().setFont(font == null ? null : new FontUIResource(font));
                    }
                }
            }
        }
    }


    /**
     * This <code>LayoutManager</code> method does nothing.  We're
     * only managing a single child and there's no support
     * for layout constraints.
     *
     * @param name ignored
     * @param child ignored
     */
    public void addLayoutComponent(String name, Component child) {
    }


    /**
     * This <code>LayoutManager</code> method does nothing.  There
     * isn't any per-child state.
     *
     * @param child ignored
     */
    public void removeLayoutComponent(Component child) {
    }


    /**
     * Returns the size of the parents insets.
     */
    private Dimension insetSize(Container parent) {
        Insets insets = parent.getInsets();
        int w = insets.left + insets.right;
        int h = insets.top + insets.bottom;
        return new Dimension(w, h);
    }


    /**
     * Returns the preferred size of first (and only) child plus the
     * size of the parents insets.
     *
     * @param parent the Container that's managing the layout
     * @return the preferred dimensions to lay out the subcomponents
     *          of the specified container.
     */
    public Dimension preferredLayoutSize(Container parent) {
        Dimension preferredSize = insetSize(parent);
        if (parent.getComponentCount() > 0) {
            Dimension childSize = getComponent(0).getPreferredSize();
            preferredSize.width += childSize.width;
            preferredSize.height += childSize.height;
        }
        return preferredSize;
    }


    /**
     * Returns the minimum size of first (and only) child plus the
     * size of the parents insets.
     *
     * @param parent the Container that's managing the layout
     * @return  the minimum dimensions needed to lay out the subcomponents
     *          of the specified container.
     */
    public Dimension minimumLayoutSize(Container parent) {
        Dimension minimumSize = insetSize(parent);
        if (parent.getComponentCount() > 0) {
            Dimension childSize = getComponent(0).getMinimumSize();
            minimumSize.width += childSize.width;
            minimumSize.height += childSize.height;
        }
        return minimumSize;
    }


    /**
     * Resize the one (and only) child to completely fill the area
     * within the parents insets.
     */
    public void layoutContainer(Container parent) {
        if (parent.getComponentCount() > 0) {
            Insets insets = parent.getInsets();
            int w = parent.getWidth() - (insets.left + insets.right);
            int h = parent.getHeight() - (insets.top + insets.bottom);
            getComponent(0).setBounds(insets.left, insets.top, w, h);
        }
    }

    /**
     * Pushes the currently edited value to the <code>SpinnerModel</code>.
     * <p>
     * The default implementation invokes <code>commitEdit</code> on the
     * <code>JFormattedTextField</code>.
     *
     * @throws ParseException if the edited value is not legal
     */
    public void commitEdit()  throws ParseException {
        // If the value in the JFormattedTextField is legal, this will have
        // the result of pushing the value to the SpinnerModel
        // by way of the <code>propertyChange</code> method.
        JFormattedTextField ftf = getTextField();

        ftf.commitEdit();
    }

    /**
     * Returns the baseline.
     *
     * @throws IllegalArgumentException {@inheritDoc}
     * @see javax.swing.JComponent#getBaseline(int,int)
     * @see javax.swing.JComponent#getBaselineResizeBehavior()
     * @since 1.6
     */
    public int getBaseline(int width, int height) {
        // check size.
        super.getBaseline(width, height);
        Insets insets = getInsets();
        width = width - insets.left - insets.right;
        height = height - insets.top - insets.bottom;
        int baseline = getComponent(0).getBaseline(width, height);
        if (baseline >= 0) {
            return baseline + insets.top;
        }
        return -1;
    }

    /**
     * Returns an enum indicating how the baseline of the component
     * changes as the size changes.
     *
     * @throws NullPointerException {@inheritDoc}
     * @see javax.swing.JComponent#getBaseline(int, int)
     * @since 1.6
     */
    public Component.BaselineResizeBehavior getBaselineResizeBehavior() {
        return getComponent(0).getBaselineResizeBehavior();
    }
    
    
    private class IncrementAction implements Action {
        private boolean isEnabled = true;
        
        public Object getValue(String key) {
            return INCREMENT;
        }
        public void putValue(String key, Object value) {
        }
        public void setEnabled(boolean b) {
            isEnabled = b;
        }
        public boolean isEnabled() {
            return isEnabled;
        }
        public void addPropertyChangeListener(PropertyChangeListener l) {
        }
        public void removePropertyChangeListener(PropertyChangeListener l) {
        }
        public void actionPerformed(ActionEvent ae) {
            Object nextValue = mySpinner.getNextValue();
            AccessibleEditor editor = (AccessibleEditor) mySpinner.getEditor();
            editor.getTextField().setValue(nextValue);
            try {
                editor.commitEdit();
            } catch (ParseException e) {
                System.err.println(e);               
            }
        }
    }

    
    private  class DecrementAction implements Action {
        private boolean isEnabled = true;
        
        public Object getValue(String key) {
            return DECREMENT;
        }
        public void putValue(String key, Object value) {
        }
        public void setEnabled(boolean b) {
            isEnabled = b;
        }
        public boolean isEnabled() {
            return isEnabled;
        }
        public void addPropertyChangeListener(PropertyChangeListener l) {
        }
        public void removePropertyChangeListener(PropertyChangeListener l) {
        }
        public void actionPerformed(ActionEvent ae) {
            Object nextValue = mySpinner.getPreviousValue();
            AccessibleEditor editor = (AccessibleEditor) mySpinner.getEditor();
            editor.getTextField().setValue(nextValue);
            try {
                editor.commitEdit();
            } catch (ParseException e) {
                System.err.println(e);               
            }
        }
    }
}
        




