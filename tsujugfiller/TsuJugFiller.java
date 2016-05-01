package scripts.tsujugfiller;

import java.awt.Color; //to get different colors
import java.awt.Graphics; //paint

import org.tribot.api.DynamicClicking;
import org.tribot.api.Timing; //to calculate time things
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting; //for onPaint()

import scripts.tsuminer.fcpaint.FCPaint;
import scripts.tsuminer.fcpaint.FCPaintable;

@ScriptManifest(authors = { "Tsuyoshi" }, name = "TsuJugFiller", category = "Money Making")

public class TsuJugFiller extends Script implements Painting, FCPaintable {

	final FCPaint PAINT = new FCPaint(this, Color.RED);
	ABCUtil abc_util = new ABCUtil();
	private final int WATERJUG = 1937;
	private final int JUG = 1935;
	private final RSTile PUMPTILE = new RSTile(2949, 3382);
	private RSObject[] pump;
	int jugsAmount;
	boolean shouldStop = false;
	int invs = 0;
	int jugPrice = PriceChecker.getOSbuddyPrice(JUG);
	int waterJugPrice = PriceChecker.getOSbuddyPrice(WATERJUG);
	

	@Override
	public void run() {
		Mouse.setSpeed(170);
		loop(30, 50);
	}

	private void loop(int min, int max) {
		if (Camera.getCameraAngle()!= 100){
			Camera.setCameraAngle(100);
		}
		while (true) {
			if(shouldStop) {
				return;
			}
			else if (Inventory.getCount(JUG) == 0) {
				bankJugs();
			} else if (Inventory.getCount(JUG) > 0) {
				fillJugs();
			}
		}
	}

	public void fillJugs() {
		
		if(!PUMPTILE.isOnScreen())
		WebWalking.walkTo(PUMPTILE);
		
		jugsAmount = Inventory.getCount(WATERJUG);
		sleep (1000,2000);
		
		while (jugsAmount == Inventory.getCount(WATERJUG)) {
			RSItem[] jugs = Inventory.find(JUG);

			if (jugs.length > 0) {
				jugs[0].click();
			}

			pump = Objects.findNearest(10, 24004);
			if (jugs.length > 0) {
				DynamicClicking.clickRSObject(pump[0], "Use");
			}
			idleAntiban();
			sleep (1000,2000);
		}
	}

	public void idleAntiban(){
		if (abc_util.shouldCheckTabs()) {
			abc_util.checkTabs();
		}
		if (abc_util.shouldCheckXP()) {
			abc_util.checkXP();
		}
		if (abc_util.shouldExamineEntity()) {
			abc_util.examineEntity();
		}
		if (abc_util.shouldMoveMouse()) {
			abc_util.moveMouse();
		}
		if (abc_util.shouldPickupMouse()) {
			abc_util.pickupMouse();
		}
		if (abc_util.shouldRightClick()) {
			abc_util.rightClick();
		}
		if (abc_util.shouldRotateCamera()) {
			abc_util.rotateCamera();
		}
		if (abc_util.shouldLeaveGame()) {
			abc_util.leaveGame();
		}
	}
	
	public boolean bankJugs() {
		if (!Game.isRunOn() && Game.getRunEnergy() > abc_util.generateRunActivation()) {
			Options.setRunOn(true);
		}
		WebWalking.walkToBank();
		while(!Banking.isBankScreenOpen()){
		Banking.openBank();
		}
		Banking.depositAll();
		if (Banking.find(JUG).length == 0){
			shouldStop = true;
		}
		Banking.withdraw(28, "Jug");
		Banking.close();
		invs++;
		sleep(200,300);
		return shouldStop;
	}

	private static final long startTime = System.currentTimeMillis();

	private long timeRan() {
		long time = System.currentTimeMillis() - startTime;
		return time;
	}

	private long invsPerHour() {
		long invsperhour = (long) (invs * (3600000 / (double) timeRan()));
		return invsperhour;
	}
	
	private int jugsPerHour(){
		int jugsperhour = (int)invsPerHour() * 28;
		return jugsperhour;
	}
	
	private int gpPerHour(){
		int gpperhour = (waterJugPrice - jugPrice) * jugsPerHour();
		return gpperhour;
	}


	public String[] getPaintInfo() {
		return new String[] { "Runtime: " + Timing.msToString(timeRan()),
				"Inventories filled: " + invs + "(" + invs * 28 + ")", "Inventories per hour: " + invsPerHour() + "(" + jugsPerHour() + ")", "GP/H: " + gpPerHour()};
	}

	public void onPaint(Graphics g) {
		PAINT.paint(g);
	}

}
