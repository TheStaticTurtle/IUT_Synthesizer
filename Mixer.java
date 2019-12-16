class Mixer{
    SourceDataLineWrapper[] audioLines = new SourceDataLineWrapper[10];

	public Mixer() {
    	for(int i=0; i<this.audioLines.length; i++) {
    		try {
    			System.out.println("Generating audioline #"+i);
				this.audioLines[i] = new SourceDataLineWrapper(i);
    		} catch(Exception e) {
            	System.out.println("[Mixer]\tError while initializing audio line n°"+i+" ("+e.toString()+")");
    		}
    	}
	}

	SourceDataLineWrapper getLine() {
    	for(int i=0; i<this.audioLines.length; i++) {
		    if(!this.audioLines[i].line.isRunning()) {
        		System.out.println("[Mixer]\tAllocating line #"+i);
        		this.audioLines[i].occupied = true;
		    	return audioLines[i];
		    }
    	}
        System.out.println("[Mixer]\tError no line left");
    	return null;
	}

}