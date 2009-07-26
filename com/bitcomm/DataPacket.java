package com.bitcomm;

public class DataPacket {
	static byte SOH=0x01;
	static byte STX=0x02;
	static byte ETX=0x03;
	static byte EOT=0x04;
	static byte ENQ=0x05;
	static byte ACK=0x06;
	static byte NAK=0x15;
	static byte ETB=0x17;
	byte MachineNumber;
	int start;
	int end;
	byte Command[];
	char crc16;
	boolean bValid;
	DataPacket()
	{
		bValid = false;
	}
	
	DataPacket(byte Machine){
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
		bValid = true;
	}
	
	boolean IsValid(byte []Data, int start)
	{
		int i;
		char crc =  (char)0xFFFF;
		i=start;
		while (i+3 < Data.length)
		{
			crc = CRC16.crc16_byte(Data[i],crc);
			if (Data[i+1]==ETX && Data[i+4]==EOT 
				&&	Data[i+2]*256+Data[i+3]==crc)
			{
				this.start = start;
				end = i;
				bValid = true;
				return true;
			}
		}
		return false;
	}
	
	int GetDataStart(byte []Data)
	{
		int i;
		i =0;
		while(Data[i]!= SOH && i < Data.length) i++;
		if (Data[i]!=SOH) return -1;
		if (Data[i] == SOH && Data[i+2]== STX)
		{
			MachineNumber = Data[i+1];
			return  i+3;
		}
		return -1;
	}
	
	
	DataPacket(byte []Data){
		int i;
		i =0;
		while(Data[i]!= SOH && i < Data.length) i++;
		if (Data[i]!=SOH) 
		{
			bValid = false;
			return;
		}
		MachineNumber = Data[++i];
		if (Data[++i]!= STX)
		{
			bValid = false;
			return;
		}
		
		i++;
		while(Data[i]!= ETX && i < Data.length) i++;
		
		byte tmp[]=new byte[2];
		tmp[0]='r';
		tmp[1]='a';
		crc16=
		
		//new byte[9];
		if (Data[0]== SOH &&
			Data[2]== STX &&
		    Data[3]== 'r' &&
		    Data[4]== 'a' &&
		    Data[5]== ETX &&
		    Data[8]== EOT &&
		    Data[6]== (byte)(crc16 & 0x00ff) &&
		    Data[7]== (byte)(crc16 >> 8 & 0x00ff))
			bValid = true;
		else 
			bValid = false;
		MachineNumber = Data[1];
		Command = Data.clone();
	}
}
