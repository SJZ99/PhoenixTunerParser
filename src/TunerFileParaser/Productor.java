package TunerFileParaser;

public class Productor {
	private static String motorName = null;
	private static String limitSourceValueF = "";
	private static String limitSourceValueR = "";
	
	/**
	 * Resolve the limitSwitch's configuration.
	 */
	public static void limitSource() {
		if(motorName != null) {
			System.out.println(motorName + ".configForwardLimitSwitchSource(" + limitSourceValueF + ");");
			System.out.println(motorName + ".configReverseLimitSwitchSource(" + limitSourceValueR + ");");
		}else {
			System.out.println("configForwardLimitSwitchSource(" + limitSourceValueF + ");");
			System.out.println("configReverseLimitSwitchSource(" + limitSourceValueR + ");");
		}
	}
	/**
	 * Set the motor name to product code.
	 * @param name
	 */
	public static void setName(String name) {
		motorName = name;
	}
	/**
	 * Main method to product code, use the phoenix Tuner json
	 * @param name       Name of that object, such as "Motor Output", "Voltage Comp"
	 * @param configName Configuration name, such as "Neutral Deadband", "Neutral Mode"
	 * @param value      Value of the configuration, such as 0.0400782, 0
	 * @return           This configuration's code.
	 */
	public static String productProgram(String name, String configName, String value) {
		boolean isTest = false;

		String[] strarr = check(name, configName, value);
		configName = strarr[1];
		value = strarr[2];

		StringBuilder str = new StringBuilder();
		if(configName.isEmpty()) {
			return "";
		}
		if(!isTest) {
			if(motorName != null) {
				str.append(motorName);
				str.append(".");
			}
		}
		str.append(selectStart((configName)));
		str.append(configName);
		str.append("(");
		str.append(value);
		str.append(");");
		return str.toString();
	}
	/**
	 * Select start of that code, config or set.
	 * @param configName
	 * @return
	 */
	public static String selectStart(String configName) {
		if(configName.equals("NeutralMode")) {
			return "set";
		}else {
			return "config";
		}
	}
	/**
	 * Clear all space of the String.
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		StringBuilder s = new StringBuilder();
		String[] arr = str.split(" ");
		for(String st : arr) {
			s.append(st.trim());
		}
		return s.toString();
	}
	/**
	 * Move a specific word to first or last
	 * @param str       Whole word
	 * @param target    Specific word
	 * @param isFront   true, if Specific word should be put on first.
	 * @return
	 */
	public static String moveToLast(String str, String target, boolean isFront) {
		StringBuilder builder = new StringBuilder();
		if(str.contains(target)) {
			String[] arr = str.split(target);
			if(isFront) {
				builder.append(target);
			}
			for(String s : arr) {
				if((s.trim()).contains(target)) {
					continue;
				}
				builder.append(s.trim());
			}
			if(!isFront) {
				builder.append(target);
			}
		}else {
			builder.append(str);
		}
		return builder.toString();
	}
	/**
	 * Make "Loop"'s "L" to "l"
	 * @param str
	 * @return
	 */
	public static String loopToLowerCase(String str) {
		if(str.contains("Loop")) {
			str = str.replace("L", "l");
		}
		return str;
	}
	/**
	 * Translate config file's Neutral Mode to code.
	 * @param mode
	 * @return
	 */
	public static String neutralMode(String mode) {
		switch (mode) {
		case "0" :
			return "NeutralMode.Coast";
		case "1" :
			return "NeutralMode.Brake";
		default :
			return "NeutralMode.EEPROMSetting";
		}
	}
	/**
	 * Translate config file's LimitSwitchType to code
	 * @param mode
	 * @return
	 */
	public static String limitSwitchType(String mode) {
		switch (mode) {
		case "0" :
			return "LimitSwitchSource.FeedbackConnector";
		case "1" :
			return "LimitSwitchSource.RemoteTalonSRX";
		case "2" :
			return "LimitSwitchSource.RemoteCANifier";
		default :
			return "LimitSwitchSource.Deactivated";
		}
	}
	/**
	 * Make value to Integer.
	 * Some method request Integer to be parameter
	 * @param value
	 * @return
	 */
	public static String toInteger(String value) {
		return value.split("\\.")[0];
	}
	/**
	 * Select Name and give appropriately solve
	 * @param name
	 * @param configName
	 * @param value
	 * @return
	 */
	public static String[] check(String name, String configName, String value) {
		String arr[] = new String[3];
		arr[0] = name;
		arr[1] = configName;
		arr[2] = value;
		switch (name) {
		case "Motor Output" :
			arr[1] = moveToLast(arr[1], "Forward", false);
			arr[1] = moveToLast(arr[1], "Reverse", false);
			arr[1] = loopToLowerCase(arr[1]);
			arr[1] = trim(arr[1]);
			if(arr[1].equals("NeutralMode")) {
				arr[2] = neutralMode(arr[2]);
			}
			break;
		case "Voltage Comp" :
			arr[1] = "VoltageCompSaturation";
			break;
		case "Hardware Limit Switches" : 
			if(arr[1].contains("Forward")) {
				if(arr[1].contains("Source")) {
					limitSourceValueF = limitSwitchType(arr[2]) + limitSourceValueF;
					arr[1] = "";
					arr[2] = "";
				}else if(arr[1].contains("ID")) {
					//Haven't been support
					//equal "" will make this configuration didn't work
					arr[1] = "";
					arr[2] = "";
				}else {
					limitSourceValueF = limitSourceValueF + (arr[2].equals("0") ? ", LimitSwitchNormal.NormallyOpen" : ", LimitSwitchNormal.NormallyClosed");
					arr[1] = "";
					arr[2] = "";
				}
			}else if(arr[1].contains("Reverse")) {
				if(arr[1].contains("Source")) {
					limitSourceValueR = limitSwitchType(arr[2]) + limitSourceValueR;
					arr[1] = "";
					arr[2] = "";
				}else if(arr[1].contains("ID")) {
					//Haven't been support
					arr[1] = "";
					arr[2] = "";
				}else {
					limitSourceValueR = limitSourceValueR + (arr[2].equals("0.0") ? ", LimitSwitchNormal.NormallyClosed" : ", LimitSwitchNormal.NormallyOpen");
					arr[1] = "";
					arr[2] = "";
				}
			}
			break;
		case "Software Limit Switches" :
			arr[1] = moveToLast(arr[1], "Forward", true);
			arr[1] = moveToLast(arr[1], "Reverse", true);
			arr[1] = arr[1].replaceAll("Value", "Threshold");
			arr[1] = trim(arr[1]);
			arr[2] = toInteger(arr[2]);
			break;
		case "Motion Magic" :
			if(arr[1].equals("Curve Strength")) {
				arr[1] = "MotionS" + arr[1];
			}else {
				arr[1] = "Motion" + arr[1];
			}
			arr[1] = trim(arr[1]);
			arr[2] = toInteger(arr[2]);
			break;
		case "Motion Profile" :
			//Haven't been support
			arr[1] = "";
			arr[2] = "";
			break;
		case "Closed Loop" :
			if(arr[1].contains("Ramp")) {
				arr[1] = "ClosedloopRamp";
			}else if(arr[1].contains("Primary Feedback")){
				arr[1] = "SelectedFeedbackSensor";
				switch (arr[2]) {
				case "0" : 
					arr[2] =  "FeedbackDevice.QuadEncoder";
					break;
	            case "1" : 
	            	arr[2] =  "TalonFXFeedbackDevice.IntegratedSensor";
	            	break;
	            case "2" : 
	            	arr[2] =  "FeedbackDevice.Analog";
	            	break;
	            case "4" : 
	            	arr[2] =  "FeedbackDevice.Tachometer";
	            	break;
	            case "8" : 
	            	arr[2] =  "FeedbackDevice.PulseWidthEncodedPosition";
	            	break;
	            case "9" : 
	            	arr[2] =  "FeedbackDevice.SensorSum";
	            	break;
	            case "10": 
	            	arr[2] =  "FeedbackDevice.SensorDifference";
	            	break;
	            case "11": 
	            	arr[2] =  "FeedbackDevice.RemoteSensor0";
	            	break;
				case "12": 
					arr[2] =  "FeedbackDevice.RemoteSensor1";
					break;
				case "14": 
					arr[2] =  "FeedbackDevice.None";
					break;
	            case "15": 
	            	arr[2] =  "FeedbackDevice.SoftwareEmulatedSensor";
	            	break;
	            default:   
	            	arr[2] =  "InvalidValue";
					System.out.println("Haven't been support");
					break;
				}
			}else {
				arr[1] = "";
				arr[2] = "";
			}
			
			break;
		case "Slot 0" : 
		case "Slot 1" :
		case "Slot 2" :
		case "Slot 3" : 
			arr[2] = slot_value(arr[0], arr[1], arr[2]);
			arr[1] = slot_config(arr[1]);
			break;
		case "Remote Sensor" : 
			//Haven't been support
			arr[1] = "";
			arr[2] = "";
			break;
		case "Home Sensor" : 
			switch (arr[1]) {
			case "Clear Pos On Forward Limit" :
				arr[1] = "ClearPositionOnLimitF";
				break;
			case "Clear Pos On Quad Index" :
				arr[1] = "ClearPositionOnQuadIdx";
				break;
			case "Clear Pos On Reverse Limit" :
				arr[1] = "ClearPositionOnQLimitR";
				break;
			}
		case "Advanced Sensor and Meas" :
			//Haven't been support
			arr[1] = "";
			arr[2] = "";
			break;
		case "Current Limit" :
			arr[1] = trim(arr[1]);
			arr[2] = toInteger(arr[2]);
		}
		return arr;
	}
	/**
	 * Slot's value solve
	 * @param name
	 * @param configName
	 * @param value
	 * @return
	 */
	public static String slot_value(String name, String configName, String value) {
		String index = name.split(" ")[1];
		switch(configName) {
		case "Allowable Error" :
		case "I Zone" :
			return index + ", " + value.split("\\.")[0];
		case "Loop Period Ms":
		case "Max Integral Accum" :
		case "Peak Output" :
		case "kP" :
		case "kI" :
		case "kD" :
		case "kF" :
		default :
			return index + ", " + value;
		}
	}
	/**
	 * slot's configName to code
	 * @param configName
	 * @return
	 */
	public static String slot_config(String configName) {
		switch(configName) {
		case "Allowable Error" :
			return "AllowableClosedloopError";
		case "I Zone" :
			return "_IntegralZone";
		case "Loop Period Ms":
			return "ClosedLoopPeriod";
		case "Max Integral Accum" :
			return "MaxIntegralAccumulator";
		case "Peak Output" :
			return "ClosedLoopPeakOutput";
		case "kP" :
			return "_kP";
		case "kI" :
			return "_kI";
		case "kD" :
			return "_kD";
		case "kF" :
			return "_kF";
		default :
			return "";
		}
		
	}
}
