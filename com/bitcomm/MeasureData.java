package com.bitcomm;

public class MeasureData {
	int ToChar(byte Hi,byte Low)
	{
		return Hi << 8 + Low;
	}
	int ToChar(byte[] data, int start)
	{
		return data[start] << 8 + data[start+1];
	}
	int ToInt(byte Hi1,byte Hi2,byte Low1,byte Low2)
	{
		return  Hi1 << 24 + Hi2 << 16 + Low1 << 8 + Low2;
	}
	int ToInt(byte[] data,int start)
	{
		return  data[start]<<24 + data[start+1] << 16 
		+ data[start+2] << 8 + data[start+3];
	}
}
