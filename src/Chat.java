import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

class Chat {
    
    /* number of chat tabs */
    final int numTabs = Constants.OFFSET_CHANNELS + Constants.CHANNELS.length;
    
    /* messages + lock object */
    //private final Object lock = new Object();
    private final Vector[] messages = new Vector[numTabs];    
    
    private final Vector abandons = new Vector();
    
    /* calendar */
    private final Calendar calendar = Calendar.getInstance();
    
    /* format */
    private final MessageFormat abandonFormat = new MessageFormat(Constants.MESSAGE_USERS_ABANDONED);
    
    void addAbandon(byte[] b) {
        abandons.addElement(b);
    }
    
    void addLine(int i, byte[] b) {
        //synchronized(lock) {
            if(messages[i] == null) messages[i] = new Vector();
            messages[i].addElement(b);
        //}
    }
    
    int numLines(int i) {
        //synchronized(lock) {
            if(messages[i] == null) return -1;
            return messages[i].size();
        //}
    }
    
    void paintLines(int tabIndex, int position, Graphics g, FontMetrics fm, int x, int fontHeight, int height) {
        //synchronized(lock) {
            Vector vector = messages[tabIndex];
            if(vector == null) return;
            int y = fontHeight;
            int len = vector.size();
            for(int i = position; i < len; i++) {
                /* get "raw" line */
                byte[] raw = (byte[])vector.elementAt(i);
                
                /* paint line */
                paintLine(g, fm, raw, x, y);
                
                /* update y offset */
                if(y >= height) break;
                y += fontHeight;
            }
        //}
    }
    
    private void paintLine(Graphics g, FontMetrics fm, byte[] raw, int x, int y) {
        /* prepare time */
        long l = Util.readLong(raw);
        calendar.setTime(new Date(l));
        String time = Util.formatTime(calendar);
        
        /* paint time */
        g.setColor(Constants.COLOR_TIME);
        g.setFont(Constants.FONT_TIME);
        g.drawString(time, 0, y);
        
        /* read attributes */
        byte extra = raw[8];
        byte usernameLen = raw[9];
        
        /* prepare username */
        String username;
        if(usernameLen == 0) {
            username = Constants.USERNAME_SYSTEM;
            g.setColor(Constants.COLOR_SYSTEM);
        } else {
            username = new String(raw, 10, usernameLen);
            g.setColor(Constants.COLOR_USERNAME[Shine.getFlairNum(username)]);
        }
        
        /* calculate username offset */
        int usernameOff = x - fm.stringWidth(username);
        
        /* paint username */
        g.setFont(Constants.FONT_REGULAR);
        g.drawString(username, usernameOff, y);
        
        /* prepare message */
        int messageOff = 10 + usernameLen;
        int messageLen = raw.length - messageOff;
        String message;
        try {
            message = new String(raw, messageOff, messageLen, "UTF8");
        } catch(UnsupportedEncodingException uee) {
            message = "?";
        }
        
        /* handle extra */
        if(extra == Constants.EXTRA_SYSTEM_ACTION) {
            if(message.equals("n")) {
                message = Constants.MESSAGE_NO_MATCH;
            } else if(message.equals("p")) {
                message = Constants.MESSAGE_PLEASE_VOTE;
            } else {
                message = abandonFormat.format(new Object[]{message});
            }
            g.setFont(Constants.FONT_ITALIC);
        } else if(extra == Constants.EXTRA_VOTE_ABANDON) {
            message = "voted to ABANDON";
            g.setColor(Constants.COLOR_ABANDON);
            g.setFont(Constants.FONT_ITALIC);
        } else if(extra == Constants.EXTRA_VOTE_CONTINUE) {
            message = "voted to STAY";
            g.setColor(Constants.COLOR_STAY);
            g.setFont(Constants.FONT_ITALIC);
        } else if(extra == Constants.EXTRA_VOTE_INCREASE) {
            message = "voted to GROW";
            g.setColor(Constants.COLOR_GROW);
            g.setFont(Constants.FONT_ITALIC);
        } else if(extra == Constants.EXTRA_VOTE) {
            message = "voted to " + message;
            g.setColor(Constants.COLOR_ERROR);
            g.setFont(Constants.FONT_ITALIC);
        } else if(extra == Constants.EXTRA_USER_ACTION) {
            g.setColor(Constants.COLOR_MESSAGE);
            g.setFont(Constants.FONT_ITALIC);
        } else {
            g.setColor(Constants.COLOR_MESSAGE);
        }
        
        /* paint message */
        g.drawString(message, x + 1, y);
    }
    
}
