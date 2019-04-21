/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroids;

/**
 *
 * @author macle
 */
public class HitboxTemplates {
    private static double[][][] asteroidTemplates = new double[][][]{ //templates are in a scale of 10(easier to type in), then scaled to the diameter of asteroid
        new double[][] { //template 0
            new double[] {5, 0}, new double[] {2, 1}, new double[] {4, 2},
            new double[] {2, 4}, new double[] {1, 5}, new double[] {-2, 4},
            new double[] {-4, 2}, new double[] {-3, -1}, new double[] {-4, -2}, 
            new double[] {-2, -3}, new double[] {-1, -4}, new double[] {1, -5}, 
            new double[] {4, -3}
        },
        
        new double[][] { //template 1
            new double[] {4, 2}, new double[] {2, 3}, new double[] {0, 5},
            new double[] {-1, 3}, new double[] {-3, 4}, new double[] {-5, 1},
            new double[] {-5, -2}, new double[] {-2, -3}, new double[] {-1, -5},
            new double[] {1, -4}, new double[] {4, -2}
        },
        
        new double[][] { //template 2
            new double[] {5, 1}, new double[] {4, 2}, new double[] {3, 4},
            new double[] {1, 5}, new double[] {-1, 4}, new double[] {-3, 4},
            new double[] {-3, 3}, new double[] {-3, 3}, new double[] {-5, 0},
            new double[] {-5, -2}, new double[] {-4, -3}, new double[] {-1, -5},
            new double[] {1, -3}, new double[] {3, -3}, new double[] {4, -2}
        }
    };
    private static double[][] playerTemplate = new double[][] { //scale of 6
        new double[] {3, 0}, new double[] { -3, 2}, new double[] {-2, 5/3.0},
        new double[] {-2, 1.2}, new double[] {-2, -1.2}, new double[] {-2, -5/3.0}, 
        new double[] {-3, -2}
    };
    private static double[][] ufoTemplate = new double[][] { //scale of 10
        new double[] {5, 0}, new double[] { 2, -2}, new double[] {2, -3}, 
        new double[] {1, -4}, new double[] {-1, -4}, new double[] {-2, -3},
        new double[] {-2, -2}, new double[] {-5, 0}, new double[] {-2, 2},
        new double[] {2, 2}
    };
    private static double[][] batteryTemplate = new double[][] {
        new double[] {0, 1}, new double[] {0, 3}, new double[] {-1, 3},
        new double[] {-1, 4}, new double[] {-9, 4}, new double[] {-9, 0}, 
        new double[] {-1, 0}, new double[] {-1, 1}
    };
    
    public static Hitbox getScaleCopyOfRandomAsteroidTemplate(double diameter) {
        int tempNum = (int)(Math.random() * 3);
        double[][] tempPoints = asteroidTemplates[tempNum];
        return getScaleCopyOfTemplate(tempPoints, diameter, 10);
    }
    
    public static Hitbox getPlayerTemplate(double diameter) {
        return getScaleCopyOfTemplate(playerTemplate, diameter, 6);
    }
    
    public static Hitbox getUFOTemplate(double diameter) {
        return getScaleCopyOfTemplate(ufoTemplate, diameter, 10);
    }
    
    public static double[][] getBatteryTemplate(double diameter) {
        return getScaleFrameOfTemplate(batteryTemplate, diameter, 9);
    }
    
    private static double[][] getScaleFrameOfTemplate(double[][] template, double diameter, double oldScale) {
        double[][] scaleCopy = new double[template.length][2];
        for(int i = 0; i < template.length; i++) {
            double[] tempPoint = template[i];
            double[] copyPoint = new double[2];
            copyPoint[0] = tempPoint[0] * diameter/oldScale;
            copyPoint[1] = tempPoint[1] * diameter/oldScale;
            scaleCopy[i] = copyPoint;
        }
        return scaleCopy;
    }
            
    private static Hitbox getScaleCopyOfTemplate(double[][] template, double diameter, double oldScale) {
        
        return new Hitbox(getScaleFrameOfTemplate(template, diameter, oldScale));
    }
    
}
