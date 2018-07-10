import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Method;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

class Constants {
    
    /* filtering options */
    static final boolean FILTER_MESSAGES = true;
    static final boolean FILTER_VOTES = true;
    
    /* values for extra attribute */
    static final byte EXTRA_USER_ACTION = 1;
    static final byte EXTRA_SYSTEM_ACTION = 2;
    static final byte EXTRA_SYSTEM_MESSAGE = 3;
    static final byte EXTRA_VOTE = 4;
    static final byte EXTRA_PART_OR_JOIN = 5;
    
    static final byte EXTRA_VOTE_ABANDON = 10;
    static final byte EXTRA_VOTE_CONTINUE = 11;
    static final byte EXTRA_VOTE_INCREASE = 12;
    
    /* maximum username length */
    static final short MAX_USERNAME_LEN = 20;
    
    /* system username */
    static final String USERNAME_SYSTEM = "[robin]";
    
    /* channel offset */
    static final int OFFSET_CHANNELS = 3;
    
    /* username colors */
    static final Color COLOR_SYSTEM = Color.white;
    static final Color[] COLOR_USERNAME;
    
    /* vote colors */
    static final Color COLOR_ABANDON = Color.decode("#f5a623");
    static final Color COLOR_STAY = Color.decode("#63a4ef");
    static final Color COLOR_GROW = Color.decode("#7ed321");
    
    /* other colors */
    static final Color COLOR_BG = Color.black;
    static final Color COLOR_FG = Color.gray;
    static final Color COLOR_ERROR = Color.pink;
    static final Color COLOR_MESSAGE = Color.lightGray;
    static final Color COLOR_TIME = COLOR_MESSAGE;
    
    /* dummy time */
    static final String TIME_DUMMY = Util.formatTime(null);
    
    /* fonts */
    static final Font FONT_REGULAR;
    static final Font FONT_ITALIC;
    static final Font FONT_TIME;
    
    /* channels */
    static final String[] CHANNELS;
    
    /* messages */
    static final String MESSAGE_NO_MATCH = "no compatible room found for matching, we will count votes and check again for a match in 1 minute.";
    static final String MESSAGE_PLEASE_VOTE = "polls are closing soon, please vote";
    static final String MESSAGE_USERS_ABANDONED = "{0} user(s) abandoned";
    
    static {
        /* get monospaced font */
        Method m = Util.getMethod("Future", "getMonospacedFont", null);
        String s = (String)Util.invoke(m, null, null);
        String fontName;
        if(s != null) {
            fontName = s;
        } else {
            fontName = "Monospaced";
        }
        
        /* set fonts */
        FONT_REGULAR = new Font(fontName, Font.PLAIN, 16);
        FONT_ITALIC = new Font(fontName, Font.ITALIC, 16);
        FONT_TIME = FONT_REGULAR;
        System.out.println("fonts prepared.");
        
        /* username color strings */
        String[] colorStrings = {
            "#e50000", /* red */
            "#db8e00", /* orange */
            "#ccc100", /* yellow */
            "#02be01", /* green */
            "#0083c7", /* blue */
            "#820080", /* purple */
        };
        
        /* generate username colors */
        int len = colorStrings.length;
        COLOR_USERNAME = new Color[len + 1];
        for(int i = 0; i < len; i++) {
            COLOR_USERNAME[i] = Color.decode(colorStrings[i]);
        }
        COLOR_USERNAME[len] = COLOR_MESSAGE;
        System.out.println("username colors prepared.");
        
        /* read channels */
        String[] channels = Channels.getChannels();
        CHANNELS = channels != null ? channels : new String[0];
        
        /* set look and feel */
        try {
            UIManager.setLookAndFeel("");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            System.out.println("set CDE/Motif L&F.");
        } catch(ClassNotFoundException cnfe) {
            
        } catch(InstantiationException ie) {
            
        } catch(IllegalAccessException iae) {
            
        } catch(UnsupportedLookAndFeelException ulafe) {
            
        } catch(ClassCastException cce) {
            
        }
    }
    
}
