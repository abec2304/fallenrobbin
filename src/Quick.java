// TODO: add mouse handler for popup menu
// TODO: implement auto-scrolling
// TODO: update view as log is read (?)
// TODO: implement client
// TODO: generate large log files for testing
// TODO: write server for testing

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class Quick {
    
    private static class ScrollListener implements AdjustmentListener {
        private final ScrollingComponent sc;
        
         ScrollListener(ScrollingComponent sc) {
             this.sc = sc;
         }
        
        /** @Override */
        public void adjustmentValueChanged(AdjustmentEvent e) {
            sc.scroll(e.getValue());
        }
    }
    
    private static class ScrollKeyListener extends KeyAdapter {
        private final ScrollingComponent sc;
        
        ScrollKeyListener(ScrollingComponent sc) {
            this.sc = sc;
        }
        
        /** @Override */
        public void keyPressed(KeyEvent e) {
            sc.keyPressed(e.getKeyCode());
        }
    }
    
    private static class CustomTabbedPane extends JTabbedPane {
        /** @Override */
        public Component getComponentAt(int index) {
            Component c = super.getComponentAt(index);
            return c != null ? c : super.getComponentAt(0);
        }
    }
    
    private static class TabChangeListener implements ChangeListener {
        private final ScrollingComponent sc;
        
        TabChangeListener(ScrollingComponent sc) {
            this.sc = sc;
        }
        
        /** @Override */
        public void stateChanged(ChangeEvent e) {
           JTabbedPane tabPane = (JTabbedPane)e.getSource();
           sc.setTab(tabPane.getSelectedIndex());
        }
    }
    
    private static void createAndShowGUI(Chat chat) {
        JScrollBar scrollbar = new JScrollBar();
        //scrollbar.setVisibleAmount(0); /* so we can actually reach max. */
        
        final ScrollingComponent sc = new ScrollingComponent(chat, scrollbar);
        
        sc.addKeyListener(new ScrollKeyListener(sc));
        
        sc.addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent e) {
               if(!sc.hasFocus()) sc.requestFocus();
           } 
        });
        
        scrollbar.addAdjustmentListener(new ScrollListener(sc));
        
        Class[] mArgs = {ScrollingComponent.class};
        Method m = Util.getMethod("MouseWheelHelper", "addListener", mArgs);
        Util.invoke(m, null, new Object[]{sc});
        
        ActionListener actionListener = new ActionListener() {
            /** @Override */
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                if(actionCommand == null) return;
                
                if(actionCommand.equals("copy")) {
                    /* TODO: get menu open pos.. */
                }
            }
        };
        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("copy chat message");
        menuItem.setActionCommand("copy");
        menuItem.addActionListener(actionListener);
        popupMenu.add(menuItem);
        //scrollingCanvas.setComponentPopupMenu(popupMenu);
        
        JTabbedPane tabbedPane = new CustomTabbedPane();
        tabbedPane.addChangeListener(new TabChangeListener(sc));
        tabbedPane.addTab("chat", sc);
        tabbedPane.addTab("votes", null);
        tabbedPane.addTab("abandons", null);
        for(int i = 0; i < Constants.CHANNELS.length; i++) {
            String s = Constants.CHANNELS[i];
            s = s.substring(0, s.length() - 1);
            tabbedPane.addTab(s, null);
        }
        tabbedPane.addTab("notes", new javax.swing.JTextPane());
        
        JTextField textField = new JTextField();
        
        JFrame frame = new JFrame("Quick") {
            /** @Override */
            public void dispose() {
                super.dispose();
                System.out.println("-closing-");
                System.exit(0);
            }
        };
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JComponent contentPane = (JComponent)frame.getContentPane();
        contentPane.setPreferredSize(new Dimension(800, 400));
        
        contentPane.add(tabbedPane);
        contentPane.add(scrollbar, BorderLayout.EAST);
        contentPane.add(textField, BorderLayout.SOUTH);
        
        frame.pack();
        sc.requestFocus();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        final Chat chat = new Chat();
        SwingUtilities.invokeLater(new Runnable() {
            /** @Override */
            public void run() {
                createAndShowGUI(chat);
            }
        });
        
        MessageParser messageParser = new MessageParser(chat);
        
        try {
            Thread.sleep(50L);
        } catch(InterruptedException ie) {
            
        }
        
        LogReader logReader = new LogReader(messageParser);
        File logFile = new File(args[0]);
        logReader.readLog(logFile);
    }
    
}
