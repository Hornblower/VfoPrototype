/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;

/**
 * Provides a transparent look and feel implementation for
 * <code>JFormattedTextField</code>.
 *
 * 
 * @author Coz
 */
public class JrxBasicFormattedTextFieldUI extends BasicFormattedTextFieldUI {

    /**
     * Creates a transparent background UI for a JFormattedTextField.
     *
     * @param c the formatted text field
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new JrxBasicFormattedTextFieldUI();
    }
    /**
     * Fetches the name used as a key to lookup properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name "FormattedTextField"
     */
    protected String getPropertyPrefix() {
        return "FormattedTextField";
    }
}


