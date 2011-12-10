package org.codehaus.groovy.grails.plugins.filter;

import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Utility class to parse values of filter
 *
 * @author maxwell
 */
public enum FieldType {

    DATE {

        public void parse( Filter filter ) {

            SimpleDateFormat format = new SimpleDateFormat( FilterConstraints.DATE_FORMAT );

            try {

                if ( !filter.getFilterValue().matches(FilterConstraints.DATE_TIME_REGEX ) ) {
                    filter.setFilterValue(filter.getFilterValue().trim() + " 00:00:00");
                }

                filter.setFilterValue(filter.getFilterValue().replace("+", " "));
                filter.setFilterValueObj(format.parse(filter.getFilterValue()));

                if ( filter.getFilterCriteria() == FilterType.LIKE || filter.getFilterCriteria() == FilterType.EQUALS ) {

                    filter.setFilterCriteria( FilterType.BETWEEN );
                    filter.setFilterValue2(filter.getFilterValue().replaceAll( FilterConstraints.TIME_REGEX, FilterConstraints.LAST_TIME ));
                    filter.setFilterValue2Obj(format.parse(filter.getFilterValue2()));

                } else if ( filter.getFilterCriteria() == FilterType.BETWEEN ) {

                    if ( !filter.getFilterValue2().toString().matches(FilterConstraints.DATE_TIME_REGEX )) {
                        filter.setFilterValue2(filter.getFilterValue2().trim() + " 23:59:59");
                    }
                    
                    filter.setFilterValue2( filter.getFilterValue2().replace("+", " ") );
                    filter.setFilterValue2Obj(format.parse( filter.getFilterValue2().toString() ));
                }
                
                } catch ( ParseException e ) {
                    throw new IllegalArgumentException("invalid.date");
                }
            
        }
    }, INT {

        public void parse(Filter filter) {
            INTEGER.parse(filter);
        }
    }, INTEGER {

        public void parse(Filter filter) {

            if (filter.getFilterCriteria() == FilterType.LIKE) {
                filter.setFilterCriteria(FilterType.EQUALS);
            }

            try {

                filter.setFilterValueObj(Integer.parseInt(filter.getFilterValue().toString()));

                if (filter.getFilterValue2() != null && !filter.getFilterValue2().equals("")) {
                    filter.setFilterValue2Obj(Integer.parseInt(filter.getFilterValue2().toString()));
                }

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid.integer");
            }

        }
    }, BOOLEAN {

        public void parse(Filter filter) {

            filter.setFilterCriteria(FilterType.EQUALS);
            filter.setFilterValueObj(filter.getFilterValue().equals("true"));
            filter.setFilterValue2(filter.getFilterValue());

        }
    }, LONG {

        public void parse(Filter filter) {

            if (filter.getFilterCriteria() == FilterType.LIKE) {
                filter.setFilterCriteria(FilterType.EQUALS);
            }

            try {

                filter.setFilterValueObj(Long.parseLong(filter.getFilterValue().toString()));

                if (filter.getFilterValue2() != null && !filter.getFilterValue2().equals("")) {
                    filter.setFilterValue2Obj(Long.parseLong(filter.getFilterValue2().toString()));
                }

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid.double");
            }

        }
    }, DOUBLE {

        public void parse(Filter filter) {

            if (filter.getFilterCriteria() == FilterType.LIKE) {
                filter.setFilterCriteria(FilterType.EQUALS);
            }

            try {

                filter.setFilterValueObj(Double.parseDouble(filter.getFilterValue().toString()));

                if (filter.getFilterValue2() != null && !filter.getFilterValue2().equals("")) {
                    filter.setFilterValue2Obj(Double.parseDouble(filter.getFilterValue2().toString()));
                }

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid.double");
            }

        }
    }, FLOAT {

        public void parse(Filter filter) {

            if (filter.getFilterCriteria() == FilterType.LIKE) {
                filter.setFilterCriteria(FilterType.EQUALS);
            }

            try {

                filter.setFilterValueObj(Float.parseFloat(filter.getFilterValue().toString()));

                if (filter.getFilterValue2() != null && !filter.getFilterValue2().equals("")) {
                    filter.setFilterValue2Obj(Float.parseFloat(filter.getFilterValue2().toString()));
                }

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid.float");
            }

        }
    }, STRING {

        public void parse(Filter filter) {
            filter.setFilterValueObj(filter.getFilterValue());
            filter.setFilterValue2Obj(filter.getFilterValue2());
        }

    };

    /**
     * Parse the value to type of field
     *
     * @param filter
     */
    public abstract void parse(Filter filter);
}
