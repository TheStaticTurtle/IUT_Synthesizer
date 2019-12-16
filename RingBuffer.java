 class RingBuffer {

	public double[] buffer; // le tampon de valeurs à proprement parler
	int last;        // indice du prochain élément à insérer +1
	public int capacity;    // nmbre maximal d'éléments dans le tampon
	int size;        // nombre d'éléments actifs dans le tampon

	public RingBuffer(int capacity) {
		this.buffer = new double[capacity];
		this.capacity = capacity;
		this.last = 0;
	}

	public int getSize() { return this.size; }
	public int getCapacity() { return this.capacity; }
	public boolean isEmpty() { return this.size == 0;}  
	public boolean isFull() { return this.size == this.capacity;}  

	public void enqueue(double x) { 
		if(!this.isFull()) {
			if(this.last==this.capacity) {this.last--;}
			this.buffer[Math.min(last,this.buffer.length-1)] = x;
			this.last++;
			this.size++;
		} else {
			//System.out.println("Overflow!!");
		}
	}

	public double  dequeue() {
		double out = this.buffer[0];
		for (int i=1; i<this.buffer.length; i++) {
			this.buffer[i-1] = this.buffer[i] ;
		}
		this.buffer[this.buffer.length-1] = 0;
		this.size--;
		return out;
	}
	public double  pick()  { return this.buffer[0]; }

	public void dump() {
		for (int i=0; i<this.buffer.length; i++) {
			System.out.println(i + " " + this.buffer[i]);
		}
	}

	public void reset(int capacity) {
		this.last = 0;
		this.size = 0;
		this.capacity = capacity;
	}

    public static void main(String[] args){
    	//Self test
        RingBuffer buf = new RingBuffer(10);
        buf.enqueue(1.5);
        buf.enqueue(2.5);
        buf.enqueue(3.5);
        buf.enqueue(4.5);
        buf.enqueue(5.5);
        buf.enqueue(6.5);
        buf.enqueue(7.5);
        buf.enqueue(8.5);
        buf.enqueue(9.5);
        buf.enqueue(10);
        buf.enqueue(10.5);
        buf.dump();
    }
}