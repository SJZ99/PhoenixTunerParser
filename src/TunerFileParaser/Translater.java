package TunerFileParaser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.json.JSONArray;
import org.json.JSONObject;

public class Translater {
	private static File file = null;
	private static String motorName = "";
	/**
	 * Input by file chooser and JOptionPane. 
	 */
	public static void input() {
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				if(arg0.getPath().endsWith(".json")) {
					return true;
				}
				return false;
			}
			@Override
			public String getDescription() {
				return null;
			}
		});
		chooser.showOpenDialog(null);
		file = chooser.getSelectedFile();
		if(file == null) {
			System.out.println("File couldn't be null");
			System.exit(1);
		}
		Object obj = JOptionPane.showInputDialog("Motor Name");
		if(obj == null) {
			System.out.println("Name couldn't be null");
			System.exit(1);
		}
		motorName = obj.toString();
	}
	public static void main(String[] args) {
		input();
		Productor.setName(motorName);
		JSONObject obj = Utility.creatObject(file.getAbsolutePath());
		JSONArray array = obj.getJSONArray("Configs");
		List<JSONObject> list = Utility.getAll(array); //contain name...
		for(JSONObject o : list) {
			Map<String, String> map = Utility.getMap(o.getJSONObject("Values"));
			Set<String> key = map.keySet();
			for(String k : key) {
				String s = Productor.productProgram(o.getString("Name"), k, map.get(k));
				if(!s.isEmpty()) {
					System.out.println(s);
				}
			}
		}
		Productor.limitSource();
	}
}
