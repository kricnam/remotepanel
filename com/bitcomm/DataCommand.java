package com.bitcomm;

public class DataCommand {
	byte MachineNumber;
	byte Command[];
	DataCommand(byte Machine){
		MachineNumber = Machine;
		Command = new byte[2];
		Command[0]='r';
		Command[1]='a';
	}

}
