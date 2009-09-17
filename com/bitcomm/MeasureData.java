package com.bitcomm;

public class MeasureData {
	int ToChar(byte Hi,byte Low)
	{
		return ((int)Hi << 8) & 0x0000FF00 | ((int)Low & 0x000000FF);
	}
	int ToChar(byte[] data, int start)
	{
		return ((int)data[start] << 8) & 0x0000FF00 | ((int)data[start+1] & 0x000000FF);
	}
	
	int ToInt(byte[] data,int start)
	{
		return  ((((int)data[start])<<24)& 0xFF000000) | ((((int)data[start+1]) << 16) & 0x00FF0000) 
		| ((((int)data[start+2]) << 8) & 0x0000FF00) | ((int)data[start+3] & 0x000000FF);
	}
	int PackChar(byte[] data,int start, char value)
	{
		data[start]=(byte)((value>>8)&0x00FF);
		data[start+1]=(byte)(value & 0x00FF);
		return start+2;
	}

	int PackChar(byte[] data,int start, short value)
	{
		data[start]=(byte)((value>>8)&0x00FF);
		data[start+1]=(byte)(value & 0x00FF);
		return start+2;
	}

	int PackInt(byte[] data,int start, int value)
	{
		data[start]=(byte)((value>>24)&0x000000FF);
		data[start+1]=(byte)((value>>16)&0x000000FF);
		data[start+2]=(byte)((value>>8)&0x000000FF);
		data[start+3]=(byte)(value & 0x000000FF);
		return start+4;
	}

	
}
