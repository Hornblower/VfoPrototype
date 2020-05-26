/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Represents a filled rectangle.
 * @author Coz
 */
public class FilledRectangle extends Geometry {
    public FilledRectangle(Rectangle bounds, Color fillColor) {
        super(bounds, fillColor, true, 4, "Filled Rectangle");
    }
    @Override
    public void draw(Graphics g) {
        g.setColor(usedColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);            
    }
} 
