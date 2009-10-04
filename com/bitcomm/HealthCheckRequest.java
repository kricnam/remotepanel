package com.bitcomm;

public class HealthCheckRequest extends MeasureData {
	int nMachineNum;
	char DataNum;
	DateTime date;
	int  nStatus;
	
	HealthCheckRequest()
	{
		
	}

	byte[] ByteStream()
	{
		byte[] request=new  byte[8];
		int pos;
		System.arraycopy(new Command(Command.CommandType.HealthCheck).ByteStream(),
				0,request,0,2);
		pos=PackChar(request,2,(short)8);//length
		pos=PackInt(request,pos,nStatus);

		return request;
	}

}
