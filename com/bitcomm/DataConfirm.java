package com.bitcomm;

public class DataConfirm extends MeasureData{
	DateTime startDate;
	DateTime endDate;
	int startNo;
	int endNo;
	int nCount;
	int DataLength;
	DataConfirm(byte[] Data)
	{
		DataLength =(int) ToChar(Data,5);
		startDate = new DateTime(Data,7); 
		startNo = ToChar(Data,13);
		endDate  = new DateTime(Data,15);
		endNo =  ToChar(Data,21);
		nCount = ToChar(Data,23);
	}
}
