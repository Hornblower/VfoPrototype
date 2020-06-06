/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.TitledBorder;


/**
 * Implements a ten digit integer display/control where each digit can be 
 * manipulated, wraps around and carries to the next digit appropriately.
 * 
 * The VfoDisplayControl digits can be incremented and decremented by the
 * up and down arrows. The left and right arrows traverse the decades. A digit
 * is given a new description upon focus which includes the decade of the 
 * digit and the current VFO frequency in Mhz.  VoiceOver announces that new
 * description.  There are shortcut keys ALT-A and ALT-B to select either
 * VFO A or VFO B.  These "radio buttons" choose which radio VFO is controlled
 * by manipulating the VFO Display Control digits.  VoiceOver DOES NOT announce
 * at all upon reaching the JInternalFrame menu and menu items.  That is a bug
 * and am submitting it to Oracle with a simple example called JInternalFrameBug.
 * To overcome this voiceOver problem, a dialog is opened when the VFO is changed.
 * Blind users can use the OPT-A and OPT-B to choose VfoA and VfoB respectively
 * without having to navigate the menu items that have no audio feedback.
 * 
 * Sighted users can click on the upper half of a digit to increment or lower
 * half to decrement.  The mouseWheel is a faster way to increment/decrement.
 * 
 * It is not possible to just type digits to enter a frequency in this app.  Each
 * digit is a JLabel.
 * 
 * To scan the band, pick a decade digit and hold down the up/down arrow.
 * 
 * @author Coz
 */
final public class VfoDisplayControl extends GroupBox 
        implements PropertyChangeListener  {
    
    static boolean wasVfoA = true;
    static boolean chooseVfoA = true;
    static boolean chooseVfoB = false;
    static long MSN_FREQ = 3563000;   // MSN 80meter CW
    static long SHAWSVILLE_REPEATER_OUTPUT_FREQ = 145330000; // Shawsville Repeater
    static String VFO_SELECT_A_TEXT = "Select radio VFO A";
    static String VFO_SELECT_B_TEXT = "Select radio VFO B";
    static String LAST_VFO = "LAST_VFO";
    
    protected ArrayList<DecadeDigit> freqDigits = null;
    public final static int QUANTITY_DIGITS = 10;
    VfoPrototype2 aFrame;
    long sv_freq;
    long currentFrequency = 3563000L;
    long oldFrequency = 0;
    boolean inhibit = true;  // Inhibit interaction during construction.
    static Vector<Component> order;
    boolean silent = false;
    
    double ONES_RELATIVE_SIZE = 0.7;
    double DIGIT_RELATIVE_SIZE = 1.0;
    
    JLayeredPane layeredPaneMegahertz; 
    JLayeredPane layeredPaneKilohertz; 
    JLayeredPane layeredPaneHertz;
    JPanel glassPane;
    Container contentPane;
    Rectangle megaBounds;
    Rectangle kiloBounds;
    Rectangle unnoBounds;
    Dimension smallDims;
    Dimension tallDims;



    public VfoDisplayControl(VfoPrototype2 frame) {
        super();
        aFrame = frame;
        setClosable(false);
        setFocusCycleRoot(true);
        setFocusable(true);
        setResizable(true);
        AccessibleContext contextVfoControl = getAccessibleContext();
        contextVfoControl.setAccessibleName("V F O Display Control");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); //not a user operation.       
    }
    /**
     * Method to use glass pane as the dynamic content pane for the frame and
     * use the content pane for static / background information and graphics.
     * 
     * We are drawing the DecadeDigits on the glass pane.
     * We are drawing the BarnDoors  on the content pane.
     * 
     * THESE ARE APPROXIMATE SIZES... THE ACTUAL SIZES HAVE CHANGED.
     * frameBounds    =   6,132,664,151
     * rootPaneBounds =   6, 25,652,120
     * contentBounds  =   0,  0,652,120
     * megaBounds     =   0, 23,273,120
     * kiloBounds     = 278, 23,205,120
     * unnoBounds     = 488, 23,143,120
     * menuBarBounds  = 
     */
    public void setupPanes() {
        VfoDisplayControl display = this;
        
        display.setBounds(0,157,642,200); // Doc says you must set bounds.
        Dimension frameSize = display.getSize();

        setupGlassPane(display);
        setupContentPane(display);
        
        
    }
    /**
     * Create all ten DecadeDigits, initialize them ,store them in an ordered 
     * collection which is used to traverse the digits, then insert them into
     * three panels indicating scientific notation grouping.
     * 
     * The one hertz digits are usually smaller on radio displays.  Use the
     * JRX proportions.
     */
    protected void initDigits() {
        freqDigits = new ArrayList<>();
        freqDigits.add(new DecadeDigit(this, 0, 0.7f));    
        freqDigits.add(new DecadeDigit(this, 1, 0.7f));    
        freqDigits.add(new DecadeDigit(this, 2, 0.7f)); 
        freqDigits.add(new DecadeDigit(this, 3, 1.0f));
        freqDigits.add(new DecadeDigit(this, 4, 1.0f));    
        freqDigits.add(new DecadeDigit(this, 5, 1.0f));      
        freqDigits.add(new DecadeDigit(this, 6, 1.0f));
        freqDigits.add(new DecadeDigit(this, 7, 1.0f));  
        freqDigits.add(new DecadeDigit(this, 8, 1.0f));   
        freqDigits.add(new DecadeDigit(this, 9, 1.0f));
        assert(freqDigits.size() == QUANTITY_DIGITS);      
        
        // Link the digits by linking all the models.
        DecadeDigit.linkAllDigits(freqDigits, QUANTITY_DIGITS);
        // Save the focus order.        
        order = new Vector<>(QUANTITY_DIGITS);
        for (int iii=0; iii<QUANTITY_DIGITS; iii++) {
            // The order vector contains the formated text fields.
            Component label = freqDigits.get(iii);
            // Every label has unique accessible info based on decade. 
            ((DecadeDigit)label).setAccessibleInfo();
            order.add(label);
        } 
        insertDigitsIntoPanels();
    }

    /**
     * On the glass pane, three panels hold the DecadeDigits that make up the
     * ten digit frequency display; create them and fill them with digits.
     */
    private void insertDigitsIntoPanels() {   
        layeredPaneMegahertz = new javax.swing.JLayeredPane();
        layeredPaneKilohertz = new javax.swing.JLayeredPane();
        layeredPaneHertz = new javax.swing.JLayeredPane();        
        layeredPaneMegahertz.setLayout(new java.awt.FlowLayout(FlowLayout.CENTER));
        layeredPaneKilohertz.setLayout(new java.awt.FlowLayout(FlowLayout.CENTER));
        layeredPaneHertz.setLayout(new java.awt.FlowLayout(FlowLayout.CENTER));        
        
        JLayeredPane pane = layeredPaneMegahertz;
        pane.removeAll();
        ((FlowLayout) pane.getLayout()).setHgap(5); //snuggle horizontally
        pane.add(freqDigits.get(9));
        pane.add(freqDigits.get(8));
        pane.add(freqDigits.get(7));
        pane.add(freqDigits.get(6));
        
        pane = layeredPaneKilohertz;
        pane.removeAll();
        ((FlowLayout) pane.getLayout()).setHgap(5);
        pane.add(freqDigits.get(5));
        pane.add(freqDigits.get(4));
        pane.add(freqDigits.get(3));
        
        pane = layeredPaneHertz;
        pane.removeAll();
        ((FlowLayout) pane.getLayout()).setHgap(5);
        pane.add(freqDigits.get(2));
        pane.add(freqDigits.get(1));
        pane.add(freqDigits.get(0));
    }
    
    
    /**
     * Create the glass pane panel and configure the layered panes that hold the
     * DecadeDigits.
     * 
     * The glass pane contains all the dynamic components.
     * 
     * @param display 
     */
    public void setupGlassPane(VfoDisplayControl display) {
        Rectangle rootBounds = display.getRootPane().getBounds();
        contentPane = display.getContentPane();
        Rectangle contentBounds = contentPane.getBounds();

        display.setGlassPane(new JPanel());
        glassPane = (JPanel) display.getGlassPane();
        glassPane.setBounds(contentBounds);
        
        Rectangle glassBounds = glassPane.getBounds();
        System.out.println("ContentPane bounds = "+ contentBounds);
        System.out.println("GlassPane bounds = "+ glassBounds);
        
        // Since we have the bounds for each component, do away with layout.
        glassPane.setLayout(null);
        
        //MUST HAVE THE FOLLOWING LINE FOR GLASS PANE TO BE TRANSPARENT!
        glassPane.setOpaque(false);
        
        //Insets gInsets = glassPane.getInsets(); // insets are zero       
        //Insets mgInsets = layeredPaneMegahertz.getInsets(); // insets are zero
 
        rootBounds = display.getRootPane().getBounds();
        contentPane = display.getContentPane();
        contentBounds = contentPane.getBounds();

        
        glassPane = (JPanel) display.getGlassPane();
        glassBounds = glassPane.getBounds();
        System.out.println("ContentPane bounds = "+ contentBounds);
        System.out.println("GlassPane bounds = "+ glassBounds);
        
        
        
        
        
        
        //////////////////////////////////////////////////////////////////
        // Compute component widths.  Some subtle math here....
        int digitGap = 5; //guess by sight.
        int offsetY = 0;
        int titleBorderWidth = 10;
        double digitWidth = 
                (double)(contentBounds.width-4*digitGap - 6*titleBorderWidth)/
                ((7.*DIGIT_RELATIVE_SIZE)+(3*ONES_RELATIVE_SIZE));
        int onesWide = 
                (int)(digitWidth * ONES_RELATIVE_SIZE * 3.0)+2*titleBorderWidth;
        int megaWide = (int)(4*digitWidth)+2*titleBorderWidth;
        int kiloWide = (int)(3*digitWidth)+2*titleBorderWidth;
        int onesOffsetX = megaWide+digitGap+kiloWide+digitGap;
        //layeredPaneMegahertz.setAlignmentY(1.0f);
        //float y = layeredPaneMegahertz.getAlignmentY();
        ////////////////////////////////////////////////////////////////////
                
        megaBounds = new Rectangle(  0,  offsetY, megaWide, 120);                   
        kiloBounds = new Rectangle(megaWide+digitGap,  offsetY, kiloWide, 120);
        unnoBounds = new Rectangle(onesOffsetX,  offsetY, onesWide, 120);
        
        System.out.println("megaBounds :" + megaBounds);
        System.out.println("kiloBounds :" + kiloBounds);
        System.out.println("unnoBounds :" + unnoBounds);
        Rectangle displayBounds = display.getBounds();
        System.out.println("displayBounds :" + displayBounds);
        
        layeredPaneMegahertz.setBounds(megaBounds);
        layeredPaneMegahertz.setOpaque(false);
        layeredPaneKilohertz.setBounds(kiloBounds);
        layeredPaneKilohertz.setOpaque(false);    
        layeredPaneHertz.setBounds(unnoBounds);
        layeredPaneHertz.setOpaque(false);
    
        glassPane.add(layeredPaneMegahertz);
        glassPane.add(layeredPaneKilohertz);
        glassPane.add(layeredPaneHertz);  
        
        layeredPaneMegahertz.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        layeredPaneMegahertz.setToolTipText("");
        layeredPaneMegahertz.setOpaque(false);
        layeredPaneMegahertz.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createTitledBorder(""),
                "Megahertz", javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.TOP, 
                new java.awt.Font("Lucida Grande", 0, 13), 
                new java.awt.Color(0, 255, 0))); 
        layeredPaneMegahertz.setForeground(new java.awt.Color(0, 255, 0));
        layeredPaneMegahertz.setPreferredSize(new java.awt.Dimension(megaBounds.width, megaBounds.height));
        layeredPaneMegahertz.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                digitsPanelAncestorResized(evt);
            }
        });
        adjustSize(layeredPaneMegahertz);
        
        layeredPaneKilohertz.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        layeredPaneKilohertz.setOpaque(false);
        layeredPaneKilohertz.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createTitledBorder(""), 
                "Kilohertz", javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.TOP, 
                new java.awt.Font("Lucida Grande", 0, 13), 
                new java.awt.Color(0, 255, 0))); 
        layeredPaneKilohertz.setForeground(new java.awt.Color(0, 255, 0));
        layeredPaneKilohertz.setPreferredSize(new java.awt.Dimension(kiloBounds.width, kiloBounds.height));
        layeredPaneKilohertz.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                digitsPanelAncestorResized(evt);
            }
        });
        adjustSize(layeredPaneKilohertz);
        
        layeredPaneHertz.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        layeredPaneHertz.setToolTipText("VFO Hertz digits");
        layeredPaneHertz.setName("VFO  zero to 1Khz panel"); // NOI18N
        layeredPaneHertz.setOpaque(false);
        layeredPaneHertz.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createTitledBorder(""), 
                "Hertz", javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.TOP, 
                new java.awt.Font("Lucida Grande", 0, 13), 
                new java.awt.Color(0, 255, 0))); 
        layeredPaneHertz.setForeground(new java.awt.Color(0, 255, 0));
        layeredPaneHertz.setPreferredSize(new java.awt.Dimension(unnoBounds.width, unnoBounds.height));
        layeredPaneHertz.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                digitsPanelAncestorResized(evt);
            }
        });
        adjustSize(layeredPaneHertz);
        
//        // At this point, all the digits have been resized and inserted into 
//        // the fixed size layered panes. Get some measurements.
//        Dimension megaPref = layeredPaneMegahertz.getPreferredSize();
//        Dimension kiloPref = layeredPaneKilohertz.getPreferredSize();
//        Dimension unnoPref = layeredPaneHertz.getPreferredSize();
//        //System.out.println("megaPref :" + megaPref);
//        //System.out.println("kiloPref :" + kiloPref);
//        //System.out.println("unnoPref :" + unnoPref);               
    }
   

    /**
     * Adjust the size of the DecadeDigit fonts (and thus the DecadeDigit dims).
     * 
     * @param resizedComponent is a layered pane.
     */
    protected void adjustSize(JLayeredPane resizedComponent) {
        for (Component comp : resizedComponent.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER)) {
            DecadeDigit digit = (DecadeDigit) comp;
            double fs = digit.fontScale;
            if ( fs < .99) {
                // Small digits are size limited by width.  fs = 0.7
                double handPickedDivisor = 2.0;
                int fontSize = (int) ((resizedComponent.getWidth()) / handPickedDivisor);
                Font font = new Font("Monospace", Font.PLAIN, (int) (fontSize * fs));
                comp.setFont(font);
                smallDims = digit.getPreferredSize(); // 40,62
            } else { 
                // Large digits are limited by layered pane height.  fs = 1.0
                double handPickedDivisor = 1.55;
                
                int fontSize = (int) ((resizedComponent.getHeight()) / handPickedDivisor);
                Font font = new Font("Monospace", Font.PLAIN, (int) (fontSize * fs));
                comp.setFont(font);
                tallDims = digit.getPreferredSize(); // 57,94
            }            
        }
        
        System.out.println("tallDims  : "+tallDims);
        System.out.println("smallDims : "+smallDims);
    }
    
    /**
     * All the dynamic components are added to the glass pane so the context
     * pane is just boilerPlate and there are no events to handle.
     * @param display 
     */
    public void setupContentPane(VfoDisplayControl display) {
        
        contentPane.setLayout(null);  //don't need no stinkin ....
        smallDims = new Dimension(40-5,62);
        tallDims  = new Dimension(57-5,94);
        // Use the dims of the small Hertz digits.               
        aFrame.jLayeredPaneHertz.setBounds(unnoBounds);
        aFrame.jLayeredPaneHertz.setBackground(Color.BLACK);
        for (int iii=0; iii<3; iii++) {
            BarnDoor door = new BarnDoor(smallDims);
            door.addShapes();           
            aFrame.jLayeredPaneHertz.add(door);
            door.setVisible(true);
        }
        // Use the dims of the tall Kilohertz digits.               
        aFrame.jLayeredPaneKilohertz.setBounds(kiloBounds);
        aFrame.jLayeredPaneKilohertz.setBackground(Color.BLACK);
        for (int iii=3; iii<6; iii++) {
            BarnDoor door = new BarnDoor(tallDims);
            door.addShapes();           
            aFrame.jLayeredPaneKilohertz.add(door);
            door.setVisible(true);
        }
        aFrame.jLayeredPaneMegahertz.setBounds(megaBounds);
        aFrame.jLayeredPaneMegahertz.setBackground(Color.BLACK);
        for (int iii=6; iii<10; iii++) {
            BarnDoor door = new BarnDoor(tallDims);
            door.addShapes();           
            aFrame.jLayeredPaneMegahertz.add(door);
            door.setVisible(true);
        }
                
    }
    
    
    public void makeVisible() { 
        inhibit = false;
        long selectedFreq = aFrame.vfoState.getSelectedVfoFrequency();
        frequencyToDigits(selectedFreq);        

        getGlassPane().setVisible(true);
        getContentPane().setVisible(true);
    }
    
    public static Vector<Component> getTraversalOrder() {
        return order;
    }
        
    public void initFrequency(long v) {
        frequencyToDigits(v);
    }
    
    public void setInhibit(boolean mode) {
        inhibit = mode;
    }
    public void setSilent(boolean mode) {
        silent = mode;
    }
    
    /**
     * Given a long representation of a frequency in hertz, set the decade
     * digits to display that frequency.
     * 
     * @param v A long representing frequency in Hertz
     */
    public void frequencyToDigits(long v) {
        if(inhibit) return;
        sv_freq = v;
        currentFrequency = sv_freq;
        long modulatedValue = v;
        // Expecting list ordered from LSD to MSD.      
        int size = freqDigits.size();
        for (int i = 0; i < size; i++) {
            DecadeDigit fd = freqDigits.get(i);
            fd.setValue( (int) (modulatedValue % 10));
            fd.setBright(modulatedValue != 0);
            modulatedValue /= 10;
        }         
    }

    /**
    * From the currently displayed digits in the VfoDisplayControl construct a
    * base ten representation and return it as a long.  Dim the leading zeroes.
    * 
    * @return the frequency in hertz shown by the collection of JSpinner digits.
    */    
    public long digitsToFrequency() {
        sv_freq = 0;
        if (!inhibit) {
            inhibit = true;
            freqDigits.forEach((dig) -> {
                DecadeModel  model = (DecadeModel) dig.getModel();
                Object value = model.getValue();
                String digitString = value.toString();
                Integer digit = Integer.valueOf(digitString);
                int decade = model.getDecade();
                sv_freq =  (long)Math.pow(10, decade) * digit + sv_freq ;
                dig.setBright(true); 
            });
            // Dim leading zeroes.
            for ( int ii = QUANTITY_DIGITS; ii>0; ii--) {
                DecadeDigit dig = freqDigits.get(ii-1);
                if ((Integer)dig.getValue() == 0) {
                    dig.setBright(false);
                } else {
                    break;
                }    
            }
        }
        inhibit = false;
        return sv_freq;
    }

   /**
     * Method called when the VfoA radio button changes the VFO selection with
     * the requirement to update the VFO display control with the
     * frequency read from the radio VFO A.
     * 
     * Turn off the VFO change handler while this update takes place.  Set focus
     * on the ones digit.
     * @return true.
     */
    public boolean loadRadioFrequencyToVfoA() {       
        setSilent(true);
        boolean success = true;
        long freqHertz;
        String valString;
        // Simlate read freq from Radio VFO a.
        freqHertz = aFrame.vfoState.getVfoAFrequency();            
        frequencyToDigits(freqHertz);
        if ( !aFrame.vfoState.vfoA_IsSelected()) aFrame.vfoState.setVfoASelected();
        //getTraversalOrder().get(0).requestFocus();
        setSilent(false);
        return success;
    }
    /**
     * Method called when the VfoB radio button changes the VFO selection with
     * the requirement to update the VFO display control with the
     * frequency read from the radio VFO B.
     * 
     * Turn off the VFO change handler while this update takes place.  Set focus
     * on the ones digit.
     * @return true.
     */
    public boolean loadRadioFrequencyToVfoB() {       
        setSilent(true);
        boolean success = true;
        long freqHertz;
        String valString;    
        // Simlate read freq from Radio VFO B.           
        freqHertz = aFrame.vfoState.getVfoBFrequency();            
        frequencyToDigits(freqHertz);
        if ( aFrame.vfoState.vfoA_IsSelected())  aFrame.vfoState.setVfoBSelected();
        //getTraversalOrder().get(0).requestFocus();
        setSilent(false);
        return success;
    }

    /**
     * This listener could possibly called very early on so there is a "silent"
     * variable that is used to prevent handling events before components are
     * created. Also, when the VFO selection is changed, that variable is set
     * true to mute the barrage of changes.
     * 
     * @param evt 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //if (silent) return;
        Object source = evt.getSource();
        DecadeDigit digit = (DecadeDigit)source;
        // getValue() synchronizes the model and label values.
        int value = (Integer) digit.getValue();        
        DecadeModel model = digit.getModel();
        DecadeModel decadeModel = (DecadeModel)model;
        int decade = decadeModel.getDecade();
        if( aFrame.vfoState == null) {
            // We are in the contruction process. Too early.
            return;
        }
        // Change the field description so voiceOver will announce it.
        long freq = digitsToFrequency();
        aFrame.vfoState.writeFrequencyToRadioSelectedVfo(freq);
        String vfoString = "VFO B";
        if (aFrame.vfoState.vfoA_IsSelected()) vfoString = "VFO A";           
        //System.out.println("handleChangeEvent - model value: "+ String.valueOf(value)); 
        for ( int iii=0; iii<QUANTITY_DIGITS; iii++) {
            StringBuilder freqString = new StringBuilder("");
            freqString.append(vfoString+" Frequency ");
            freqString.append(Double.toString(((double)freq)/1000000.)+" Mhz; ");
            Component comp = order.get(iii);
            JLabel label = (JLabel)comp;
            freqString.append(label.getAccessibleContext().getAccessibleName());
            label.getAccessibleContext().setAccessibleDescription(freqString.toString());
        }
    }       
    
    private void digitsPanelAncestorResized(java.awt.event.HierarchyEvent evt) {                                            
        Component comp = evt.getComponent();
        JLayeredPane pane = (JLayeredPane) comp;
        //adjustSize(pane);        
    }                                           

}
