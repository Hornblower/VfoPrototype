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


import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import java.text.DecimalFormat;
import VfoPrototype.CyclingSpinnerNumberModel;



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
    

 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VfoSelection = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        frequencyVfoA = new javax.swing.JFormattedTextField();
        frequencyVfoB = new javax.swing.JFormattedTextField();
        vfoDisplayPanel = new VfoDisplayPanel(singletonInstance);
        VfoA = new javax.swing.JRadioButton();
        VfoB = new javax.swing.JRadioButton();
        jLayeredPaneHertz = new javax.swing.JLayeredPane();
        try {
            jSpinner1Hertz = new FreqDigit(singletonInstance, 0);
            ;
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            jSpinner10Hertz = new FreqDigit(singletonInstance, 1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            jSpinner100Hertz = new FreqDigit(singletonInstance, 2);
        } catch(Exception e) {    e.printStackTrace(); }
        jLayeredPaneKilohertz = new javax.swing.JLayeredPane();
        try {
            jSpinner1khz = new FreqDigit(singletonInstance, 3);
        } catch(Exception e) {    e.printStackTrace(); }
        try {
            jSpinner10khz = new FreqDigit(singletonInstance, 4);
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            jSpinner100khz = new FreqDigit(singletonInstance, 5);
        } catch(Exception e) {
            e.printStackTrace();
        }
        jLayeredPaneMegahertz = new javax.swing.JLayeredPane();
        try {
            jSpinner1Mhz = new FreqDigit(singletonInstance, 6);
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            jSpinner10Mhz = new FreqDigit(singletonInstance, 7);
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            jSpinner100Mhz = new FreqDigit(singletonInstance, 8);
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            jSpinner1000Mhz = new FreqDigit(singletonInstance, 9);
        } catch(Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VFO Display Prototype");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("TestingPrototypeDebugInfo"));

        frequencyVfoA.setEditable(false);
        frequencyVfoA.setBorder(javax.swing.BorderFactory.createTitledBorder("RADIO VFO A Frequency Mhz"));
        frequencyVfoA.setText("0466.000000");
        frequencyVfoA.setToolTipText("Radio VFO  A");
        frequencyVfoA.setFocusCycleRoot(true);
        frequencyVfoA.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoA.setName("Read Only VFO A"); // NOI18N
        frequencyVfoA.setNextFocusableComponent(frequencyVfoB);

        frequencyVfoB.setEditable(false);
        frequencyVfoB.setBorder(javax.swing.BorderFactory.createTitledBorder("RADIO VFO B Frequency Mhz"));
        frequencyVfoB.setText("0145.330000");
        frequencyVfoB.setToolTipText("Radio VFO B Mhz");
        frequencyVfoB.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        frequencyVfoB.setNextFocusableComponent(VfoA);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(frequencyVfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frequencyVfoB, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frequencyVfoA, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        frequencyVfoA.getAccessibleContext().setAccessibleName("Radio VFO A frequency MHZ");
        frequencyVfoA.getAccessibleContext().setAccessibleDescription("Read Only Radio VFO A ");
        frequencyVfoB.getAccessibleContext().setAccessibleName("RADIO VFO B Frequency MHZ");
        frequencyVfoB.getAccessibleContext().setAccessibleDescription("Read Only Radio VFO B ");

        vfoDisplayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("VFO"));
        vfoDisplayPanel.setToolTipText("V F O use arrow keys or type frequency");
        vfoDisplayPanel.setName("VFO Display Panel"); // NOI18N
        vfoDisplayPanel.setNextFocusableComponent(jSpinner1Hertz);

        VfoSelection.add(VfoA);
        VfoA.setSelected(true);
        VfoA.setToolTipText("VFO A ");
        VfoA.setLabel("VFO A");
        VfoA.setNextFocusableComponent(VfoB);
        VfoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VfoAActionPerformed(evt);
            }
        });

        VfoSelection.add(VfoB);
        VfoB.setToolTipText("VFO B");
        VfoB.setLabel("VFO B");
        VfoB.setNextFocusableComponent(jSpinner1Hertz);
        VfoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VfoBActionPerformed(evt);
            }
        });

        jLayeredPaneHertz.setBorder(javax.swing.BorderFactory.createTitledBorder("Hertz"));
        jLayeredPaneHertz.setToolTipText("VFO Hertz digits");
        jLayeredPaneHertz.setName("VFO  zero to 1Khz panel"); // NOI18N
        jLayeredPaneHertz.setNextFocusableComponent(jSpinner1Hertz);

        jSpinner1Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1Hertz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1Hertz.setToolTipText("1 hertz vfo digit");
        jSpinner1Hertz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1Hertz, "#"));
        jSpinner1Hertz.setName("1 Hertz digit"); // NOI18N
        jSpinner1Hertz.setNextFocusableComponent(jSpinner10Hertz);

        jSpinner10Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10Hertz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner10Hertz.setToolTipText("10 hertz digit");
        jSpinner10Hertz.setAutoscrolls(true);
        jSpinner10Hertz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner10Hertz, ""));
        jSpinner10Hertz.setName("10 Hertz digit"); // NOI18N
        jSpinner10Hertz.setNextFocusableComponent(jSpinner100Hertz);

        jSpinner100Hertz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100Hertz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner100Hertz.setToolTipText("100 hertz digit");
        jSpinner100Hertz.setAutoscrolls(true);
        jSpinner100Hertz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner100Hertz, ""));
        jSpinner100Hertz.setName("100 Hertz digit"); // NOI18N
        jSpinner100Hertz.setNextFocusableComponent(jSpinner1khz);

        jLayeredPaneHertz.setLayer(jSpinner1Hertz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneHertz.setLayer(jSpinner10Hertz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneHertz.setLayer(jSpinner100Hertz, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPaneHertzLayout = new javax.swing.GroupLayout(jLayeredPaneHertz);
        jLayeredPaneHertz.setLayout(jLayeredPaneHertzLayout);
        jLayeredPaneHertzLayout.setHorizontalGroup(
            jLayeredPaneHertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPaneHertzLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSpinner100Hertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner10Hertz, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1Hertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPaneHertzLayout.setVerticalGroup(
            jLayeredPaneHertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSpinner100Hertz)
            .addComponent(jSpinner10Hertz)
            .addComponent(jSpinner1Hertz, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jSpinner1Hertz.getAccessibleContext().setAccessibleName("1 Hertz VFO digit");
        jSpinner1Hertz.getAccessibleContext().setAccessibleDescription("Change VFO by 1 Hertz steps");
        jSpinner10Hertz.getAccessibleContext().setAccessibleName("10 Hertz VFO Digit");
        jSpinner10Hertz.getAccessibleContext().setAccessibleDescription("Change VFO by 10 hertz step");
        jSpinner100Hertz.getAccessibleContext().setAccessibleName("100 Hertz VFO Digit");
        jSpinner100Hertz.getAccessibleContext().setAccessibleDescription("Change VFO by 100 Hertz step.");

        jLayeredPaneKilohertz.setBorder(javax.swing.BorderFactory.createTitledBorder("Kilohertz"));

        jSpinner1khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1khz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1khz.setToolTipText("1 Kilohertz digit");
        jSpinner1khz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1khz, ""));
        jSpinner1khz.setName("1 Kilohertz digit"); // NOI18N
        jSpinner1khz.setNextFocusableComponent(jSpinner10khz);

        jSpinner10khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10khz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner10khz.setToolTipText("10 Kilohertz digit");
        jSpinner10khz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner10khz, ""));
        jSpinner10khz.setName("10 Kilohertz digit"); // NOI18N
        jSpinner10khz.setNextFocusableComponent(jSpinner100khz);

        jSpinner100khz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100khz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner100khz.setToolTipText("100 Kilohertz digit");
        jSpinner100khz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner100khz, ""));
        jSpinner100khz.setName("100 Kilohertz digit"); // NOI18N
        jSpinner100khz.setNextFocusableComponent(jSpinner1Mhz);

        jLayeredPaneKilohertz.setLayer(jSpinner1khz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneKilohertz.setLayer(jSpinner10khz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneKilohertz.setLayer(jSpinner100khz, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPaneKilohertzLayout = new javax.swing.GroupLayout(jLayeredPaneKilohertz);
        jLayeredPaneKilohertz.setLayout(jLayeredPaneKilohertzLayout);
        jLayeredPaneKilohertzLayout.setHorizontalGroup(
            jLayeredPaneKilohertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneKilohertzLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSpinner100khz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner10khz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1khz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jLayeredPaneKilohertzLayout.setVerticalGroup(
            jLayeredPaneKilohertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSpinner100khz)
            .addComponent(jSpinner10khz)
            .addComponent(jSpinner1khz)
        );

        jSpinner1khz.getAccessibleContext().setAccessibleName("one kilohertz VFO digit  ");
        jSpinner1khz.getAccessibleContext().setAccessibleDescription("Change VFO by 1 kilohertz ");
        jSpinner10khz.getAccessibleContext().setAccessibleName("10Khz VFO digit ");
        jSpinner10khz.getAccessibleContext().setAccessibleDescription(" Change VFO by 10 Kilohertz steps");
        jSpinner100khz.getAccessibleContext().setAccessibleName("100khz VFO digit");
        jSpinner100khz.getAccessibleContext().setAccessibleDescription("Change VFO by 100 Kilohertz step");

        jLayeredPaneMegahertz.setBorder(javax.swing.BorderFactory.createTitledBorder("Megahertz"));
        jLayeredPaneMegahertz.setToolTipText("");

        jSpinner1Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1Mhz.setToolTipText("1 Megahertz digit");
        jSpinner1Mhz.setAutoscrolls(true);
        jSpinner1Mhz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1Mhz, ""));
        jSpinner1Mhz.setName("1 Megahertz digit"); // NOI18N
        jSpinner1Mhz.setNextFocusableComponent(jSpinner10Mhz);

        jSpinner10Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner10Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner10Mhz.setToolTipText("10 Megahertz digit");
        jSpinner10Mhz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner10Mhz, ""));
        jSpinner10Mhz.setName("10 megahertz digit"); // NOI18N
        jSpinner10Mhz.setNextFocusableComponent(jSpinner100Mhz);

        jSpinner100Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner100Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner100Mhz.setToolTipText("100 Megahertz digit");
        jSpinner100Mhz.setAutoscrolls(true);
        jSpinner100Mhz.setName("100 Megahertz digit"); // NOI18N
        jSpinner100Mhz.setNextFocusableComponent(jSpinner1000Mhz);

        jSpinner1000Mhz.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        jSpinner1000Mhz.setModel(new CyclingSpinnerNumberModel(0,0,9,1));
        jSpinner1000Mhz.setToolTipText("1000 Megahertz digit");
        jSpinner1000Mhz.setAutoscrolls(true);
        jSpinner1000Mhz.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1000Mhz, ""));
        jSpinner1000Mhz.setName("1000 Megahertz digit"); // NOI18N
        jSpinner1000Mhz.setNextFocusableComponent(frequencyVfoA);

        jLayeredPaneMegahertz.setLayer(jSpinner1Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneMegahertz.setLayer(jSpinner10Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneMegahertz.setLayer(jSpinner100Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneMegahertz.setLayer(jSpinner1000Mhz, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPaneMegahertzLayout = new javax.swing.GroupLayout(jLayeredPaneMegahertz);
        jLayeredPaneMegahertz.setLayout(jLayeredPaneMegahertzLayout);
        jLayeredPaneMegahertzLayout.setHorizontalGroup(
            jLayeredPaneMegahertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneMegahertzLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSpinner1000Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner100Mhz, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSpinner10Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1Mhz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPaneMegahertzLayout.setVerticalGroup(
            jLayeredPaneMegahertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneMegahertzLayout.createSequentialGroup()
                .addGroup(jLayeredPaneMegahertzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSpinner100Mhz, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                    .addComponent(jSpinner10Mhz, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinner1Mhz, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinner1000Mhz, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGroup(vfoDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vfoDisplayPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLayeredPaneMegahertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLayeredPaneKilohertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(vfoDisplayPanelLayout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(VfoA)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(vfoDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(VfoB)
                    .addComponent(jLayeredPaneHertz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        vfoDisplayPanelLayout.setVerticalGroup(
            vfoDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vfoDisplayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vfoDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VfoA)
                    .addComponent(VfoB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(vfoDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLayeredPaneHertz)
                    .addComponent(jLayeredPaneMegahertz)
                    .addComponent(jLayeredPaneKilohertz))
                .addGap(24, 24, 24))
        );

        VfoA.getAccessibleContext().setAccessibleDescription("get VFO A frequency from radio and adjust");
        VfoB.getAccessibleContext().setAccessibleDescription("Get VFO B frequency from radio and adjust");
        jLayeredPaneHertz.getAccessibleContext().setAccessibleName("Hertz VFO group");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vfoDisplayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vfoDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        vfoDisplayPanel.getAccessibleContext().setAccessibleName("V F O");

        getAccessibleContext().setAccessibleName("VFO Display prototype ");
        getAccessibleContext().setAccessibleDescription("Each VFO digit is a spinner responding to up down arrows.");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void VfoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VfoBActionPerformed
        loadRadioFrequencyToVfo(chooseVfoB);
    }//GEN-LAST:event_VfoBActionPerformed

    private void VfoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VfoAActionPerformed
        loadRadioFrequencyToVfo(chooseVfoA);
    }//GEN-LAST:event_VfoAActionPerformed
    
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
                //Why to I have to force the focus? Tab order?
                frame.jSpinner1Hertz.requestFocusInWindow();
                frame.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JRadioButton VfoA;
    public javax.swing.JRadioButton VfoB;
    public javax.swing.ButtonGroup VfoSelection;
    public javax.swing.JFormattedTextField frequencyVfoA;
    public javax.swing.JFormattedTextField frequencyVfoB;
    public javax.swing.JLayeredPane jLayeredPaneHertz;
    public javax.swing.JLayeredPane jLayeredPaneKilohertz;
    public javax.swing.JLayeredPane jLayeredPaneMegahertz;
    public javax.swing.JPanel jPanel1;
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