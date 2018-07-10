/* requires JRE 1.2+ */

import java.awt.GraphicsEnvironment;

class Future {
    
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    public static String getMonospacedFont() {
        String[] fontNames = new Future().ge.getAvailableFontFamilyNames();
        for(int i = 0; i < fontNames.length; i++) {
            String s = fontNames[i];
            if(!s.equals("Unifont")) continue;
            System.out.println("unifont detected.");
            return s;
        }
        return null;
    }
    
}
