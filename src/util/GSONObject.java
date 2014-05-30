package util;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public final class GSONObject extends HashMap<String, Object> {

	private static final long serialVersionUID = 4838514457948241231L;

	private static Gson gson = null;

	/**
	 * 初始化gson
	 */
	private static void init() {
		if (gson != null) {
			return;
		}
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	public GSONObject() {
		super();
	}

	/**
	 * json解码
	 * 
	 * @param json
	 * @return
	 * @throws JsonSyntaxException
	 */
	public GSONObject(String json) throws JsonSyntaxException {
		init();
		HashMap<String, Object> jsonMap = gson.fromJson(json,
				new TypeToken<HashMap<String, Object>>() {
				}.getType());

		if (jsonMap != null) {
			this.putAll(jsonMap);
		}
	}

	/**
	 * json解码
	 * 
	 * @param json
	 * @return
	 */
	public static GSONObject decode(String json) {
		return new GSONObject(json);
	}

	/**
	 * json转任意类型
	 * 
	 * @param json
	 * @param typeOfT
	 *            类型
	 * @return T
	 * @throws JsonSyntaxException
	 */
	public static <T> T decode(String json, Type typeOfT)
			throws JsonSyntaxException {
		init();
		return gson.fromJson(json, typeOfT);
	}

	/**
	 * 将{a=b,c=2}数据转为map
	 * 
	 * @author Huang
	 * @date 2013-6-19 上午10:44:08
	 * @return GSONObject
	 */
	public static GSONObject decodeMap(String jsonm) {

		return null;
	}

	/**
	 * json编码
	 * 
	 * @param src
	 * @return String
	 */
	public static String encode(Object src) {
		init();
		return gson.toJson(src);
	}
	public static String encode(Object src , Type typeOfSrc) {
		init();
		return gson.toJson(src, typeOfSrc);
	}

	/**
	 * put
	 * 
	 * @param key
	 * @param value
	 */
	public GSONObject put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	/**
	 * getObject
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject(String key) {
		return super.get(key);
	}

	/**
	 * getString
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		if (this.containsKey(key)) {
			return getObject(key).toString();
		}
		return "";
	}

	/**
	 * getInt
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return (int) getDouble(key);
	}

	/**
	 * getInt
	 * 
	 * @param key
	 * @return
	 */
	public short getShort(String key) {
		return (short) getDouble(key);
	}

	/**
	 * getLong
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		return (long) getDouble(key);
	}

	/**
	 * getDouble
	 * 
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		String v = getString(key);
		if (v.length() == 0 || v.isEmpty()) {
			return 0;
		}
		try {
			return Double.parseDouble(v);
		} catch (Exception e) {
			return 0;
			// throw new JSONException(key + "is not a number.");
		}

	}

	/**
	 * getFloat
	 * 
	 * @param key
	 * @return
	 */
	public float getFloat(String key) {
		return (float) getDouble(key);
	}

	/**
	 * getBoolean
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		String v = getString(key);
		if (v.isEmpty()) {
			return false;
		}
		return Boolean.valueOf(v);
	}

	/**
	 * getChar
	 * 
	 * @author King
	 * @date 2013-4-9 下午4:59:13
	 * @return char
	 */
	public char getChar(String key) {
		return getString(key).charAt(0);
	}

	/**
	 * getJSON
	 * 
	 * @param key
	 * @return JSON
	 */
	public GSONObject getJSON(String key) {
		String v = getString(key);
		if (v.isEmpty()) {
			new GSONObject("");
		}
		return new GSONObject(v);
	}

	/**
	 * toString
	 */
	public String toString() {
		return encode(this);
	}
}
