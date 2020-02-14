package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

import edu.wpi.first.wpilibj.DriverStation;

import java.applet.AudioClip;
import java.nio.Buffer;
import java.rmi.server.Operation;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.hal.PDPJNI;
import frc.robot.autostep.*;
import edu.wpi.first.wpilibj.Compressor;
import java.util.*;

//import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

//BUTTONS
//		TELEOP
// 	Right: 

// 6 Shooter with PowerAxis

// 8 Climb up !DOUBLE CHECK!
// 9 climb down !DOUBLE CHECK!

// 10 Shooter at Low Power
// 11 Shooter at High Power

// 	Left:
//1 intake

// 6 LL Forward
// 7 LL Backward

// 8 Color Wheel # reset
// 9 Color Wheel Spin X times

// 10 Color Wheel Spin
// 11 Color Wheel Find

//		TEST
//	Right:
// 6 Power Axis

//	Left:
// 6 TEST
// 7 TEST

// 11 Indexing motor(beam break override)

public class Robot extends TimedRobot {

	// Sensors
	public AHRS navx = new AHRS(SPI.Port.kMXP);

	// private ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
	public DigitalInput ColorWheelLimit = new DigitalInput(7);
	public float encoderSpeed1 = 0;
	public float encoderSpeed2 = 0;
	// public Encoder shooterEncoder = new Encoder();

	public boolean beamReset = true;

	// Drivetrain
	public DriveTrain driveTrain = new DriveTrain(54, 55, 56, 57, navx);

	// Nuemataics
	public DoubleSolenoid intakeSolenoid = new DoubleSolenoid(6, 7);

	// Logic
	public boolean speedToggle = false;

	// Joysticks
	public Joystick driver;
	public Joystick operator;
	public boolean arcadeDrive = false;
	public Joystick flightStickLeft;
	public Joystick flightStickRight;

	public Shooter shooter = new Shooter();
	public Indexer indexer = new Indexer(shooter);
	public Limelight limelight = new Limelight();

	// Motors
	public TalonSRX intakeSeven = new TalonSRX(6);

	public TalonSRX MotorSeven = new TalonSRX(30);
	public TalonSRX MotorEight = new TalonSRX(31);

	boolean limitPressed;

	public enum DriveScale {
		linear, parabala, tangent, inverse, cb, cbrt,
	}

	enum AutoChoice {
		// Left, Right
	}

	enum ColorWheel {
		Red, Green, Blue, Yellow, Unknown, Broken;
	}

	public ColorWheel LastColor;
	public int colorChangsCount;

	public RobotState currentState;

	// Auto
	public LinkedList<AutoStep> autonomousSelected;
	// start on the line, backup, and shoot
	public LinkedList<AutoStep> mostBasicShoot;
	// start on the right, get two balls, and shoot five
	public LinkedList<AutoStep> rightFiveBall;
	// start on line then move back grab 2 balls from center, then go to trench to
	// grab three balls
	public LinkedList<AutoStep> centerEightBall;

	public int currentAutoStep;
	// public float auto = 0;

	public int accum;

	public void robotInit() {
		limelight.SetLight(false);
		shooter.Init();

		// SmartDashboard.putNumber("AutoMode", 0);
		SmartDashboard.putBoolean("RobotEnabled", true);
		indexer.indexerFive.set(ControlMode.PercentOutput, 0.0f);
	}

	public void disabledInit() {
		// Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);
	}

	public void autonomousInit() {

		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(1);
		// autonomousSelected = SmartDashboard.getNumber("/SmartDashboard/autoMode", 0);

		limelight.AutoSettings();
		driveTrain.SetBreak();
		limelight.SetLight(true);
		indexer.ballCounter = 3;

		currentAutoStep = 0;

		mostBasicShoot = new LinkedList<AutoStep>();
		mostBasicShoot.add(new NavxReset(navx));
		mostBasicShoot.add(new ShooterRev(shooter, 4100.0));
		// Configuration Complete
		mostBasicShoot.add(new EncoderForward(driveTrain, 50000f, -0.15f));
		mostBasicShoot.add(new LimelightTrack(driveTrain, shooter, limelight, 0.0f));
		mostBasicShoot.add(new Shoot(shooter, indexer, 4100.0, 3));

		rightFiveBall = new LinkedList<AutoStep>();
		rightFiveBall.add(new NavxReset(navx));
		rightFiveBall.add(new ShooterRev(shooter, 3600.0));
		rightFiveBall.add(new IntakeDrop(intakeSolenoid));
		rightFiveBall.add(new IntakeRun(intakeSeven, 0.6f));
		// Configuration Complete
		rightFiveBall.add(new EncoderForward(driveTrain, 120000f, -0.8f));
		// 5 balls intaked
		rightFiveBall.add(new LimelightTrack(driveTrain, shooter, limelight, -0.85f));
		rightFiveBall.add(new Shoot(shooter, indexer, 3600.0, 5));
		// All balls shoot
		rightFiveBall.add(new NavxTurn(driveTrain, navx, 0, .3f));
		rightFiveBall.add(new EncoderForward(driveTrain, 50000f, -0.25f));
		rightFiveBall.add(new NavxTurn(driveTrain, navx, 140, .3f));
		rightFiveBall.add(new EncoderForward(driveTrain, 70000f, -0.25f));

		centerEightBall = new LinkedList<AutoStep>();
		centerEightBall.add(new NavxReset(navx));
		centerEightBall.add(new ShooterRev(shooter, 3600.0));
		centerEightBall.add(new IntakeDrop(intakeSolenoid));
		centerEightBall.add(new IntakeRun(intakeSeven, 0.6f));
		// Configuration Done
		centerEightBall.add(new EncoderForward(driveTrain, 63000f, -0.8f));
		centerEightBall.add(new NavxTurn(driveTrain, navx, 70, .3f));
		centerEightBall.add(new EncoderForward(driveTrain, 25000f, -0.2f));
		// Balls intaked
		centerEightBall.add(new EncoderForward(driveTrain, 25000f, 0.2f));
		centerEightBall.add(new NavxTurn(driveTrain, navx, 0, .3f));
		centerEightBall.add(new LimelightTrack(driveTrain, shooter, limelight, 0.0f));
		// First Shooting Session
		centerEightBall.add(new Shoot(shooter, indexer, 4100.0, 3));
		centerEightBall.add(new NavxTurn(driveTrain, navx, -90, .3f));
		centerEightBall.add(new EncoderForward(driveTrain, 25000f, -0.6f));
		centerEightBall.add(new NavxTurn(driveTrain, navx, 0, .3f));
		centerEightBall.add(new EncoderForward(driveTrain, 90000f, -0.2f));
		centerEightBall.add(new LimelightTrack(driveTrain, shooter, limelight, 0.0f));
		centerEightBall.add(new Shoot(shooter, indexer, 4100.0, 3));

		autonomousSelected = centerEightBall;
		autonomousSelected.get(0).Begin();
	}

	public void autonomousPeriodic() {

		// System.out.println(driveTrain.GetEncoder());

		shooter.Update();
		shooter.UpdatePID();

		// autonomous loop
		System.out.println("Current auto step " + currentAutoStep);
		if (currentAutoStep < autonomousSelected.size()) {

			autonomousSelected.get(currentAutoStep).Update();

			if (autonomousSelected.get(currentAutoStep).autoIndex) {
				indexer.RunAutomatic();
			}

			if (autonomousSelected.get(currentAutoStep).isDone) {
				currentAutoStep = currentAutoStep + 1;
				if (currentAutoStep < autonomousSelected.size()) {
					autonomousSelected.get(currentAutoStep).Begin();
				}
			}
		} else {
			// System.out.println("Autonomous Done");
			driveTrain.SetBothSpeed(0.0f);
			// currentState = RobotState.Teleop;
		}

		UpdateMotors();
	}

	public void teleopInit() {

		limelight.TeleopSettings();
		limelight.SetLight(false);

		// colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(2);

		shooter.Init();

		driveTrain.SetCoast();

		// Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);
		flightStickLeft = new Joystick(3);
		flightStickRight = new Joystick(2);
	}

	// Drive Scale
	public float DriveScaleSelector(float ControllerInput, DriveScale selection) {

		float multiplier = (ControllerInput / (float) Math.abs(ControllerInput));

		if (selection == DriveScale.parabala) {
			return multiplier * (float) Math.pow(ControllerInput, 2);
		} else if (selection == DriveScale.tangent) {
			return multiplier * (0.4f * (float) Math.tan(1.8 * (multiplier * ControllerInput) - .9) + 0.5f);
		} else if (selection == DriveScale.inverse) {
			return (float) Math.pow(ControllerInput, 1 / 2);
		} else if (selection == DriveScale.cb) {
			return (float) Math.pow(ControllerInput, 3);
		} else if (selection == DriveScale.cbrt) {
			return multiplier * (0.63f * (float) Math.cbrt((multiplier * ControllerInput) - 0.5f) + 0.5f);
		} else {
			return ControllerInput;
		}
	}

	public void teleopPeriodic() {

		shooter.Update();

		// Color Wheel
		if (false) {
			// Intake Sensor (31)
			if (ColorWheelLimit.get() == true && !limitPressed) {
				limitPressed = true;
				colorChangsCount = colorChangsCount + 1;
			}
			if (ColorWheelLimit.get() == false) {
				limitPressed = false;
			}
			// Color Sensor (25)
			// ColorWheel currentColor = GetCurrentColor();
			// if (currentColor != LastColor && currentColor != ColorWheel.Unknown) {
			// colorChangsCount = colorChangsCount + 1;
			// LastColor = currentColor;
			// }

			// Reset Color CHanges Number
			if (flightStickLeft.getRawButton(8)) {
				colorChangsCount = 0;
			}

			// Color Wheel Spin
			if (flightStickLeft.getRawButton(9)) {
				if (colorChangsCount <= 31) {// 25
					MotorSeven.set(ControlMode.PercentOutput, 1.0f);
				} else {
					MotorSeven.set(ControlMode.PercentOutput, 0.0f);
				}

			} else {
				if (flightStickLeft.getRawButton(10)) {
					MotorSeven.set(ControlMode.PercentOutput, 0.5f);
				} else {
					MotorSeven.set(ControlMode.PercentOutput, 0.0f);
				}
			}

			// Color Wheel Find
			if (flightStickLeft.getRawButton(11)) {
				if (GetTargetColor() != LastColor) {
					MotorSeven.set(ControlMode.PercentOutput, 1.0f);
				} else {
					MotorSeven.set(ControlMode.PercentOutput, 0.0f);
				}
			}
		}

		// Intake 2=B, 4=Y
		// TODO add to Dashboard
		if (operator.getRawButton(2)) {
			intakeSeven.set(ControlMode.PercentOutput, 0.6f);
			SmartDashboard.putNumber("intakeMotor", 0.6f);
			// floorBeltEight.set(ControlMode.PercentOutput, -0.0f);
			// indexerFive.set(ControlMode.PercentOutput, -0.5f);
		} else if (operator.getRawButton(4)) {
			intakeSeven.set(ControlMode.PercentOutput, -0.6f);
			SmartDashboard.putNumber("intakeMotor", -0.6f);

			// floorBeltEight.set(ControlMode.PercentOutput, -0.0f);
			// indexerFive.set(ControlMode.PercentOutput, -0.5f);
		} else {
			intakeSeven.set(ControlMode.PercentOutput, 0.0f);
			SmartDashboard.putNumber("intakeMotor", 0.0f);

			// indexerFive.set(ControlMode.PercentOutput, 0.0f);
		}

		// Intake Solenoid 1=A
		if (operator.getRawButtonPressed(1)) {
			if (intakeSolenoid.get() == Value.kForward) {
				intakeDown();
			} else if (intakeSolenoid.get() == Value.kReverse) {
				intakeUp();
			} else {
				intakeUp();
			}
		}

		// Shooter
		shooter.rpmTarget = SmartDashboard.getNumber(shooter.shooterRPMKey, shooter.shooterRPMTarget);
		if (operator.getRawButton(5)) {
			shooter.rpmTarget = 4100;
			// SmartDashboard.putNumber(shooter.shooterRPMKey, 5700);
			limelight.SetLight(true);
		} else {
			limelight.SetLight(false);
		}
		shooter.UpdatePID();

		// Indexer 3=X 6=Right Bumper
		if (operator.getRawButton(6)) {
			indexer.RunManualForward();
			SmartDashboard.putNumber("ballCounter", 0);
		} else if (operator.getRawButton(3)) { // Manual Override
			indexer.indexerFive.set(ControlMode.PercentOutput, -0.4f);
			indexer.ballCounter = 0;
			SmartDashboard.putNumber("ballCounter", 0);
		} else {
			indexer.RunAutomatic();
			SmartDashboard.putNumber("ballCounter", indexer.ballCounter);

		}

		// // Climber
		// if (flightStickRight.getRawButton(9)) {

		// MotorSeven.set(ControlMode.PercentOutput, 1.0f * -1);
		// MotorEight.set(ControlMode.PercentOutput, 1.0f);

		// } else if (flightStickRight.getRawButton(8)) {

		// MotorSeven.set(ControlMode.PercentOutput, 1.0f);
		// MotorEight.set(ControlMode.PercentOutput, 1.0f * -1);

		// } else {
		// MotorSeven.set(ControlMode.PercentOutput, 0);
		// MotorEight.set(ControlMode.PercentOutput, 0);
		// }
		if (flightStickLeft.getRawButton(6) || (flightStickLeft.getRawButton(7))) {
			if (flightStickLeft.getRawButton(6)) {
				limelight.Position(driveTrain, 1, 0);
			} else {
				limelight.Position(driveTrain, -1, 0);
			}

		} else {
			ControllerDrive();
		}

		UpdateMotors();
	}

	public void testInit() {

		navx.reset();


		// Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);
		flightStickLeft = new Joystick(3);
		flightStickRight = new Joystick(2);

		shooter.Init();

	}

	public float Lerp(float v0, float v1, float t) {

		if (t < 0) {
			t = 0;

		} else if (t > 1) {
			t = 1;
		}

		return (v0 + t * (v1 - v0));
	}

	public void testPeriodic() {
		// System.out.println("Roll: " + navx.getRoll());
		System.out.println("Yaw: " + navx.getYaw());

		limelight.SetLight(true);

		/*
		 * 
		 * boolean usePid = false;
		 * 
		 * // Encoder float oldSpeed = encoderSpeed1;
		 * 
		 * encoderSpeed1 = MotorSeven.getSelectedSensorVelocity(); //
		 * System.out.println(encoderSpeed1);
		 * 
		 * if (oldSpeed != encoderSpeed1) {
		 * 
		 * // SmartDashboard.putNumber("encoderSpeed1", encoderSpeed1);
		 * 
		 * }
		 * 
		 * // encoderSpeed2 = MotorEight.getSelectedSensorVelocity(); //
		 * System.out.println("climber two" + encoderSpeed2);
		 * 
		 * // Manual Motor index drive // if (flightStickLeft.getRawButton(11)) {
		 * 
		 * // indexerFive.set(ControlMode.PercentOutput, 0.5f);
		 * 
		 * // } else { // indexerFive.set(ControlMode.PercentOutput, 0.0f); // }
		 * 
		 * ControllerDrive(); UpdateMotors();
		 */

	}

	public void ControllerDrive() {
		if (arcadeDrive) {

			// Arcade
			float horJoystick = TranslateController((float) driver.getRawAxis(4));
			float verJoystick = TranslateController((float) driver.getRawAxis(1));

			driveTrain.SetRightSpeed(-verJoystick + -horJoystick);
			driveTrain.SetLeftSpeed(-verJoystick + horJoystick);
			driveTrain.SetCoast();
		} else {

			// tank
			float leftJoystick = DriveScaleSelector((float) flightStickLeft.getRawAxis(1), DriveScale.linear);
			float rightJoystick = DriveScaleSelector((float) flightStickRight.getRawAxis(1), DriveScale.linear);

			driveTrain.SetRightSpeed(-rightJoystick);
			driveTrain.SetLeftSpeed(-leftJoystick);
			driveTrain.SetCoast();
		}
	}

	/*
	 * public ColorWheel GetCurrentColor() {
	 * 
	 * Color detectedColor = colorSensor.getColor();
	 * //System.out.println(detectedColor.red + " - " + detectedColor.green + " - "
	 * + detectedColor.blue);
	 * 
	 * // detecting blue { float redMin = 0.15f; float greenMin = 0.45f; float
	 * blueMin = 0.4f;
	 * 
	 * if (detectedColor.red < redMin && detectedColor.green < greenMin &&
	 * detectedColor.blue > blueMin) { return ColorWheel.Blue; } }
	 * 
	 * // detecting green { float redMin = 0.2f; float greenMin = 0.54f; float
	 * blueMin = 0.28f;
	 * 
	 * if (detectedColor.red < redMin && detectedColor.green > greenMin &&
	 * detectedColor.blue < blueMin) { return ColorWheel.Green; }
	 * 
	 * }
	 * 
	 * // detecting Red { float redMin = 0.45f; float greenMin = 0.4f; float blueMin
	 * = 0.16f;
	 * 
	 * if (detectedColor.red > redMin && detectedColor.green < greenMin &&
	 * detectedColor.blue < blueMin) { return ColorWheel.Red; } }
	 * 
	 * // detecting yellow { float redMin = 0.3f; float greenMin = 0.53f; float
	 * blueMin = 0.1f;
	 * 
	 * if (detectedColor.red > redMin && detectedColor.green > greenMin &&
	 * detectedColor.blue > blueMin) { return ColorWheel.Yellow; } }
	 * 
	 * if (detectedColor.red == 0 && detectedColor.green == 0 && detectedColor.blue
	 * == 0) { return ColorWheel.Broken; }
	 * 
	 * return ColorWheel.Unknown; }
	 */

	public void UpdateMotors() {
		driveTrain.Update();
	}

	public float TranslateController(float input) {
		float deadzone = 0.15f;
		if (input > -deadzone && input < deadzone) {
			input = 0.0f;
		}
		float a = 0.7f;
		float output = (a * input * input * input) + (1 - a) * input;
		return output;
	}

	// Intake Solenoid
	public void intakeDown() {
		System.out.println("Enabled");
		SmartDashboard.putBoolean("intakeExtended", true);
		intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	}

	public void intakeUp() {
		System.out.println("Disabled");
		SmartDashboard.putBoolean("intakeExtended", false);
		intakeSolenoid.set(DoubleSolenoid.Value.kForward);
	}

	public ColorWheel GetTargetColor() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		if (gameData.length() > 0) {
			switch (gameData.charAt(0)) {
			case 'B':
				return ColorWheel.Blue;
			case 'G':
				return ColorWheel.Green;
			case 'R':
				return ColorWheel.Red;
			case 'Y':
				return ColorWheel.Yellow;
			}
		}

		return ColorWheel.Unknown;
	}

}