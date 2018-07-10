import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JScrollBar;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;

class ScrollingComponent extends JComponent {
    
    private int tabIndex;
    private int visibleLines;
    
    private final Chat chat;
    private final JScrollBar scrollbar;
    private final int[] positions;
    
    ScrollingComponent(Chat chat, JScrollBar scrollbar) {
        this.chat = chat;
        this.scrollbar = scrollbar;
        positions = new int[chat.numTabs];
        setOpaque(true);
    }
    
    void setTab(int n) {
        if(n < 0) return;
        tabIndex = n;
    }
    
    void scroll(int n) {
        if(positions[tabIndex] == n) return;
        positions[tabIndex] = n;
        repaint();
    }
    
    void keyPressed(int keyCode) {
        if(keyCode == KeyEvent.VK_UP) {
            scrollbar.setValue(scrollbar.getValue() - 1);
        } else if(keyCode == KeyEvent.VK_DOWN) {
            scrollbar.setValue(scrollbar.getValue() + 1);
        } else if(keyCode == KeyEvent.VK_PAGE_UP) {
            scrollbar.setValue(scrollbar.getValue() - visibleLines);
        } else if(keyCode == KeyEvent.VK_PAGE_DOWN) {
            scrollbar.setValue(scrollbar.getValue() + visibleLines);
        } else if(keyCode == KeyEvent.VK_F10) {
            // TODO: switch tabbed pane -> component..
            /*Container parent = getParent();
            JPanel root = (JPanel)parent.getParent();
            root.add(this);*/
        }
    }
    
    void mouseWheelMoved(int unitsToScroll) {
        int totalScrollAmount = unitsToScroll * scrollbar.getUnitIncrement();
        scrollbar.setValue(scrollbar.getValue() + totalScrollAmount);
    }
    
    /** @Override */
    public boolean isFocusTraversable() {
        // doesn't change anything !?
        return true;
    }
    
    /** @Override */
    public void paintComponent(Graphics g) {
        /* get height */
        int height = getHeight();
        
        /* paint background */
        g.setColor(Constants.COLOR_BG);
        g.fillRect(0, 0, getWidth(), height);
        g.setColor(Constants.COLOR_FG);
        
        /* calculate font metrics */
        FontMetrics fm = g.getFontMetrics(Constants.FONT_REGULAR);
        int fontHeight = fm.getAscent();
        int nameWidth = fm.charWidth('W') * Constants.MAX_USERNAME_LEN;
        
        /* calculate time font metrics */
        int timeWidth;
        if(Constants.FONT_TIME != Constants.FONT_REGULAR) {
            FontMetrics timeFm = g.getFontMetrics(Constants.FONT_TIME);
            int timeHeight = timeFm.getAscent();
            if(timeHeight > fontHeight) fontHeight = timeHeight;
            timeWidth = timeFm.stringWidth(Constants.TIME_DUMMY);
        } else {
            timeWidth = fm.stringWidth(Constants.TIME_DUMMY);
        }
        
        /* add divider */
        int lineOff = timeWidth + nameWidth;
        g.drawLine(lineOff, 0, lineOff, height);
        
        /* calculate visible lines */
        visibleLines = height / fontHeight;
        
        /* get total lines */
        int totalLines = chat.numLines(tabIndex);
        
        /* update scroll bar maximum */
        int maximum = totalLines - visibleLines;
        if(maximum < 0) maximum = 0;
        int position = positions[tabIndex];
        scrollbar.setValues(position, 0, 0, maximum);
        
        /* paint text */
        chat.paintLines(tabIndex, position, g, fm, lineOff, fontHeight, height);
    }
    
}
