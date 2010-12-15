/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.system;

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

	///**
	// * The path to the uevent file.
	// */
	//private static String UEVENT_PATH = "/sys/devices/platform/ds2784-battery/power_supply/battery/uevent";

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

		String result = catFile(STATUS_REGISTER_PATH);

		//If the result is empty, the kernel most likely does not have the battery driver modifications
		if(result.length() > 0) {
			switch(the_register_position) {
				case 1: value = ((Integer.parseInt("02", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
				case 2: value = ((Integer.parseInt("04", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
				case 4: value = ((Integer.parseInt("10", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
				case 5: value = ((Integer.parseInt("20", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
				case 6: value = ((Integer.parseInt("40", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
				case 7: value = ((Integer.parseInt("80", 16) & Integer.parseInt(result.substring(2), 16)) != 0)?1:0; break;
				default: break;
			}
		}

		return value;
	}

	/**
	 * Returns the current voltage.
	 * @return Returns the current voltage.
	 */
	public String getVoltage() {
		String voltage = catFile(VOLTAGE_PATH).trim();

		return voltage;
	}

	/**
	 * Returns the current current.
	 * @return Returns the current current.
	 */
	public String getCurrent() {
		String current = catFile(GET_CURRENT_PATH).trim();

		return current;
	}

	/**
	 * Returns the current average current.
	 * @return Returns the current average current.
	 */
	public String getAvgCurrent() {
		String avg_current = catFile(GET_AVG_CURRENT_PATH).trim();

		return avg_current;
	}

	/**
	 * Returns the current getFull40.
	 * @return Returns the current getFull40.
	 */
	public String getFull40() {
		String full_40 = catFile(GET_FULL40_PATH).trim();

		//If the full_40 is empty, the kernel most likely does not have the battery driver modifications
		if(full_40.length() > 0) {
			//Remove unneeded text
			full_40 = full_40.substring(full_40.indexOf(" ") + 1, full_40.lastIndexOf(" "));
		}

		return full_40;
	}

	/**
	 * Returns the current getmAh.
	 * @return Returns the current getmAh.
	 */
	public String getMAh() {
		String mAh = catFile(GET_MAH_PATH).trim();

		return mAh;
	}

	/**
	 * Returns the current temperature of the battery.
	 * @return The current temperature of the battery.
	 */
	public String getTemperature() {
		String temperature = "";

		String temp_MSB = getDumpRegister(10);
        String temp_LSB = getDumpRegister(11);
        
        //If the either temp_MSB or temp_LSB is empty, the kernel most likely does not have the battery driver modifications
	    if(temp_MSB.length() > 0 && temp_LSB.length() > 0) {
		    int temp_MSB_converted = Integer.parseInt(temp_MSB, 16);
		    int temp_LSB_converted = Integer.parseInt(temp_LSB, 16);
		    int temp = (((temp_MSB_converted<<8) | (temp_LSB_converted))>>5)*10/8;

		    temperature = Integer.toString(temp);
        }

	    return temperature;
	}
	
	/**
	 * Returns the contents of the dump register.
	 * @return The contents of the dump register.
	 */
	public String getDumpRegister() {
		String dump_register_contents = catFile(DUMP_REGISTER_PATH);
		return dump_register_contents;
	}

	/**
	 * Returns the value of the dump register from the given position.
	 * @param the_dump_register_position The position in the dump register to return.
	 * @return The value of the dump register from the given position.
	 */
	public String getDumpRegister(int the_dump_register_position) {
		String register = "";

		String dump_register_contents = catFile(DUMP_REGISTER_PATH);

		//If the dump register is empty, the kernel most likely does not have the battery driver modifications
		if(dump_register_contents.length() > 0) {
			//Replace newline characters with spaces
			dump_register_contents = dump_register_contents.replaceAll("\n", " ").trim();

			//Create an 82 element array to hold the 82 values from the register
			String[] dump_reg = new String[82];

			String temp = "";
			int index = 0;
			while(dump_register_contents.contains(" ")) {
				temp = dump_register_contents.substring(0, dump_register_contents.indexOf(" "));
				//If the value isn't the first column
				if(!temp.contains(":")) {
					dump_reg[index] = temp.trim();
					index++;
				}

				dump_register_contents = dump_register_contents.substring(dump_register_contents.indexOf(" ") + 1);
			}
			//Add final register value
			dump_reg[index] = temp.trim();

			register = dump_reg[the_dump_register_position];
		}

		return register;
	}

	/**
	 * Sets the battery age.
	 * @param the_new_age The new battery age, between 80-100.
	 */
	public void setAge(int the_new_age) {
		if(the_new_age > 100 || the_new_age < 65) {
			return;
		} else {
			String hex_age = Integer.toHexString(the_new_age);
			runSystemCommandAsRoot("echo 0x14 " + hex_age + " > /sys/devices/platform/ds2784-battery/setreg");
		}
	}

	/**
	 * Sets the full 40 value.
	 * @param the_new_mAh The new mAh to set the full 40 to, between 1200-3700.
	 */
	public void setFull40(int the_new_mAh) {
		if(the_new_mAh < 1200 || the_new_mAh > 3700) {
			return;
		}
		//1452mAh * 15 mO / 6.25 = 3485 
		//1200 * 15 / 6.25 = 2880 minimum
		//3400 * 15 / 6.25 = 8160 maximum

		//3485/256 = 13.61 --> so 13 converted to hex is 0d which goes into register 0x6a
		//0.61*256 = 157 --> so 157 converted to hex is 9d which goes into register 0x6b
		//set full40 will be more difficult, bit shifting is involved so perhaps this will go in its own method?
		//the command would take one 4 digit number, say 3485, and bit shift it by 8.  take that whole number and throw it into register 6a.
		//then take the remainder, bit shift it by 8 the other direction, and throw than into register 6b.

		double raw_value = (the_new_mAh * 15) / 6.25;
		String reg_6a = Integer.toHexString((int)(raw_value / 256));
		String reg_6b = Integer.toHexString((int)Math.round((((raw_value / 256) - (int)(raw_value / 256)) * 256)));

		runSystemCommandAsRoot("echo 0x6a " + reg_6a + " > /sys/devices/platform/ds2784-battery/setreg");
		runSystemCommandAsRoot("echo 0x6b " + reg_6b + " > /sys/devices/platform/ds2784-battery/setreg");
	}
	
	/**
	 * Bumps up the remaining voltage.
	 */
	public void setACR() {
		runSystemCommandAsRoot("echo 0x10 02 > /sys/devices/platform/ds2784-battery/setreg");
	}

	//Private Methods	

	/**
	 * Returns the contents of a file.
	 * @param the_file The file of which to return the contents of.
	 * @return Returns the contents of a file.
	 */
	private String catFile(String the_file) {
		String file_contents = "";

		file_contents = runSystemCommand("cat " + the_file);

		return file_contents;
	}

	/**
	 * Runs a system command.
	 * @param the_command_to_run The system command to run.
	 * @return The result of the command.
	 */
	private String runSystemCommand(String the_command_to_run) {
		String result = "";

		try {
			ShellCommand shell_command = new ShellCommand();
			CommandResult command_result = shell_command.sh.runWaitFor(the_command_to_run);

			if (!command_result.success()) {
				Log.v(TAG, "Error " + command_result.stderr);
			} else {
				result = command_result.stdout;
			}
		} catch(Exception e) {
			Log.v(TAG, "Error " + e.getMessage());
		}

		return result;
	}

	/**
	 * Runs a system command as root.
	 * @param the_command_to_run The system command to run as root.
	 * @return The result of the system command.
	 */
	private String runSystemCommandAsRoot(String the_command_to_run) {
		String result = "";
		
		try {
			ShellCommand shell_command = new ShellCommand();
			//If root commands are possible
			if(shell_command.canSU()) {
				CommandResult command_result = shell_command.su.runWaitFor(the_command_to_run);
				
				if (!command_result.success()) {
					Log.v(TAG, "Error " + command_result.stderr);
				} else {
					result = command_result.stdout;
				}
			}
		} catch(Exception e) {
			Log.v(TAG, "Error " + e.getMessage());
		}
		
		return result;
	}
}
//End of class BatteryInfo