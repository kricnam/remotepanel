CXXFLAGS =	-O2 -g -Wall -fmessage-length=0 -lpthread

SMSD_OBJS =	Modem.o SerialPort.o TraceLog.o pdu.o Config.o\
		EM660Modem.o  smsd.o
COMD_OBJS =	SerialPort.o TraceLog.o Channel.o Config.o\
		TCPPort.o  comd.o
LIBS = -lutil

SMSD_TARGET =	smsd
COMD_TARGET =   comd

$(SMSD_TARGET):	$(SMSD_OBJS)
	$(CXX) $(CXXFLAGS) -o $(SMSD_TARGET) $(SMSD_OBJS) $(LIBS)

$(COMD_TARGET):	$(COMD_OBJS)
	$(CXX) $(CXXFLAGS) -o $(COMD_TARGET) $(COMD_OBJS) $(LIBS)

all:	$(SMSD_TARGET)  $(COMD_TARGET)

clean:
	rm -f *.o $(SMSD_TARGET) $(COMD_TARGET)
