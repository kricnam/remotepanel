package com.bitcomm;

public class CommunicationHistoryData {
	final static int DoseRate=0;
	final static int Spectrum=1;
	CommunicationPort port;
	byte MachineNum;
	DateTime startTime;
	DateTime endTime;
	DataConfirm Confirmed;
	int DataType;
	Command cmdConfirm;
	Command cmdRequest;
	CommunicationHistoryData(CommunicationPort port, byte MachineNum,int DataType)
	{
		this.port = port;
		this.MachineNum = MachineNum;
		this.DataType = DataType;
		startTime = new DateTime();
		endTime = new DateTime();
		if (DataType==DoseRate)
		{
			cmdConfirm = new Command(Command.CommandType.DoseRateHistoryDataConfirm);
			cmdRequest = new Command(Command.CommandType.DoseRateHistoryDataRequest);
		}
		else
		{
			cmdConfirm = new Command(Command.CommandType.SpectrumDataConfirm);
			cmdRequest = new Command(Command.CommandType.SpectrumDataRequest);
		}
		
	}
	void Confirm() throws Exception
	{
		byte[] cmd;
		cmd = cmdConfirm.ByteStream();
		byte[] start = startTime.ByteStream();
		byte[] end = endTime.ByteStream();
		short length =(short) (cmd.length + start.length + end.length + 2);
		byte[] pack = new byte[length];
		System.arraycopy(cmd, 0, pack, 0, cmd.length);
		pack[cmd.length]= (byte)((length>>8)&0x00FF);
		pack[cmd.length+1]=(byte)(length & 0x00FF);
		System.arraycopy(start, 0, pack, cmd.length+2, start.length);
		System.arraycopy(end, 0, pack, cmd.length+start.length+2, end.length);
		DataPacket packet = new DataPacket(MachineNum,pack);
		try{
			port.Send(packet.ByteStream());
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	void ConfirmAnswer() throws Exception
	{
		DataPacket packet = port.RecvPacket();
		if (packet.getPacketType() == cmdConfirm.Type())
		{
			Confirmed = new DataConfirm(packet.ByteStream());
		}
		
	}
	
	void DataRequest() throws Exception 
	{
		DataPacket packet = new DataPacket(MachineNum,
				new DataRequest(cmdRequest,Confirmed.startNo,Confirmed.endNo,Confirmed.nCount).ByteStream());//Confirmed.nCount)
		try{
			
			port.Send(packet.ByteStream());
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	HiLowData DataAnswerDoseRate() throws Exception
	{
		DataPacket packet = port.RecvPacket();
		if (packet==null) return null;
		if (packet.getPacketType()== Command.CommandType.DoseRateHistoryDataRequest)
		{
			 return new HiLowData(packet.ByteStream());
		}
			
		return null;
	}

	SpectrumData DataAnswerSpectrum() throws Exception
	{
		DataPacket packet = port.RecvPacket();
		if (packet==null) return null;
		
		if (packet.getPacketType()== Command.CommandType.SpectrumDataRequest)
		{
			 return new SpectrumData(packet.ByteStream());
		}
			
		return null;
	}
}
