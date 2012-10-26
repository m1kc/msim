package com.tomclaw.bingear;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Игорь
 */
public class IncorrectValueException extends Throwable {

    /**
     * Constructs a <code>IncorrectValueException</code> with no detail message.
     */
    public IncorrectValueException() {
        super();
    }

    /**
     * Constructs a <code>IncorrectValueException</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public IncorrectValueException(String s) {
        super(s);
    }
}
