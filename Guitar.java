import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.*;
 
public class Guitar {

    double soundAttenuation;

    double[] notes = new double[127];
    BgGuitarNote[] notesObjects = new BgGuitarNote[127];
    Thread[] notesThreads = new Thread[127];
    Mixer audioMixer;

    public Guitar(Mixer audioMixer,double attenuation) {
        this.audioMixer = audioMixer;
        this.soundAttenuation = attenuation;
        int a = 440; // A is 440 hz...
        for (int x = 0; x < 127; ++x) {
            notes[x] = (a / 32.0) * (Math.pow(2,((x - 9.0) / 12.0)));
            notesObjects[x] = new BgGuitarNote( notes[x] ,  this.soundAttenuation, audioMixer);
            // notesThreads[x] = new Thread( notesObjects[x] );
            // notesThreads[x].start();
        }
    }

    public void playNote(int midiNote) {
        BgGuitarNote n = notesObjects[midiNote];
        if(!n.isPlaying()) {
            Thread t = new Thread(n);
            t.start();
            // notesThreads[midiNote].interrupt();  
            // notesObjects[midiNote].play();
            System.out.println("[Guitar]\tStarting note");
        } else {
            n.resetNote();
            System.out.println("[Guitar]\tResting note");
        }
    }
    public void stopNote(int midiNote) {
        BgGuitarNote n = notesObjects[midiNote];
        n.shutTheFuckUp();
    }
}

class BgGuitarNote implements Runnable {
    public static final int SAMPLE_RATE = 44100;
    double frequency = 50;
    double attenuation = 0.5;

    public boolean running = false;
    boolean stfu;

    Mixer audioMixer;
    GuitarNote note;

    int i=0;

    public BgGuitarNote(double frequency, double attenuation, Mixer audioMixer) {
        this.frequency = frequency;
        this.attenuation = attenuation;
        this.stfu = false;
        this.audioMixer = audioMixer;
        this.note = new GuitarNote(this.frequency,this.attenuation,SAMPLE_RATE*1);
        this.note.generate();
    }

    public boolean isPlaying() {
        return this.running;
    }
    public void play() {
        this.running = true;
    }
    public void shutTheFuckUp() {
        this.stfu = true;
    }

    public void resetNote() {
        this.stfu = false;
        this.note = new GuitarNote(this.frequency,this.attenuation,SAMPLE_RATE*1);
        this.note.generate();
        this.i = 0;
    }

    @Override
    public void run() {
        System.out.println("[Guitar]\tNote of frequency "+this.frequency+"Hz is started");
        // while(true) {
            // try { Thread.sleep(250); } catch (Exception e) { }
            // if(this.running) {
                this.running = true;
                System.out.println("Thread is here");
                try {
                    System.out.println("[Guitar]\tNote thread started");
                    SourceDataLineWrapper wrapper = audioMixer.getLine();
                    wrapper.line.start();
                    if(wrapper.line != null) {
                        this.note = new GuitarNote(this.frequency,this.attenuation,SAMPLE_RATE*1);
                        this.note.generate();

                        byte[] buffer = new byte[this.note .out.length/10 +1];
                        int bufferSize = 0;

                        this.stfu = false;
                        for (this.i=0; this.i<this.note .out.length && !this.stfu; this.i++) {
                            double sample = this.note .out[this.i];
                            if (sample < -1.0) sample = -1.0;
                            if (sample > +1.0) sample = +1.0;

                            short s = (short) (Short.MAX_VALUE * sample);

                            buffer[bufferSize] = (byte) s;
                            bufferSize++;
                            buffer[bufferSize] = (byte) (s >> 8);   // little Endian
                            bufferSize++;
                            if(bufferSize >= this.note .out.length/10 && !this.stfu) {
                                wrapper.line.write(buffer, 0, bufferSize);  
                                bufferSize = 0;
                            }
                        }
                        if(this.stfu) {
                        }
                    }

                    System.out.println("[Guitar]\tNote has finished playing");
                    wrapper.line.stop();
                    wrapper.line.flush();
                    wrapper.line.drain();
                    // Thread.sleep(100);
                    wrapper.occupied = false;
                } catch (Exception e) {
                    System.out.println("[Guitar]\tException while playing. ("+e.toString()+")");
                }
                this.running= false;
            // }
        // }
        
    }
}


class GuitarNote {
    final Random RANDGEN = new Random();

    RingBuffer buf ;
    double[] out;

    int bufferCursor = 0;
    int duration;
    double frequency;
    double attenuation;

    public GuitarNote(double frequency, double attenuation, int duration){
        int len = (int)(BgGuitarNote.SAMPLE_RATE / frequency + 0.5);
        this.buf = new RingBuffer(len);
        this.out = new double[duration];
        this.duration    = duration;
        this.frequency   = frequency;
        this.attenuation = attenuation;
        this.bufferCursor = 0;
    }


    public void pluck(){
        for(int i=0; i<this.buf.getCapacity(); i++) {
            this.buf.enqueue(RANDGEN.nextGaussian()-0.5);
        }
    }

    public void tic(){
        for (int i=0;i<this.buf.getSize() ; i++) {
            double a = this.buf.pick();
            this.buf.dequeue();
            double b = this.buf.pick();

            this.buf.enqueue( (a+b) * 0.5 * this.attenuation );
            this.out[ Math.min(this.bufferCursor,this.out.length-1) ] =((a+b) * 0.5 * this.attenuation) * 0.15;
            this.bufferCursor++;
        }
    }

    public void generate(){   
        this.out = new double[this.duration];
        this.pluck();
        for (int i=0;i<BgGuitarNote.SAMPLE_RATE/this.buf.getCapacity() ; i++) {
            this.tic();
        }
    }
}
