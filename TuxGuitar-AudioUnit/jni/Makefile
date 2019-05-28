CXX = clang++

INCLUDES = -I $(JAVA_HOME)/include/

LIBRARIES = -framework CoreAudio -framework JavaVM -framework AudioUnit -framework AudioToolbox -framework CoreServices

LIBRARY = libtuxguitar-audiounit-jni.jnilib

FILENAME = org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI.cpp

all:	$(LIBRARY)

$(LIBRARY):	clean
	$(CXX) $(INCLUDES) -arch x86_64 $(LIBRARIES) -msse -bundle -o intel64_$(LIBRARY) $(FILENAME)
	lipo -create intel64_$(LIBRARY) -output ../$(LIBRARY)

clean:
	rm -f intel64_$(LIBRARY) ../$(LIBRARY)
