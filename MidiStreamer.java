import javax.sound.midi.*;

import java.util.*;
import java.io.*;
import java.awt.event.*;


public class MidiStreamer {
	private static final String MIDI_DEVICE = "OnePlus";

	public static void main(String[] args) throws InvalidMidiDataException {
		//acquire all connected MIDI devices and print them
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for(int i=0;i<infos.length;i++) {
		    System.out.println("[Midi]\t\tDevice index:"+i+" "+infos[i].getName() + " - " + infos[i].getDescription());
		}
		
		Transmitter transmitter;
		Receiver receiver = null;

		for (int i = 0; i < infos.length; i++) {
		    try {
		    	MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
		        MidiDevice.Info d = infos[i];
		        

		        if( d.getName().contains(MIDI_DEVICE) && device.getMaxTransmitters() == 0)  {
		        	System.out.println("[Midi]\t\tOpening " + d.getName() + " / max receivers: " + device.getMaxReceivers() + " / max transmitters: " + device.getMaxTransmitters());
		        	receiver = device.getReceiver();
		        	device.open();
		        }

		        if( d.getName().contains(MIDI_DEVICE) && device.getMaxReceivers() == 0)  {
		        	System.out.println("[Midi]\t\tOpening " + d.getName() + " / max receivers: " + device.getMaxReceivers() + " / max transmitters: " + device.getMaxTransmitters());
		        	transmitter = device.getTransmitter();
		        	transmitter.setReceiver(new MidiInputReceiver(device.getDeviceInfo().getName()));
		        	device.open();
		        }
		    } catch (MidiUnavailableException e) {
		    	System.out.println("[Midi]\t\tException occurred while getting MIDI devices: " + infos[i].getName());
		    }
		}
	}
}


class MidiInputReceiver implements Receiver {
	public String name;
	public Mixer instrumentMixer;
	public Guitar guitar;

	public MidiInputReceiver(String name) {
		this.name = name;
		this.instrumentMixer = new Mixer();
		this.guitar = new Guitar(this.instrumentMixer,0.994);
	}
	    
	public void send(MidiMessage message, long timeStamp) {
		try {
			ShortMessage sm = (ShortMessage) message;
			if(sm.getCommand() == 144 && sm.getData2() != 0) {
				
				System.out.println("[Midi]\t\tNOTE_ON  Note: "+sm.getData1()+" / Velocity: "+sm.getData2());
				this.guitar.playNote(sm.getData1());

			} else if(sm.getCommand() == 128 || sm.getCommand() == 144 && sm.getData2() == 0) {

				System.out.println("[Midi]\t\tNOTE_OFF Note: "+sm.getData1()+" / Velocity: "+sm.getData2());
				this.guitar.stopNote(sm.getData1());

			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public void close() {}
}