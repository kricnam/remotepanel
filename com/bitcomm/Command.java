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
		HistoryData,
		SpectrumDataRequest,
		SpectrumDataConfirm
    }
	Command()
	{
		CommandCode = new byte[2];
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
}
