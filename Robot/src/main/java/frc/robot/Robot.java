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
import edu.wpi.first.wpilibj.controller.PIDController;
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

// 6 Shooter with PowerAxis

// 8 Climb up !DOUBLE CHECK!
// 9 climb down !DOUBLE CHECK!

// 10 Shooter at Low Power
// 11 Shooter at High Power

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

	// Logic
	public boolean speedToggle = false;

	// Joysticks
	public Joystick driver;
	public Joystick operator;
	public boolean arcadeDrive = false;
	public Joystick flightStickLeft;
	public Joystick flightStickRight;

	public double powerAxis;

	// Motors
	public TalonSRX indexerThree = new TalonSRX(3);

	public TalonSRX preShooterFour = new TalonSRX(4);
	public TalonSRX preShooterSix = new TalonSRX(6);

	public TalonSRX shooterTwo = new TalonSRX(2);
	public TalonSRX shooterFive = new TalonSRX(5);

	public TalonSRX MotorSeven = new TalonSRX(7);
	public TalonSRX MotorEight = new TalonSRX(8);

	// Timer
	public Timer indexTimer = new Timer();

	// PID
	public double shooterP = 0.002;
	public double shooterI = 0.001;
	public double shooterD = 0.00001;
	public double shooterRPMTarget;

	String shooterPKey = "Shooter_P";
	String shooterIKey = "Shooter_I";
	String shooterDKey = "Shooter_D";
	String shooterRPMKey = "Shooter_RPMTarget";

	PIDController shooterPid = new PIDController(shooterP, shooterI, shooterD);

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

	// Auto
	public AutoStep[] autonomous;
	public int currentAutoStep;

	public int accum;

	public void robotInit() {
		SmartDashboard.putNumber(shooterPKey, shooterP);
		SmartDashboard.putNumber(shooterIKey, shooterI);
		SmartDashboard.putNumber(shooterDKey, shooterD);
		SmartDashboard.putNumber(shooterRPMKey, shooterRPMTarget);

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

		// Color Wheel

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

		// Reset Color CHanges Number
		if (flightStickLeft.getRawButton(8)) {

			colorChangsCount = 0;

		}

		// SHOOTER CODE #2

		powerAxis = (Math.round(((((flightStickRight.getRawAxis(2) * -100) + 100) / 2))) / 100.0);

		// Auto Shoot without limelight
		SmartDashboard.putNumber("Shooter_Speed", shooterTwo.getSelectedSensorVelocity());

		if (flightStickRight.getRawButton(11)) {
			// speedToggle = true;
			// preShooterFour.set(ControlMode.PercentOutput, 0.25f * -1);
			// preShooterSix.set(ControlMode.PercentOutput, 0.25f * -1);
			shooterTwo.set(ControlMode.PercentOutput, 0.7f);
			shooterFive.set(ControlMode.PercentOutput, 0.7f * -1);
		} else {
			// } else if (flightStickRight.getRawButton(2) && speedToggle == true) {
			// speedToggle = false;
			// preShooterFour.set(ControlMode.PercentOutput, 0.0f);
			// preShooterSix.set(ControlMode.PercentOutput, 0.0f);
			shooterTwo.set(ControlMode.PercentOutput, 0.0f);
			shooterFive.set(ControlMode.PercentOutput, 0.0f);
		}

		// Manual Motor index drive
		if (flightStickRight.getRawButton(10)) {

			indexerThree.set(ControlMode.PercentOutput, 0.25f);

		} else {
			indexerThree.set(ControlMode.PercentOutput, 0.0f);
		}

		// Beam Break
		// if (flightStickRight.getRawButton(10)) {

		// indexerThree.set(ControlMode.PercentOutput, 0.4f);

		// } else {
		// if (beamBreak.get() == false) {

		// if (beamReset) {
		// beamReset = false;
		// indexerThree.set(ControlMode.PercentOutput, 0.4f);
		// indexTimer.reset();
		// indexTimer.start();
		// }

		// } else {
		// beamReset = true;
		// }

		// if (indexTimer.get() >= .2) {

		// indexerThree.set(ControlMode.PercentOutput, 0.0f);

		// }
		// }

		// Color Wheel Spin
		if (flightStickLeft.getRawButton(9)) {

			if (colorChangsCount <= 31) {// 25

				MotorSeven.set(ControlMode.PercentOutput, 1.0f);

			} else {

				MotorSeven.set(ControlMode.PercentOutput, 0.0f);

			}

		} else {

			if (flightStickLeft.getRawButton(10)) {

				MotorSeven.set(ControlMode.PercentOutput, 1.0f);

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

		// Disk Break
		// if (flightStickRight.getRawButtonPressed(11)) {

		// DiskBrakeEnable();

		// } else if (flightStickRight.getRawButtonPressed(10)) {

		// DiskBrakeDisable();

		// }

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

		boolean usePid = true;

		// rpm pid
		if (usePid) {
			float rpm = TalonVelocityToRPM(shooterTwo.getSelectedSensorVelocity(), 1.0f);
			SmartDashboard.putNumber("Shooter_RPM", rpm);

			shooterP = SmartDashboard.getNumber(shooterPKey, shooterP);
			shooterI = SmartDashboard.getNumber(shooterIKey, shooterI);
			shooterD = SmartDashboard.getNumber(shooterDKey, shooterD);
			shooterRPMTarget = SmartDashboard.getNumber(shooterRPMKey, shooterRPMTarget);

			shooterPid.setP(shooterP);
			shooterPid.setI(shooterI);
			shooterPid.setD(shooterD);

			double motorSpeed = shooterPid.calculate(rpm, shooterRPMTarget);

			System.out.println("Speed " + motorSpeed + " RPM " + rpm);

			ShooterPower((float) motorSpeed, 0.0f);
		} else {

			// Shooter
			if (flightStickRight.getRawButton(11)) {
				ShooterPower(1.0f, 1.0f);
			} else if (flightStickRight.getRawButton(10)) {
				ShooterPower(0.25f, 1.0f);
			} else if (flightStickRight.getRawButton(6)) {
				ShooterPower((float) powerAxis, 1.0f);
			} else {
				ShooterPower(0.0f, 0.0f);
			}
		}

		// Encoder
		float oldSpeed = encoderSpeed1;

		encoderSpeed1 = MotorSeven.getSelectedSensorVelocity();
		// System.out.println(encoderSpeed1);

		if (oldSpeed != encoderSpeed1) {

			// SmartDashboard.putNumber("encoderSpeed1", encoderSpeed1);

		}

		// encoderSpeed2 = MotorEight.getSelectedSensorVelocity();
		// System.out.println("climber two" + encoderSpeed2);

		ControllerDrive();
		UpdateMotors();

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

	// DiskBrake
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

	// Shooter Motors
	public void ShooterPower(float power, float preshooterpower) {

		preShooterFour.set(ControlMode.PercentOutput, preshooterpower);
		MotorSeven.set(ControlMode.PercentOutput, power * -1);
		MotorEight.set(ControlMode.PercentOutput, power);
		shooterFive.set(ControlMode.PercentOutput, power * -1);
		shooterTwo.set(ControlMode.PercentOutput, power);

	}

	// RPM Caculator
	public float TalonVelocityToRPM(float ticks, float gearBoxReduction) {
		float rpm = ((ticks / 4652) * 600);
		return Math.abs(rpm);
	}

}