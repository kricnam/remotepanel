package com.bitcomm;

public class DataCommand {
	static byte SOH=0x01;
	static byte STX=0x02;
	static byte ETX=0x03;
	static byte EOT=0x04;
	static byte ENQ=0x05;
	static byte ACK=0x06;
	static byte NAK=0x15;
	static byte ETB=0x17;
	byte MachineNumber;
	byte Command[];
	char crc16;
	DataCommand(byte Machine){
		MachineNumber = Machine;
		byte tmp[]=new byte[2];
		Command = new byte[9];
		Command[0]= SOH;
		Command[1]= Machine;
		Command[2]= STX;
		Command[3]='r';
		Command[4]='a';
		Command[5]=ETX;
		Command[8]=EOT;
		tmp[0]='r';
		tmp[1]='a';
		crc16=CRC16.crc16((char)0xFFFF, tmp,tmp.length );
		Command[6] = (byte)(crc16 & 0x00ff);
		Command[7] = (byte)(crc16 >> 8 & 0x00ff);
	}

}
