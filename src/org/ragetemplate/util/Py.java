package org.ragetemplate.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;

/**
 * Python-like utils for Java
 *
 * @author jensck
 *
 */
public class Py {
    /**
     * Returns true *only* if *every* object in the varargs array is truthy
     *
     * Call truthy() on each object in the varargs - this is called all() in
     * Python, so that's what I'm calling it here.
     *
     * @param objects
     * @return
     */
    public static boolean all(Object... objects) {
        if (!truthy(objects)) {
            // is our varargs list null or empty?
            return false;
        }

        for (Object obj : objects) {
            if (!truthy(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if *any* object in the varargs array is truthy
     *
     * Calls truthy() on each object in the varargs - this is called any() in
     * Python, so that's what I'm calling it here.
     *
     * @param objects
     * @return
     */
    public static boolean any(Object... objects) {
        if (!truthy(objects)) {
            // is our varargs list null or empty?
            return false;
        }

        for (Object obj : objects) {
            if (truthy(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simple function to create inline Collections without all the extra noise
     *
     * The main utility here is for creating inline 'foreach' loops, like to
     * check if an arbitrary number of vars are null, e.g.:
     *
     * for (Object x : Utils.list(foo, bar, baz, quux) { if (x == null) throw
     * new NullPointerException(); }
     *
     * @param objects
     *            arbitrary number of objects.
     *
     * @return a List from the objects param
     */
    public static List<Object> list(Object... objects) {
        return new ArrayList<Object>(Arrays.asList(objects));
    }

    /**
     * Test the "truthiness" of an object, ala Python - see http://goo.gl/JebVU
     *
     * The following will return false: - null - Any empty collection - a string
     * consisting of "" - a Number (like Integer and Long) with a value of 0
     *
     * ANYTHING else will return true.
     *
     * @param obj
     * @return is obj "truthy"
     */
    @SuppressWarnings("rawtypes")
    public static boolean truthy(Object obj) {
        if (obj == null) {
            return false;

        } else if (obj instanceof Collection) {
            return (!((Collection) obj).isEmpty());

        } else if (obj instanceof Object[]) {
            if (((Object[]) obj).length < 1) {
                return false;
            }

        } else if (obj instanceof Number) {
            if (((Number) obj).longValue() == 0) {
                return false;
            }
        } else if (obj instanceof String) {
            if (((String) obj).equals("")) {
                return false;
            }
        } else if (obj instanceof JSONArray) {
            return (((JSONArray) obj).length() > 0);
        } else if (obj instanceof Boolean) {
            return (((Boolean) obj).booleanValue());
        }

        return true;
    }

    public static <T> List<T> typedList(T... objects) {
        return new ArrayList<T>(Arrays.asList(objects));
    }

}
