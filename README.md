# IUT_Synthesizer

Assignment for the semester 1 at the IUT of Belfort Monbeliard.

You can generate a note with only these components:
- StdAudio.java
- RingBuffer.java
- GuitarNote class in Guitar.java

I've bypassed StdAudio due to the multiple note at one time (not part of the assignement).
For this there is a background thread that's created and ran each time you want to play a note
There is also a dispatcher (Mixer.java) that distribute the 10 audio lines to each currently played notes

MidiStreamer.java listens for midi notes being played from a phone (using the Midi Keyboard app) for example and plays the notes

## Build / Starting

```bash
samuel@FlutterShy:~/Projects/IUT_Synthesizer$ ./launch.sh
```
