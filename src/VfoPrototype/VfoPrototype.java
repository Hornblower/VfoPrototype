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
import javax.swing.JPanel;
import java.text.DecimalFormat;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleText;
import static javax.accessibility.AccessibleText.WORD;
import javax.accessibility.AccessibleValue;
import javax.swing.JSpinner;
import javax.swing.UIManager;



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
            // Must instantiate components before initialization of VfoDisplayPanel.
            VfoDisplayPanel panel = (VfoDisplayPanel) vfoDisplayPanel;
            panel.initDigits();
            panel.frequencyToDigits(freqVfoA); // Vfo A is default.
            sendFreqToRadioVfoB(freqVfoB); // It looks more normal to have a value already.
            UIManager.put("FormattedTextField.background", Color.RED);
        } catch(Exception e) {
            e.printStackTrace();
        }      
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
        VfoDisplayPanel panel = (VfoDisplayPanel) vfoDisplayPanel;
        panel.frequencyToDigits(freqHertz);
        return success;
    }
    
    private void handleChangeEvent(javax.swing.event.ChangeEvent evt) {
        JSpinner source = (JSpinner)evt.getSource();
        Component ftf = source.getEditor().getComponent(0);
        ftf.setForeground(Color.GREEN);
        ftf.setBackground(Color.BLACK); // Does nothing?
         System.out.println("ftf at changeEvent background color is " + ftf.getBackground().toString());
        int value = (int) source.getModel().getValue();
        VfoDisplayPanel panel = (VfoDisplayPanel) singletonInstance.vfoDisplayPanel;
        long freq = panel.digitsToFrequency();
        AccessibleContext context = source.getAccessibleContext();
        AccessibleValue accVal =  context.getAccessibleValue();
        Number currentVal = accVal.getCurrentAccessibleValue();
        AccessibleText  accText =  context.getAccessibleText();
        String wordStr = accText.getAtIndex(WORD, 0);
        System.out.println("handleChangeEvent - model value: "+ String.valueOf(value));
        System.out.println("handleChangeEvent - currentAccessibleValue: "+ currentVal.toString());
        System.out.println("handleChangeEvent - currentAccessibleText WORD at index 0 : "+ wordStr);
    }

 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VfoSelection = new javax.swing.ButtonGroup();
        jInternalFrame2 = new javax.swing.JInternalFrame();
        jInternalFrame3 = new javax.swing.JInternalFrame();
        frequencyVfoA = new javax.swing.JTextField();
        VfoA = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jInternalFrame4 = new javax.swing.JInternalFrame();
        VfoB = new javax.swing.JRadioButton();
        frequencyVfoB = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        vfoDisplayPanel = new VfoDisplayPanel(singletonInstance);
        jLayeredPaneHertz = new javax.swing.JLayeredPane();
        jSpinner1Hertz = new javax.swing.JSpinner();
        jSpinner10Hertz = new javax.swing.JSpinner();
        jSpinner100Hertz = new javax.swing.JSpinner();
        jLayeredPaneKilohertz = new javax.swing.JLayeredPane();
        jSpinner1khz = new javax.swing.JSpinner();
        jSpinner10khz = new javax.swing.JSpinner();
        jSpinner100khz = new javax.swing.JSpinner();
        jLayeredPaneMegahertz = new javax.swing.JLayeredPane();
        jSpinner1Mhz = new javax.swing.JSpinner();
        jSpinner10Mhz = new javax.swing.JSpinner();
        jSpinner100Mhz = new javax.swing.JSpinner();
        jSpinner1000Mhz = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VFO Display Prototype");
        setFocusTraversalPolicyProvider(true);
        setFocusable(false);

        jInternalFrame2.setBorder(javax.swing.BorderFactory.createTitledBorder("TestingPrototypeDebugInfo"));
        jInternalFrame2.setTitle("Debug Info");
        jInternalFrame2.setToolTipText("Choose VFO A or VFO B");
        jInternalFrame2.setNextFocusableComponent(jInternalFrame1);
        jInternalFrame2.setVisible(true);

        jInternalFrame3.setBorder(javax.swing.BorderFactory.createTitledBorder("VFO A"));
        jInternalFrame3.setVisible(true);

        frequencyVfoA.setEditable(false);
        frequencyVfoA.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
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

        javax.swing.GroupLayout jInternalFrame3Layout = new javax.swing.GroupLayout(jInternalFrame3.getContentPane());
        jInternalFrame3.getContentPane().setLayout(jInternalFrame3Layout);
        jInternalFrame3Layout.setHorizontalGroup(
            jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jInternalFrame3Layout.createSequentialGroup()
                        .addGroup(jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(frequencyVfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(VfoA))
                        .addGap(0, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jInternalFrame3Layout.setVerticalGroup(
            jInternalFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame3Layout.createSequentialGroup()
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

        jInternalFrame4.setBorder(javax.swing.BorderFactory.createTitledBorder("VFO B"));
        jInternalFrame4.setVisible(true);

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
        frequencyVfoB.setText("1296100000");
        frequencyVfoB.setToolTipText("VFO B Frequency Mhz");
        frequencyVfoB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        frequencyVfoB.setNextFocusableComponent(jInternalFrame1);

        jLabel2.setText("Radio VFO readOnly current frequency Mhz");

        javax.swing.GroupLayout jInternalFrame4Layout = new javax.swing.GroupLayout(jInternalFrame4.getContentPane());
        jInternalFrame4.getContentPane().setLayout(jInternalFrame4Layout);
        jInternalFrame4Layout.setHorizontalGroup(
            jInternalFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jInternalFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(VfoB))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jInternalFrame4Layout.setVerticalGroup(
            jInternalFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame4Layout.createSequentialGroup()
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

        javax.swing.GroupLayout jInternalFrame2Layout = new javax.swing.GroupLayout(jInternalFrame2.getContentPane());
        jInternalFrame2.getContentPane().setLayout(jInternalFrame2Layout);
        jInternalFrame2Layout.setHorizontalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jInternalFrame3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jInternalFrame4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jInternalFrame2Layout.setVerticalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jInternalFrame4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrame2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jInternalFrame3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jInternalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "VFO DIAL", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 18))); // NOI18N
        jInternalFrame1.setTitle("VFO DIAL");
        jInternalFrame1.setToolTipText("Use arrow keys to change value or traverse digits");
        jInternalFrame1.setNextFocusableComponent(jSpinner1Hertz);
        jInternalFrame1.setOpaque(true);
        jInternalFrame1.setVisible(true);

        vfoDisplayPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        vfoDisplayPanel.setToolTipText("");
        vfoDisplayPanel.setName("VFO Display Panel"); // NOI18N
        vfoDisplayPanel.setNextFocusableComponent(jSpinner1Hertz);

        jLayeredPaneHertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jLayeredPaneHertz.setToolTipText("VFO Hertz digits");
        jLayeredPaneHertz.setName("VFO  zero to 1Khz panel"); // NOI18N
        jLayeredPaneHertz.setNextFocusableComponent(jSpinner1Hertz);
        jLayeredPaneHertz.setOpaque(true);

        jSpinner1Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1Hertz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1Hertz.setToolTipText("1 hertz vfo digit");
        jSpinner1Hertz.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSpinner1Hertz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1Hertz, "#"));
        jSpinner1Hertz.setName("1 Hertz digit"); // NOI18N
        jSpinner1Hertz.setNextFocusableComponent(jSpinner10Hertz);
        jSpinner1Hertz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1HertzStateChanged(evt);
            }
        });

        jSpinner10Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10Hertz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner10Hertz.setToolTipText("10 hertz digit");
        jSpinner10Hertz.setAutoscrolls(true);
        jSpinner10Hertz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner10Hertz, ""));
        jSpinner10Hertz.setName("10 Hertz digit"); // NOI18N
        jSpinner10Hertz.setNextFocusableComponent(jSpinner100Hertz);
        jSpinner10Hertz.setRequestFocusEnabled(false);
        jSpinner10Hertz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner10HertzStateChanged(evt);
            }
        });

        jSpinner100Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100Hertz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner100Hertz.setToolTipText("100 hertz digit");
        jSpinner100Hertz.setAutoscrolls(true);
        jSpinner100Hertz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner100Hertz, ""));
        jSpinner100Hertz.setName("100 Hertz digit"); // NOI18N
        jSpinner100Hertz.setNextFocusableComponent(jSpinner1khz);
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
            .addComponent(jSpinner10Hertz)
            .addComponent(jSpinner1Hertz)
            .addComponent(jSpinner100Hertz)
        );

        jSpinner1Hertz.getAccessibleContext().setAccessibleName("1 Hertz VFO digit");
        jSpinner1Hertz.getAccessibleContext().setAccessibleDescription("Change VFO by 1 Hertz steps");
        jSpinner10Hertz.getAccessibleContext().setAccessibleName("10 Hertz VFO Digit");
        jSpinner10Hertz.getAccessibleContext().setAccessibleDescription("Change VFO by 10 hertz step");
        jSpinner100Hertz.getAccessibleContext().setAccessibleName("100 Hertz VFO Digit");
        jSpinner100Hertz.getAccessibleContext().setAccessibleDescription("Change VFO by 100 Hertz step.");

        jLayeredPaneKilohertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kilohertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jLayeredPaneKilohertz.setOpaque(true);

        jSpinner1khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1khz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1khz.setToolTipText("1 Kilohertz digit");
        jSpinner1khz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1khz, ""));
        jSpinner1khz.setName("1 Kilohertz digit"); // NOI18N
        jSpinner1khz.setNextFocusableComponent(jSpinner10khz);
        jSpinner1khz.setRequestFocusEnabled(false);
        jSpinner1khz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1khzStateChanged(evt);
            }
        });

        jSpinner10khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10khz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner10khz.setToolTipText("10 Kilohertz digit");
        jSpinner10khz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner10khz, ""));
        jSpinner10khz.setName("10 Kilohertz digit"); // NOI18N
        jSpinner10khz.setNextFocusableComponent(jSpinner100khz);
        jSpinner10khz.setRequestFocusEnabled(false);
        jSpinner10khz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner10khzStateChanged(evt);
            }
        });

        jSpinner100khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100khz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner100khz.setToolTipText("100 Kilohertz digit");
        jSpinner100khz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner100khz, ""));
        jSpinner100khz.setName("100 Kilohertz digit"); // NOI18N
        jSpinner100khz.setNextFocusableComponent(jSpinner1Mhz);
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

        jSpinner1khz.getAccessibleContext().setAccessibleName("1 kilohertz VFO digit  ");
        jSpinner1khz.getAccessibleContext().setAccessibleDescription("Change VFO by 1 kilohertz ");
        jSpinner10khz.getAccessibleContext().setAccessibleName("10Khz VFO digit ");
        jSpinner10khz.getAccessibleContext().setAccessibleDescription(" Change VFO by 10 Kilohertz steps");
        jSpinner100khz.getAccessibleContext().setAccessibleName("100khz VFO digit");
        jSpinner100khz.getAccessibleContext().setAccessibleDescription("Change VFO by 100 Kilohertz step");

        jLayeredPaneMegahertz.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Megahertz", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jLayeredPaneMegahertz.setForeground(new java.awt.Color(255, 255, 255));
        jLayeredPaneMegahertz.setToolTipText("");
        jLayeredPaneMegahertz.setOpaque(true);

        jSpinner1Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1Mhz.setToolTipText("1 Megahertz digit");
        jSpinner1Mhz.setAutoscrolls(true);
        jSpinner1Mhz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1Mhz, ""));
        jSpinner1Mhz.setName("1 Megahertz digit"); // NOI18N
        jSpinner1Mhz.setNextFocusableComponent(jSpinner10Mhz);
        jSpinner1Mhz.setRequestFocusEnabled(false);
        jSpinner1Mhz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1MhzStateChanged(evt);
            }
        });

        jSpinner10Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner10Mhz.setToolTipText("10 Megahertz digit");
        jSpinner10Mhz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner10Mhz, ""));
        jSpinner10Mhz.setName("10 megahertz digit"); // NOI18N
        jSpinner10Mhz.setNextFocusableComponent(jSpinner100Mhz);
        jSpinner10Mhz.setRequestFocusEnabled(false);
        jSpinner10Mhz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner10MhzStateChanged(evt);
            }
        });

        jSpinner100Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner100Mhz.setToolTipText("100 Megahertz digit");
        jSpinner100Mhz.setAutoscrolls(true);
        jSpinner100Mhz.setName("100 Megahertz digit"); // NOI18N
        jSpinner100Mhz.setNextFocusableComponent(jSpinner1000Mhz);
        jSpinner100Mhz.setRequestFocusEnabled(false);
        jSpinner100Mhz.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner100MhzStateChanged(evt);
            }
        });

        jSpinner1000Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1000Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1000Mhz.setToolTipText("1000 Megahertz digit");
        jSpinner1000Mhz.setAutoscrolls(true);
        jSpinner1000Mhz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1000Mhz, ""));
        jSpinner1000Mhz.setName("1000 Megahertz digit"); // NOI18N
        jSpinner1000Mhz.setNextFocusableComponent(jInternalFrame1);
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

        jSpinner1Mhz.getAccessibleContext().setAccessibleName("1 Mhz VFO digit");
        jSpinner1Mhz.getAccessibleContext().setAccessibleDescription("Change VFO by 1 Megahertz step");
        jSpinner10Mhz.getAccessibleContext().setAccessibleName("10 Mhz VFO digit ");
        jSpinner10Mhz.getAccessibleContext().setAccessibleDescription("Change VFO by 10 Megahertz step");
        jSpinner100Mhz.getAccessibleContext().setAccessibleName("100 Mhz VFO digit");
        jSpinner100Mhz.getAccessibleContext().setAccessibleDescription("Change VFO by 100 Megahertz steps");
        jSpinner1000Mhz.getAccessibleContext().setAccessibleName("1000 MHZ VFO digit");
        jSpinner1000Mhz.getAccessibleContext().setAccessibleDescription("Change VFO by 1000 Megahertz step");

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
            .addGroup(vfoDisplayPanelLayout.createSequentialGroup()
                .addComponent(jLayeredPaneHertz)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jLayeredPaneKilohertz)
        );

        jLayeredPaneHertz.getAccessibleContext().setAccessibleName("Hertz VFO panel");

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jInternalFrame1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(vfoDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 135, Short.MAX_VALUE)
            .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jInternalFrame1Layout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(vfoDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(7, Short.MAX_VALUE)))
        );

        vfoDisplayPanel.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jInternalFrame1)
                    .addComponent(jInternalFrame2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jInternalFrame2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jInternalFrame2.getAccessibleContext().setAccessibleName("Debug Information");

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
                frame.jSpinner1Hertz.requestFocusInWindow();
                frame.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JRadioButton VfoA;
    public javax.swing.JRadioButton VfoB;
    public javax.swing.ButtonGroup VfoSelection;
    public javax.swing.JTextField frequencyVfoA;
    public javax.swing.JTextField frequencyVfoB;
    public javax.swing.JInternalFrame jInternalFrame1;
    public javax.swing.JInternalFrame jInternalFrame2;
    public javax.swing.JInternalFrame jInternalFrame3;
    public javax.swing.JInternalFrame jInternalFrame4;
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
