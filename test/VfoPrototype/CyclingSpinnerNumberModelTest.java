/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import javax.swing.event.ChangeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Coz
 */
public class CyclingSpinnerNumberModelTest {
    
    public CyclingSpinnerNumberModelTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of linkModels method, of class CyclingSpinnerNumberModel.
     */
    @Test
    public void testLinkModels() {
        System.out.println("linkModels");
        CyclingSpinnerNumberModel lowModel = null;
        CyclingSpinnerNumberModel highModel = null;
        CyclingSpinnerNumberModel.linkModels(lowModel, highModel);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextValue method, of class CyclingSpinnerNumberModel.
     */
    @Test
    public void testGetNextValue() {
        System.out.println("getNextValue");
        CyclingSpinnerNumberModel instance = null;
        Object expResult = null;
        Object result = instance.getNextValue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPreviousValue method, of class CyclingSpinnerNumberModel.
     */
    @Test
    public void testGetPreviousValue() {
        System.out.println("getPreviousValue");
        CyclingSpinnerNumberModel instance = null;
        Object expResult = null;
        Object result = instance.getPreviousValue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stateChanged method, of class CyclingSpinnerNumberModel.
     */
    @Test
    public void testStateChanged() {
        System.out.println("stateChanged");
        ChangeEvent e = null;
        CyclingSpinnerNumberModel instance = null;
        instance.stateChanged(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
