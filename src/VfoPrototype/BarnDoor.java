/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.*;

/**
 * Class is used to create a background on the JFrame content pane which depicts
 * a black background and white two fake buttons, top and bottom, that show click 
 * area for a DecadeDigit and are drawn behind a dynamic JTextField used by 
 * DecadeDigit which is contained by the same JFrame's glassPane.
 * 
 * This class depends on classes GeometryModel and Geometry.
 * 
 * Default color of line drawing is white.
 * Default color of fill is black.
 * 
 * @author Coz
 */
public class BarnDoor extends JPanel {
    private GeometryCollection model;   
    private  Color outlineColor = Color.DARK_GRAY;
    private  Color fillColor = Color.BLACK;
    private  boolean hasFill = false;
    private  Dimension prefSize;

    public BarnDoor(Dimension pSize) {
        model = new GeometryCollection();
        setPreferredSize(pSize);
        prefSize = pSize;       
    }
   
   public void setFillColor(Color c) {
       fillColor = c;
   }

    public void setOutlineColor(Color c) {
       outlineColor = c;
    }
   
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        model.draw(g);
    }
   
    public void addShapes() {
        setOpaque(true);
        // Painting coordinates are positive going to the right and down from
        // the top left corner of the space.
        // Fill the complete space occupied by the textField.
        Rectangle bounds = new Rectangle(0, 0, prefSize.width, prefSize.height);
        FilledRectangle filledRect =  new FilledRectangle(bounds, fillColor); 
        model.addGeometry((Geometry) filledRect);
           
        // Draw the upper button outline rectangle with inset of 5 percent of the 
        // fieldWidth.
        int inset = prefSize.width/20;
        int boxHeight = (prefSize.height/2)-(2*inset);
        int boxWidth = prefSize.width-(2*inset);
        Rectangle upperBoxRect = new Rectangle(inset,inset,boxWidth,boxHeight);
        OutlineRectangle upperBox = new OutlineRectangle( upperBoxRect, outlineColor);
        model.addGeometry((Geometry) upperBox);
        // Draw the lower outline rectangle.        
        Rectangle lowerBoxRect = new Rectangle(inset, inset+prefSize.height/2, 
                boxWidth, boxHeight);
        OutlineRectangle lowerBox = new OutlineRectangle(lowerBoxRect, outlineColor);
        model.addGeometry((Geometry)lowerBox);
               
        // Draw the upper arrow triangle outline at 5 percent inset.
        int Tinset = 2*inset;
        boxWidth = prefSize.width - (4*inset);
        boxHeight = prefSize.height/2 - (4*inset);
        Rectangle upArrowRect = new Rectangle( Tinset, Tinset, boxWidth, boxHeight);
        UpwardOutlineTriangle upTriangle = 
                new UpwardOutlineTriangle(upArrowRect, outlineColor);
        model.addGeometry(upTriangle);
        // Draw the lower arrow triangle outline.
        Rectangle downArrowRect = new Rectangle( Tinset, Tinset+prefSize.height/2,
                    boxWidth, boxHeight);
        DownwardOutlineTriangle downTriangle = 
                new DownwardOutlineTriangle(downArrowRect, outlineColor);
        model.addGeometry(downTriangle);

    }
}

