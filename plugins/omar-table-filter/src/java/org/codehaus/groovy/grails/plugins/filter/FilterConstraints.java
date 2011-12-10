package org.codehaus.groovy.grails.plugins.filter;

/**
 * Filter constraints
 * This class probably will be removed, when the plugin support customization of date format
 *
 * @author maxwell
 */
public interface FilterConstraints {

    public static final String DATE_REGEX = "\\d{4}[-]\\d{2}[-]\\d{2}";

	public static final String DATE_TIME_REGEX = "\\d{4}[-]\\d{2}[-]\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";

	public static final String TIME_REGEX = ".?\\d{2}:\\d{2}:\\d{2}$";

	public static final String LAST_TIME = " 23:59:59";

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

}
