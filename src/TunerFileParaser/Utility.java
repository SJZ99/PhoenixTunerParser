package TunerFileParaser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {
	/**
	 * Create jsonObject 
	 * @param path json file's path
	 * @return JSONObject
	 */
	public static JSONObject creatObject(String path) {
		StringBuilder str = new StringBuilder();
		Scanner input = null;
		try {
			input = new Scanner(new File(path));
			while(input.hasNext()) {
				str.append(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			input.close();
		}
		JSONObject obj = new JSONObject(str.toString());
		return obj;
	}
	/**
	 * Return a list that contain all Object in the array.
	 * @param array 
	 * @return a list of all object
	 */
	public static List<JSONObject> getAll(JSONArray array){
		List<JSONObject> list = new ArrayList<JSONObject>();
		int count = 0;
		while(true) {
			JSONObject t = null;
			try {
				t = array.getJSONObject(count);
			}catch(Exception e) {}
			if(t == null) {
				break;
			}
			list.add(t);
			count++;
		}
		return list;
	}
	/**
	 * Get all object from a Object.
	 * @param in
	 * @return 
	 */
	public static Map<String, String> getMap(JSONObject in){
		Map<String, String> map = new HashMap<String, String>();
		Set<String> key = in.keySet();
		for(String str : key) {
			map.put(str, in.get(str).toString());
		}
		return map;
	}
}
