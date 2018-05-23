package edu.mit.cci.pogs.utils;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SqlTimestampPropertyEditor extends PropertyEditorSupport {

    public static final String DEFAULT_BATCH_PATTERN = "dd/MM/yyyy HH:mm";

    private final SimpleDateFormat sdf;

    /**
     * uses default pattern yyyy-MM-dd for date parsing.
     */
    public SqlTimestampPropertyEditor() {
        this.sdf = new SimpleDateFormat(SqlTimestampPropertyEditor.DEFAULT_BATCH_PATTERN);
    }

    /**
     * Uses the given pattern for dateparsing, see {@link SimpleDateFormat} for allowed patterns.
     *
     * @param pattern the pattern describing the date and time format
     * @see SimpleDateFormat#SimpleDateFormat(String)
     */
    public SqlTimestampPropertyEditor(String pattern) {
        this.sdf = new SimpleDateFormat(pattern);
    }

    /**
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        try {
            setValue(new Timestamp(this.sdf.parse(text).getTime()));
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
        }
    }

    /**
     * Format the Timestamp as String, using the specified DateFormat.
     */
    @Override
    public String getAsText() {
        Timestamp value = (Timestamp) getValue();
        return (value != null ? this.sdf.format(value) : "");
    }
}