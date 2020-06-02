/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package VfoPrototype;


import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFormattedTextField;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import static java.lang.reflect.Array.set;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.KeyStroke;
import java.util.prefs.*;


/**
 * Implements a prototype replacement for the JRX app VFO control to enhance the
 * accessibility adding frequency updates as the digits are manipulated.
 * @see VfoDisplayControl class.
 * 
 * @author Coz
 */
final public class VfoPrototype2 extends javax.swing.JFrame {
    public static String version = "Version 2.1.4";
    VfoDisplayControl vfoGroup;
    protected Preferences prefs;

    /**
     * Creates new form VfoPrototype which is used as a testbed for the multi-
     * digit VfoDisplayControl which is designed to be blind accessible.
     */
    public VfoPrototype2() {
        try {
            initComponents();
        } catch(Exception e) {
            e.printStackTrace();
        }      
    }
    
    public void setUpVfoComponents() {
        setTitle("VFO Prototype "+version);
        // Create an Prefernces object for access to this user's preferences.
        prefs = Preferences.userNodeForPackage(this.getClass()); 
        this.setResizable(false);
        // Must instantiate components before initialization of VfoDisplayControl.
        vfoGroup = (VfoDisplayControl) digitsParent;
        vfoGroup.initDigits();       
        vfoGroup.setupPanes();        
        vfoGroup.addMenuBar();       
        vfoGroup.makeVisible();
        // Cause the ones digit ftf to get the focus when the JFrame gets focus.                              
        JFormattedTextField textField;
        Vector<Component> order = vfoGroup.getTraversalOrder();
        textField = (JFormattedTextField) order.get(0);
         this.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                textField.requestFocusInWindow();
            }
        });
        // Set up TestInfo GroupBox.
        jInternalFrame1.setFocusable(true);
        
        
        // Associate labels with fields for accessibility.
        jLabel1.setLabelFor(frequencyVfoA);
        jLabel2.setLabelFor(frequencyVfoB);
    }
    
    @SuppressWarnings("unchecked")
    public void setUpFocusManager() {
        // Make sure that the JFrame is focus manager.
        // It appears that voiceOver StepInto is ignoring focus manager.
        setFocusCycleRoot(true);
        VfoDigitTraversalPolicy policy; 
        Vector<Component> order = vfoGroup.getTraversalOrder();
        policy = new VfoDigitTraversalPolicy(order);
        setFocusTraversalPolicy(policy);
        setFocusTraversalPolicyProvider(true);
        setFocusable(true);
        setVisible(true);
        // Add focus traverse keys left and right arrow.
        // In this case, FORWARD is to the left.
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

        Set set = new HashSet<>( 
            getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS ) );
       
        final AWTKeyStroke keyStrokeRight = KeyStroke.getKeyStroke( "RIGHT");
        set.add(keyStrokeRight) ;
        setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
        
        set = new HashSet<>( getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS ) );
        final AWTKeyStroke keyStrokeLeft = KeyStroke.getKeyStroke( "LEFT" );           
        set.add(keyStrokeLeft);
        setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set );
        setFocusTraversalKeysEnabled(true);
                    
        assert(areFocusTraversalKeysSet(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS) );
        
        // The JFrame is the root of all the traversal.  Add up and down to it.
        // Add the focus traversal keys for up cycle and down cycle.
        // Use Option UpArrow for up cycle and Option DownArrow for down cycle.
        // On MACosx ALT is OPT.
        setFocusTraversalKeys(
                KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, null);
        setFocusTraversalKeys(
                KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, null);
        
        set = new HashSet<>( getFocusTraversalKeys(
            KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS ) );
        final AWTKeyStroke keyStrokeUp =  KeyStroke.getKeyStroke( 
                KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK);  
        set.add( keyStrokeUp);
        setFocusTraversalKeys(
             KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, set);
        
        final AWTKeyStroke keyStrokeDown =  KeyStroke.getKeyStroke(
                KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK);               
        set = new HashSet<>( getFocusTraversalKeys(
            KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS ) );        
        set.add( keyStrokeDown );
        setFocusTraversalKeys(
                 KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, set);      
                
        // Hold onto your hats, we're takin over the reins.
        setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() );
        ContainerOrderFocusTraversalPolicy fPolicy = 
                (ContainerOrderFocusTraversalPolicy) getFocusTraversalPolicy();
        // Docs say that if you provide up/down traversal, you must do this:
        fPolicy.setImplicitDownCycleTraversal(false);
         //requestFocusInWindow(boolean temporary)
        assert( getFocusTraversalPolicy() != null);
        assert( isFocusCycleRoot());
        setEnabled(true);
             
    }       
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        digitsParent = new VfoDisplayControl(this);
        jLayeredPaneMegahertz = new javax.swing.JLayeredPane();
        jLayeredPaneKilohertz = new javax.swing.JLayeredPane();
        jLayeredPaneHertz = new javax.swing.JLayeredPane();
        jInternalFrame1 = new GroupBox();
        jLabel1 = new javax.swing.JLabel();
        frequencyVfoB = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        frequencyVfoA = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VFO Display Prototype");
        setFocusTraversalPolicyProvider(true);
        setFocusable(false);

        digitsParent.setVisible(true);
        digitsParent.getContentPane().setLayout(new java.awt.FlowLayout());

        jLayeredPaneMegahertz.setBackground(new java.awt.Color(255, 255, 255));
        jLayeredPaneMegahertz.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(0, 255, 0)), "Megahertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP)); // NOI18N
        jLayeredPaneMegahertz.setForeground(new java.awt.Color(0, 255, 0));
        jLayeredPaneMegahertz.setToolTipText("");
        jLayeredPaneMegahertz.setOpaque(true);
        jLayeredPaneMegahertz.setPreferredSize(new java.awt.Dimension(267, 120));
        jLayeredPaneMegahertz.setLayout(new java.awt.FlowLayout());
        digitsParent.getContentPane().add(jLayeredPaneMegahertz);

        jLayeredPaneKilohertz.setBackground(new java.awt.Color(255, 255, 255));
        jLayeredPaneKilohertz.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(0, 255, 0)), "Kilohertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP)); // NOI18N
        jLayeredPaneKilohertz.setForeground(new java.awt.Color(0, 255, 0));
        jLayeredPaneKilohertz.setOpaque(true);
        jLayeredPaneKilohertz.setPreferredSize(new java.awt.Dimension(200, 120));
        jLayeredPaneKilohertz.setLayout(new java.awt.FlowLayout());
        digitsParent.getContentPane().add(jLayeredPaneKilohertz);

        jLayeredPaneHertz.setBackground(new java.awt.Color(255, 255, 255));
        jLayeredPaneHertz.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Hertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));
        jLayeredPaneHertz.setForeground(new java.awt.Color(0, 255, 0));
        jLayeredPaneHertz.setToolTipText("VFO Hertz digits");
        jLayeredPaneHertz.setName("VFO  zero to 1Khz panel"); // NOI18N
        jLayeredPaneHertz.setOpaque(true);
        jLayeredPaneHertz.setPreferredSize(new java.awt.Dimension(155, 120));
        jLayeredPaneHertz.setLayout(new java.awt.FlowLayout());
        digitsParent.getContentPane().add(jLayeredPaneHertz);
        jLayeredPaneHertz.getAccessibleContext().setAccessibleName("Hertz VFO panel");

        jInternalFrame1.setVisible(true);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Radio VFO A, readOnly  Mhz");

        frequencyVfoB.setEditable(false);
        frequencyVfoB.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoB.setText("1296100000");
        frequencyVfoB.setToolTipText("VFO B Frequency Mhz");
        frequencyVfoB.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Radio VFO B, readOnly Mhz");

        frequencyVfoA.setEditable(false);
        frequencyVfoA.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoA.setText("5446200000");
        frequencyVfoA.setToolTipText("VFO A Frequency Mhz");
        frequencyVfoA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        frequencyVfoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frequencyVfoAActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(frequencyVfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(frequencyVfoB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(frequencyVfoA, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                    .addComponent(frequencyVfoB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        frequencyVfoB.getAccessibleContext().setAccessibleName("Frequency of VFO B Mhz");
        frequencyVfoB.getAccessibleContext().setAccessibleDescription("Simulates Radio VFO B Display");
        frequencyVfoA.getAccessibleContext().setAccessibleName("Frequency of VFO A Mhz");
        frequencyVfoA.getAccessibleContext().setAccessibleDescription("Simulates Radio VFO A Display");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jInternalFrame1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(digitsParent))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(digitsParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 26, Short.MAX_VALUE))
        );

        jInternalFrame1.getAccessibleContext().setAccessibleName("Test Info");
        jInternalFrame1.getAccessibleContext().setAccessibleDescription("This group is for testing VFO settings.");

        getAccessibleContext().setAccessibleName("VFO Display prototype App");
        getAccessibleContext().setAccessibleDescription("Each VFO digit is a spinner responding to up down arrows.");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void frequencyVfoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frequencyVfoAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_frequencyVfoAActionPerformed
    
    /**
     * Nimbus gives a nice outline to the keyboard focus component;  Mac OS X
     * does not.
     * 
     * @param args the command line arguments
     * 
     */
    @SuppressWarnings("empty-statement")
    public static void main(String args[])  {        
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            try {           
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
                if ("Mac OS X".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());                
                    break;
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(VfoPrototype2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(VfoPrototype2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(VfoPrototype2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(VfoPrototype2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }    
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {           
            public void run() {
                
                VfoPrototype2 frame = new VfoPrototype2();
                frame.setUpVfoComponents();
                frame.setUpFocusManager();
                // Give the ones digit JFormattedTextField focus upon opening window.
                Vector<Component> order =  VfoDisplayControl.getTraversalOrder();
                order.get(0).requestFocusInWindow();
                frame.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JInternalFrame digitsParent;
    public javax.swing.JTextField frequencyVfoA;
    public javax.swing.JTextField frequencyVfoB;
    public javax.swing.JInternalFrame jInternalFrame1;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLayeredPane jLayeredPaneHertz;
    public javax.swing.JLayeredPane jLayeredPaneKilohertz;
    public javax.swing.JLayeredPane jLayeredPaneMegahertz;
    // End of variables declaration//GEN-END:variables

}
