package cf4j.process;

/**
 * <p>Class that manages the execution of processes. To use this class, you must have previously 
 * loaded a dataModel. This datamodel should be sent to the specific Partible algorithms</p>
 * 
 * <p>Its mains methods are:</p>
 * <ul>
 * 		<li>get/setThreads (...): indicates the number of threads to the Partible execution.</li>
 * 		<li>parallelExecute (...): execute a specific Partible implementation.</li>
 * </ul>
 * 
 * @author Fernando Ortega, Jesús Mayor
 */
public class Processor {

	/**
	 * Class instance (Singleton pattern)
	 */
	private static Processor instance = null;

	/**
	 * Number of thread to be used
	 */
	private int numThreads;

	/**
	 * Gets the single instance of the class.
	 * @return Single instance
	 */
	public static Processor getInstance() {
		if (instance == null) {
			instance = new Processor();
		}
		return instance;
	}

	/**
	 * Creates a new instance. The number of executions sets is set based on
	 * the available processors.
	 */
	private Processor () {
		this.setNumThreads(Runtime.getRuntime().availableProcessors() * 2);
	}

	/**
	 * Returns the number of thread used.
	 * @return Number of Threads
	 */
	public int getNumThreads () {
		return this.numThreads;
	}

	/**
	 * Set the number of thread to be used.
	 * @param threads Number of threads
	 */
	public void setNumThreads (int threads) {
		this.numThreads = threads;
	}

	/**
	 * Execute a Partible implementation for users.
	 * @see Partible
	 * @param partible partible implementation of the datamodel focused on a specific runnable algorithm.
	 */
	public synchronized void parallelExec (Partible partible) {
		this.parallelExec(partible, true);
	}

	/**
	 * Execute a Partible implementation for users.
	 * @see Partible
	 * @param partible partible implementation of the datamodel focused on a specific runnable algorithm.
	 */
	public synchronized void parallelExec (Partible partible, boolean verbose) {
		if (verbose) System.out.println("\nProcessing... " + this.getClass().getName());

		// Error control
		if (partible.getTotalIndexes() < 1)
			throw new RuntimeException("Test array can not be empty");

		if (this.numThreads < 1)
			throw new RuntimeException("The number of threads must be one or more");

		// We compute number of indexes per thread
		int indexesPerThread = partible.getTotalIndexes() / numThreads;
		if (partible.getTotalIndexes() % numThreads != 0)
			indexesPerThread++;

		// Do some stuff...
		partible.beforeRun();

		// Launch all threads
		int index;
		PartibleThread[] pt = new PartibleThread[numThreads];
		//First thread is the received instance of the partibles.
		for (index = 0; index < this.numThreads && index < partible.getTotalIndexes(); index++) {
			pt[index] = new PartibleThread(partible,index,indexesPerThread,verbose);
		}

		// Wait until all threads end
		try {
			for (index = 0; index < this.numThreads && index < partible.getTotalIndexes(); index++) {
				pt[index].join();
			}
		} catch (InterruptedException ie) {
			System.out.println("ERROR: " + ie);
		}

		// Do some stuff...
		partible.afterRun();
	}
}
