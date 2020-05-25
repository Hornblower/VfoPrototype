/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;



import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;


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
final public class VfoDisplayControl extends JInternalFrame 
        implements PropertyChangeListener , ItemListener {
    
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
    


    public VfoDisplayControl(VfoPrototype2 frame) {
        super("VFO Display Control");
        aFrame = frame;
        setFrameIcon(null); // does not remove icon
        setClosable(false);
        setFocusCycleRoot(true);
        setFocusable(true);
        setResizable(false);
        setVisible(false);
        
    }

    public void initDigits() {
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
        addMenuBar();
        inhibit = false;
        long selectedFreq = vfoState.getSelectedVfoFrequency();
        frequencyToDigits(selectedFreq);

        
    }
        
    private void addMenuBar() {    
        JMenuBar menuBar = new JMenuBar();
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
        JRadioButtonMenuItem menuItemA = new JRadioButtonMenuItem(VFO_SELECT_A_TEXT, true);
        menuItemA.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItemA.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        AccessibleContext itemAContext = menuItemA.getAccessibleContext();
        itemAContext.setAccessibleDescription(
            "VFO panel controls radio VFO A");
        itemAContext.setAccessibleName("Choose radio VFO A");       
        menuItemA.addItemListener(this);
        menu.add(menuItemA);
        //Set JMenuItem B.
        JRadioButtonMenuItem menuItemB = new JRadioButtonMenuItem(VFO_SELECT_B_TEXT, false);
        menuItemB.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_B, ActionEvent.ALT_MASK));
        AccessibleContext itemBContext = menuItemB.getAccessibleContext();
        itemBContext.setAccessibleDescription(
            "VFO panel controls radio VFO B");
        itemBContext.setAccessibleName("Choose radio VFO B");
        menuItemB.addItemListener(this);
        menu.add(menuItemB);
        // Add VFO copy items.
        menu.addSeparator();
        JMenuItem a2b = new JMenuItem("Copy VFO A to VFO B", KeyEvent.VK_C);
        AccessibleContext a2bContext = a2b.getAccessibleContext();
        a2bContext.setAccessibleName("Copy Vfo A to Vfo B");
        a2bContext.setAccessibleDescription("Use shortcut key option C");
        menu.add(a2b);
        JMenuItem swap = new JMenuItem("Swap VFO A with VFO B", KeyEvent.VK_S);
        AccessibleContext swapContext = a2b.getAccessibleContext();
        swapContext.setAccessibleName("Swap Vfo A with Vfo B");
        swapContext.setAccessibleDescription("Use shortcut key option S");
        menu.add(swap);

        VfoAState stateA = new VfoAState(aFrame.frequencyVfoA, menuItemA);
        VfoBState stateB = new VfoBState(aFrame.frequencyVfoB, menuItemB);

        ArrayList<VfoState> vfoStates = new ArrayList<VfoState>(
                Arrays.asList((VfoState)stateA, (VfoState)stateB ));

       VfoStateContext vfoContext = new VfoStateContext(vfoStates);




        //Below is another version of the state machine.

        
        vfoState = new VfoSelectionInterface(menuItemA, menuItemB,
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
 
    }

    
    private void insertDigitsIntoPanels() {
        aFrame.digitsParent.setBackground(Color.black);
        JLayeredPane pane = aFrame.jLayeredPaneMegahertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(9));
        pane.add(freqDigits.get(8));
        pane.add(freqDigits.get(7));
        pane.add(freqDigits.get(6));
        
        pane = aFrame.jLayeredPaneKilohertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(5));
        pane.add(freqDigits.get(4));
        pane.add(freqDigits.get(3));
        
        pane = aFrame.jLayeredPaneHertz;
        pane.removeAll();
        pane.setBackground(Color.black);
        ((FlowLayout) pane.getLayout()).setHgap(0);
        pane.add(freqDigits.get(2));
        pane.add(freqDigits.get(1));
        pane.add(freqDigits.get(0));
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
    
    public void adjustSize(JLayeredPane resizedComponent) {
        int fontSize = (int) (this.getWidth() / 9.0);
        for (Component comp : resizedComponent.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER)) {
            DecadeDigit digit = (DecadeDigit) comp;
            double fs = digit.fontScale;
            Font font = new Font("Monospace", Font.PLAIN, (int) (fontSize * fs));
            comp.setFont(font);
        }
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
        getTraversalOrder().get(0).requestFocus();
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
        getTraversalOrder().get(0).requestFocus();
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
        System.out.println("handleChangeEvent - model value: "+ String.valueOf(value)); 
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
    public void itemStateChanged(ItemEvent e) {
        if (inhibit) return;
        Object itemObj = e.getItem();
        JMenuItem item = (JMenuItem) itemObj;
        String itemText = item.getText();
        System.out.println("item.name :"+itemText);
        
        //int id = e.getID();
        //System.out.println("itemEvent ID "+ id);
        
        if (itemText.equals(VFO_SELECT_A_TEXT)) {
            //item.firePropertyChange("MENU_ITEM1", false, true);
            if (item.isSelected()) {
                vfoState.setVfoASelected();
                aFrame.prefs.put(LAST_VFO, VFO_SELECT_A_TEXT);
                // If voiceOver enabled, need this dialog to announce vfo change.
                JOptionPane.showMessageDialog(this,
                    "VFO A Selected",
                    "VFO A Selected", // VoiceOver reads only this line
                    JOptionPane.PLAIN_MESSAGE);
            }           
        }
        else if (itemText.equals(VFO_SELECT_B_TEXT)) {
            //item.firePropertyChange("MENU_ITEM1", false, true);
            if (item.isSelected()) {
                vfoState.setVfoBSelected();
                aFrame.prefs.put(LAST_VFO, VFO_SELECT_B_TEXT);
                // If voiceOver enabled, need this dialog to announce vfo change.
                JOptionPane.showMessageDialog(this,
                    "VFO B Selected",
                    "VFO B Selected",  // VoiceOver reads only this line
                    JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("Unknown menu item handled in itemStateChanged()");
        }
        long freq = vfoState.getSelectedVfoFrequency();
        frequencyToDigits(freq);
        // Cause voiceOver to anounce frequency and thus which radio VFO.
        freqDigits.get(0).requestFocus();
    }
}
