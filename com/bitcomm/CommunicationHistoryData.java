package com.bitcomm;

public class CommunicationHistoryData {
	CommunicationPort port;
	byte MachineNum;
	DateTime startTime;
	DateTime endTime;
	DataConfirm Confirmed;
	CommunicationHistoryData(CommunicationPort port, byte MachineNum)
	{
		this.port = port;
		this.MachineNum = MachineNum;
		startTime = new DateTime();
		endTime = new DateTime();
	}
	void Confirm() throws Exception
	{
		byte[] cmd = new Command(Command.CommandType.HistoryDataConfirm).ByteStream();
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
		if (packet.getPacketType()==Command.CommandType.HistoryDataConfirm)
		{
			Confirmed = new DataConfirm(packet.ByteStream());
		}
	}
	
	void DataRequest() throws Exception 
	{
		DataPacket packet = new DataPacket(MachineNum,
				new DataRequest(Confirmed.startNo,Confirmed.endNo,Confirmed.nCount).ByteStream());
		try{
			port.Send(packet.ByteStream());
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	HiLowData DataAnswer() throws Exception
	{
		DataPacket packet = port.RecvPacket();
		if (packet.getPacketType()==Command.CommandType.HistoryDataRequest)
		{
			 return new HiLowData(packet.ByteStream());
		}
		return null;
	}

}
