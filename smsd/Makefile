CXXFLAGS =	-O2 -g -Wall -fmessage-length=0 -lpthread

VS_OBJS =		Modem.o SerialPort.o TraceLog.o CallingProcess.o \
			EM770WModem.o Process.o UDPSender.o MU203Modem.o
PPP_OBJS =		Modem.o SerialPort.o TraceLog.o  PPPDMessageParser.o\
			EM770WModem.o Process.o UDPSender.o WAPProcess.o MU203Modem.o CmdShell.o\
			NetStatProcess.o WGetProcess.o
LIBS = -lutil

VS_TARGET =	VSprobe 
PPP_TARGET =    PPPprobe
MODEM_TEST = modem_test

$(VS_TARGET):	$(VS_OBJS)
	$(CXX) $(CXXFLAGS) -o $(VS_TARGET) $(VS_OBJS) $(LIBS)
$(PPP_TARGET):	$(PPP_OBJS)
	$(CXX) $(CXXFLAGS) -o $(PPP_TARGET) $(PPP_OBJS) $(LIBS)
$(MODEM_TEST):  Modem.cpp SerialPort.cpp TraceLog.cpp EM770WModem.cpp pdu.cpp SMSPDUHead.cpp
	$(CXX) -DTEST_MODEM -g -o $(MODEM_TEST) $^

all:	$(VS_TARGET) $(PPP_TARGET)

ppp:	$(PPP_TARGET)

clean:
	rm -f *.o $(VS_TARGET) $(PPP_TARGET)
