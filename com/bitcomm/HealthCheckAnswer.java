package com.bitcomm;

public class HealthCheckAnswer extends MeasureData {
	int nMachineNum;
	short DataLength;
	char DataNum;
	DateTime date;
	int  nStatus;

	HealthCheckAnswer(byte[] Data)
	{
		nMachineNum = Data[1];
		DataLength =(short) ToChar(Data,5);
		DataNum = (char) ToChar(Data,7);
		date = new DateTime(Data,9);
		nStatus = ToInt(Data,15);
	}

}
