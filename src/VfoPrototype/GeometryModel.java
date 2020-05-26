/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VfoPrototype;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Polymorphic storage for component shapes that extend class Geometry and 
 * override the draw() method.
 * 
 * @author Coz
 */
public class GeometryModel {
    private ArrayList<Geometry> shapes;

    public GeometryModel() {
        this.shapes = new ArrayList<Geometry>();
    }
    
    public void addGeometry( Geometry geo) {
        shapes.add(geo);
    }

    /**
     * Draw each the members of the ArrayList in order.
     * @param g is Graphics g from Component to be drawn on.
    */
    public void draw(Graphics g) {
         for(Geometry geo:shapes){  
            geo.draw(g);         
        }
    }
}

