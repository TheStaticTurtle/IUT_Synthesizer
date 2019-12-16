import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.*;

public class SourceDataLineWrapper {
    public static final int SAMPLE_RATE = 44100;
    private static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
    private static final int SAMPLE_BUFFER_SIZE = 4096;

	SourceDataLine line;
	boolean occupied = false;
	int id=-1;

	public SourceDataLineWrapper(int id) throws LineUnavailableException{
		this.id = id;
    	System.out.println("Generating audioline #"+this.id);

		AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		this.line = (SourceDataLine) AudioSystem.getLine(info);
		this.line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
    }
}