package net.jonrichards.batteryapp.system;

import android.util.Log;

import com.teslacoilsw.quicksshd.ShellCommand;
import com.teslacoilsw.quicksshd.ShellCommand.CommandResult;

/**
 * A class for getting information about the battery.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class DS2784Battery {
	
	//Static Variables
	
	/**
	 * The tag to identify the source of a log message. 
	 */
	private static final String TAG = "BatteryInfo.java";
	
	/**
	 * The path to the statusreg file.
	 */
	private static String STATUS_REGISTER_PATH = "/sys/devices/platform/ds2784-battery/statusreg";
	
	/**
	 * The path to the getvoltage file.
	 */
	private static String VOLTAGE_PATH = "/sys/devices/platform/ds2784-battery/getvoltage";
	
	/**
	 * The path to the uevent file.
	 */
	private static String UEVENT_PATH = "/sys/devices/platform/ds2784-battery/power_supply/battery/uevent";
	
	/**
	 * The path to the dumpreg file.
	 */
	private static String DUMP_REGISTER_PATH = "/sys/devices/platform/ds2784-battery/dumpreg";
	
	/**
	 * The path to the getcurrent file.
	 */
	private static String GET_CURRENT_PATH = "/sys/devices/platform/ds2784-battery/getcurrent";
	
	/**
	 * The path to the getavgcurrent file.
	 */
	private static String GET_AVG_CURRENT_PATH = "/sys/devices/platform/ds2784-battery/getavgcurrent";
	
	/**
	 * The path to the getfull40 file.
	 */
	private static String GET_FULL40_PATH = "/sys/devices/platform/ds2784-battery/getFull40";
	
	/**
	 * The path to the getmAh file.
	 */
	private static String GET_MAH_PATH = "/sys/devices/platform/ds2784-battery/getmAh";
	
	//Instance Variables
	
	//Constructors
	
	/**
	 * Constructs a new BatteryInfo object.
	 */
	public DS2784Battery() {
		
	}
	
	//Public Methods
	
	/**
	 * Returns the value of the corresponding bit in the status register, or -1 if the given bit placement is invalid.
	 * @param the_register_position A 0 based position of the bit in the status register to return the value for.
	 */
	public int getStatusRegister(int the_register_position) {
		int value = -1;
		
		//If the register position is invalid
		if(the_register_position < 0 || the_register_position > 7) {
			return value;
		}
		
		String result = this.catFile(STATUS_REGISTER_PATH);
		
		switch(the_register_position) {
			case 1: value = ((Integer.parseInt("02", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
			case 2: value = ((Integer.parseInt("04", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
			case 4: value = ((Integer.parseInt("10", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
			case 5: value = ((Integer.parseInt("20", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
			case 6: value = ((Integer.parseInt("40", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
			case 7: value = ((Integer.parseInt("80", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
			default: break;
		}
		
		return value;
	}
	
	/**
	 * Returns the current voltage.
	 * @return Returns the current voltage.
	 */
	public String getVoltage() {
		String voltage = this.catFile(VOLTAGE_PATH).trim();
		
		return voltage;
	}
	
	/**
	 * Returns the current current.
	 * @return Returns the current current.
	 */
	public String getCurrent() {
		String current = this.catFile(GET_CURRENT_PATH).trim();
		
		return current;
	}
	
	/**
	 * Returns the current average current.
	 * @return Returns the current average current.
	 */
	public String getAvgCurrent() {
		String avg_current = this.catFile(GET_AVG_CURRENT_PATH).trim();
		
		return avg_current;
	}
	
	/**
	 * Returns the current getFull40.
	 * @return Returns the current getFull40.
	 */
	public String getFull40() {
		String full_40 = this.catFile(GET_FULL40_PATH).trim();
		
		//Remove unneeded text
		full_40 = full_40.substring(full_40.indexOf(" ") + 1, full_40.lastIndexOf(" "));
		
		return full_40;
	}
	
	/**
	 * Returns the current getmAh.
	 * @return Returns the current getmAh.
	 */
	public String getMAh() {
		String mAh = this.catFile(GET_MAH_PATH).trim();
		
		return mAh;
	}
	
	/**
	 * Returns the value of the dump register from the given position.
	 * @param the_dump_register_position The position in the dump register to return.
	 * @return The value of the dump register from the given position.
	 */
	public String getDumpRegister(int the_dump_register_position) {
		String value = this.catFile(DUMP_REGISTER_PATH);
		
		//Replace newline characters with spaces
		value = value.replaceAll("\n", " ").trim();
		
		//Create an 82 element array to hold the 82 values from the register
		String[] dump_reg = new String[82];
		
		String temp = "";
		int index = 0;
		while(value.contains(" ")) {
			temp = value.substring(0, value.indexOf(" "));
			//If the value isn't the first column
			if(!temp.contains(":")) {
				dump_reg[index] = temp.trim();
				index++;
			}
			
			value = value.substring(value.indexOf(" ") + 1);
		}
		//Add final register value
		dump_reg[index] = temp.trim();
		
		return dump_reg[the_dump_register_position];
	}
	
	//Private Methods	
	
	/**
	 * Returns the contents of a file.
	 * @param the_file The file of which to return the contents of.
	 * @return Returns the contents of a file.
	 */
	private String catFile(String the_file) {
		String file_contents = "";
		
		ShellCommand cmd = new ShellCommand();
		CommandResult r = cmd.sh.runWaitFor("cat " + the_file);
		
		if (!r.success()) {
			Log.v(TAG, "Error " + r.stderr);
		} else {
			file_contents = r.stdout;
		}
		
		return file_contents;
	}
}
//End of class BatteryInfo