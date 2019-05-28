CXX = clang++

INCLUDES = -I $(JAVA_HOME)/include/

LIBRARIES = -framework CoreAudio -framework JavaVM -framework AudioUnit -framework AudioToolbox -framework Carbon

LIBRARY = libtuxguitar-coreaudio-jni.jnilib

FILENAME = org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI.cpp

all:	$(LIBRARY)

$(LIBRARY):	clean
	$(CXX) $(INCLUDES) -arch x86_64 $(LIBRARIES) -msse -dynamiclib -o intel64_$(LIBRARY) $(FILENAME)
	lipo -create intel64_$(LIBRARY) -output ../$(LIBRARY)

clean:
	rm -f intel64_$(LIBRARY) ../$(LIBRARY)
