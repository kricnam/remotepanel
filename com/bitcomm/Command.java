/**
 * 
 */
package com.bitcomm;

/**
 * @author mxx
 *
 */
public class Command {
	byte CommandCode[];
	enum CommandType
	{
		HealthCheck,
		Alarm,
		CurrentData,
		DoseRateHistoryDataRequest,
		DoseRateHistoryDataConfirm,
		SpectrumHistoryDataRequest,
		SpectrumHistoryDataConfirm,
		SpectrumDataRequest,
		SpectrumDataConfirm,
		Unknown
    }
	Command()
	{
		CommandCode = new byte[2];
	}
	Command(byte[] cmd)
	{
		CommandCode = new byte[2];
		if (cmd !=null)
		{
			CommandCode[0]=cmd[0];
			CommandCode[1]=cmd[1];
		}
	}
	Command(CommandType nType)
	{
		CommandCode = new byte[2];
		switch(nType)
		{
		case HealthCheck:
		case Alarm:
			CommandCode[0] = 'w';
			CommandCode[1] = 'a';
			break;
		case CurrentData:
			CommandCode[0]='r';
			CommandCode[1]='a';
			break;
		case DoseRateHistoryDataRequest:
			CommandCode[0]='h';
			CommandCode[1]='a';
			break;
		case DoseRateHistoryDataConfirm:
			CommandCode[0]='h';
			CommandCode[1]='A';
			break;
		case SpectrumDataRequest:
			CommandCode[0]='h';
			CommandCode[1]='c';
			break;
		case SpectrumDataConfirm:
			CommandCode[0]='h';
			CommandCode[1]='C';
			break;
		default:
			break;
			
		}
	}
	
	CommandType Type()
	{
		if (CommandCode[0]=='r' && CommandCode[1]=='a')
			return CommandType.CurrentData;
		if (CommandCode[0]=='w' && CommandCode[1]=='a')
			return CommandType.Alarm;
		if (CommandCode[0]=='h' && CommandCode[1]=='c')
			return CommandType.SpectrumDataRequest;
		if (CommandCode[0]=='h' && CommandCode[1]=='a')
			return CommandType.DoseRateHistoryDataRequest;
		if (CommandCode[0]=='h' && CommandCode[1]=='A')
			return CommandType.DoseRateHistoryDataConfirm;

		return CommandType.Unknown;
	}
	
	byte[] ByteStream()
	{ 
		return CommandCode;
	}
	String ToString()
	{
		return String.valueOf((char)CommandCode[0])+String.valueOf((char)CommandCode[1]);
	}
}