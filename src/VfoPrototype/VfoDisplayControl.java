/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.JFormattedTextField;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;


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
 * digit is a formatted text field, but editing is disabled.  Editing introduces
 * many complications which include the question of when to commit.  It is far
 * easier and quicker to use the arrow keys.   That is why editing is disabled.
 * 
 * To scan the band, pick a decade digit and hold down the up/down arrow.
 * 
 * @author Coz
 */
final public class VfoDisplayControl extends GroupBox 
        implements PropertyChangeListener , ItemListener, ActionListener {
    
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
    VfoSelectionInterface vfoState;
    double ONES_RELATIVE_SIZE = 0.7;
    double DIGIT_RELATIVE_SIZE = 1.0;
    
    JLayeredPane layeredPaneMegahertz; 
    JLayeredPane layeredPaneKilohertz; 
    JLayeredPane layeredPaneHertz;
    JPanel glassPane;       
    Rectangle megaBounds;                
    Rectangle kiloBounds; 
    Rectangle unnoBounds;



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
        Rectangle frameBounds = display.getBounds();
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
        freqDigits.add(new DecadeDigit(this, 0.7));    
        freqDigits.add(new DecadeDigit(this, 0.7));    
        freqDigits.add(new DecadeDigit(this, 0.7)); 
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.add(new DecadeDigit(this, 1.0));    
        freqDigits.add(new DecadeDigit(this, 1.0));      
        freqDigits.add(new DecadeDigit(this, 1.0));
        freqDigits.add(new DecadeDigit(this, 1.0));  
        freqDigits.add(new DecadeDigit(this, 1.0));   
        freqDigits.add(new DecadeDigit(this, 1.0));
        assert(freqDigits.size() == QUANTITY_DIGITS);      
        
        // Link the digits by linking all the models.
        DecadeDigit.linkAllDigits(freqDigits, QUANTITY_DIGITS);
        // Save the focus order.        
        order = new Vector<>(QUANTITY_DIGITS);
        for (int iii=0; iii<QUANTITY_DIGITS; iii++) {
            // The order vector contains the formated text fields.
            Component ftf = freqDigits.get(iii);
            // Every ftf has unique accessible info based on decade. 
            ((DecadeDigit)ftf).setAccessibleInfo();
            order.add(ftf);
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
        Container contentPane = display.getContentPane();
        Rectangle contentBounds = contentPane.getBounds();
        
        contentPane.setLayout(null);

        display.setGlassPane(new JPanel());
        glassPane = (JPanel) display.getGlassPane();
        
        // We have the bounds for each component, do away with layout manager.
        glassPane.setLayout(null);
                
        
        //MUST HAVE THE FOLLOWING LINE FOR GLASS PANE TO BE TRANSPARENT!
        glassPane.setOpaque(false);
        
        //Insets gInsets = glassPane.getInsets(); // insets are zero       
        //Insets mgInsets = layeredPaneMegahertz.getInsets(); // insets are zero
        
        //////////////////////////////////////////////////////////////////
        // Compute component widths.  Some subtle math here....
        int digitGap = 5; // Is hgap for FlowLayout.
        int offsetY = 5; //  Moves digits down.
        int panelGap = 5; // Gap between panels and left and right ends
        int offsetX = panelGap; // Leave a border on the left.
        int titleBorderWidth = 5;
        int titleHeight = 23;
        int panelHeight = 130;
        int digitHeight = panelHeight - titleHeight - titleBorderWidth;
        double digitWidth = 
                (double)(contentBounds.width - 7*digitGap - 6*titleBorderWidth - 4*panelGap)/
                ((7.*DIGIT_RELATIVE_SIZE)+(3*ONES_RELATIVE_SIZE));
        int onesWide = 
                (int)(digitWidth * ONES_RELATIVE_SIZE * 3.0)+2*titleBorderWidth;
        int megaWide = (int)(4*digitWidth)+2*titleBorderWidth;
        int kiloWide = (int)(3*digitWidth)+2*titleBorderWidth;
        int onesOffsetX = offsetX+megaWide+panelGap+kiloWide+panelGap;
        //layeredPaneMegahertz.setAlignmentY(1.0f);
        
        int bigFontSize = computeFontSize((int) digitWidth,  digitHeight, "0", freqDigits.get(9).getFont());
        int littleFontSize = computeFontSize ((int)(digitWidth*.7), (int)(digitHeight*0.7), "0", freqDigits.get(9).getFont());
        System.out.println("digitWidth : "+digitWidth);
        System.out.println("digitHeight : "+digitHeight);
        
        for (int index=0; index<QUANTITY_DIGITS; index++) {
            DecadeDigit digit = freqDigits.get(index);
            double fs = digit.fontScale;
            if (index <3) {
                Font font = new Font("Monospace", Font.PLAIN, (int) (littleFontSize * fs * 0.75));
                digit.setFont(font);
            } else {
                Font font = new Font("Monospace", Font.PLAIN, (int) (bigFontSize * fs * 0.65));
                digit.setFont(font);
               
            } 
            Dimension prefSize = digit.getPreferredSize();
            System.out.println("Digit "+index+" preferredSize :"+prefSize);
        }
                
        ////////////////////////////////////////////////////////////////////
                
        megaBounds = new Rectangle( offsetX,  offsetY, megaWide, panelHeight);                   
        kiloBounds = new Rectangle(megaWide+digitGap+offsetX,  offsetY, kiloWide, panelHeight);
        unnoBounds = new Rectangle(onesOffsetX,  offsetY, onesWide, panelHeight);
        
        System.out.println("megaBounds :" + megaBounds);
        System.out.println("kiloBounds :" + kiloBounds);
        System.out.println("unnoBounds :" + unnoBounds);
        
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
        //adjustSize(layeredPaneMegahertz);

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
        //adjustSize(layeredPaneKilohertz);

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
        //adjustSize(layeredPaneHertz);
        
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
    protected void oldAdjustSize(JLayeredPane resizedComponent) {
        for (Component comp : resizedComponent.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER)) {
            DecadeDigit digit = (DecadeDigit) comp;
            double fs = digit.fontScale;
            if ( fs < .99) {
                // Small digits are size limited by width.  fs = 0.7
                double handPickedDivisor = 2.1;
                int titleBorderWidth = 20;
                int fontSize = (int) ((resizedComponent.getWidth()-titleBorderWidth*2) / handPickedDivisor);
                Font font = new Font("Monospace", Font.PLAIN, (int) (fontSize * fs));
                comp.setFont(font);
            } else { 
                // Large digits are limited by layered pane height.  fs = 1.0
                double handPickedDivisor = 1.7;
                int hertzTitleHeight = 23;
                int fontSize = (int) ((resizedComponent.getHeight()-hertzTitleHeight) / handPickedDivisor);
                Font font = new Font("Monospace", Font.PLAIN, (int) (fontSize * fs));
                comp.setFont(font);
            }
            Dimension prefSize = digit.getPreferredSize();
            int decade = digit.getDecade();
            //System.out.println("DecadeDigit : "+decade+" preferredSize : "+prefSize);
        }
    }

    /**
     * Given the label dimensions, the text, and the font, what is the font size 
     * that will fit?
     * 
     * @param fieldWidth
     * @param fieldHeight
     * @param text
     * @param font
     * @return fontSize integer
     */
    protected int computeFontSize( int fieldWidth, int fieldHeight, String text, Font font) {
        
        //Font font = ftf.getFont();
        //String text = ftf.getText();

        int stringWidth = getFontMetrics(font).stringWidth(text);
        int componentWidth = fieldWidth;

        // Find out how much the font can grow in width.
        double widthRatio = (double)componentWidth / (double)stringWidth;
        System.out.println("widthRatio of "+font.getName()+" is " + String.valueOf(widthRatio) );
        int newFontSize = (int)(font.getSize() * widthRatio);
        int componentHeight = fieldHeight;

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);
        System.out.println("Font size to use is "+ String.valueOf(fontSizeToUse));
        return fontSizeToUse;
        

    }





    
    /**
     * All the dynamic components are added to the glass pane so the context
     * pane is just boilerPlate and there are no events to handle.
     * @param display 
     */
    public void setupContentPane(VfoDisplayControl display) {
        
        // Get the dims of the small Hertz digits.               
        Dimension smallDims = new Dimension(34,56);
        aFrame.jLayeredPaneHertz.setBounds(unnoBounds);
        aFrame.jLayeredPaneHertz.setBackground(Color.BLACK);
        for (int iii=0; iii<3; iii++) {
            BarnDoor door = new BarnDoor(smallDims);
            door.addShapes();           
            aFrame.jLayeredPaneHertz.add(door);
            door.setVisible(true);
        }
        // Get the dims of the tall Hertz digits.               
        Dimension tallDims = new Dimension(51,89);
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
        
            // Add an exclusive interface to the Vfo selector so that only one thread
        // at a time gains access.
        vfoState = new VfoSelectionInterface(aFrame.menuItemA, aFrame.menuItemB,
            aFrame.frequencyVfoA, aFrame.frequencyVfoB );
 
        // @todo Later we will get these from Preferences.  When do we save a freq?
        vfoState.writeFrequencyToRadioVfoA(MSN_FREQ);
        vfoState.writeFrequencyToRadioVfoB(SHAWSVILLE_REPEATER_OUTPUT_FREQ);
      
          // @todo Add this later with stored frequency of the selected vfo.
        String lastVfo = aFrame.prefs.get("LAST_VFO", "VFO_SELECT_A_TEXT");
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
        inhibit = false;        
    }
    
    
    public void makeVisible() {
        
        long selectedFreq = vfoState.getSelectedVfoFrequency();
        frequencyToDigits(selectedFreq);        

        getGlassPane().setVisible(true);
        getContentPane().setVisible(true);
        
        getContentPane().repaint();

        //Rectangle menuBarBounds = menuBar.getBounds(); //Does not work.  Bug.
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
        freqHertz = vfoState.getVfoAFrequency();            
        frequencyToDigits(freqHertz);
        if ( !vfoState.vfoA_IsSelected()) vfoState.setVfoASelected();
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
        freqHertz = vfoState.getVfoBFrequency();            
        frequencyToDigits(freqHertz);
        if ( vfoState.vfoA_IsSelected())  vfoState.setVfoBSelected();
        //getTraversalOrder().get(0).requestFocus();
        setSilent(false);
        return success;
    }
    
    @SuppressWarnings("unchecked")
    public void setUpFocusManager() {
        // Make sure that the JFrame is focus manager.
        // It appears that voiceOver StepInto is ignoring focus manager.
        setFocusCycleRoot(true);
        VfoDigitTraversalPolicy policy; 
        Vector<Component> order = getTraversalOrder();
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
       
        final AWTKeyStroke keyStrokeRight = KeyStroke.getKeyStroke("LEFT");
        set.add(keyStrokeRight) ;
        setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
        
        set = new HashSet<>( getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS ) );
        final AWTKeyStroke keyStrokeLeft = KeyStroke.getKeyStroke("RIGHT");           
        set.add(keyStrokeLeft);
        setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set );
        setFocusTraversalKeysEnabled(true);
                    
        assert(areFocusTraversalKeysSet(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS) );
        
        assert( getFocusTraversalPolicy() != null);
        assert( isFocusCycleRoot());
        setEnabled(true);
             
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
        String name = digit.getName();
        // getValue() synchronizes the model and ftf values.
        int value = (Integer) digit.getValue();
        //System.out.print("PropertyChangeEvent :");
        //System.out.print(" source : " + name);
        //System.out.println(" ; getValue() returns :"+ value);        
        Component ftf = (Component)source;         
        // Update this ftf description with new frequency and decade name.
        // Update the debug panel frequency.           
        DecadeModel model = digit.getModel();
        DecadeModel decadeModel = (DecadeModel)model;
        int decade = decadeModel.getDecade();
        if( vfoState == null) {
            // We are in the contruction process. Too early.
            return;
        }
        // Change the field description so voiceOver will announce it.
        long freq = digitsToFrequency();
        vfoState.writeFrequencyToRadioSelectedVfo(freq);
        String vfoString = "VFO B";
        if (vfoState.vfoA_IsSelected()) vfoString = "VFO A";           
        //System.out.println("handleChangeEvent - model value: "+ String.valueOf(value)); 
        for ( int iii=0; iii<QUANTITY_DIGITS; iii++) {
            StringBuilder freqString = new StringBuilder("");
            freqString.append(vfoString+" Frequency "+Double.toString(((double)freq)/1000000.)+" Mhz; ");
            Component comp = order.get(iii);
            JFormattedTextField ftfield = (JFormattedTextField)comp;
            freqString.append(ftfield.getAccessibleContext().getAccessibleName());
            ftfield.getAccessibleContext().setAccessibleDescription(freqString.toString());
        }
        // Print out just this field's name and description.
        //String ftfName = ftf.getAccessibleContext().getAccessibleName();
        //System.out.println("ftf accessible name :"+ftfName);
        String ftfDesc = ftf.getAccessibleContext().getAccessibleDescription();
        System.out.println("ftf accessible description :"+ftfDesc);               
    }       

    

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionString = e.getActionCommand();
        if (actionString == "Copy VFO A to VFO B") {
            vfoState.copyAtoB();
                 
            if (!vfoState.vfoA_IsSelected()) {
                long freqA = vfoState.getVfoAFrequency();
                aFrame.vfoGroup.frequencyToDigits(freqA);
            }
            JOptionPane.showMessageDialog(this,
                    "VFO A copied to VFO B",
                    "VFO A copied to VFO B",  // VoiceOver reads only this line.
                    JOptionPane.PLAIN_MESSAGE);
        } else if (actionString == "Swap VFO A with VFO B") {
            vfoState.swapAwithB();
                 
            if (vfoState.vfoA_IsSelected()) {
                long freqA = vfoState.getVfoAFrequency();
                aFrame.vfoGroup.frequencyToDigits(freqA);
            } else {
                long freqB = vfoState.getVfoBFrequency();
                aFrame.vfoGroup.frequencyToDigits(freqB);
                
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
                aFrame.prefs.put(LAST_VFO, VFO_SELECT_A_TEXT);
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
                aFrame.prefs.put(LAST_VFO, VFO_SELECT_B_TEXT);
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
        frequencyToDigits(freq);
    }

    
    
    private void digitsPanelAncestorResized(java.awt.event.HierarchyEvent evt) {                                            
        Component comp = evt.getComponent();
        JLayeredPane pane = (JLayeredPane) comp;
        //adjustSize(pane);        
    }                                           

}
