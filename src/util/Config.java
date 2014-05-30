package util;

import java.util.HashMap;

/**
 * 配置类
 * 
 */
public class Config {

	private Config() {
	}

	private static HashMap<String, ?> configData;

	/**
	 * Flash安全沙箱返回时的XML代码
	 */
	public static String policyXML = "<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>\0";

	/**
	 * 初始化配置
	 */
	public static void init(HashMap<String, String> xmlMap) {
		// Config.serverDir = serverDir;
		// String xmlFile = Config.serverDir + "/config.xml";
		// configData = XMLManger.readXml(xmlFile);
		configData = xmlMap;
	}

	/**
	 * 获取配置
	 * 
	 * @param key
	 * @return Integer
	 */
	public static int getInt(String key) {
		return Integer.parseInt(configData.get(key).toString());
	}

	/**
	 * 获取配置
	 * 
	 * @param key
	 * @return String
	 */
	public static String getString(String key) {
		return configData.get(key).toString();
	}

	/**
	 * 获取配置
	 * 
	 * @param key
	 * @return Float
	 */
	public static Float getFloat(String key) {
		return Float.parseFloat(configData.get(key).toString());
	}

	/**
	 * 获取配置
	 * 
	 * @param key
	 * @return Double
	 */
	public static Double getDouble(String key) {
		return Double.parseDouble(configData.get(key).toString());
	}

	/**
	 * 获取配置
	 * 
	 * @param key
	 * @return Boolean
	 */
	public static Boolean getBoolean(String key) {
		Object o = configData.get(key);
		if (o == null) {
			return false;
		}
		return Boolean.parseBoolean(configData.get(key).toString());
	}
}
