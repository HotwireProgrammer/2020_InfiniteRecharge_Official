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

import java.nio.Buffer;
import java.rmi.server.Operation;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Ultrasonic;
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
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.hal.PDPJNI;
import frc.robot.autostep.*;
import edu.wpi.first.wpilibj.Compressor;

//import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

//BUTTONS
//		TELEOP
// 	Right: 

// 8 Climb up !DOUBLE CHECK!
// 9 climb down !DOUBLE CHECK!

// 10 Diskbreak off
// 11 Diskbreak on

// 	Left:
// 6 LL Forward
// 7 LL Backward

// 8 Color Wheel # reset
// 9 Color Wheel Spin X times

// 10 Color Wheel Spin
// 11 Color Wheel Find

//		TEST
//	Right:
// 6 Power Axis

// 10 Indexing motor(beam break override)

//	Left:
// 6 TEST
// 7 TEST

public class Robot extends TimedRobot {

	// Sensors
	public AHRS navx = new AHRS(SPI.Port.kMXP);
	public DigitalInput beamBreak = new DigitalInput(0);
	// private ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
	public DigitalInput ColorWheelLimit = new DigitalInput(7);
	public float encoderSpeed1 = 0;
	public float encoderSpeed2 = 0;
	// public Encoder shooterEncoder = new Encoder();

	public boolean beamReset = true;

	// Drivetrain
	public DriveTrain driveTrain = new DriveTrain(0, 1, 2, 3, navx);

	// Nuemataics
	public DoubleSolenoid diskBrake = new DoubleSolenoid(3, 2);
	
	//Logic
	public boolean speedToggle = false;

	// Joysticks
	public Joystick driver;
	public Joystick operator;
	public boolean arcadeDrive = false;
	public Joystick flightStickLeft;
	public Joystick flightStickRight;

	public double powerAxis;

	public TalonSRX preShooter = new TalonSRX(9);
	public TalonSRX preShooterTwo = new TalonSRX(0);
	public TalonSRX indexer = new TalonSRX(0);
	public TalonSRX shooterThree = new TalonSRX(10);
	public TalonSRX shooterFour = new TalonSRX(2);
	public TalonSRX climberOne = new TalonSRX(7);
	public TalonSRX climberTwo = new TalonSRX(8);
	public Timer indexTimer = new Timer();

	public int testNumber = 0;

	boolean limitPressed;

	public enum DriveScale {
		linear, parabala, tangent, inverse, cb, cbrt,
	}

	enum AutoChoice {
		// Left, Right
	}

	enum RobotState {
		Autonomous, Teleop;
	}

	enum ColorWheel {
		Red, Green, Blue, Yellow, Unknown, Broken;
	}

	public ColorWheel LastColor;
	public int colorChangsCount;

	public RobotState currentState;

	public enum LimelightPlacement {
	};

	// Auto
	public AutoStep[] autonomous;
	public int currentAutoStep;

	public int accum;

	public void robotInit() {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(2);
	}

	public void disabledInit() {
		// Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);
	}

	public void autonomousInit() {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(1);

		driveTrain.SetBreak();
		currentState = RobotState.Autonomous;

		// currentAutoStep = 0;
		// autonomous = new AutoStep[21];
	}

	public void autonomousPeriodic() {
	}

	public void DiskBrakeEnable() {
		System.out.println("Enabled");
		SmartDashboard.putBoolean("diskBrakeStatus", true);
		diskBrake.set(DoubleSolenoid.Value.kReverse);
	}

	public void DiskBrakeDisable() {
		System.out.println("Disabled");
		SmartDashboard.putBoolean("diskBrakeStatus", false);
		diskBrake.set(DoubleSolenoid.Value.kForward);
	}

	public void teleopInit() {
		// colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(2);

		currentState = RobotState.Teleop;

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
		SmartDashboard.putNumber("testKey", testNumber++);
		// Intake Sensor (31)
		if (ColorWheelLimit.get() == true && !limitPressed) {
			limitPressed = true;
			colorChangsCount = colorChangsCount + 1;
		}
		if (ColorWheelLimit.get() == false) {
			limitPressed = false;
		}
		// System.out.println("Color Changes: " + colorChangsCount);

		// Color Sensor (25)
		// ColorWheel currentColor = GetCurrentColor();
		// if (currentColor != LastColor && currentColor != ColorWheel.Unknown) {
		// colorChangsCount = colorChangsCount + 1;
		// LastColor = currentColor;
		// }

		if (flightStickLeft.getRawButton(8)) {

			colorChangsCount = 0;

		}
		// Auto Shoot without limelight
	//TODO
		if (flightStickRight.getRawButton(2) && speedToggle == false ){
			speedToggle = true;
			preShooter.set(ControlMode.PercentOutput, 1.0f);
			preShooterTwo.set(ControlMode.PercentOutput, 1.0f);
			shooterFour.set(ControlMode.PercentOutput, 1.0f);
			shooterThree.set(ControlMode.PercentOutput, 1.0f);
		}else if (flightStickRight.getRawButton(2) && speedToggle == true ){
			speedToggle = false;
			preShooter.set(ControlMode.PercentOutput, 0.0f);
			preShooterTwo.set(ControlMode.PercentOutput, 0.0f);
			shooterFour.set(ControlMode.PercentOutput, 0.0f);
			shooterThree.set(ControlMode.PercentOutput, 0.0f);
		}

		// Beam Break
		if (flightStickRight.getRawButton(10)) {

			indexer.set(ControlMode.PercentOutput, 0.4f);

		} else {
			if (beamBreak.get() == false) {

				if (beamReset) {
					beamReset = false;
					indexer.set(ControlMode.PercentOutput, 0.4f);
					indexTimer.reset();
					indexTimer.start();
				}

			} else {
				beamReset = true;
			}

			if (indexTimer.get() >= .2) {

				indexer.set(ControlMode.PercentOutput, 0.0f);

			}
		}





		// Color Wheel Spin

		if (flightStickLeft.getRawButton(9)) {

			if (colorChangsCount <= 31) {// 25

				climberOne.set(ControlMode.PercentOutput, 1.0f);

			} else {

				climberOne.set(ControlMode.PercentOutput, 0.0f);

			}

		} else {

			if (flightStickLeft.getRawButton(10)) {

				climberOne.set(ControlMode.PercentOutput, 1.0f);

			} else {

				climberOne.set(ControlMode.PercentOutput, 0.0f);

			}
		}

		// Color Wheel Find
		if (flightStickLeft.getRawButton(11)) {

			if (GetTargetColor() != LastColor) {

				climberOne.set(ControlMode.PercentOutput, 1.0f);

			} else {

				climberOne.set(ControlMode.PercentOutput, 0.0f);

			}

		}

		// Disk Break
		if (flightStickRight.getRawButtonPressed(11)) {

			DiskBrakeEnable();

		} else if (flightStickRight.getRawButtonPressed(10)) {

			DiskBrakeDisable();
		}

		SmartDashboard.putNumber("UltrasonicDown", 123.0);

		double valueFromDashboard = SmartDashboard.getNumber("dashboard_key", 0.0);
		boolean runShooter = SmartDashboard.getBoolean("RunShooter", false);
		if (runShooter) {
			// Run shooter motors
		}

		// // Climber
		// if (flightStickRight.getRawButton(9)) {

		// climberOne.set(ControlMode.PercentOutput, 1.0f * -1);
		// climberTwo.set(ControlMode.PercentOutput, 1.0f);

		// } else if (flightStickRight.getRawButton(8)) {

		// climberOne.set(ControlMode.PercentOutput, 1.0f);
		// climberTwo.set(ControlMode.PercentOutput, 1.0f * -1);

		// } else {
		// climberOne.set(ControlMode.PercentOutput, 0);
		// climberTwo.set(ControlMode.PercentOutput, 0);
		// }

		// Limelight
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tx = table.getEntry("tx");
		NetworkTableEntry ty = table.getEntry("ty");
		NetworkTableEntry ta = table.getEntry("ta");

		double x = tx.getDouble(0.0);
		double y = ty.getDouble(0.0);
		double area = ta.getDouble(0.0);

		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(0);

		if (flightStickLeft.getRawButton(6) || (flightStickLeft.getRawButton(7))) {

			float inverted = 1;

			if (flightStickLeft.getRawButton(6)) {

				inverted = 1;

			} else {

				inverted = -1;

			}

			float maxArea = 3.4f;
			float minArea = 0.26f;
			float currentAreaPercentage = ((float) area - minArea) / (maxArea - minArea);

			float maxSpeed = 0.9f;
			float minSpeed = 0.7f;
			float currentSpeed = Lerp(minSpeed, maxSpeed, currentAreaPercentage);

			float turnBuffer = 1.5f;
			float turnSpeedSlow = -currentSpeed * 0.5f;

			if (x * inverted > turnBuffer) {

				driveTrain.SetLeftSpeed(currentSpeed * inverted);
				driveTrain.SetRightSpeed(turnSpeedSlow * inverted);

			} else if (x * inverted < -turnBuffer) {

				driveTrain.SetLeftSpeed(turnSpeedSlow * inverted);
				driveTrain.SetRightSpeed(currentSpeed * inverted);

			} else {

				driveTrain.SetLeftSpeed(0.0f);
				driveTrain.SetRightSpeed(0.0f);

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

	}

	public void testPeriodic() {

		powerAxis = (Math.round(((((flightStickRight.getRawAxis(2) * -100) + 100) / 2))) / 100.0);

		
		// System.out.println(climberOne.getSelectedSensorPosition());
		System.out.println(powerAxis);

		// Shooter
		if (flightStickRight.getRawButton(11)) {
			preShooter.set(ControlMode.PercentOutput, 1.0f);
			climberOne.set(ControlMode.PercentOutput, 1.0f * -1);
			climberTwo.set(ControlMode.PercentOutput, 1.0f);
			shooterThree.set(ControlMode.PercentOutput, 1.0f * -1);
			shooterFour.set(ControlMode.PercentOutput, 1.0f);

		} else if (flightStickRight.getRawButton(10)) {

			preShooter.set(ControlMode.PercentOutput, 1.0f);
			climberOne.set(ControlMode.PercentOutput, 0.25f * -1);
			climberTwo.set(ControlMode.PercentOutput, 0.25f);
			shooterThree.set(ControlMode.PercentOutput, 0.25f * -1);
			shooterFour.set(ControlMode.PercentOutput, 0.25f);

		} else if (flightStickRight.getRawButton(6)) {

			preShooter.set(ControlMode.PercentOutput, 1.0f);
			shooterThree.set(ControlMode.PercentOutput, powerAxis * -1);
			shooterFour.set(ControlMode.PercentOutput, powerAxis);
			climberOne.set(ControlMode.PercentOutput, powerAxis * -1);
			climberTwo.set(ControlMode.PercentOutput, powerAxis);

		} else {

			preShooter.set(ControlMode.PercentOutput, 0);
			climberOne.set(ControlMode.PercentOutput, 0);
			climberTwo.set(ControlMode.PercentOutput, 0);
			shooterThree.set(ControlMode.PercentOutput, 0);
			shooterFour.set(ControlMode.PercentOutput, 0);
		}

		// Encoder
		float oldSpeed = encoderSpeed1;

		encoderSpeed1 = climberOne.getSelectedSensorVelocity();
		// System.out.println(encoderSpeed1);

		if (oldSpeed != encoderSpeed1) {

			SmartDashboard.putNumber("encoderSpeed1", encoderSpeed1);

		}

		// encoderSpeed2 = climberTwo.getSelectedSensorVelocity();
		// System.out.println("climber two" + encoderSpeed2);

		ControllerDrive();
		UpdateMotors();

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

	public float Lerp(float v0, float v1, float t) {

		if (t < 0) {
			t = 0;

		} else if (t > 1) {
			t = 1;
		}

		return (v0 + t * (v1 - v0));
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

	// RPM Caculator
	public float ticksToRPM(float ticks, float gearBoxReduction) {
		// Ticks is encoder measurment of distance 1024 ticks per revolution.
		// gearBoxReduction is the reduction AFTER the encoder to the object that you
		// want
		// to caculate the RPMs for.
		// Multiply by six hundred because time was measured in 1 milli seconds.
		// Basing off of Vexpro VersaPlanetary gearbox system.
		float rpm = (((ticks / 1024) * 600) / gearBoxReduction);

		return rpm;
	}

}