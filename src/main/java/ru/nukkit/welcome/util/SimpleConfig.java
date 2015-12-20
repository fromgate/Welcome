package ru.nukkit.welcome.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleConfig {
	
	
	private Map<String,Object> cfg;
	
	public SimpleConfig(){
		cfg = new HashMap<String,Object>();
	}
	
	public void set(String key, Object value){
		if (key==null||key.isEmpty()) return;
		cfg.put(key, value);
		if (value == null)  cfg.remove(key);
	}
	
	public Set<String> allKeys(){
		return cfg.keySet();
	}
	
	public Object get (String key, Object defaultValue){
		return cfg.containsKey(key) ? cfg.get(key) : defaultValue;  
	}
	
	public Object get (String key){
		return cfg.containsKey(key) ? cfg.get(key) : null; 
	}

	public void load (File file) throws IOException {
		Yaml yaml = new Yaml();
        InputStream in = new FileInputStream(file);
		cfg = yaml.loadAs(in,HashMap.class);
		in.close();
	}
	
	public void save (File file) throws IOException{
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
		FileWriter writer = new FileWriter (file);
		yaml.dump(cfg, writer);
		writer.close();
	}

}
