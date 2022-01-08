package cz.kfkl.mstruct.gui.utils.ic;

public class SimpleCount implements Count {
	private long startTime;

	public SimpleCount() {
		startTime = System.nanoTime();
	}

	@Override
	public long stop() {
		long newElapsed = System.nanoTime() - startTime;

		return newElapsed / 1000000;
	}

}
