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


import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleValue;
import javax.swing.JFormattedTextField;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;


/**
 * Implements a prototype replacement for the JRX app VFO control to enhance the
 * accessibility adding frequency updates as the digits are manipulated.
 * 
 * @author Coz
 */
final public class VfoPrototype2 extends javax.swing.JFrame {
    public static String version = "Version 2.0.0";
    static public VfoPrototype2 singletonInstance;
    static boolean wasVfoA = true;
    static boolean chooseVfoA = true;
    static boolean chooseVfoB = false;
    VfoDisplayControl vfoGroup;
    public VfoSelectStateMachine vfoState;
    
    long freqVfoA = 3563000;   // MSN 
    long freqVfoB = 145330000; // Shawsville Repeater

    /**
     * Creates new form VfoPrototype.
     */
    public VfoPrototype2() {
        singletonInstance = this;    
        try {
            initComponents();
        } catch(Exception e) {
            e.printStackTrace();
        }      
    }
    
    public void setUpVfoComponents() {
        singletonInstance.setTitle("VFO Prototype Display Control "+version);
        // Must instantiate components before initialization of VfoDisplayControl.
        vfoGroup = (VfoDisplayControl) digitsParent;
        vfoGroup.initDigits();
        vfoGroup.frequencyToDigits(freqVfoA); // Vfo A is arbitrary default.
        vfoState = new VfoSelectStateMachine(VfoSelection);
        vfoState.setVfoASelected(); // Vfo A is arbitrary default, later will persist.
        vfoState.writeFrequencyToRadioVfoA(freqVfoA);
        vfoState.writeFrequencyToRadioVfoB(freqVfoB);
        
        //Make ones digit textField get the focus whenever frame is activated.
        
        JFormattedTextField textField;
        Vector<Component> order = vfoGroup.getTraversalOrder();
        textField = (JFormattedTextField) order.get(0);
        // Cause the ftf to get the focus when the JFrame gets focus.        
        this.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                textField.requestFocusInWindow();
            }
        });

        // @todo Coz, make VFO panel extend JInternalFrame instead.
        // Make sure that the VfoControlFrame is focus manager.
        // It appears that voiceOver StepInto is ignoring focus manager.
        singletonInstance.setFocusCycleRoot(true);
        VfoDigitTraversalPolicy policy; 
        policy = new VfoDigitTraversalPolicy(order);
        singletonInstance.setFocusTraversalPolicy(policy);
        singletonInstance.setFocusTraversalPolicyProvider(true);
        singletonInstance.setFocusable(true);
        singletonInstance.setVisible(true);
        // Add focus traverse keys left and right arrow.
        // In this case, FORWARD is to the left.
        singletonInstance.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        singletonInstance.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        Set set = new HashSet( getFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS ) );
        set.add( KeyStroke.getKeyStroke( "RIGHT" ) );
        singletonInstance.setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set );

        set = new HashSet( getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS ) );
        set.add( KeyStroke.getKeyStroke( "LEFT" ) );
        singletonInstance.setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set );
        singletonInstance.setFocusTraversalKeysEnabled(true);

        assert(singletonInstance.areFocusTraversalKeysSet(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS) );
        
        // The JFrame is the root of all the traversal.  Add up and down to it.
        // Add the focus traversal keys for up cycle and down cycle.
        // Use Option UpArrow for up cycle and Option DownArrow for down cycle.
        // I guess that ALT and OPT are the same thing....
        singletonInstance.setFocusTraversalKeys(
                KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, null);
        singletonInstance.setFocusTraversalKeys(
                KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, null);
        set = new HashSet( getFocusTraversalKeys(
            KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS ) );
        set.add( KeyStroke.getKeyStroke( KeyEvent.VK_UP, InputEvent.ALT_MASK) );
        singletonInstance.setFocusTraversalKeys(
                 KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, set);
        
        set = new HashSet( getFocusTraversalKeys(
            KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS ) );
        set.add( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, InputEvent.ALT_MASK) );
        singletonInstance.setFocusTraversalKeys(
                 KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, set);
        
        
        // Hold onto your hats, we're takin over the reins.
        singletonInstance.setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() );
        ContainerOrderFocusTraversalPolicy fPolicy = (ContainerOrderFocusTraversalPolicy) singletonInstance.getFocusTraversalPolicy();
        
        // ContainerOrderFocusTraversalPolicy fPolicy = (ContainerOrderFocusTraversalPolicy) singletonInstance.getFocusTraversalPolicy();
        // Docs say that if you provide up/down traversal, you must do this:
        fPolicy.setImplicitDownCycleTraversal(false);
         //requestFocusInWindow(boolean temporary)
        assert( singletonInstance.getFocusTraversalPolicy() != null);
        assert( singletonInstance.isFocusCycleRoot());
        singletonInstance.setEnabled(true);
        
        if (false) {  // This code does not work.  Why?
        
            // Register shortcut keys for radio buttons with JFrame.rootPane
            KeyStroke strokeA = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_MASK);
            rootPane.registerKeyboardAction( new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        vfoState.setVfoASelected();
                        loadRadioFrequencyToVfo(chooseVfoA);
                    }
                }, strokeA, JComponent.WHEN_IN_FOCUSED_WINDOW);

            KeyStroke strokeB = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.ALT_MASK);
            rootPane.registerKeyboardAction( new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        vfoState.setVfoBSelected();
                        loadRadioFrequencyToVfo(chooseVfoB);
                    }
                }, strokeB, JComponent.WHEN_IN_FOCUSED_WINDOW);   
        }

        
    }
    
    
    /**
     * Method called when the vfo radio button changes the VFO selection with
     * the requirement to update the VFO display control with the
     * frequency read from the radio VFO.
     * 
     * Need to turn off the VFO change handler while this
     * update takes place.
     * 
     * @param isVfoA
     * @return 
     */
    public boolean loadRadioFrequencyToVfo(boolean isVfoA) {       
        VfoDisplayControl vfoGroup = (VfoDisplayControl) digitsParent;
        vfoGroup.setSilent(true);
        boolean success = true;
        long freqHertz;
        String valString;
        // Change radio button state.
        // Simlate read freq from Radio.
        if ( isVfoA && !wasVfoA) {
            // Only take action when Vfo selection has changed.
            // Read frequency from Radio VFO A.
            freqHertz = vfoState.getVfoAFrequency();
            
            vfoGroup.frequencyToDigits(freqHertz);
            wasVfoA = true;
        } else if (!isVfoA && wasVfoA) {
            // Only take action when Vfo selection has changed.
            // Read frequencyl from Radio VFO B.
            freqHertz = vfoState.getVfoBFrequency();
            vfoGroup.frequencyToDigits(freqHertz);
            wasVfoA = false;
        }
        vfoGroup.getTraversalOrder().get(0).requestFocus();
        vfoGroup.setSilent(false);       
        return success;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VfoSelection = new javax.swing.ButtonGroup();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        VfoA = new javax.swing.JRadioButton();
        VfoB = new javax.swing.JRadioButton();
        frequencyVfoA = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        frequencyVfoB = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        digitsParent = new VfoDisplayControl(singletonInstance);
        jLayeredPaneMegahertz = new javax.swing.JLayeredPane();
        jLayeredPaneKilohertz = new javax.swing.JLayeredPane();
        jLayeredPaneHertz = new javax.swing.JLayeredPane();

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VFO Display Prototype");
        setFocusTraversalPolicyProvider(true);
        setFocusable(false);

        VfoSelection.add(VfoA);
        VfoA.setMnemonic('A');
        VfoA.setText("VFO A is controlled by VFO Display Control");
        VfoA.setToolTipText("copies radio VFO A to control digits");
        VfoA.setRequestFocusEnabled(false);
        VfoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VfoAActionPerformed(evt);
            }
        });

        VfoSelection.add(VfoB);
        VfoB.setMnemonic('B');
        VfoB.setText("VFO B is controlled by VFO Display Control");
        VfoB.setToolTipText("copies radio VFO B to control digits");
        VfoB.setActionCommand("Connect VFO Control to radio VFO B ");
        VfoB.setRequestFocusEnabled(false);
        VfoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VfoBActionPerformed(evt);
            }
        });

        frequencyVfoA.setEditable(false);
        frequencyVfoA.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoA.setText("5446200000");
        frequencyVfoA.setToolTipText("VFO A Frequency Mhz");
        frequencyVfoA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        frequencyVfoA.setNextFocusableComponent(VfoA);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Radio VFO A, readOnly current frequency Mhz");

        frequencyVfoB.setEditable(false);
        frequencyVfoB.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoB.setText("1296100000");
        frequencyVfoB.setToolTipText("VFO B Frequency Mhz");
        frequencyVfoB.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Radio VFO B, readOnly current frequency Mhz");

        digitsParent.setVisible(true);
        digitsParent.getContentPane().setLayout(new java.awt.FlowLayout());

        jLayeredPaneMegahertz.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Megahertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP));
        jLayeredPaneMegahertz.setToolTipText("");
        jLayeredPaneMegahertz.setOpaque(true);
        jLayeredPaneMegahertz.setPreferredSize(new java.awt.Dimension(270, 130));
        jLayeredPaneMegahertz.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                digitsPanelAncestorResized(evt);
            }
        });
        jLayeredPaneMegahertz.setLayout(new java.awt.FlowLayout());
        digitsParent.getContentPane().add(jLayeredPaneMegahertz);

        jLayeredPaneKilohertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kilohertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP));
        jLayeredPaneKilohertz.setOpaque(true);
        jLayeredPaneKilohertz.setPreferredSize(new java.awt.Dimension(200, 130));
        jLayeredPaneKilohertz.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                digitsPanelAncestorResized(evt);
            }
        });
        jLayeredPaneKilohertz.setLayout(new java.awt.FlowLayout());
        digitsParent.getContentPane().add(jLayeredPaneKilohertz);

        jLayeredPaneHertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP));
        jLayeredPaneHertz.setToolTipText("VFO Hertz digits");
        jLayeredPaneHertz.setName("VFO  zero to 1Khz panel"); // NOI18N
        jLayeredPaneHertz.setOpaque(true);
        jLayeredPaneHertz.setPreferredSize(new java.awt.Dimension(168, 130));
        jLayeredPaneHertz.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                digitsPanelAncestorResized(evt);
            }
        });
        jLayeredPaneHertz.setLayout(new java.awt.FlowLayout());
        digitsParent.getContentPane().add(jLayeredPaneHertz);
        jLayeredPaneHertz.getAccessibleContext().setAccessibleName("Hertz VFO panel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(frequencyVfoA)
                    .addComponent(VfoA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VfoB))
                .addGap(17, 17, 17))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(digitsParent))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frequencyVfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VfoB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(digitsParent, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        VfoA.getAccessibleContext().setAccessibleDescription("get VFO A frequency from radio and adjust");
        VfoB.getAccessibleContext().setAccessibleDescription("Get VFO B frequency from radio and adjust");
        frequencyVfoA.getAccessibleContext().setAccessibleName("Frequency of VFO A Mhz");
        frequencyVfoA.getAccessibleContext().setAccessibleDescription("Simulates Radio VFO A Display");
        frequencyVfoB.getAccessibleContext().setAccessibleName("Frequency of VFO B Mhz");
        frequencyVfoB.getAccessibleContext().setAccessibleDescription("Simulates Radio VFO B Display");

        getAccessibleContext().setAccessibleName("VFO Display prototype App");
        getAccessibleContext().setAccessibleDescription("Each VFO digit is a spinner responding to up down arrows.");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void VfoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VfoBActionPerformed
        loadRadioFrequencyToVfo(chooseVfoB);
    }//GEN-LAST:event_VfoBActionPerformed

    private void VfoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VfoAActionPerformed
        loadRadioFrequencyToVfo(chooseVfoA);
    }//GEN-LAST:event_VfoAActionPerformed

    private void digitsPanelAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_digitsPanelAncestorResized
        Component comp = evt.getComponent();
        JLayeredPane pane = (JLayeredPane) comp;
        VfoDisplayControl control = (VfoDisplayControl) digitsParent;
        control.adjustSize(pane);
        
    }//GEN-LAST:event_digitsPanelAncestorResized
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.LookAndFeelInfo[] installedLookAndFeels=javax.swing.UIManager.getInstalledLookAndFeels();
            for (int idx=0; idx<installedLookAndFeels.length; idx++)
                if ("Nimbus".equals(installedLookAndFeels[idx].getName())) {
                    javax.swing.UIManager.setLookAndFeel(installedLookAndFeels[idx].getClassName());
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
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {           
            public void run() {
                
                VfoPrototype2 frame = new VfoPrototype2();
                frame.setUpVfoComponents();
                // Give the ones digit JFormattedTextField focus upon opening window.
                Vector<Component> order =  singletonInstance.vfoGroup.getTraversalOrder();
                order.get(0).requestFocusInWindow();
                frame.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JRadioButton VfoA;
    public javax.swing.JRadioButton VfoB;
    public javax.swing.ButtonGroup VfoSelection;
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
