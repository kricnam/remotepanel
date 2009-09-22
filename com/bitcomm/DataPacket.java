package com.bitcomm;

import java.util.Arrays;

public class DataPacket {
	static byte SOH = 0x01;

	static byte STX = 0x02;

	static byte ETX = 0x03;

	static byte EOT = 0x04;

	static byte ENQ = 0x05;

	static byte ACK = 0x06;

	static byte NAK = 0x15;

	static byte ETB = 0x17;

	byte MachineNumber;

	int start;

	int end;

	byte Content[];

	boolean bValid;

	DataPacket() {
		bValid = false;
	}

	DataPacket(byte Machine, byte[] Data) {
		MachineNumber = Machine;
		Content = Arrays.copyOf(Data, Data.length);
		bValid = true;
	}

	DataPacket(byte[] Data) {
		bValid = false;
		start = GetDataStart(Data, Data.length);
		if (start < 0)
			return;

		if (Is_CRC_OK(Data, start, Data.length - 3)) {
			Content = Arrays.copyOfRange(Data, start + 3, end);
		}
	}

	DataPacket(byte[] Data, int size) {
		bValid = false;
		start = GetDataStart(Data, size);
		if (start < 0)
			return;

		if (Is_CRC_OK(Data, start, size - 3)) {
			Content = Arrays.copyOfRange(Data, start, end);
		}
	}

	byte[] ByteStream() {
		byte[] out;
		if (bValid) {
			out = new byte[Content.length + 7];
			out[0] = SOH;
			out[1] = MachineNumber;
			out[2] = STX;
			int i;
			for (i = 0; i < Content.length; i++)
				out[i + 3] = Content[i];
			out[i + 3] = ETX;

			char crc = CRC16.crc16((char) 0xFFFF, out, 3, Content.length + 1);
			out[i + 4] = (byte) ((crc >> 8) & 0x00ff);
			out[i + 5] = (byte) (crc & 0x00ff);
			out[i + 6] = EOT;
		} else {
			out = new byte[0];
		}
		return out;
	}

	boolean Is_CRC_OK(byte[] Data, int start, int size) {
		
		char crc = (char) 0xFFFF;
		
		int data_length = ((Data[start + 2] << 8)&0x0000FF00) | (Data[start + 3] & 0x000000FF);
		if (data_length > (size - 3))
			return false;

		if (Data[start + data_length - 1] == ETX
				&& Data[start + data_length + 2] == EOT)

		{
			crc = CRC16.crc16((char) 0xFFFF, Data, start, data_length);

			if (((((char) (Data[start + data_length]) << 8) & (char) 0xff00) | ((char) 0x00FF & (char) Data[start
					+ data_length + 1])) == crc) {
				end = start + data_length - 1;
				bValid = true;
				return true;
			}
		}
		return false;
	}

	protected int GetDataStart(byte[] Data, int size) {
		int i;
		i = 0;
		do {
			while (Data[i] != SOH && i < Math.min(size, Data.length))
				i++;
			if (Data[i] != SOH)
				return -1;
			if (Data[i] == SOH && Data[i + 2] == STX) {
				MachineNumber = Data[i + 1];
				return i + 3;
			}
			i++;
		} while (i < Math.min(size, Data.length));
		return -1;
	}

	Command.CommandType getPacketType()
	{
		Command cmd = new Command(Content);
		return cmd.Type();
	}
}
