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


import static VfoPrototype.VfoDisplayControl.LAST_VFO;
import static VfoPrototype.VfoDisplayControl.VFO_SELECT_A_TEXT;
import static VfoPrototype.VfoDisplayControl.VFO_SELECT_B_TEXT;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFormattedTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.KeyStroke;
import java.util.prefs.*;
import javax.accessibility.AccessibleContext;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;


/**
 * Implements a prototype replacement for the JRX app VFO control to enhance the
 * accessibility adding frequency updates as the digits are manipulated.
 * @see VfoDisplayControl class.
 * 
 * @author Coz
 */
final public class VfoPrototype2 extends javax.swing.JFrame  implements ItemListener , ActionListener {
    public static String version = "Version 2.2.0";
    VfoDisplayControl vfoGroup;
    protected Preferences prefs;
    JMenuBar menuBar;
    public JRadioButtonMenuItem menuItemA;
    public JRadioButtonMenuItem menuItemB;
    VfoSelectionInterface vfoState;
    protected boolean inhibit = true;

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
        setBounds(0,0,648,320);
        setResizable(true);
        Dimension minSize = new Dimension(500,300);
        this.setMinimumSize(minSize);
        // Use the Mac OSX menuBar at the top of the screen.
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        addMenuBar(); // Need to have menu created before setupPanes();       
        // Add an exclusive interface to the Vfo selector so that only one thread
        // at a time gains access.
        vfoState = new VfoSelectionInterface(menuItemA, menuItemB,
            frequencyVfoA, frequencyVfoB );
 
        // @todo Later we will get these from Preferences.  When do we save a freq?
        vfoState.writeFrequencyToRadioVfoA(vfoGroup.MSN_FREQ);
        vfoState.writeFrequencyToRadioVfoB(vfoGroup.SHAWSVILLE_REPEATER_OUTPUT_FREQ);
      
          // @todo Add this later with stored frequency of the selected vfo.
        String lastVfo = prefs.get("LAST_VFO", "VFO_SELECT_A_TEXT");
        if ( lastVfo == null) {
            // There is no history.
            // Vfo A is arbitrary default,
            vfoState.setVfoASelected();
        } else if ( lastVfo.equals(VFO_SELECT_A_TEXT)) {
            vfoState.setVfoASelected();
        } else if ( lastVfo.equals(VFO_SELECT_B_TEXT)) {
            vfoState.setVfoBSelected();
        } else {
            // Do no recognize the entry.
            System.out.println("Unrecognized preference :"+lastVfo);
            vfoState.setVfoASelected();
        }        
        // Must instantiate components before initialization of VfoDisplayControl.
        vfoGroup = (VfoDisplayControl) digitsParent;
        vfoGroup.initDigits();       
        vfoGroup.setupPanes();                      
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
        // Set up TestInfo GroupBox.  This is an example of grouping accessible
        // controls to make an accessibility tree that uses few key strokes.
        jInternalFrame1.setFocusable(true);       
        String infoStr  = "<html>VFO Display Digits can be <br>"+
                                "adjusted using up/down <br>"+
                                "arrows, left click and the<br>"+
                                "scroll wheel.             <br>"+
                                "Left/right arrows traverse<br>"+
                                "digits.  ";
        keysInfo.setText(infoStr);
        // Associate labels with fields for accessibility.
        jLabel1.setLabelFor(frequencyVfoA);
        jLabel2.setLabelFor(frequencyVfoB);        
    }
    
    /**
     * Create the menu bar for the display and add menu items to operate the
     * VFO selection and copy tasks.
     */        
    protected void addMenuBar() {    
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        //Build the first menu.
        JMenu menu = new JMenu("Choose Radio VFO Operation");
        menu.setMnemonic(KeyEvent.VK_V);
        AccessibleContext menuContext = menu.getAccessibleContext();
        menuContext.setAccessibleDescription(
            "Pick the radio VFO that the VFO Panel controls");
        menuContext.setAccessibleName("Choose Radio VFO");
        menuBar.add(menu);
        
        //Set JMenuItem A.
        menuItemA = new JRadioButtonMenuItem(VFO_SELECT_A_TEXT, true);
        menuItemA.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        AccessibleContext itemAContext = menuItemA.getAccessibleContext();
        itemAContext.setAccessibleDescription(
            "VFO panel controls radio VFO A");
        itemAContext.setAccessibleName("Choose radio VFO A");       
        menuItemA.addItemListener(this);
        menu.add(menuItemA);
        //Set JMenuItem B.
        menuItemB = new JRadioButtonMenuItem(VFO_SELECT_B_TEXT, false);
        menuItemB.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_B, ActionEvent.ALT_MASK));
        AccessibleContext itemBContext = menuItemB.getAccessibleContext();
        itemBContext.setAccessibleDescription(
            "VFO panel controls radio VFO B");
        itemBContext.setAccessibleName("Choose radio VFO B");
        menuItemB.addItemListener(this);
        menu.add(menuItemB);
        // Add VFO "copy" menu items.
        menu.addSeparator();
        JMenuItem a2b = new JMenuItem("Copy VFO A to VFO B", KeyEvent.VK_C);
        AccessibleContext a2bContext = a2b.getAccessibleContext();
        a2bContext.setAccessibleName("Copy Vfo A to Vfo B");
        a2bContext.setAccessibleDescription("Use shortcut key option C");
        a2b.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK));
        a2b.addItemListener(this);
        a2b.addActionListener(this);
        menu.add(a2b);
        JMenuItem swap = new JMenuItem("Swap VFO A with VFO B", KeyEvent.VK_S);
        AccessibleContext swapContext = a2b.getAccessibleContext();
        swapContext.setAccessibleName("Swap Vfo A with Vfo B");
        swapContext.setAccessibleDescription("Use shortcut key option S");
        swap.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));
        swap.addItemListener(this);
        swap.addActionListener(this);
        menu.add(swap);
        
        }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jInternalFrame1 = new GroupBox();
        jLabel1 = new javax.swing.JLabel();
        frequencyVfoB = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        frequencyVfoA = new javax.swing.JTextField();
        keysInfo = new javax.swing.JLabel();
        digitsParent = new VfoDisplayControl(this);
        jLayeredPaneMegahertz = new javax.swing.JLayeredPane();
        jLayeredPaneKilohertz = new javax.swing.JLayeredPane();
        jLayeredPaneHertz = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VFO Display Prototype");
        setFocusTraversalPolicyProvider(true);
        setFocusable(false);

        jInternalFrame1.setVisible(true);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setLabelFor(frequencyVfoA);
        jLabel1.setText("Radio VFO A, Mhz");

        frequencyVfoB.setEditable(false);
        frequencyVfoB.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoB.setText("1296100000");
        frequencyVfoB.setToolTipText("");
        frequencyVfoB.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        frequencyVfoB.setVerifyInputWhenFocusTarget(false);

        jLabel2.setLabelFor(frequencyVfoB);
        jLabel2.setText("Radio VFO B, Mhz");

        frequencyVfoA.setEditable(false);
        frequencyVfoA.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoA.setText("5446200000");
        frequencyVfoA.setToolTipText("");
        frequencyVfoA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        frequencyVfoA.setVerifyInputWhenFocusTarget(false);
        frequencyVfoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frequencyVfoAActionPerformed(evt);
            }
        });

        keysInfo.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        keysInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        keysInfo.setText("<html>VFO Display Digits can be<br>incremented using up down<br>arrows and left right arrows<br>traverse digits.");
        keysInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(frequencyVfoB, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addComponent(frequencyVfoA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(keysInfo)
                .addContainerGap())
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(keysInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jInternalFrame1Layout.createSequentialGroup()
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(frequencyVfoA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleName("Radio VFO A,  Mhz");
        frequencyVfoB.getAccessibleContext().setAccessibleName("Frequency of VFO B Mhz");
        frequencyVfoB.getAccessibleContext().setAccessibleDescription("REED ONLY, DISPLAY");
        frequencyVfoA.getAccessibleContext().setAccessibleName("Frequency of VFO A Mhz");
        frequencyVfoA.getAccessibleContext().setAccessibleDescription("REED ONLY, DISPLAY");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame1)
            .addComponent(digitsParent, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(digitsParent, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
        );

        jInternalFrame1.getAccessibleContext().setAccessibleName("Test Info");
        jInternalFrame1.getAccessibleContext().setAccessibleDescription("This group is for testing VFO settings.");

        getAccessibleContext().setAccessibleName("VFO Display prototype App");
        getAccessibleContext().setAccessibleDescription("Each VFO digit  responds to up down arrows.");

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
                frame.vfoGroup.setUpFocusManager();
                // Give the ones digit JFormattedTextField focus upon opening window.
                Vector<Component> order =  VfoDisplayControl.getTraversalOrder();
                order.get(0).requestFocusInWindow();
                frame.setVisible(true);
                frame.inhibit = false;
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
    public javax.swing.JLabel keysInfo;
    // End of variables declaration//GEN-END:variables

   @Override
    public void actionPerformed(ActionEvent e) {
        String actionString = e.getActionCommand();
        if (actionString == "Copy VFO A to VFO B") {
            vfoState.copyAtoB();
                 
            if (!vfoState.vfoA_IsSelected()) {
                long freqA = vfoState.getVfoAFrequency();
                vfoGroup.frequencyToDigits(freqA);
            }
            JOptionPane.showMessageDialog(this,
                    "VFO A copied to VFO B",
                    "VFO A copied to VFO B",  // VoiceOver reads only this line.
                    JOptionPane.PLAIN_MESSAGE);
        } else if (actionString == "Swap VFO A with VFO B") {
            vfoState.swapAwithB();
                 
            if (vfoState.vfoA_IsSelected()) {
                long freqA = vfoState.getVfoAFrequency();
                vfoGroup.frequencyToDigits(freqA);
            } else {
                long freqB = vfoState.getVfoBFrequency();
                vfoGroup.frequencyToDigits(freqB);
                
            }
            JOptionPane.showMessageDialog(this,
                    "VFO A swapped with VFO B",
                    "VFO A swapped with VFO B",  // VoiceOver reads only this line.
                    JOptionPane.PLAIN_MESSAGE);            
        }
    } 
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (inhibit) return;        
        
        Object itemObj = e.getItem();
        JMenuItem item = (JMenuItem) itemObj;
        String itemText = item.getText();
        //System.out.println("item.name :"+itemText);
        if (itemText.equals(VFO_SELECT_A_TEXT)) {
            //item.firePropertyChange("MENU_ITEM1", false, true);
            if (item.isSelected()) {
                vfoState.setVfoASelected();
                prefs.put(LAST_VFO, VFO_SELECT_A_TEXT);
                // If voiceOver enabled, need this dialog to announce vfo change.
                JOptionPane.showMessageDialog(this,
                    "VFO A Selected", // VoiceOver does not read the text in dialog.
                    "VFO A Selected", // VoiceOver reads only this line, the title.
                    JOptionPane.PLAIN_MESSAGE);
            }           
        } else if (itemText.equals(VFO_SELECT_B_TEXT)) {
            //item.firePropertyChange("MENU_ITEM1", false, true);
            if (item.isSelected()) {
                vfoState.setVfoBSelected();
                prefs.put(LAST_VFO, VFO_SELECT_B_TEXT);
                // If voiceOver enabled, need this dialog to announce vfo change.
                JOptionPane.showMessageDialog(this,
                    "VFO B Selected",
                    "VFO B Selected",  // VoiceOver reads only this line.
                    JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("Unknown menu item handled in itemStateChanged()");
        }
        long freq = vfoState.getSelectedVfoFrequency();
        vfoGroup.frequencyToDigits(freq);
    }

}
