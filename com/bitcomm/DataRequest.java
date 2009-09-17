package com.bitcomm;

public class DataRequest extends MeasureData{
	int startNo;
	int endNo;
	int nCount;
	DataRequest(int start,int end,int count)
	{
		startNo = start;
		endNo = end;
		nCount = count;
	}
	byte[] ByteStream()
	{
		byte[] pack = new byte[10];
		int pos;
		System.arraycopy(new Command(Command.CommandType.HistoryDataRequest).ByteStream(),
				0,pack,0,2);
		pos=PackChar(pack,2,(short)11);//length
		pos=PackChar(pack,pos,(short)startNo);
		pos=PackChar(pack,pos,(short)endNo);
		pos=PackChar(pack, pos, (short)nCount);
		return pack;
	}
}
