package com.bitcomm;

import java.util.Arrays;

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
	byte Content[];
	boolean bValid;
	DataPacket()
	{
		bValid = false;
	}
	
	DataPacket(byte Machine,byte[] Data){
		MachineNumber = Machine;
		Content = Arrays.copyOf(Data, Data.length);
		bValid = true;
	}
	
	DataPacket(byte []Data){
		bValid = false;
		start = GetDataStart(Data);
		if (start<0)	return;

		if (Is_CRC_OK(Data,start))
		{
		   Content = Arrays.copyOfRange(Data, start, end);
		}
	}
	
	byte[] ByteStream()
	{
		byte []out;
		if (bValid)
		{
			out = new byte[Content.length + 3 + 5];
			out[0]=SOH;
			out[1]=MachineNumber;
			out[2]=STX;
			int i;
			for ( i=0;i<Content.length;i++)
				out[i+3]=Content[i];
			out[i+3]=ETX;
			char crc = CRC16.crc16((char)0xFFFF,Content, Content.length);
			out[i+4]= (byte)((crc >> 8) & 0x00ff);
			out[i+5]= (byte)(crc & 0x00ff);
			out[i+6]= EOT;
		}
		else
		{
			out = new byte[0];
		}
		return out;
	}

	boolean Is_CRC_OK(byte []Data, int start)
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
		i = 0;
		while(Data[i]!= SOH && i < Data.length) i++;
		if (Data[i]!=SOH) return -1;
		if (Data[i] == SOH && Data[i+2]== STX)
		{
			MachineNumber = Data[i+1];
			return  i+3;
		}
		return -1;
	}
	
}
