package vidivox.ui;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * A JTextField which automatically enforces a character limit
 * Reference: http://stackoverflow.com/questions/3519151/how-to-limit-the-number-of-characters-in-jtextfield
 */
public class JTextFieldWithCharacterLimit extends JTextField{
    private int limit;
    public JTextFieldWithCharacterLimit(int limit){
        super();
        this.limit=limit;
    }

    @Override
    protected Document createDefaultModel() {
        return new DocumentWithCharacterLimit();
    }

    private class DocumentWithCharacterLimit extends PlainDocument {
        /**
         * Override the insertString method of the Document so that the string is inserted only if there is space
         */
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if(str==null){
                return;
            }
            if((getLength()+str.length())<=limit){
                super.insertString(offs,str,a);
            }
        }
    }
}
