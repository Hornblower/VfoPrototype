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


import java.awt.Color;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import java.text.DecimalFormat;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleValue;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;


/**
 * Implements a prototype replacement for the JRX app VFO control to enhance the
 * accessibility adding frequency updates as the digits are manipulated.
 * 
 * @author Coz
 */
final public class VfoPrototype extends javax.swing.JFrame {
    static public VfoPrototype singletonInstance;
    static public JPanel  displayPanel;
    static boolean chooseVfoA = true;
    static boolean chooseVfoB = false;
    
    
    long freqVfoA = 3563000;   // MSN 
    long freqVfoB = 145330000; // Shawsville Repeater

    /**
     * Creates new form VfoPrototype.
     */
    public VfoPrototype() {
        singletonInstance = this;    
        try {
            initComponents();
        } catch(Exception e) {
            e.printStackTrace();
        }      
    }
    
    public void setUpVfoComponents() {
        // Must instantiate components before initialization of VfoDisplayControl.
        VfoDisplayControl panel = (VfoDisplayControl) vfoDisplayPanel;
        panel.initDigits();
        panel.frequencyToDigits(freqVfoA); // Vfo A is default.
        sendFreqToRadioVfoB(freqVfoB); // It looks more normal to have a value already.
        // The following statement does not work.
        UIManager.put("FormattedTextField.background", Color.RED);

        //Make jSpinner1Hertz textField get the focus whenever frame is activated.
        JSpinner spinner = (JSpinner)jSpinner1Hertz;
        JFormattedTextField textField;
        textField = (JFormattedTextField)spinner.getEditor().getComponent(0);
        // Cause the ftf to get the focus when the JFrame gets focus.        
        this.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                textField.requestFocusInWindow();
            }
        });

        // @todo Coz, make VFO panel extend JInternalFrame instead.
        // Make sure that the VfoControlFrame is focus manager.
        // It appears that voiceOver StepInto is ignoring focus manager.
        VfoControlFrame.setFocusCycleRoot(true);
        VfoDigitTraversalPolicy policy; 
        Vector<Component> order = panel.getTraversalOrder();
        policy = new VfoDigitTraversalPolicy(order);
        VfoControlFrame.setFocusTraversalPolicy(policy);
        VfoControlFrame.setFocusTraversalPolicyProvider(true);
        VfoControlFrame.setFocusable(true);
        VfoControlFrame.setVisible(true);
        // Add focus traverse keys left and right arrow.
        // In this case, FORWARD is to the left.
        VfoControlFrame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        VfoControlFrame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        Set set = new HashSet( getFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS ) );
        set.add( KeyStroke.getKeyStroke( "LEFT" ) );
        VfoControlFrame.setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set );

        set = new HashSet( getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS ) );
        set.add( KeyStroke.getKeyStroke( "RIGHT" ) );
        VfoControlFrame.setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set );
        VfoControlFrame.setFocusTraversalKeysEnabled(true);

        if (!VfoControlFrame.areFocusTraversalKeysSet(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS) ) {
             System.out.println("Coz, why aren't the FORWARD_TRAVERSAL_KEYS set?");
        }
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
        
        
        
        // Now set which component is to receive focus on downward focus.
        // May have to do this in a focusGained event handler.
        // Modify the input map to handle shift-control-option-downArrow to
        // put focus on the ones digit ftf.  Hopefully someone does not swallow
        // the voice over key combo first.
        // Nothing below was able to overcome voiceOver's bad conduct.
        // If you stop voiceOver and restart it while on a spinner, it's
        // behavior goes back to "good boy".  As soon as you leave the VFO group
        // its back to "bad boy" until you restart voiceOver.  WHAT?
        //
        InputMap inputMap = VfoControlFrame.getInputMap( JComponent.WHEN_FOCUSED);
        //KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK);
        //KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        KeyStroke stroke = KeyStroke.getKeyStroke("F2");
        if (stroke != null) {
            inputMap.put(stroke,"OnesDigitTftFocus");
            Component onesFtf = panel.getTraversalOrder().get(0);
            Action focusFieldAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    onesFtf.requestFocusInWindow();
                    System.out.print(e);
                    System.out.println(" Executed focusFieldAction");
               }
            };
            VfoControlFrame.getActionMap().put("OnesDigitTftFocus",focusFieldAction);
        }
        
        
        //requestFocusInWindow(boolean temporary)
        assert( VfoControlFrame.getFocusTraversalPolicy() != null);
        assert( VfoControlFrame.isFocusCycleRoot());
        VfoControlFrame.setEnabled(true);       
    }
    
    
    /*
     * @Return true when frequency successfully communicated to radio.
    */
    public boolean sendFreqToRadio(long frequencyHertz) {
        // Simulate sending freq value to Radio for testing.
        // Update the radio frequency display.
        // Announce the frequency in Megahertz.
        double mhz = (double) frequencyHertz;
        mhz = mhz / 1000000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.000000");
        String numberAsString = decimalFormat.format(mhz);
        if (singletonInstance.VfoA.isSelected())
            singletonInstance.frequencyVfoA.setText(numberAsString);
        else
            singletonInstance.frequencyVfoB.setText(numberAsString);
        return true;
    }
    
    private boolean sendFreqToRadioVfoB(long frequencyHertz) {
        // Simulate sending freq value to Radio for testing.
        // Update the radio frequency display.
        // Announce the frequency in Megahertz.
        double mhz = (double) frequencyHertz;
        mhz = mhz / 1000000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.000000");
        String numberAsString = decimalFormat.format(mhz);
        singletonInstance.frequencyVfoB.setText(numberAsString);
        return true;
    }
   
    public boolean loadRadioFrequencyToVfo(boolean isVfoA) {
        boolean success = true;
        long freqHertz;
        String valString;
        //Simlate read freq from Radio.
        if ( isVfoA ) {
            // Read frequency from Radio VFO A.
            valString = singletonInstance.frequencyVfoA.getText();
        } else {
            // Read frequencyl from Radio VFO B.
            valString = singletonInstance.frequencyVfoB.getText();
        }
        double freqMhz = Double.valueOf(valString);
        freqHertz = (long) (freqMhz * 1.E06) ;
        VfoDisplayControl panel = (VfoDisplayControl) vfoDisplayPanel;
        panel.frequencyToDigits(freqHertz);
        return success;
    }
    
    private void handleChangeEvent(javax.swing.event.ChangeEvent evt) {
        DecadeSpinner source = (DecadeSpinner)evt.getSource();
        Component ftf = source.getEditor().getComponent(0);
        ftf.setForeground(Color.GREEN);
        ftf.setBackground(Color.BLACK); // Does nothing?
         System.out.println("ftf at changeEvent background color is " + ftf.getBackground().toString());
        int value = (int) source.getModel().getValue();
        AccessibleContext context = source.getAccessibleContext();
        AccessibleValue accVal =  context.getAccessibleValue();
        Number currentVal = accVal.getCurrentAccessibleValue();        
       
        // Execute commit so that spinner and model values agree.
        try {
            source.commitEdit();
        } catch (Exception e)  {
            System.out.println(e);
        }
        
        // Update this ftf description with new frequency and decade name.
        // Update the debug panel frequency. 
        AccessibleEditor editor = (AccessibleEditor) source.getEditor();    
        DecadeSpinner decadeSpinner = editor.mySpinner;
        SpinnerModel model = decadeSpinner.getModel();
        DecadeSpinnerModel decadeModel = (DecadeSpinnerModel)model;
        int decade = decadeModel.getDecade();
        // Change the field description so voiceOver will announce it.
        VfoDisplayControl panel = (VfoDisplayControl) singletonInstance.vfoDisplayPanel;
        long freq = panel.digitsToFrequency();
        System.out.println("handleChangeEvent - model value: "+ String.valueOf(value));
        System.out.println("handleChangeEvent - currentAccessibleValue: "+ currentVal.toString());
     
        // Set all the ftf accessible context since VoiceOver only announces ones digit????? 
        Vector<Component> order = ((VfoDisplayControl) vfoDisplayPanel).getTraversalOrder();
        for ( int iii=0; iii<VfoDisplayControl.QUANTITY_DIGITS; iii++) {
            StringBuilder freqString = new StringBuilder("");
            freqString.append("VFO Frequency "+Double.toString(((double)freq)/1000000.)+" Mhz; ");
            Component comp = order.get(iii);
            JFormattedTextField ftfield = (JFormattedTextField)comp;
            freqString.append(ftfield.getAccessibleContext().getAccessibleName());
            ftfield.getAccessibleContext().setAccessibleDescription(freqString.toString());
        }
        // Print out just this field's name and description.
        String ftfName = ftf.getAccessibleContext().getAccessibleName();
        System.out.println("ftf accessible name :"+ftfName);
        String ftfDesc = ftf.getAccessibleContext().getAccessibleDescription();
        System.out.println("ftf accessible description :"+ftfDesc);    
        
         //JOptionPane.showMessageDialog(null,freqString );     
    }

 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VfoSelection = new javax.swing.ButtonGroup();
        TestingInfoFrame = new javax.swing.JInternalFrame();
        VfoAFrame = new javax.swing.JInternalFrame();
        frequencyVfoA = new javax.swing.JTextField();
        VfoA = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        VfoBFrame = new javax.swing.JInternalFrame();
        VfoB = new javax.swing.JRadioButton();
        frequencyVfoB = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        VfoControlFrame = new javax.swing.JInternalFrame();
        vfoDisplayPanel = new VfoDisplayControl(singletonInstance);
        jLayeredPaneHertz = new javax.swing.JLayeredPane();
        jSpinner1Hertz = new DecadeSpinner();
        jSpinner10Hertz = new DecadeSpinner();
        jSpinner100Hertz = new DecadeSpinner();
        jLayeredPaneKilohertz = new javax.swing.JLayeredPane();
        jSpinner1khz = new DecadeSpinner();
        jSpinner10khz = new DecadeSpinner();
        jSpinner100khz = new DecadeSpinner();
        jLayeredPaneMegahertz = new javax.swing.JLayeredPane();
        jSpinner1Mhz = new DecadeSpinner();
        jSpinner10Mhz = new DecadeSpinner();
        jSpinner100Mhz = new DecadeSpinner();
        jSpinner1000Mhz = new DecadeSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VFO Display Prototype");
        setFocusTraversalPolicyProvider(true);
        setFocusable(false);

        TestingInfoFrame.setBorder(javax.swing.BorderFactory.createTitledBorder("TestingPrototypeDebugInfo"));
        TestingInfoFrame.setTitle("Debug Info");
        TestingInfoFrame.setToolTipText("Choose VFO A or VFO B");
        TestingInfoFrame.setNextFocusableComponent(VfoControlFrame);
        TestingInfoFrame.setVisible(true);

        VfoAFrame.setBorder(javax.swing.BorderFactory.createTitledBorder("VFO A"));
        VfoAFrame.setTitle("Radio VFO A, Simulation");
        VfoAFrame.setToolTipText("Contains simulated Radio VFO A, frequency");
        VfoAFrame.setFocusTraversalPolicyProvider(true);
        VfoAFrame.setName("VFO A Group"); // NOI18N
        VfoAFrame.setNextFocusableComponent(VfoBFrame);
        VfoAFrame.setVisible(true);

        frequencyVfoA.setEditable(false);
        frequencyVfoA.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoA.setText("5446200000");
        frequencyVfoA.setToolTipText("VFO A Frequency Mhz");
        frequencyVfoA.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        frequencyVfoA.setNextFocusableComponent(VfoA);

        VfoSelection.add(VfoA);
        VfoA.setSelected(true);
        VfoA.setText("Connect VFO DIAL to  VFO A ");
        VfoA.setToolTipText("copies VFO A to  dial");
        VfoA.setNextFocusableComponent(VfoB);
        VfoA.setRequestFocusEnabled(false);
        VfoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VfoAActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Radio VFO readOnly current frequency Mhz");

        javax.swing.GroupLayout VfoAFrameLayout = new javax.swing.GroupLayout(VfoAFrame.getContentPane());
        VfoAFrame.getContentPane().setLayout(VfoAFrameLayout);
        VfoAFrameLayout.setHorizontalGroup(
            VfoAFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VfoAFrameLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(VfoAFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(VfoAFrameLayout.createSequentialGroup()
                        .addGroup(VfoAFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(frequencyVfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(VfoA))
                        .addGap(0, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );
        VfoAFrameLayout.setVerticalGroup(
            VfoAFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VfoAFrameLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(VfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frequencyVfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        frequencyVfoA.getAccessibleContext().setAccessibleName("Frequency of VFO A Mhz");
        frequencyVfoA.getAccessibleContext().setAccessibleDescription("Simulates Radio VFO A Display");
        VfoA.getAccessibleContext().setAccessibleDescription("get VFO A frequency from radio and adjust");

        VfoBFrame.setBorder(javax.swing.BorderFactory.createTitledBorder("VFO B"));
        VfoBFrame.setTitle("Radio VFO B, simulation ");
        VfoBFrame.setToolTipText("Contains simulated radio VFO B frequency");
        VfoBFrame.setFocusTraversalPolicyProvider(true);
        VfoBFrame.setName("VFO B Group"); // NOI18N
        VfoBFrame.setNextFocusableComponent(TestingInfoFrame);
        VfoBFrame.setVisible(true);

        VfoSelection.add(VfoB);
        VfoB.setText("Connect Radio VFO B to Dial");
        VfoB.setToolTipText("copies VFO B to dial");
        VfoB.setNextFocusableComponent(frequencyVfoB);
        VfoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VfoBActionPerformed(evt);
            }
        });

        frequencyVfoB.setEditable(false);
        frequencyVfoB.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyVfoB.setText("1296100000");
        frequencyVfoB.setToolTipText("VFO B Frequency Mhz");
        frequencyVfoB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        frequencyVfoB.setNextFocusableComponent(VfoControlFrame);

        jLabel2.setText("Radio VFO readOnly current frequency Mhz");

        javax.swing.GroupLayout VfoBFrameLayout = new javax.swing.GroupLayout(VfoBFrame.getContentPane());
        VfoBFrame.getContentPane().setLayout(VfoBFrameLayout);
        VfoBFrameLayout.setHorizontalGroup(
            VfoBFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VfoBFrameLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(VfoBFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VfoB))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        VfoBFrameLayout.setVerticalGroup(
            VfoBFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VfoBFrameLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(VfoB)
                .addGap(30, 30, 30)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        VfoB.getAccessibleContext().setAccessibleDescription("Get VFO B frequency from radio and adjust");
        frequencyVfoB.getAccessibleContext().setAccessibleName("Frequency of VFO B Mhz");
        frequencyVfoB.getAccessibleContext().setAccessibleDescription("Simulates Radio VFO B Display");

        javax.swing.GroupLayout TestingInfoFrameLayout = new javax.swing.GroupLayout(TestingInfoFrame.getContentPane());
        TestingInfoFrame.getContentPane().setLayout(TestingInfoFrameLayout);
        TestingInfoFrameLayout.setHorizontalGroup(
            TestingInfoFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TestingInfoFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(VfoAFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(VfoBFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        TestingInfoFrameLayout.setVerticalGroup(
            TestingInfoFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TestingInfoFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TestingInfoFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(VfoBFrame, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TestingInfoFrameLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(VfoAFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        VfoControlFrame.setTitle("VFO Control Prototype");
        VfoControlFrame.setToolTipText("Enter group with RIGHT ARROW KEY");
        VfoControlFrame.setFocusTraversalPolicy(VfoControlFrame.getFocusTraversalPolicy());
        VfoControlFrame.setFocusTraversalPolicyProvider(true);
        VfoControlFrame.setNextFocusableComponent(jSpinner1Hertz.getEditor());
        VfoControlFrame.setVisible(true);

        vfoDisplayPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        vfoDisplayPanel.setToolTipText("");
        vfoDisplayPanel.setFocusTraversalPolicy(vfoDisplayPanel.getFocusTraversalPolicy());
        vfoDisplayPanel.setFocusTraversalPolicyProvider(true);
        vfoDisplayPanel.setFocusable(false);
        vfoDisplayPanel.setName("VFO Display Panel"); // NOI18N
        vfoDisplayPanel.setNextFocusableComponent(jSpinner1Hertz);

        jLayeredPaneHertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jLayeredPaneHertz.setToolTipText("VFO Hertz digits");
        jLayeredPaneHertz.setName("VFO  zero to 1Khz panel"); // NOI18N
        jLayeredPaneHertz.setNextFocusableComponent(jSpinner1Hertz);
        jLayeredPaneHertz.setOpaque(true);

        jSpinner1Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1Hertz.setModel(jSpinner1Hertz.getModel());
        jSpinner1Hertz.setToolTipText("1 hertz vfo digit");
        jSpinner1Hertz.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSpinner1Hertz.setEditor(jSpinner1Hertz.getEditor());
        jSpinner1Hertz.setFocusTraversalKeysEnabled(jSpinner1Hertz.getFocusTraversalKeysEnabled());
        jSpinner1Hertz.setName("1 Hertz digit"); // NOI18N
        jSpinner1Hertz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1HertzStateChanged(evt);
            }
        });

        jSpinner10Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10Hertz.setModel(jSpinner10Hertz.getModel());
        jSpinner10Hertz.setToolTipText("10 hertz digit");
        jSpinner10Hertz.setAutoscrolls(true);
        jSpinner10Hertz.setEditor(jSpinner10Hertz.getEditor());
        jSpinner10Hertz.setName("10 Hertz digit"); // NOI18N
        jSpinner10Hertz.setRequestFocusEnabled(false);
        jSpinner10Hertz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner10HertzStateChanged(evt);
            }
        });

        jSpinner100Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100Hertz.setModel(jSpinner100Hertz.getModel());
        jSpinner100Hertz.setToolTipText("100 hertz digit");
        jSpinner100Hertz.setAutoscrolls(true);
        jSpinner100Hertz.setEditor(jSpinner100Hertz.getEditor());
        jSpinner100Hertz.setName("100 Hertz digit"); // NOI18N
        jSpinner100Hertz.setRequestFocusEnabled(false);
        jSpinner100Hertz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner100HertzStateChanged(evt);
            }
        });

        jLayeredPaneHertz.setLayer(jSpinner1Hertz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneHertz.setLayer(jSpinner10Hertz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneHertz.setLayer(jSpinner100Hertz, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPaneHertzLayout = new javax.swing.GroupLayout(jLayeredPaneHertz);
        jLayeredPaneHertz.setLayout(jLayeredPaneHertzLayout);
        jLayeredPaneHertzLayout.setHorizontalGroup(
            jLayeredPaneHertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPaneHertzLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSpinner100Hertz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner10Hertz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSpinner1Hertz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPaneHertzLayout.setVerticalGroup(
            jLayeredPaneHertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSpinner10Hertz, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
            .addComponent(jSpinner1Hertz)
            .addComponent(jSpinner100Hertz)
        );

        jSpinner1Hertz.getAccessibleContext().setAccessibleName("");
        jSpinner1Hertz.getAccessibleContext().setAccessibleDescription("");
        jSpinner1Hertz.getAccessibleContext().setAccessibleParent(VfoControlFrame);
        jSpinner10Hertz.getAccessibleContext().setAccessibleName("");
        jSpinner10Hertz.getAccessibleContext().setAccessibleDescription("");
        jSpinner10Hertz.getAccessibleContext().setAccessibleParent(VfoControlFrame);
        jSpinner100Hertz.getAccessibleContext().setAccessibleName("");
        jSpinner100Hertz.getAccessibleContext().setAccessibleDescription("");
        jSpinner100Hertz.getAccessibleContext().setAccessibleParent(VfoControlFrame);

        jLayeredPaneKilohertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kilohertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jLayeredPaneKilohertz.setOpaque(true);

        jSpinner1khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1khz.setModel(jSpinner1khz.getModel());
        jSpinner1khz.setToolTipText("1 Kilohertz digit");
        jSpinner1khz.setEditor(jSpinner1khz.getEditor());
        jSpinner1khz.setName("1 Kilohertz digit"); // NOI18N
        jSpinner1khz.setNextFocusableComponent(jSpinner10khz.getEditor());
        jSpinner1khz.setRequestFocusEnabled(false);
        jSpinner1khz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1khzStateChanged(evt);
            }
        });

        jSpinner10khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10khz.setModel(jSpinner10khz.getModel());
        jSpinner10khz.setToolTipText("10 Kilohertz digit");
        jSpinner10khz.setEditor(jSpinner10khz.getEditor());
        jSpinner10khz.setName("10 Kilohertz digit"); // NOI18N
        jSpinner10khz.setNextFocusableComponent(jSpinner100khz.getEditor());
        jSpinner10khz.setRequestFocusEnabled(false);
        jSpinner10khz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner10khzStateChanged(evt);
            }
        });

        jSpinner100khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100khz.setModel(jSpinner100khz.getModel());
        jSpinner100khz.setToolTipText("100 Kilohertz digit");
        jSpinner100khz.setEditor(jSpinner100khz.getEditor());
        jSpinner100khz.setName("100 Kilohertz digit"); // NOI18N
        jSpinner100khz.setNextFocusableComponent(jSpinner1Mhz.getEditor());
        jSpinner100khz.setRequestFocusEnabled(false);
        jSpinner100khz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner100khzStateChanged(evt);
            }
        });

        jLayeredPaneKilohertz.setLayer(jSpinner1khz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneKilohertz.setLayer(jSpinner10khz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneKilohertz.setLayer(jSpinner100khz, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPaneKilohertzLayout = new javax.swing.GroupLayout(jLayeredPaneKilohertz);
        jLayeredPaneKilohertz.setLayout(jLayeredPaneKilohertzLayout);
        jLayeredPaneKilohertzLayout.setHorizontalGroup(
            jLayeredPaneKilohertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneKilohertzLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSpinner100khz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner10khz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1khz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPaneKilohertzLayout.setVerticalGroup(
            jLayeredPaneKilohertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSpinner100khz)
            .addComponent(jSpinner10khz)
            .addComponent(jSpinner1khz)
        );

        jSpinner1khz.getAccessibleContext().setAccessibleName("");
        jSpinner1khz.getAccessibleContext().setAccessibleDescription("");
        jSpinner1khz.getAccessibleContext().setAccessibleParent(VfoControlFrame);
        jSpinner10khz.getAccessibleContext().setAccessibleName("");
        jSpinner10khz.getAccessibleContext().setAccessibleDescription("");
        jSpinner10khz.getAccessibleContext().setAccessibleParent(VfoControlFrame);
        jSpinner100khz.getAccessibleContext().setAccessibleName("");
        jSpinner100khz.getAccessibleContext().setAccessibleDescription("");
        jSpinner100khz.getAccessibleContext().setAccessibleParent(VfoControlFrame);

        jLayeredPaneMegahertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Megahertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jLayeredPaneMegahertz.setForeground(new java.awt.Color(255, 255, 255));
        jLayeredPaneMegahertz.setToolTipText("");
        jLayeredPaneMegahertz.setOpaque(true);

        jSpinner1Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1Mhz.setModel(jSpinner1Mhz.getModel());
        jSpinner1Mhz.setToolTipText("1 Megahertz digit");
        jSpinner1Mhz.setAutoscrolls(true);
        jSpinner1Mhz.setEditor(jSpinner1Mhz.getEditor());
        jSpinner1Mhz.setName("1 Megahertz digit"); // NOI18N
        jSpinner1Mhz.setNextFocusableComponent(jSpinner10Mhz.getEditor());
        jSpinner1Mhz.setRequestFocusEnabled(false);
        jSpinner1Mhz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1MhzStateChanged(evt);
            }
        });

        jSpinner10Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10Mhz.setModel(jSpinner10Mhz.getModel());
        jSpinner10Mhz.setToolTipText("10 Megahertz digit");
        jSpinner10Mhz.setEditor(jSpinner10Mhz.getEditor());
        jSpinner10Mhz.setName("10 megahertz digit"); // NOI18N
        jSpinner10Mhz.setNextFocusableComponent(jSpinner100Mhz.getEditor());
        jSpinner10Mhz.setRequestFocusEnabled(false);
        jSpinner10Mhz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner10MhzStateChanged(evt);
            }
        });

        jSpinner100Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100Mhz.setModel(jSpinner100Mhz.getModel());
        jSpinner100Mhz.setToolTipText("100 Megahertz digit");
        jSpinner100Mhz.setAutoscrolls(true);
        jSpinner100Mhz.setEditor(jSpinner100Mhz.getEditor());
        jSpinner100Mhz.setName("100 Megahertz digit"); // NOI18N
        jSpinner100Mhz.setNextFocusableComponent(jSpinner1000Mhz.getEditor());
        jSpinner100Mhz.setRequestFocusEnabled(false);
        jSpinner100Mhz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner100MhzStateChanged(evt);
            }
        });

        jSpinner1000Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1000Mhz.setModel(jSpinner1000Mhz.getModel());
        jSpinner1000Mhz.setToolTipText("1000 Megahertz digit");
        jSpinner1000Mhz.setAutoscrolls(true);
        jSpinner1000Mhz.setEditor(jSpinner1000Mhz.getEditor());
        jSpinner1000Mhz.setName("1000 Megahertz digit"); // NOI18N
        jSpinner1000Mhz.setNextFocusableComponent(jSpinner1Hertz.getEditor());
        jSpinner1000Mhz.setRequestFocusEnabled(false);
        jSpinner1000Mhz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1000MhzStateChanged(evt);
            }
        });

        jLayeredPaneMegahertz.setLayer(jSpinner1Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneMegahertz.setLayer(jSpinner10Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneMegahertz.setLayer(jSpinner100Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneMegahertz.setLayer(jSpinner1000Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPaneMegahertzLayout = new javax.swing.GroupLayout(jLayeredPaneMegahertz);
        jLayeredPaneMegahertz.setLayout(jLayeredPaneMegahertzLayout);
        jLayeredPaneMegahertzLayout.setHorizontalGroup(
            jLayeredPaneMegahertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneMegahertzLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSpinner1000Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner100Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner10Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPaneMegahertzLayout.setVerticalGroup(
            jLayeredPaneMegahertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPaneMegahertzLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSpinner100Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSpinner1000Mhz)
            .addComponent(jSpinner1Mhz, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSpinner10Mhz, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jSpinner1Mhz.getAccessibleContext().setAccessibleName("");
        jSpinner1Mhz.getAccessibleContext().setAccessibleDescription("");
        jSpinner1Mhz.getAccessibleContext().setAccessibleParent(VfoControlFrame);
        jSpinner10Mhz.getAccessibleContext().setAccessibleName("");
        jSpinner10Mhz.getAccessibleContext().setAccessibleDescription("");
        jSpinner10Mhz.getAccessibleContext().setAccessibleParent(VfoControlFrame);
        jSpinner100Mhz.getAccessibleContext().setAccessibleName("");
        jSpinner100Mhz.getAccessibleContext().setAccessibleDescription("");
        jSpinner100Mhz.getAccessibleContext().setAccessibleParent(VfoControlFrame);
        jSpinner1000Mhz.getAccessibleContext().setAccessibleName("");
        jSpinner1000Mhz.getAccessibleContext().setAccessibleDescription("");
        jSpinner1000Mhz.getAccessibleContext().setAccessibleParent(VfoControlFrame);

        javax.swing.GroupLayout vfoDisplayPanelLayout = new javax.swing.GroupLayout(vfoDisplayPanel);
        vfoDisplayPanel.setLayout(vfoDisplayPanelLayout);
        vfoDisplayPanelLayout.setHorizontalGroup(
            vfoDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vfoDisplayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPaneMegahertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPaneKilohertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPaneHertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vfoDisplayPanelLayout.setVerticalGroup(
            vfoDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPaneMegahertz)
            .addComponent(jLayeredPaneHertz)
            .addComponent(jLayeredPaneKilohertz)
        );

        jLayeredPaneHertz.getAccessibleContext().setAccessibleName("Hertz VFO panel");
        jLayeredPaneHertz.getAccessibleContext().setAccessibleParent(VfoControlFrame);

        javax.swing.GroupLayout VfoControlFrameLayout = new javax.swing.GroupLayout(VfoControlFrame.getContentPane());
        VfoControlFrame.getContentPane().setLayout(VfoControlFrameLayout);
        VfoControlFrameLayout.setHorizontalGroup(
            VfoControlFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(VfoControlFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(VfoControlFrameLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(vfoDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        VfoControlFrameLayout.setVerticalGroup(
            VfoControlFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 134, Short.MAX_VALUE)
            .addGroup(VfoControlFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(VfoControlFrameLayout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(vfoDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        vfoDisplayPanel.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(VfoControlFrame)
                    .addComponent(TestingInfoFrame))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(TestingInfoFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(VfoControlFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TestingInfoFrame.getAccessibleContext().setAccessibleName("Debug Information");

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

    private void jSpinner1HertzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1HertzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner1HertzStateChanged

    private void jSpinner10HertzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner10HertzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner10HertzStateChanged

    private void jSpinner100HertzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner100HertzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner100HertzStateChanged

    private void jSpinner1khzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1khzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner1khzStateChanged

    private void jSpinner10khzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner10khzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner10khzStateChanged

    private void jSpinner100khzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner100khzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner100khzStateChanged

    private void jSpinner1MhzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1MhzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner1MhzStateChanged

    private void jSpinner10MhzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner10MhzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner10MhzStateChanged

    private void jSpinner100MhzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner100MhzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner100MhzStateChanged

    private void jSpinner1000MhzStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1000MhzStateChanged
        handleChangeEvent(evt);
    }//GEN-LAST:event_jSpinner1000MhzStateChanged
    
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
            java.util.logging.Logger.getLogger(VfoPrototype.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VfoPrototype.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VfoPrototype.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VfoPrototype.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {           
            public void run() {
                
                VfoPrototype frame = new VfoPrototype();
                frame.setUpVfoComponents();
                // Give the spinner ftf focus upon opening window.
                frame.jSpinner1Hertz.getEditor().getComponent(0).requestFocusInWindow();
                frame.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JInternalFrame TestingInfoFrame;
    public javax.swing.JRadioButton VfoA;
    public javax.swing.JInternalFrame VfoAFrame;
    public javax.swing.JRadioButton VfoB;
    public javax.swing.JInternalFrame VfoBFrame;
    public javax.swing.JInternalFrame VfoControlFrame;
    public javax.swing.ButtonGroup VfoSelection;
    public javax.swing.JTextField frequencyVfoA;
    public javax.swing.JTextField frequencyVfoB;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLayeredPane jLayeredPaneHertz;
    public javax.swing.JLayeredPane jLayeredPaneKilohertz;
    public javax.swing.JLayeredPane jLayeredPaneMegahertz;
    public javax.swing.JSpinner jSpinner1000Mhz;
    public javax.swing.JSpinner jSpinner100Hertz;
    public javax.swing.JSpinner jSpinner100Mhz;
    public javax.swing.JSpinner jSpinner100khz;
    public javax.swing.JSpinner jSpinner10Hertz;
    public javax.swing.JSpinner jSpinner10Mhz;
    public javax.swing.JSpinner jSpinner10khz;
    public javax.swing.JSpinner jSpinner1Hertz;
    public javax.swing.JSpinner jSpinner1Mhz;
    public javax.swing.JSpinner jSpinner1khz;
    public javax.swing.JPanel vfoDisplayPanel;
    // End of variables declaration//GEN-END:variables
    
}
