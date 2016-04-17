package scripts.tsuminer;

import java.awt.Color; //to get different colors
import java.awt.Dimension;
import java.awt.Graphics; //paint
import java.awt.Toolkit;

import org.tribot.api.Timing; //to calculate time things
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills; //to get XP/levels
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting; //for onPaint()

import scripts.tsuminer.fcpaint.FCPaintable;
import scripts.tsuminer.fcpaint.FCPaint;


@ScriptManifest(authors = { "Tsuyoshi" }, name = "TsuMiner", category = "Mining")

public class TsuMiner extends Script implements Painting, FCPaintable {

	final FCPaint PAINT = new FCPaint(this, Color.RED);
	DPathNavigator navigator = new DPathNavigator();

	private boolean shouldBank;

	private final int[] COPPER_ID = { 7484, 7453 };
	private final int[] TIN_ID = { 7486, 7485 };
	private final int[] CLAY_ID = { 7454, 7487 };
	private final int[] COAL_ID = { 7456, 7489 };
	private final int[] IRON_ID = { 7455, 7488 };
	private final int[] SILVER_ID = { 7457, 7490 };
	private final int[] GOLD_ID = { 7491, 7458 };

	private final int MINING_ANIM = 624;
	private int ORE_INV_ID;
	public final int PICKAXE_ID[] = { 1265, 1267, 1271, 1273, 1275, 11920, 12297 };
	private int[] ORE_ID;

	private RSObject[] ores;
	private RSTile START_TILE;

	Timer time = new Timer(1300);

	private boolean GUI_COMPLETE = false;
	private ABCUtil abc_util = new ABCUtil();
	boolean should_hover = false;
	boolean open_menu = false;

	@Override
	public void run() {

		GUI GUI = new GUI();

		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenW = (screensize.width) / 2;
		int screenH = (screensize.height) / 2;

		GUI.setVisible(true);

		GUI.setLocation((screenW / 2), (screenH / 2));

		while (!GUI_COMPLETE) {
			sleep(300);
		}

		GUI.setVisible(false);

		startScript();

		loop(30, 50);

	}

	private void loop(int min, int max) {
		while (true) {
			if (!Game.isRunOn() && Game.getRunEnergy() > abc_util.generateRunActivation()) {
				Options.setRunOn(true);
			}
			if (Combat.isAutoRetaliateOn()) {
				Combat.setAutoRetaliate(false);
			}
			if (Player.getRSPlayer().isInCombat()) {
				Options.setRunOn(true);
				navigator.traverse(20);
				if (!Player.getRSPlayer().isInCombat()) {
					WebWalking.walkTo(START_TILE);
				}
			}

			
			sleep(100, 200);
			if (Inventory.isFull()) {
				if (shouldBank) {
					bank(START_TILE);
				} else {
					dropOre();
				}
			} else {
				mineOre();
			}
			time.reset();

		}
	}

	private void startScript() {
		Mouse.setSpeed(100);
		START_TILE = Player.getPosition();

	}

	private void mineOre() {

		ores = Objects.findNearest(20, ORE_ID);
		if (ores == null || ores.length < 1)
			return;
		RSObject next_target = (RSObject) this.abc_util.selectNextTarget(ores);

		if (Player.getAnimation() != MINING_ANIM && !Player.isMoving()) {
			if (next_target.isOnScreen()) {

				next_target.click("Mine Rocks");
				if (this.abc_util.shouldCheckTabs()) {
					this.abc_util.checkTabs();
				}
				if (this.abc_util.shouldCheckXP()) {
					this.abc_util.checkXP();
				}
				if (this.abc_util.shouldExamineEntity()) {
					this.abc_util.examineEntity();
				}
				if (this.abc_util.shouldMoveMouse()) {
					this.abc_util.moveMouse();
				}
				if (this.abc_util.shouldPickupMouse()) {
					this.abc_util.pickupMouse();
				}
				if (this.abc_util.shouldRightClick()) {
					this.abc_util.rightClick();
				}
				if (this.abc_util.shouldRotateCamera()) {
					this.abc_util.rotateCamera();
				}
				if (this.abc_util.shouldLeaveGame()) {
					this.abc_util.leaveGame();
				}

			} else {
				Camera.turnToTile(next_target.getPosition());
				Walking.walkTo(next_target.getPosition());

			}
		}

		while (Player.getAnimation() == -1 && time.isRunning()) {
			sleep(10);
		}
	}

	private void dropOre() {
		Mouse.setSpeed(200);
		Inventory.drop(ORE_INV_ID, 1623, 1619, 1617, 1621);
		Mouse.setSpeed(100);
	}

	private void bank(RSTile startTile) {
		WebWalking.walkToBank();
		RSNPC[] nearestBanker = NPCs.findNearest("Banker");
		
		if (nearestBanker.length > 0){
		Camera.turnToTile(nearestBanker[0].getPosition());
		
		while (!nearestBanker[0].isOnScreen()){
			sleep(100);
		}
		}
		Banking.openBank();
		Banking.depositAllExcept(PICKAXE_ID);
		Banking.close();
		WebWalking.walkTo(startTile);
	}

	/**
	 *
	 * @author Jordan
	 */
	public class GUI extends javax.swing.JFrame {

		/**
		 * Creates new form GUI
		 */
		public GUI() {
			initComponents();
		}

		/**
		 * This method is called from within the constructor to initialize the
		 * form. WARNING: Do NOT modify this code. The content of this method is
		 * always regenerated by the Form Editor.
		 */
		// <editor-fold defaultstate="collapsed" desc="Generated Code">
		private void initComponents() {

			buttonGroup1 = new javax.swing.ButtonGroup();
			jLabel1 = new javax.swing.JLabel();
			start = new javax.swing.JButton();
			bank = new javax.swing.JRadioButton();
			drop = new javax.swing.JRadioButton();
			oreToMine = new javax.swing.JComboBox<>();
			jLabel2 = new javax.swing.JLabel();
			jLabel3 = new javax.swing.JLabel();

			jLabel1.setFont(new java.awt.Font("Segoe UI Light", 1, 36)); // NOI18N
			jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel1.setText("TsuMiner");

			start.setText("Start");
			start.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					startActionPerformed(evt);
				}
			});

			buttonGroup1.add(bank);
			bank.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
			bank.setSelected(true);
			bank.setText("Bank");
			bank.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					bankActionPerformed(evt);
				}
			});

			buttonGroup1.add(drop);
			drop.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
			drop.setText("Drop");
			drop.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					dropActionPerformed(evt);
				}
			});

			oreToMine.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
			oreToMine.setModel(new javax.swing.DefaultComboBoxModel<>(
					new String[] { "Tin", "Copper", "Iron", "Clay", "Silver", "Coal", "Gold" }));
			oreToMine.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					oreToMineActionPerformed(evt);
				}
			});

			jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
			jLabel2.setText("Ore to mine:");

			jLabel3.setText("Make sure to start script at where you want to mine!");

			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
							.createSequentialGroup().addGroup(layout
									.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
											.createSequentialGroup().addContainerGap().addGroup(layout
													.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
													.addComponent(start, javax.swing.GroupLayout.Alignment.TRAILING,
															javax.swing.GroupLayout.DEFAULT_SIZE,
															javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
													.addGroup(layout.createSequentialGroup().addGap(18, 18, 18)
															.addComponent(jLabel2,
																	javax.swing.GroupLayout.PREFERRED_SIZE, 104,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addGap(18, 18, 18)
															.addComponent(oreToMine,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addGap(0, 0, Short.MAX_VALUE))
													.addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING,
															javax.swing.GroupLayout.DEFAULT_SIZE,
															javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
									.addGroup(layout.createSequentialGroup()
											.addGroup(layout.createParallelGroup(
													javax.swing.GroupLayout.Alignment.LEADING)
													.addGroup(layout.createSequentialGroup().addGap(45, 45, 45)
															.addComponent(jLabel3))
													.addGroup(layout.createSequentialGroup().addGap(136, 136, 136)
															.addGroup(layout
																	.createParallelGroup(
																			javax.swing.GroupLayout.Alignment.TRAILING)
																	.addComponent(drop).addComponent(bank))))
											.addGap(0, 34, Short.MAX_VALUE)))
							.addContainerGap()));
			layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup().addComponent(jLabel1).addGap(13, 13, 13).addComponent(bank)
							.addGap(18, 18, 18).addComponent(drop).addGap(18, 18, 18)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
									.addComponent(oreToMine, javax.swing.GroupLayout.PREFERRED_SIZE,
											javax.swing.GroupLayout.DEFAULT_SIZE,
											javax.swing.GroupLayout.PREFERRED_SIZE)
									.addComponent(jLabel2))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
							.addComponent(jLabel3).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(start).addGap(22, 22, 22)));

			pack();
		}// </editor-fold>

		private void startActionPerformed(java.awt.event.ActionEvent evt) {

			if (bank.isSelected()) {
				shouldBank = true;
			} else {
				shouldBank = false;
			}

			Object contents = oreToMine.getSelectedItem();

			if (contents == "Tin") {
				ORE_ID = TIN_ID;
				ORE_INV_ID = 438;
			} else if (contents == "Clay") {
				ORE_ID = CLAY_ID;
				ORE_INV_ID = 434;
			} else if (contents == "Iron") {
				ORE_ID = IRON_ID;
				ORE_INV_ID = 440;
			} else if (contents == "Copper") {
				ORE_ID = COPPER_ID;
				ORE_INV_ID = 436;
			} else if (contents == "Silver") {
				ORE_ID = SILVER_ID;
				ORE_INV_ID = 442;
			} else if (contents == "Coal") {
				ORE_ID = COAL_ID;
				ORE_INV_ID = 453;
			} else {
				ORE_ID = GOLD_ID;
				ORE_INV_ID = 444;
			}

			GUI_COMPLETE = true;
		}

		private void bankActionPerformed(java.awt.event.ActionEvent evt) {
		}

		private void dropActionPerformed(java.awt.event.ActionEvent evt) {
		}

		private void oreToMineActionPerformed(java.awt.event.ActionEvent evt) {
		}

		// Variables declaration - do not modify
		private javax.swing.JRadioButton bank;
		private javax.swing.ButtonGroup buttonGroup1;
		private javax.swing.JRadioButton drop;
		private javax.swing.JLabel jLabel1;
		private javax.swing.JLabel jLabel2;
		private javax.swing.JLabel jLabel3;
		private javax.swing.JComboBox<String> oreToMine;
		private javax.swing.JButton start;
		// End of variables declaration
	}

	private static final long startTime = System.currentTimeMillis();
	private int startLvl = Skills.getActualLevel(Skills.SKILLS.MINING);
	private int startXP = Skills.getXP(Skills.SKILLS.MINING);

	private long timeRan() {
		long time = System.currentTimeMillis() - startTime;
		return time;
	}

	private int currentLvl() {
		int current = Skills.getActualLevel(Skills.SKILLS.MINING);
		return current;
	}

	private int gainedLvl() {
		int gainedlvl = currentLvl() - startLvl;
		return gainedlvl;
	}

	private int gainedXP() {
		int gainedxp = Skills.getXP(Skills.SKILLS.MINING) - startXP;
		return gainedxp;
	}

	private int xpToLevel() {
		int xptolevel = Skills.getXPToNextLevel(Skills.SKILLS.MINING);
		return xptolevel;
	}

	private long xpPerHour() {
		long xpperhour = (long) (gainedXP() * (3600000 / (double) timeRan()));
		return xpperhour;
	}


	public String[] getPaintInfo() {
		return new String[] { "Runtime: " + Timing.msToString(timeRan()),
				"Current Level: " + currentLvl() + "(" + gainedLvl() + ")", "XP Gained: " + gainedXP(),
				"XP TNL: " + xpToLevel(), "XP/H: " + xpPerHour() };
	}

	public void onPaint(Graphics g) {
		PAINT.paint(g);
	}

}
