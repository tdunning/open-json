/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This black box test was written without inspecting the non-free org.json sourcecode.
 */
public class JSONObjectTest {
    @Test
    public void testKeyset() throws Exception {
        JSONObject x = new JSONObject("{'a':1, 'b':2, 'c':3}");
        Set<String> k = new TreeSet<String>();
        for (String kx : Arrays.asList("a", "b", "c")) {
            k.add(kx);
        }
        assertEquals(x.keySet(), k);
        x = new JSONObject("{}");
        assertEquals(x.keySet().size(), 0);
    }

    @Test
    public void testEmptyObject() throws JSONException {
        JSONObject object = new JSONObject();
        assertEquals(0, object.length());

        // bogus (but documented) behaviour: returns null rather than the empty object!
        assertNull(object.names());

        // returns null rather than an empty array!
        assertNull(object.toJSONArray(new JSONArray()));
        assertEquals("{}", object.toString());
        assertEquals("{}", object.toString(5));
        try {
            object.get("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getBoolean("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getDouble("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getInt("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getJSONArray("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getJSONObject("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getLong("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getString("foo");
            fail();
        } catch (JSONException ignored) {
        }
        assertFalse(object.has("foo"));
        assertTrue(object.isNull("foo")); // isNull also means "is not present"
        assertNull(object.opt("foo"));
        assertEquals(false, object.optBoolean("foo"));
        assertEquals(true, object.optBoolean("foo", true));
        assertEquals(Double.NaN, object.optDouble("foo"), 0);
        assertEquals(5.0, object.optDouble("foo", 5.0), 0);
        assertEquals(0, object.optInt("foo"));
        assertEquals(5, object.optInt("foo", 5));
        assertEquals(null, object.optJSONArray("foo"));
        assertEquals(null, object.optJSONObject("foo"));
        assertEquals(0, object.optLong("foo"));
        assertEquals(Long.MAX_VALUE-1, object.optLong("foo", Long.MAX_VALUE-1));
        assertEquals("", object.optString("foo")); // empty string is default!
        assertEquals("bar", object.optString("foo", "bar"));
        assertNull(object.remove("foo"));
    }

    @Test
    public void testEqualsAndHashCode() throws JSONException {
        JSONObject a = new JSONObject();
        JSONObject b = new JSONObject();

        // JSON object doesn't override either equals or hashCode (!)
        assertFalse(a.equals(b));
        assertEquals(a.hashCode(), System.identityHashCode(a));
    }

    @Test
    public void testGet() throws JSONException {
        JSONObject object = new JSONObject();
        Object value = new Object();
        object.put("foo", value);
        object.put("bar", new Object());
        object.put("baz", new Object());
        assertSame(value, object.get("foo"));
        try {
            object.get("FOO");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put(null, value);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.get(null);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testPut() throws JSONException {
        JSONObject object = new JSONObject();
        assertSame(object, object.put("foo", true));
        object.put("foo", false);
        assertEquals(false, object.get("foo"));

        object.put("foo", 5.0d);
        assertEquals(5.0d, object.get("foo"));
        object.put("foo", 0);
        assertEquals(0, object.get("foo"));
        object.put("bar", Long.MAX_VALUE - 1);
        assertEquals(Long.MAX_VALUE - 1, object.get("bar"));
        object.put("baz", "x");
        assertEquals("x", object.get("baz"));
        object.put("bar", JSONObject.NULL);
        assertSame(JSONObject.NULL, object.get("bar"));
    }

    @Test
    public void testPutNullRemoves() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        object.put("foo", null);
        assertEquals(0, object.length());
        assertFalse(object.has("foo"));
        try {
            object.get("foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testPutOpt() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        object.putOpt("foo", null);
        assertEquals("bar", object.get("foo"));
        object.putOpt(null, null);
        assertEquals(1, object.length());
        object.putOpt(null, "bar");
        assertEquals(1, object.length());
    }

    @Test
    public void testPutOptUnsupportedNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.putOpt("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.putOpt("foo", Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.putOpt("foo", Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testRemove() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        assertEquals(null, object.remove(null));
        assertEquals(null, object.remove(""));
        assertEquals(null, object.remove("bar"));
        assertEquals("bar", object.remove("foo"));
        assertEquals(null, object.remove("foo"));
    }

    @Test
    public void testBooleans() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", true);
        object.put("bar", false);
        object.put("baz", "true");
        object.put("quux", "false");
        assertEquals(4, object.length());
        assertEquals(true, object.getBoolean("foo"));
        assertEquals(false, object.getBoolean("bar"));
        assertEquals(true, object.getBoolean("baz"));
        assertEquals(false, object.getBoolean("quux"));
        assertFalse(object.isNull("foo"));
        assertFalse(object.isNull("quux"));
        assertTrue(object.has("foo"));
        assertTrue(object.has("quux"));
        assertFalse(object.has("missing"));
        assertEquals(true, object.optBoolean("foo"));
        assertEquals(false, object.optBoolean("bar"));
        assertEquals(true, object.optBoolean("baz"));
        assertEquals(false, object.optBoolean("quux"));
        assertEquals(false, object.optBoolean("missing"));
        assertEquals(true, object.optBoolean("foo", true));
        assertEquals(false, object.optBoolean("bar", true));
        assertEquals(true, object.optBoolean("baz", true));
        assertEquals(false, object.optBoolean("quux", true));
        assertEquals(true, object.optBoolean("missing", true));

        object.put("foo", "truE");
        object.put("bar", "FALSE");
        assertEquals(true, object.getBoolean("foo"));
        assertEquals(false, object.getBoolean("bar"));
        assertEquals(true, object.optBoolean("foo"));
        assertEquals(false, object.optBoolean("bar"));
        assertEquals(true, object.optBoolean("foo", false));
        assertEquals(false, object.optBoolean("bar", false));
    }

    // http://code.google.com/p/android/issues/detail?id=16411
    @Test
    public void testCoerceStringToBoolean() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "maybe");
        try {
            object.getBoolean("foo");
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals(false, object.optBoolean("foo"));
        assertEquals(true, object.optBoolean("foo", true));
    }

    @Test
    public void testNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", Double.MIN_VALUE);
        object.put("bar", 9223372036854775806L);
        object.put("baz", Double.MAX_VALUE);
        object.put("quux", -0d);
        assertEquals(4, object.length());

        String toString = object.toString();
        assertTrue(toString, toString.contains("\"foo\":4.9E-324"));
        assertTrue(toString, toString.contains("\"bar\":9223372036854775806"));
        assertTrue(toString, toString.contains("\"baz\":1.7976931348623157E308"));

        // toString() and getString() return different values for -0d!
        assertTrue(toString, toString.contains("\"quux\":-0}") // no trailing decimal point
                || toString.contains("\"quux\":-0,"));

        assertEquals(Double.MIN_VALUE, object.get("foo"));
        assertEquals(9223372036854775806L, object.get("bar"));
        assertEquals(Double.MAX_VALUE, object.get("baz"));
        assertEquals(-0d, object.get("quux"));
        assertEquals(Double.MIN_VALUE, object.getDouble("foo"), 0);
        assertEquals(9.223372036854776E18, object.getDouble("bar"), 0);
        assertEquals(Double.MAX_VALUE, object.getDouble("baz"), 0);
        assertEquals(-0d, object.getDouble("quux"), 0);
        assertEquals(0, object.getLong("foo"));
        assertEquals(9223372036854775806L, object.getLong("bar"));
        assertEquals(Long.MAX_VALUE, object.getLong("baz"));
        assertEquals(0, object.getLong("quux"));
        assertEquals(0, object.getInt("foo"));
        assertEquals(-2, object.getInt("bar"));
        assertEquals(Integer.MAX_VALUE, object.getInt("baz"));
        assertEquals(0, object.getInt("quux"));
        assertEquals(Double.MIN_VALUE, object.opt("foo"));
        assertEquals(9223372036854775806L, object.optLong("bar"));
        assertEquals(Double.MAX_VALUE, object.optDouble("baz"), 0);
        assertEquals(0, object.optInt("quux"));
        assertEquals(Double.MIN_VALUE, object.opt("foo"));
        assertEquals(9223372036854775806L, object.optLong("bar"));
        assertEquals(Double.MAX_VALUE, object.optDouble("baz"), 0);
        assertEquals(0, object.optInt("quux"));
        assertEquals(Double.MIN_VALUE, object.optDouble("foo", 5.0d), 0);
        assertEquals(9223372036854775806L, object.optLong("bar", 1L));
        assertEquals(Long.MAX_VALUE, object.optLong("baz", 1L));
        assertEquals(0, object.optInt("quux", -1));
        assertEquals("4.9E-324", object.getString("foo"));
        assertEquals("9223372036854775806", object.getString("bar"));
        assertEquals("1.7976931348623157E308", object.getString("baz"));
        assertEquals("-0.0", object.getString("quux"));
    }

    @Test
    public void testFloats() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", (Float) Float.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Float) Float.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Float) Float.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testOtherNumbers() throws JSONException {
        Number nan = new Number() {
            public int intValue() {
                throw new UnsupportedOperationException();
            }
            public long longValue() {
                throw new UnsupportedOperationException();
            }
            public float floatValue() {
                throw new UnsupportedOperationException();
            }
            public double doubleValue() {
                return Double.NaN;
            }
            @Override public String toString() {
                return "x";
            }
        };

        JSONObject object = new JSONObject();
        try {
            object.put("foo", nan);
            fail("Object.put() accepted a NaN (via a custom Number class)");
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testForeignObjects() throws JSONException {
        Object foreign = new Object() {
            @Override public String toString() {
                return "x";
            }
        };

        // foreign object types are accepted and treated as Strings!
        JSONObject object = new JSONObject();
        object.put("foo", foreign);
        assertEquals("{\"foo\":\"x\"}", object.toString());
    }

    @Test
    public void testNullKeys() {
        try {
            new JSONObject().put(null, false);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, 0.0d);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, 5);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, 5L);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            new JSONObject().put(null, "foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testStrings() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "true");
        object.put("bar", "5.5");
        object.put("baz", "9223372036854775806");
        object.put("quux", "null");
        object.put("height", "5\"8' tall");

        assertTrue(object.toString().contains("\"foo\":\"true\""));
        assertTrue(object.toString().contains("\"bar\":\"5.5\""));
        assertTrue(object.toString().contains("\"baz\":\"9223372036854775806\""));
        assertTrue(object.toString().contains("\"quux\":\"null\""));
        assertTrue(object.toString().contains("\"height\":\"5\\\"8' tall\""));

        assertEquals("true", object.get("foo"));
        assertEquals("null", object.getString("quux"));
        assertEquals("5\"8' tall", object.getString("height"));
        assertEquals("true", object.opt("foo"));
        assertEquals("5.5", object.optString("bar"));
        assertEquals("true", object.optString("foo", "x"));
        assertFalse(object.isNull("foo"));

        assertEquals(true, object.getBoolean("foo"));
        assertEquals(true, object.optBoolean("foo"));
        assertEquals(true, object.optBoolean("foo", false));
        assertEquals(0, object.optInt("foo"));
        assertEquals(-2, object.optInt("foo", -2));

        assertEquals(5.5d, object.getDouble("bar"), 0);
        assertEquals(5L, object.getLong("bar"));
        assertEquals(5, object.getInt("bar"));
        assertEquals(5, object.optInt("bar", 3));

        // The last digit of the string is a 6 but getLong returns a 7. It's probably parsing as a
        // double and then converting that to a long. This is consistent with JavaScript.
        assertEquals(9223372036854775807L, object.getLong("baz"));
        assertEquals(9.223372036854776E18, object.getDouble("baz"), 0);
        assertEquals(Integer.MAX_VALUE, object.getInt("baz"));

        assertFalse(object.isNull("quux"));
        try {
            object.getDouble("quux");
            fail();
        } catch (JSONException e) {
            // expected
        }
        assertEquals(Double.NaN, object.optDouble("quux"), 0);
        assertEquals(-1.0d, object.optDouble("quux", -1.0d), 0);

        object.put("foo", "TRUE");
        assertEquals(true, object.getBoolean("foo"));
    }

    @Test
    public void testJSONObjects() throws JSONException {
        JSONObject object = new JSONObject();

        JSONArray a = new JSONArray();
        JSONObject b = new JSONObject();
        object.put("foo", a);
        object.put("bar", b);

        assertSame(a, object.getJSONArray("foo"));
        assertSame(b, object.getJSONObject("bar"));
        try {
            object.getJSONObject("foo");
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.getJSONArray("bar");
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals(a, object.optJSONArray("foo"));
        assertEquals(b, object.optJSONObject("bar"));
        assertEquals(null, object.optJSONArray("bar"));
        assertEquals(null, object.optJSONObject("foo"));
    }

    @Test
    public void testNullCoercionToString() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", JSONObject.NULL);
        assertEquals("null", object.getString("foo"));
    }

    @Test
    public void testArrayCoercion() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "[true]");
        try {
            object.getJSONArray("foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testObjectCoercion() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "{}");
        try {
            object.getJSONObject("foo");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testAccumulateValueChecking() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.accumulate("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        object.accumulate("foo", 1);
        try {
            object.accumulate("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        object.accumulate("foo", 2);
        try {
            object.accumulate("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testToJSONArray() throws JSONException {
        JSONObject object = new JSONObject();
        Object value = new Object();
        object.put("foo", true);
        object.put("bar", 5.0d);
        object.put("baz", -0.0d);
        object.put("quux", value);

        JSONArray names = new JSONArray();
        names.put("baz");
        names.put("quux");
        names.put("foo");

        JSONArray array = object.toJSONArray(names);
        assertEquals(-0.0d, array.get(0));
        assertEquals(value, array.get(1));
        assertEquals(true, array.get(2));

        object.put("foo", false);
        assertEquals(true, array.get(2));
    }

    @Test
    public void testToJSONArrayMissingNames() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", true);
        object.put("bar", 5.0d);
        object.put("baz", JSONObject.NULL);

        JSONArray names = new JSONArray();
        names.put("bar");
        names.put("foo");
        names.put("quux");
        names.put("baz");

        JSONArray array = object.toJSONArray(names);
        assertEquals(4, array.length());

        assertEquals(5.0d, array.get(0));
        assertEquals(true, array.get(1));
        try {
            array.get(2);
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals(JSONObject.NULL, array.get(3));
    }

    @Test
    public void testToJSONArrayNull() throws JSONException {
        JSONObject object = new JSONObject();
        assertEquals(null, object.toJSONArray(null));
        object.put("foo", 5);
        try {
            object.toJSONArray(null);
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testToJSONArrayEndsUpEmpty() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        JSONArray array = new JSONArray();
        array.put("bar");
        assertEquals(1, object.toJSONArray(array).length());
    }

    @Test
    public void testToJSONArrayNonString() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("null", 10);
        object.put("false", 15);

        JSONArray names = new JSONArray();
        names.put(JSONObject.NULL);
        names.put(false);
        names.put("foo");

        // array elements are converted to strings to do name lookups on the map!
        JSONArray array = object.toJSONArray(names);
        assertEquals(3, array.length());
        assertEquals(10, array.get(0));
        assertEquals(15, array.get(1));
        assertEquals(5, array.get(2));
    }

    @Test
    public void testPutUnsupportedNumbers() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testPutUnsupportedNumbersAsObjects() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("foo", (Double) Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Double) Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            object.put("foo", (Double) Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
    }

    /**
     * Although JSONObject is usually defensive about which numbers it accepts,
     * it doesn't check inputs in its constructor.
     */
    @Test
    public void testCreateWithUnsupportedNumbers() throws JSONException {
        Map<String, Object> contents = new HashMap<String, Object>();
        contents.put("foo", Double.NaN);
        contents.put("bar", Double.NEGATIVE_INFINITY);
        contents.put("baz", Double.POSITIVE_INFINITY);

        JSONObject object = new JSONObject(contents);
        assertEquals(Double.NaN, object.get("foo"));
        assertEquals(Double.NEGATIVE_INFINITY, object.get("bar"));
        assertEquals(Double.POSITIVE_INFINITY, object.get("baz"));
    }

    @Test
    public void testToStringWithUnsupportedNumbers() {
        // when the object contains an unsupported number, toString returns null!
        JSONObject object = new JSONObject(Collections.singletonMap("foo", Double.NaN));
        assertEquals(null, object.toString());
    }

    @Test
    public void testMapConstructorCopiesContents() throws JSONException {
        Map<String, Object> contents = new HashMap<String, Object>();
        contents.put("foo", 5);
        JSONObject object = new JSONObject(contents);
        contents.put("foo", 10);
        assertEquals(5, object.get("foo"));
    }

    @Test
    public void testMapConstructorWithBogusEntries() {
        Map<Object, Object> contents = new HashMap<Object, Object>();
        contents.put(5, 5);

        try {
            new JSONObject(contents);
            fail("JSONObject constructor doesn't validate its input!");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testTokenerConstructor() throws JSONException {
        JSONObject object = new JSONObject(new JSONTokener("{\"foo\": false}"));
        assertEquals(1, object.length());
        assertEquals(false, object.get("foo"));
    }

    @Test
    public void testTokenerConstructorWrongType() throws JSONException {
        try {
            new JSONObject(new JSONTokener("[\"foo\", false]"));
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testTokenerConstructorNull() throws JSONException {
        try {
            new JSONObject((JSONTokener) null);
            fail();
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void testTokenerConstructorParseFail() {
        try {
            new JSONObject(new JSONTokener("{"));
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testStringConstructor() throws JSONException {
        JSONObject object = new JSONObject("{\"foo\": false}");
        assertEquals(1, object.length());
        assertEquals(false, object.get("foo"));
    }

    @Test
    public void testStringConstructorWrongType() throws JSONException {
        try {
            new JSONObject("[\"foo\", false]");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testStringConstructorNull() throws JSONException {
        try {
            new JSONObject((String) null);
            fail();
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void testStringConstructorParseFail() {
        try {
            new JSONObject("{");
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testCopyConstructor() throws JSONException {
        JSONObject source = new JSONObject();
        source.put("a", JSONObject.NULL);
        source.put("b", false);
        source.put("c", 5);

        JSONObject copy = new JSONObject(source, new String[] { "a", "c" });
        assertEquals(2, copy.length());
        assertEquals(JSONObject.NULL, copy.get("a"));
        assertEquals(5, copy.get("c"));
        assertEquals(null, copy.opt("b"));
    }

    @Test
    public void testCopyConstructorMissingName() throws JSONException {
        JSONObject source = new JSONObject();
        source.put("a", JSONObject.NULL);
        source.put("b", false);
        source.put("c", 5);

        JSONObject copy = new JSONObject(source, new String[]{ "a", "c", "d" });
        assertEquals(2, copy.length());
        assertEquals(JSONObject.NULL, copy.get("a"));
        assertEquals(5, copy.get("c"));
        assertEquals(0, copy.optInt("b"));
    }

    @Test
    public void testAccumulateMutatesInPlace() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.accumulate("foo", 6);
        JSONArray array = object.getJSONArray("foo");
        assertEquals("[5,6]", array.toString());
        object.accumulate("foo", 7);
        assertEquals("[5,6,7]", array.toString());
    }

    @Test
    public void testAccumulateExistingArray() throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("foo", array);
        object.accumulate("foo", 5);
        assertEquals("[5]", array.toString());
    }

    @Test
    public void testAccumulatePutArray() throws JSONException {
        JSONObject object = new JSONObject();
        object.accumulate("foo", 5);
        assertEquals("{\"foo\":5}", object.toString());
        object.accumulate("foo", new JSONArray());
        assertEquals("{\"foo\":[5,[]]}", object.toString());
    }

    @Test
    public void testAccumulateNull() {
        JSONObject object = new JSONObject();
        try {
            object.accumulate(null, 5);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testEmptyStringKey() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("", 5);
        assertEquals(5, object.get(""));
        assertEquals("{\"\":5}", object.toString());
    }

    @Test
    public void testNullValue() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", JSONObject.NULL);
        object.put("bar", null);

        // there are two ways to represent null; each behaves differently!
        assertTrue(object.has("foo"));
        assertFalse(object.has("bar"));
        assertTrue(object.isNull("foo"));
        assertTrue(object.isNull("bar"));
    }

    @Test
    public void testNullValue_equalsAndHashCode() {
        //noinspection ObjectEqualsNull
        assertTrue(JSONObject.NULL.equals(null)); // guaranteed by javadoc
        // not guaranteed by javadoc, but seems like a good idea
        assertEquals(Objects.hashCode(null), JSONObject.NULL.hashCode());
    }

    @Test
    public void testHas() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        assertTrue(object.has("foo"));
        assertFalse(object.has("bar"));
        assertFalse(object.has(null));
    }

    @Test
    public void testOptNull() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", "bar");
        assertEquals(null, object.opt(null));
        assertEquals(false, object.optBoolean(null));
        assertEquals(Double.NaN, object.optDouble(null), 0);
        assertEquals(0, object.optInt(null));
        assertEquals(0L, object.optLong(null));
        assertEquals(null, object.optJSONArray(null));
        assertEquals(null, object.optJSONObject(null));
        assertEquals("", object.optString(null));
        assertEquals(true, object.optBoolean(null, true));
        assertEquals(0.0d, object.optDouble(null, 0.0d), 0);
        assertEquals(1, object.optInt(null, 1));
        assertEquals(1L, object.optLong(null, 1L));
        assertEquals("baz", object.optString(null, "baz"));
    }

    @Test
    public void testToStringWithIndentFactor() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", new JSONArray(Arrays.asList(5, 6)));
        object.put("bar", new JSONObject());
        String foobar = "{\n" +
                "     \"foo\": [\n" +
                "          5,\n" +
                "          6\n" +
                "     ],\n" +
                "     \"bar\": {}\n" +
                "}";
        String barfoo = "{\n" +
                "     \"bar\": {},\n" +
                "     \"foo\": [\n" +
                "          5,\n" +
                "          6\n" +
                "     ]\n" +
                "}";
        String string = object.toString(5);
        assertTrue(string, foobar.equals(string) || barfoo.equals(string));
    }

    @Test
    public void testNames() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("bar", 6);
        object.put("baz", 7);
        JSONArray array = object.names();
        assertTrue(array.toString().contains("foo"));
        assertTrue(array.toString().contains("bar"));
        assertTrue(array.toString().contains("baz"));
    }

    @Test
    public void testKeysEmptyObject() {
        JSONObject object = new JSONObject();
        assertFalse(object.keys().hasNext());
        try {
            object.keys().next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    @Test
    public void testKeys() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        object.put("bar", 6);
        object.put("foo", 7);

        @SuppressWarnings("unchecked")
        Iterator<String> keys = object.keys();
        Set<String> result = new HashSet<String>();
        assertTrue(keys.hasNext());
        result.add(keys.next());
        assertTrue(keys.hasNext());
        result.add(keys.next());
        assertFalse(keys.hasNext());
        assertEquals(new HashSet<String>(Arrays.asList("foo", "bar")), result);

        try {
            keys.next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    @Test
    public void testMutatingKeysMutatesObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        Iterator keys = object.keys();
        keys.next();
        keys.remove();
        assertEquals(0, object.length());
    }

    @Test
    public void testQuote() {
        // covered by JSONStringerTest.testEscaping
    }

    @Test
    public void testQuoteNull() throws JSONException {
        assertEquals("\"\"", JSONObject.quote(null));
    }

    @Test
    public void testNumberToString() throws JSONException {
        assertEquals("5", JSONObject.numberToString(5));
        assertEquals("-0", JSONObject.numberToString(-0.0d));
        assertEquals("9223372036854775806", JSONObject.numberToString(9223372036854775806L));
        assertEquals("4.9E-324", JSONObject.numberToString(Double.MIN_VALUE));
        assertEquals("1.7976931348623157E308", JSONObject.numberToString(Double.MAX_VALUE));
        try {
            JSONObject.numberToString(Double.NaN);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            JSONObject.numberToString(Double.NEGATIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        try {
            JSONObject.numberToString(Double.POSITIVE_INFINITY);
            fail();
        } catch (JSONException ignored) {
        }
        assertEquals("0.001", JSONObject.numberToString(new BigDecimal("0.001")));
        assertEquals("9223372036854775806",
                JSONObject.numberToString(new BigInteger("9223372036854775806")));
        try {
            JSONObject.numberToString(null);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void test_wrap() throws Exception {
        assertEquals(JSONObject.NULL, JSONObject.wrap(null));

        JSONArray a = new JSONArray();
        assertEquals(a, JSONObject.wrap(a));

        JSONObject o = new JSONObject();
        assertEquals(o, JSONObject.wrap(o));

        assertEquals(JSONObject.NULL, JSONObject.wrap(JSONObject.NULL));

        assertTrue(JSONObject.wrap(new byte[0]) instanceof JSONArray);
        assertTrue(JSONObject.wrap(new ArrayList<String>()) instanceof JSONArray);
        assertTrue(JSONObject.wrap(new HashMap<String, String>()) instanceof JSONObject);
        assertTrue(JSONObject.wrap(0.0) instanceof Double);
        assertTrue(JSONObject.wrap("hello") instanceof String);
    }

    // https://code.google.com/p/android/issues/detail?id=55114
    @Test
    public void test_toString_listAsMapValue() throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add("a");
        list.add(new ArrayList<String>());
        Map<String, Object> map = new TreeMap<String, Object>();
        map.put("x", "l");
        map.put("y", list);
        assertEquals("{\"x\":\"l\",\"y\":[\"a\",[]]}", new JSONObject(map).toString());
    }

    @Test
    public void testAppendExistingInvalidKey() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("foo", 5);
        try {
            object.append("foo", 6);
            fail();
        } catch (JSONException ignored) {
        }
    }

    @Test
    public void testAppendExistingArray() throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("foo", array);
        object.append("foo", 5);
        assertEquals("[5]", array.toString());
    }

    @Test
    public void testAppendPutArray() throws JSONException {
        JSONObject object = new JSONObject();
        object.append("foo", 5);
        assertEquals("{\"foo\":[5]}", object.toString());
        object.append("foo", new JSONArray());
        assertEquals("{\"foo\":[5,[]]}", object.toString());
    }

    @Test
    public void testAppendNull() {
        JSONObject object = new JSONObject();
        try {
            object.append(null, 5);
            fail();
        } catch (JSONException ignored) {
        }
    }

    // https://code.google.com/p/android/issues/detail?id=103641
    @Test
    public void testInvalidUnicodeEscape() {
        try {
            new JSONObject("{\"q\":\"\\u\", \"r\":[]}");
            fail();
        } catch (JSONException ignored) {
        }
    }
}
