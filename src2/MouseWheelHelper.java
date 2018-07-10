/* requires JRE 1.4+ */

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class MouseWheelHelper {
    
    private static class Listener implements MouseWheelListener {
        private final ScrollingComponent sc;
        
        Listener(ScrollingComponent sc) {
            this.sc = sc;
        }
        
        /** @Override */
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL) return;
            sc.mouseWheelMoved(e.getUnitsToScroll());
        }
    }
    
    public static void addListener(ScrollingComponent sc) {
        sc.addMouseWheelListener(new Listener(sc));
        System.out.println("added mouse wheel listener.");
    }
    
}
