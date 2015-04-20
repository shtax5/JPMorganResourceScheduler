package jpmorgan.hlevnjak.messagingsystem.impl;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import jpmorgan.hlevnjak.messagingsystem.Gateway;
import jpmorgan.hlevnjak.messagingsystem.Message;
import jpmorgan.hlevnjak.messagingsystem.Resource;

/***
 * A simple implementation of Resource. It contains a main thread that wait for an amount of time, 
 * then calls the method send() from Gateway. The purpose of this is purely to simulate the time needed 
 * for a remote resource to process or communicate with the system in real time. 
 * The amount of waiting time is decided randomly at runtime, between a minimum and a maximum. These values 
 * can be decided by the user.
 * @author mhlevnjak
 *
 */
public class ResourceSimple extends Resource {

	// Did not use 'static' modifier here, to ensure thread safety
	private int minProcessingTime = 1000;
	private int maxProcessingTime = 2000;

	private Random rand = new Random();
	private volatile boolean available = true;

	public ResourceSimple(String name) {
		super(name);
	}

	public ResourceSimple(String name, int minProcessingTime, int maxProcessingTime) {
		super(name);
		this.minProcessingTime = minProcessingTime;
		this.maxProcessingTime = maxProcessingTime;
	}

	@Override
	public void start(Message msg, Gateway gateway, CountDownLatch resourceEnds) throws InterruptedException {
		CountDownLatch resourceStarts = new CountDownLatch(1);
		Thread resThread = null;
		if (resThread == null) {
			resThread = new Thread(new ResourceThread(msg, gateway, resourceStarts, resourceEnds));
		}
		if (!resThread.isAlive()) {
			resThread.start();
			// Wait for the thread to actually do work, then proceed
			resourceStarts.await();
		}
	}
	
	/**
	 * In this simple implementation, the speed is the int value of the average 
	 * of min and max processing time.
	 */
	@Override
	public int getProcessingSpeed() {
		return (minProcessingTime + maxProcessingTime)/2;
	}
	
	/**
	 * @return the availability of the resource
	 */
	@Override
	public boolean isAvailable() {
		return available;
	}

	/**
	 * Represents the processing that a resource does. In
	 * this implementation, the resource only waits for a random amount of time
	 * between minProcessingTime and maxProcessingTime.
	 * 
	 * @author buivuhoang
	 * 
	 */
	private class ResourceThread implements Runnable {
		private final CountDownLatch resourceStarts;
		private final CountDownLatch resourceEnds;
		
		private Message msg;
		private Gateway gateway;
		
		public ResourceThread(Message msg, Gateway gateway, CountDownLatch resourceStarts, CountDownLatch resourceEnds) {
			this.resourceStarts = resourceStarts;
			this.resourceEnds = resourceEnds;
			this.msg = msg;
			this.gateway = gateway;
		}

		public void run() {
			available = false;
			// count down to signal that this thread actually started
			resourceStarts.countDown();
			try {
				// Pause for processing
				Thread.sleep(randInt(minProcessingTime, maxProcessingTime));
				// if the message is not sent yet (isCompleted() == false) , call the method send(Message) of Gateway
				if (!msg.isCompleted())
					gateway.send(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				available = true;
				// count down to signal that this thread ended
				resourceEnds.countDown();
			}
		}

		private int randInt(int min, int max) {
			int randomNum = rand.nextInt((max - min) + 1) + min;
			return randomNum;
		}
	}
}
